package dev.krinry.jarvis.ui.screens.chat

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.krinry.jarvis.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatMessage(
    val text: String,
    val time: String,
    val isUser: Boolean,
    val type: MessageType = MessageType.TEXT
)

enum class MessageType { TEXT, WEATHER, LOADING }

@Composable
fun ChatScreen() {
    var isChatMode by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    
    var messages by remember { mutableStateOf(
        listOf(
            ChatMessage("Hello Anuj! \uD83D\uDC4B\nHow can I assist you today?", "09:41 AM", false),
            ChatMessage("What's the weather in Mumbai?", "09:41 AM", true),
            ChatMessage("", "09:41 AM", false, MessageType.WEATHER)
        )
    ) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        ChatHeader()
        
        ModeToggle(isChatMode = isChatMode, onModeChanged = { isChatMode = it })
        
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(messages) { message ->
                when (message.type) {
                    MessageType.TEXT -> {
                        if (message.isUser) {
                            UserMessage(message.text, message.time)
                        } else {
                            AssistantMessage(message.text, message.time)
                        }
                    }
                    MessageType.WEATHER -> {
                        RichWeatherMessage(message.time)
                    }
                    MessageType.LOADING -> {
                        dev.krinry.jarvis.ui.components.TerminalLoadingIndicator()
                    }
                }
            }
        }
        
        dev.krinry.jarvis.ui.components.TerminalInput(
            onSendMessage = { text ->
                val time = getCurrentTime()
                messages = messages + ChatMessage(text, time, true)
                messages = messages + ChatMessage("", time, false, MessageType.LOADING)
                
                coroutineScope.launch {
                    val apiKey = dev.krinry.jarvis.security.SecureKeyStore.getProviderApiKey(context, "gemini")
                        ?: dev.krinry.jarvis.security.SecureKeyStore.getProviderApiKey(context, "groq")
                    
                    if (apiKey.isNullOrEmpty()) {
                        messages = messages.filter { it.type != MessageType.LOADING }
                        messages = messages + ChatMessage(
                            "⚠️ No API key found. Please go to Setup and add your Gemini or Groq API key.",
                            getCurrentTime(), false, MessageType.TEXT
                        )
                        return@launch
                    }

                    try {
                        val provider = dev.krinry.jarvis.security.SecureKeyStore.getSelectedProvider(context) ?: "gemini"
                        val model = dev.krinry.jarvis.security.SecureKeyStore.getSelectedModel(context) ?: "gemini-2.0-flash"
                        
                        val response = dev.krinry.jarvis.ai.LlmProvider.chat(
                            context = context,
                            provider = provider,
                            model = model,
                            userMessage = text,
                            isChatMode = isChatMode
                        )
                        
                        messages = messages.filter { it.type != MessageType.LOADING }
                        messages = messages + ChatMessage(
                            response ?: "Sorry, I could not get a response. Please try again.",
                            getCurrentTime(), false, MessageType.TEXT
                        )
                    } catch (e: Exception) {
                        messages = messages.filter { it.type != MessageType.LOADING }
                        messages = messages + ChatMessage(
                            "❌ Error: ${e.message}",
                            getCurrentTime(), false, MessageType.TEXT
                        )
                    }
                }
            }
        )
    }
}

fun getCurrentTime(): String {
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
}

@Composable
fun ChatHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .border(1.dp, SurfaceVariantDark, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                dev.krinry.jarvis.ui.components.Glowing3DIcon(Icons.Default.SmartToy, contentDescription = "AI Avatar", tint = NeonGreen, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("CHAT WITH MAX", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("AI CONVERSATION INTERFACE", color = TextSecondary, fontSize = 10.sp, letterSpacing = 0.5.sp)
            }
        }
        IconButton(onClick = { /* TODO */ }) {
            Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = TextSecondary)
        }
    }
}

@Composable
fun ModeToggle(isChatMode: Boolean, onModeChanged: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(48.dp)
            .background(SurfaceDark, RoundedCornerShape(24.dp))
            .border(1.dp, SurfaceVariantDark, RoundedCornerShape(24.dp))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(if (isChatMode) NeonGreen.copy(alpha = 0.15f) else Color.Transparent, RoundedCornerShape(20.dp))
                .clickable { onModeChanged(true) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "CHAT MODE", 
                color = if (isChatMode) NeonGreen else TextSecondary, 
                fontWeight = FontWeight.Bold, 
                fontSize = 12.sp
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(if (!isChatMode) NeonGreen.copy(alpha = 0.15f) else Color.Transparent, RoundedCornerShape(20.dp))
                .clickable { onModeChanged(false) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "AGENT MODE", 
                color = if (!isChatMode) NeonGreen else TextSecondary, 
                fontWeight = FontWeight.Bold, 
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun AssistantMessage(text: String, time: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        dev.krinry.jarvis.ui.components.Glowing3DIcon(Icons.Default.SmartToy, contentDescription = "AI", tint = NeonGreen, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text("MAX", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text, color = TextPrimary, fontSize = 15.sp, lineHeight = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(time, color = TextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
fun UserMessage(text: String, time: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Column(
            modifier = Modifier
                .background(NeonGreen.copy(alpha = 0.15f), RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp))
                .border(1.dp, NeonGreen.copy(alpha = 0.3f), RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp))
                .padding(16.dp)
        ) {
            Text(text, color = TextPrimary, fontSize = 15.sp)
        }
    }
    Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.End) {
        Text(time, color = TextSecondary, fontSize = 10.sp)
    }
}

@Composable
fun RichWeatherMessage(time: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        dev.krinry.jarvis.ui.components.Glowing3DIcon(Icons.Default.SmartToy, contentDescription = "AI", tint = NeonGreen, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("MAX", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark, RoundedCornerShape(16.dp))
                    .border(1.dp, SurfaceVariantDark, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text("Mumbai, India", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Cloud, contentDescription = "Cloudy", tint = Color.White, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("28°C", color = TextPrimary, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        Text("Cloudy", color = NeonGreen, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Feels like 31°C", color = TextSecondary, fontSize = 12.sp)
                    Text("Humidity: 78%", color = TextSecondary, fontSize = 12.sp)
                }
                Text("Wind: 12 km/h   AQI: 42 (Good)", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Quick action chips
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipAction("Open WhatsApp")
                ChipAction("Set reminder")
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            Text(time, color = TextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
fun ChipAction(text: String) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .background(SurfaceDark, RoundedCornerShape(16.dp))
            .border(1.dp, SurfaceVariantDark, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable { Toast.makeText(context, text, Toast.LENGTH_SHORT).show() }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.PlayCircleOutline, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text, color = TextPrimary, fontSize = 12.sp)
        }
    }
}