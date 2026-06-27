package dev.krinry.jarvis.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A spacing scale similar to Tailwind CSS, adapted for native Android DP units.
 */
data class Spacing(
    val default: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,    // Equivalent to p-1
    val small: Dp = 8.dp,         // Equivalent to p-2
    val medium: Dp = 16.dp,       // Equivalent to p-4
    val large: Dp = 24.dp,        // Equivalent to p-6
    val extraLarge: Dp = 32.dp,   // Equivalent to p-8
    val xxl: Dp = 48.dp,          // Equivalent to p-12
    val xxxl: Dp = 64.dp          // Equivalent to p-16
)

val LocalSpacing = compositionLocalOf { Spacing() }

/**
 * Extension property to access spacing directly from MaterialTheme.
 * Usage: MaterialTheme.spacing.medium
 */
val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current