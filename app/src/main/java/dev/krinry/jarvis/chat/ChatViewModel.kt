package dev.krinry.jarvis.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.krinry.jarvis.agent.AgentLlmEngine
import dev.krinry.jarvis.ai.GroqApiClient
import dev.krinry.jarvis.security.SecureKeyStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed interface ChatMessage {
    data class User(val text: String, val timestamp: Long = System.currentTimeMillis()) : ChatMessage
    data class Assistant(
        val text: String,
        val isStreaming: Boolean = false,
        val timestamp: Long = System.currentTimeMillis(),
        val isError: Boolean = false
    ) : ChatMessage
    data class System(val text: String, val timestamp: Long = System.currentTimeMillis()) : ChatMessage
}

class ChatViewModel(private val context: Context) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening = _isListening.asStateFlow()

    private val _suggestions = MutableStateFlow<List<String>>(getDefaultSuggestions())
    val suggestions = _suggestions.asStateFlow()

    private val agentEngine = AgentLlmEngine(context)
    private var currentResponseChannel: Channel<String>? = null

    init {
        addMessage(ChatMessage.System("Tap the microphone or type to start. Say \"Hey Max\" for hands-free."))
    }

    private fun getDefaultSuggestions(): List<String> {
        return listOf(
            "Open WhatsApp",
            "Take a screenshot",
            "Open YouTube",
            "Explain quantum physics",
            "What's the weather?",
            "Set a timer for 5 minutes"
        )
    }

    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank()) return

        addMessage(ChatMessage.User(trimmed))
        _suggestions.value = emptyList()
        processMessage(trimmed)
    }

    private fun processMessage(userMessage: String) {
        _isProcessing.value = true
        val assistantMessage = ChatMessage.Assistant("", isStreaming = true)
        addMessage(assistantMessage)

        viewModelScope.launch {
            try {
                val provider = GroqApiClient.getActiveProvider(context)
                val apiKey = provider.getApiKey(context)
                val model = SecureKeyStore.getPrimaryModel(context).ifEmpty { provider.defaultModel }

                if (apiKey.isNullOrEmpty()) {
                    updateLastAssistantMessage("❌ No API key configured. Go to Settings → AI Configuration to add one.", isError = true)
                    return@launch
                }

                // Determine if this is a command or chat
                val isCommand = isLikelyCommand(userMessage)

                if (isCommand) {
                    // Use agent mode for device commands
                    agentEngine.startTask(userMessage, viewModelScope)
                    // Agent speaks via TTS and updates status through callback
                    // We'll show a brief message and let the agent handle the rest
                    updateLastAssistantMessage("🤖 Executing command...", isError = false)
                } else {
                    // Use chat mode for conversation
                    val response = agentEngine.chatOnly(userMessage)
                    if (response != null) {
                        updateLastAssistantMessage(response, isError = false)
                    } else {
                        updateLastAssistantMessage("❌ No response from AI. Check your connection and API key.", isError = true)
                    }
                }
            } catch (e: Exception) {
                updateLastAssistantMessage("❌ Error: ${e.message}", isError = true)
            } finally {
                _isProcessing.value = false
            }
        }
    }

    private fun isLikelyCommand(text: String): Boolean {
        val lower = text.lowercase()
        val commandKeywords = listOf(
            "open", "launch", "start", "close", "stop", "click", "tap", "type", "scroll",
            "swipe", "screenshot", "back", "home", "recent", "notification", "volume",
            "brightness", "flashlight", "call", "message", "send", "search", "find",
            "play", "pause", "next", "previous", "setting", "wifi", "bluetooth", "alarm"
        )
        return commandKeywords.any { lower.contains(it) }
    }

    private fun addMessage(message: ChatMessage) {
        _messages.update { it + message }
    }

    private fun updateLastAssistantMessage(text: String, isError: Boolean) {
        _messages.update { list ->
            if (list.isEmpty()) return@update list
            val last = list.last()
            if (last is ChatMessage.Assistant) {
                list.dropLast(1) + last.copy(text = text, isStreaming = false, isError = isError)
            } else {
                list + ChatMessage.Assistant(text, isStreaming = false, isError = isError)
            }
        }
    }

    fun startVoiceInput() {
        _isListening.value = true
        // Trigger floating bubble service to start recording
        val intent = android.content.Intent(context, dev.krinry.jarvis.service.FloatingBubbleService::class.java).apply {
            action = dev.krinry.jarvis.service.FloatingBubbleService.ACTION_WAKE_TRIGGERED
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopVoiceInput() {
        _isListening.value = false
    }

    fun onVoiceResult(text: String) {
        sendMessage(text)
    }

    fun clearChat() {
        _messages.update { emptyList() }
        addMessage(ChatMessage.System("Chat cleared. Tap the microphone or type to start."))
        _suggestions.value = getDefaultSuggestions()
    }

    fun regenerateLastResponse() {
        val lastUserMessage = _messages.value
            .filter { it is ChatMessage.User }
            .lastOrNull() as? ChatMessage.User

        lastUserMessage?.let {
            // Remove last assistant message
            _messages.update { list ->
                if (list.size >= 2 && list[list.size - 2] is ChatMessage.User && list.last() is ChatMessage.Assistant) {
                    list.dropLast(1)
                } else {
                    list
                }
            }
            processMessage(it.text)
        }
    }

    fun copyMessage(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Jarvis Response", text)
        clipboard.primaryClip = clip
    }

    override fun onCleared() {
        agentEngine.cancelTask()
        super.onCleared()
    }
}