package dev.krinry.jarvis.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.krinry.jarvis.ui.theme.NeonGreen

@Composable
fun TerminalLoadingIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    Row(
        modifier = modifier.padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Processing...",
            color = NeonGreen,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (i in 0 until 3) {
                val offset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes {
                            durationMillis = 1200
                            0.0f at 0 with FastOutSlowInEasing
                            1.0f at 400 + (i * 200) with FastOutSlowInEasing
                            0.0f at 800 + (i * 200) with FastOutSlowInEasing
                            0.0f at 1200
                        },
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "bounce_$i"
                )

                val rotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 180f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1200, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "rotate_$i"
                )

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .graphicsLayer {
                            translationY = -offset * 12.dp.toPx()
                            rotationX = rotation
                            rotationY = rotation
                        }
                        .shadow(
                            elevation = 6.dp,
                            spotColor = NeonGreen,
                            ambientColor = NeonGreen
                        )
                        .background(NeonGreen.copy(alpha = 0.8f), RoundedCornerShape(2.dp))
                        .border(1.dp, NeonGreen, RoundedCornerShape(2.dp))
                )
            }
        }
    }
}