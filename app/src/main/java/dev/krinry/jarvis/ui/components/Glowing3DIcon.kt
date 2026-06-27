package dev.krinry.jarvis.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.krinry.jarvis.ui.theme.NeonGreen

@Composable
fun Glowing3DIcon(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = NeonGreen,
    glowColor: Color = tint.copy(alpha = 0.3f)
) {
    Box(modifier = modifier) {
        // Shadow/Glow layer
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = glowColor,
            modifier = Modifier
                .matchParentSize()
                .offset(y = 2.dp) // Slight offset for 3D depth
                .blur(radius = 4.dp)
                .scale(1.02f)
        )
        
        // Base layer
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint.copy(alpha = 0.5f),
            modifier = Modifier
                .matchParentSize()
                .offset(x = 1.dp, y = 1.dp)
        )
        
        // Top highlight layer
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.matchParentSize()
        )
    }
}