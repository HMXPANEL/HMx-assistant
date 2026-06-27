package dev.krinry.jarvis.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import dev.krinry.jarvis.ui.theme.NeonGreen

@Composable
fun AnimatedCyberBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_anim")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 150f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val gridSize = 150f
        val width = size.width
        val height = size.height

        val gridColor = NeonGreen.copy(alpha = 0.04f)

        // Draw vertical lines
        var x = offset % gridSize
        while (x < width) {
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 2f
            )
            x += gridSize
        }

        // Draw horizontal lines
        var y = offset % gridSize
        while (y < height) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 2f
            )
            y += gridSize
        }
    }
}