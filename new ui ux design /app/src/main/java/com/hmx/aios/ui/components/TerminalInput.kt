package com.hmx.aios.ui.components

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hmx.aios.ui.theme.DarkBackground
import com.hmx.aios.ui.theme.NeonGreen
import com.hmx.aios.ui.theme.SurfaceDark
import com.hmx.aios.ui.theme.TextPrimary

@Composable
fun TerminalInput(
    modifier: Modifier = Modifier,
    onSendMessage: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                text = matches[0]
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = NeonGreen,
                ambientColor = NeonGreen
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SurfaceDark,
                        DarkBackground
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = NeonGreen.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = ">_",
                color = NeonGreen,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                textStyle = TextStyle(
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp
                ),
                cursorBrush = SolidColor(NeonGreen),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (text.isEmpty()) {
                        Text(
                            text = "awaiting command...",
                            color = NeonGreen.copy(alpha = 0.4f),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            )
            
            IconButton(
                onClick = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your command...")
                    }
                    try {
                        speechLauncher.launch(intent)
                    } catch (e: Exception) {
                        // Ignore if no speech recognizer app is available
                    }
                },
                modifier = Modifier.size(36.dp)
            ) {
                Glowing3DIcon(
                    icon = Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = NeonGreen
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSendMessage(text)
                        text = ""
                    }
                },
                modifier = Modifier.size(36.dp)
            ) {
                Glowing3DIcon(
                    icon = Icons.Default.Send,
                    contentDescription = "Execute",
                    tint = NeonGreen
                )
            }
        }
    }
}
