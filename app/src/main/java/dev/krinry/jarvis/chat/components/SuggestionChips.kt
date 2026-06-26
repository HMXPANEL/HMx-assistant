package dev.krinry.jarvis.chat.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.krinry.jarvis.ui.theme.CyanGlow
import dev.krinry.jarvis.ui.theme.GlassWhite
import dev.krinry.jarvis.ui.theme.TextPrimary

@Composable
fun SuggestionChips(
    suggestions: List<String>,
    onSuggestionTap: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (suggestions.isEmpty()) return
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (suggestion in suggestions) {
            Surface(
                onClick = { onSuggestionTap(suggestion) },
                shape = RoundedCornerShape(16.dp),
                color = GlassWhite,
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    text = suggestion,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}