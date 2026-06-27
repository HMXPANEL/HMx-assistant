package com.hmx.aios.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hmx.aios.ui.navigation.Screen
import com.hmx.aios.ui.theme.DarkBackground
import com.hmx.aios.ui.theme.NeonGreen
import com.hmx.aios.ui.theme.TextSecondary

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = DarkBackground,
        contentColor = TextSecondary,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple(Screen.Home.route, "HOME", Icons.Default.Home),
            Triple(Screen.Setup.route, "SETUP", Icons.Default.Settings),
            Triple(Screen.Chat.route, "CHAT", Icons.Default.Chat),
            Triple(Screen.Voice.route, "VOICE", Icons.Default.Mic),
            Triple(Screen.Profile.route, "PROFILE", Icons.Default.Person)
        )

        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = { onNavigate(route) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = NeonGreen.copy(alpha = 0.15f),
                    selectedIconColor = NeonGreen,
                    selectedTextColor = NeonGreen,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}
