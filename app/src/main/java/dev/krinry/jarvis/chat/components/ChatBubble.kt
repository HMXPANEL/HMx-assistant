package dev.krinry.jarvis.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.krinry.jarvis.chat.ChatMessage
import dev.krinry.jarvis.ui.theme.*

@Composable
fun ChatBubble(
    message: ChatMessage,
    onCopy: (String) -> Unit,
    onRegenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically { it / 2 }
    ) {
        when (message) {
            is ChatMessage.User -> UserBubble(message.text)
            is ChatMessage.Assistant -> AssistantBubble(
                message = message,
                onCopy = onCopy,
                onRegenerate = onRegenerate
            )
            is ChatMessage.System -> SystemMessage(message.text)
        }
    }
}

@Composable
private fun UserBubble(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.End
    ) {
        SelectionContainer {
            Text(
                text = text,
                color = Color.White,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp))
                    .background(CyanGlow.copy(alpha = 0.2f))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .widthIn(max = 280.dp)
            )
        }
    }
}

@Composable
private fun AssistantBubble(
    message: ChatMessage.Assistant,
    onCopy: (String) -> Unit,
    onRegenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            if (message.isStreaming && message.text.isEmpty()) {
                TypingIndicator()
            } else {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp))
                        .background(
                            if (message.isError) StatusRed.copy(alpha = 0.15f)
                            else GlassWhite
                        )
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .widthIn(max = 300.dp)
                ) {
                    if (message.isError) {
                        SelectionContainer {
                            Text(
                                text = message.text,
                                color = StatusRed,
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            )
                        }
                    } else {
                        ChatMarkdownContent(
                            text = message.text,
                            isStreaming = message.isStreaming
                        )
                    }
                }
            }
        }
        if (!message.isStreaming && message.text.isNotEmpty()) {
            Row(
                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                BubbleActionButton(onClick = { onCopy(message.text) }, icon = "\uD83D\uDCCB")
                if (onRegenerate != { }) {
                    BubbleActionButton(onClick = onRegenerate, icon = "\uD83D\uDD04")
                }
            }
        }
    }
}

@Composable
private fun BubbleActionButton(onClick: () -> Unit, icon: String) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        color = Color.Transparent,
        modifier = Modifier.size(28.dp)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(icon, fontSize = 12.sp)
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp))
            .background(GlassWhite)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text("●", fontSize = 10.sp, color = CyanGlow)
        Spacer(Modifier.width(4.dp))
        Text("●", fontSize = 10.sp, color = CyanGlow.copy(alpha = 0.6f))
        Spacer(Modifier.width(4.dp))
        Text("●", fontSize = 10.sp, color = CyanGlow.copy(alpha = 0.3f))
    }
}

@Composable
fun SystemMessage(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            color = TextMuted,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            modifier = Modifier.widthIn(max = 300.dp)
        )
    }
}