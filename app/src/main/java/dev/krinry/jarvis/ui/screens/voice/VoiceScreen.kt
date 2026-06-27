package dev.krinry.jarvis.ui.screens.voice

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.krinry.jarvis.ui.theme.*
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun VoiceScreen() {
    val context = LocalContext.current
    val isListening = remember { mutableStateOf(dev.krinry.jarvis.service.WakeWordService.isRunning) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.GraphicEq, contentDescription = "Voice", tint = NeonGreen)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("VOICE ASSISTANT", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("HANDS-FREE INTERFACE", color = TextSecondary, fontSize = 10.sp, letterSpacing = 1.sp)
            }
            Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = TextSecondary)
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.Center
        ) {
            dev.krinry.jarvis.ui.components.AICoreOrb(modifier = Modifier.size(240.dp))
        }
        
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(if (isListening.value) "LISTENING" else "STANDBY", color = NeonGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Say \"Hey Max\" to wake me up", color = TextSecondary, fontSize = 14.sp)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, RoundedCornerShape(16.dp))
                .border(1.dp, SurfaceVariantDark, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("DETECTION HISTORY", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("See All", color = NeonGreen, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            DetectionItem("Hey Max", "Today, 09:30 AM")
            DetectionItem("Hey Max, call Mom", "Today, 08:10 AM")
            DetectionItem("Hey Max, set alarm", "Yesterday, 10:15 PM")
            DetectionItem("What's the time?", "Yesterday, 09:00 PM")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (!isListening.value) {
            Button(
                onClick = {
                    val intent = Intent(context, dev.krinry.jarvis.service.WakeWordService::class.java).apply {
                        action = dev.krinry.jarvis.service.WakeWordService.ACTION_START_WAKE
                    }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }
                    isListening.value = true
                    Toast.makeText(context, "Listening for Hey Max...", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = DarkBackground),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("START LISTENING", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        } else {
            Button(
                onClick = {
                    val intent = Intent(context, dev.krinry.jarvis.service.WakeWordService::class.java).apply {
                        action = dev.krinry.jarvis.service.WakeWordService.ACTION_STOP_WAKE
                    }
                    context.startService(intent)
                    isListening.value = false
                    Toast.makeText(context, "Wake word detection stopped", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = NeonGreen),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen)
            ) {
                Icon(Icons.Default.StopCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("STOP LISTENING", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
fun DetectionItem(text: String, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            dev.krinry.jarvis.ui.components.Glowing3DIcon(Icons.Default.GraphicEq, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text, color = TextPrimary, fontSize = 14.sp)
                Text(time, color = TextSecondary, fontSize = 12.sp)
            }
        }
        dev.krinry.jarvis.ui.components.Glowing3DIcon(Icons.Default.GraphicEq, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
    }
}