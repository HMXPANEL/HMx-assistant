package dev.krinry.jarvis.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import dev.krinry.jarvis.MainActivity
import dev.krinry.jarvis.R
import dev.krinry.jarvis.ai.GroqApiClient
import dev.krinry.jarvis.security.SecureKeyStore
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class WakeWordService : Service() {

    companion object {
        private const val TAG = "WakeWordService"
        private const val CHANNEL_ID = "jarvis_wakeword_channel"
        private const val NOTIFICATION_ID = 43
        private const val SAMPLE_RATE = 16000
        private const val CHUNK_SECONDS = 2  // Listen in 2-second chunks
        private const val SILENCE_THRESHOLD = 800  // Amplitude threshold to skip silent chunks
        const val ACTION_START_WAKE = "dev.krinry.jarvis.START_WAKE"
        const val ACTION_STOP_WAKE = "dev.krinry.jarvis.STOP_WAKE"

        // Wake word variations for "Hey Max"
        private val WAKE_WORDS = listOf(
            "hey max", "hay max", "hey macs", "hey mex",
            "hey mac", "a max", "okay max", "ok max",
            "heymax", "max", "hey mx"
        )

        @Volatile
        var isRunning = false
            private set
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var audioRecord: AudioRecord? = null
    private var isListening = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        startForegroundNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP_WAKE -> { stopSelf(); return START_NOT_STICKY }
        }
        if (!isListening) startListening()
        return START_STICKY
    }

    override fun onDestroy() {
        isRunning = false
        isListening = false
        scope.cancel()
        try { audioRecord?.stop(); audioRecord?.release() } catch (_: Exception) {}
        audioRecord = null
        super.onDestroy()
    }

    private fun startListening() {
        isListening = true
        scope.launch {
            Log.d(TAG, "Wake word listening started for: Hey Max")
            val bufferSize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
            ) * 4

            while (isListening && isActive) {
                try {
                    // Skip if FloatingBubbleService is already recording
                    if (FloatingBubbleService.isRunning) {
                        delay(500)
                        continue
                    }

                    // Skip if no Groq API key (needed for Whisper)
                    val groqKey = SecureKeyStore.getProviderApiKey(applicationContext, "groq")
                    if (groqKey.isNullOrEmpty()) {
                        delay(2000)
                        continue
                    }

                    audioRecord = AudioRecord(
                        MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize
                    )

                    if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                        delay(1000); continue
                    }

                    val chunkSamples = SAMPLE_RATE * CHUNK_SECONDS
                    val audioBuffer = mutableListOf<Short>()
                    val buffer = ShortArray(bufferSize / 2)

                    audioRecord?.startRecording()

                    while (isListening && audioBuffer.size < chunkSamples) {
                        val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                        if (read > 0) for (i in 0 until read) audioBuffer.add(buffer[i])
                    }

                    audioRecord?.stop()
                    audioRecord?.release()
                    audioRecord = null

                    // Check amplitude — skip silent chunks (saves API calls)
                    val maxAmplitude = audioBuffer.maxOrNull()?.toInt()?.let { Math.abs(it) } ?: 0
                    if (maxAmplitude < SILENCE_THRESHOLD) {
                        audioBuffer.clear()
                        continue
                    }

                    // Save chunk and transcribe
                    val wavFile = saveAsWav(audioBuffer)
                    if (wavFile != null) {
                        val transcript = try {
                            GroqApiClient.transcribeAudio(applicationContext, wavFile, "en")
                        } catch (_: Exception) { null }
                        wavFile.delete()

                        if (!transcript.isNullOrBlank()) {
                            val lower = transcript.trim().lowercase()
                            Log.d(TAG, "Heard: $lower")
                            val detected = WAKE_WORDS.any { wake -> lower.contains(wake) }
                            if (detected) {
                                Log.d(TAG, "WAKE WORD DETECTED!")
                                onWakeWordDetected()
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Wake word error: ${e.message}")
                    try { audioRecord?.stop(); audioRecord?.release() } catch (_: Exception) {}
                    audioRecord = null
                    delay(2000)
                }
            }
        }
    }

    private fun onWakeWordDetected() {
        // Trigger FloatingBubbleService to start recording the command
        val intent = Intent(applicationContext, FloatingBubbleService::class.java).apply {
            action = FloatingBubbleService.ACTION_WAKE_TRIGGERED
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Could not trigger bubble: ${e.message}")
        }
    }

    private fun saveAsWav(audioData: List<Short>): File? {
        return try {
            val file = File(cacheDir, "wake_chunk_${System.currentTimeMillis()}.wav")
            val totalPcmBytes = audioData.size * 2
            FileOutputStream(file).use { fos ->
                val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
                header.put("RIFF".toByteArray()); header.putInt(36 + totalPcmBytes)
                header.put("WAVE".toByteArray()); header.put("fmt ".toByteArray())
                header.putInt(16); header.putShort(1); header.putShort(1)
                header.putInt(SAMPLE_RATE); header.putInt(SAMPLE_RATE * 2)
                header.putShort(2); header.putShort(16)
                header.put("data".toByteArray()); header.putInt(totalPcmBytes)
                fos.write(header.array())
                val pcmBytes = ByteBuffer.allocate(totalPcmBytes).order(ByteOrder.LITTLE_ENDIAN)
                for (sample in audioData) pcmBytes.putShort(sample)
                fos.write(pcmBytes.array())
            }
            file
        } catch (e: Exception) { null }
    }

    private fun startForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Hey Max Wake Word",
                NotificationManager.IMPORTANCE_MIN
            ).apply { setShowBadge(false); setSound(null, null) }
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Listening for Hey Max")
            .setContentText("Say \"Hey Max\" to give a command")
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .setSilent(true)
            .build()

        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(NOTIFICATION_ID, notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }
}
