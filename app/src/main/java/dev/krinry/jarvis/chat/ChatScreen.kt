package dev.krinry.jarvis.chat

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.krinry.jarvis.chat.components.ChatBubble
import dev.krinry.jarvis.chat.components.SuggestionChips
import dev.krinry.jarvis.ui.components.AnimatedBackgroundOrbs
import dev.krinry.jarvis.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateToSettings: () -> Unit,
    chatViewModel: ChatViewModel = viewModel(factory = ChatViewModelFactory(LocalContext.current))
) {
    val messages by chatViewModel.messages.collectAsState()
    val isProcessing by chatViewModel.isProcessing.collectAsState()
    val isListening by chatViewModel.isListening.collectAsState()
    val suggestions by chatViewModel.suggestions.collectAsState()
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val hasMicPermission = remember {
        ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(DeepSpace)) {
        AnimatedBackgroundOrbs()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("JARVIS", fontWeight = FontWeight.Black, fontSize = 20.sp, letterSpacing = 3.sp, color = TextPrimary)
                            Text("AI Assistant", fontSize = 10.sp, letterSpacing = 1.sp, color = CyanGlow.copy(alpha = 0.8f))
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Outlined.Settings, "Settings", tint = TextMuted)
                        }
                    },
                    navigationIcon = {},
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            bottomBar = {
                ChatInputBar(
                    inputText = inputText,
                    onInputChange = { inputText = it },
                    onSend = {
                        if (inputText.isNotBlank()) {
                            chatViewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    },
                    onVoiceTap = {
                        if (hasMicPermission) chatViewModel.startVoiceInput()
                    },
                    isProcessing = isProcessing,
                    modifier = Modifier.background(Color.Transparent)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                if (messages.isEmpty()) {
                    EmptyChatPlaceholder(suggestions) { chatViewModel.sendMessage(it) }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
                    ) {
                        items(
                            items = messages,
                            key = { it.id }
                        ) { message ->
                            val onRegenerate: (() -> Unit)? = if (message is ChatMessage.Assistant && !message.isStreaming) {
                                { chatViewModel.regenerateLastResponse() }
                            } else null
                            ChatBubble(
                                message = message,
                                onCopy = { chatViewModel.copyMessage(it) },
                                onRegenerate = onRegenerate
                            )
                        }
                        item {
                            if (isProcessing && messages.lastOrNull() !is ChatMessage.Assistant) {
                                ChatBubble(
                                    message = ChatMessage.Assistant("", isStreaming = true),
                                    onCopy = {},
                                    onRegenerate = {}
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }
            }
        }

        // Clear chat FAB
        if (messages.isNotEmpty()) {
            FloatingActionButton(
                onClick = { chatViewModel.clearChat() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 72.dp)
                    .size(40.dp),
                containerColor = GlassWhite,
                contentColor = TextMuted,
                shape = CircleShape
            ) {
                Icon(Icons.Outlined.DeleteSweep, "Clear", modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun EmptyChatPlaceholder(
    suggestions: List<String>,
    onSuggestionTap: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.weight(1f))

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.Transparent,
            modifier = Modifier.padding(40.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CyanGlow.copy(alpha = 0.1f),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Outlined.SmartToy,
                            null,
                            tint = CyanGlow,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    "How can I help you?",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Ask me anything or control your device",
                    color = TextMuted,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(Modifier.weight(1f))

        SuggestionChips(
            suggestions = suggestions,
            onSuggestionTap = onSuggestionTap
        )

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ChatInputBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onVoiceTap: () -> Unit,
    isProcessing: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = DeepSpace.copy(alpha = 0.95f),
        tonalElevation = 0.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Voice button
            Surface(
                onClick = { if (!isProcessing) onVoiceTap() },
                shape = CircleShape,
                color = if (isProcessing) GlassWhite else CyanGlow.copy(alpha = 0.15f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.Mic,
                        null,
                        tint = if (isProcessing) TextMuted else CyanGlow,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Text input
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("Ask something...", color = TextMuted, fontSize = 14.sp)
                },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanGlow.copy(alpha = 0.5f),
                    unfocusedBorderColor = GlassBorder,
                    cursorColor = CyanGlow,
                    focusedContainerColor = GlassWhite,
                    unfocusedContainerColor = GlassWhite,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { if (inputText.isNotBlank()) onSend() }),
                maxLines = 1
            )

            // Send button
            Surface(
                onClick = {
                    if (inputText.isNotBlank()) onSend()
                },
                shape = CircleShape,
                color = if (inputText.isNotBlank()) CyanGlow else GlassWhite,
                modifier = Modifier.size(44.dp)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.Send,
                        null,
                        tint = if (inputText.isNotBlank()) Color(0xFF0A1628) else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}