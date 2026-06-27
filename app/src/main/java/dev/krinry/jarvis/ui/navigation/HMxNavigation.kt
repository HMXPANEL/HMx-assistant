package dev.krinry.jarvis.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.krinry.jarvis.ui.screens.chat.ChatScreen
import dev.krinry.jarvis.ui.screens.home.HomeScreen
import dev.krinry.jarvis.ui.screens.profile.ProfileScreen
import dev.krinry.jarvis.ui.screens.setup.SetupScreen
import dev.krinry.jarvis.ui.screens.voice.VoiceScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Setup : Screen("setup")
    object Chat : Screen("chat")
    object Voice : Screen("voice")
    object Profile : Screen("profile")
}

@Composable
fun HMxNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = Screen.Home.route, modifier = modifier) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Setup.route) {
            SetupScreen()
        }
        composable(Screen.Chat.route) {
            ChatScreen()
        }
        composable(Screen.Voice.route) {
            VoiceScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
    }
}