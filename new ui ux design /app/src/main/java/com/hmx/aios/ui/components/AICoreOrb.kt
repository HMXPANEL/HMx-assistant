package com.hmx.aios.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.hmx.aios.ui.theme.NeonGreen

@Composable
fun AICoreOrb(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "ai_core_orb")

    val rotation1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "rotation1"
    )

    val rotation2 by infiniteTransition.animateFloat(
        initialValue = 360f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing)),
        label = "rotation2"
    )

    val rotation3 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = "rotation3"
    )

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(200.dp)) {
        // Outer Ring 1
        Box(modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationX = 45f
                rotationY = rotation1
                rotationZ = rotation1
            }
            .border(2.dp, NeonGreen.copy(alpha = 0.3f), CircleShape)
        )

        // Outer Ring 2
        Box(modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationX = 60f
                rotationY = rotation2
                rotationZ = -rotation2
            }
            .border(2.dp, NeonGreen.copy(alpha = 0.6f), CircleShape)
        )

        // Outer Ring 3
        Box(modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationX = 75f
                rotationY = rotation3
                rotationZ = rotation3
            }
            .border(2.dp, NeonGreen, CircleShape)
        )

        // Glowing Core
        val coreScale by infiniteTransition.animateFloat(
            initialValue = 0.8f, targetValue = 1.2f,
            animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
            label = "coreScale"
        )
        Box(modifier = Modifier
            .fillMaxSize(0.3f)
            .graphicsLayer {
                scaleX = coreScale
                scaleY = coreScale
            }
            .background(NeonGreen.copy(alpha = 0.15f), CircleShape)
            .border(2.dp, NeonGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            com.hmx.aios.ui.components.Glowing3DIcon(Icons.Default.GraphicEq, contentDescription = null, tint = NeonGreen)
        }
    }
}
