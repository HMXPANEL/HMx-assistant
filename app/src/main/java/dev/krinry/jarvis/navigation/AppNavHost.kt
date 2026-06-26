package dev.krinry.jarvis.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.krinry.jarvis.chat.ChatScreen
import dev.krinry.jarvis.settings.SettingsScreen

@Composable
fun AppNavHost(navController: NavHostController, startDestination: String = NavigationGraph.Chat.route) {
    NavHost(navController, startDestination) {
        composable(NavigationGraph.Chat.route) {
            ChatScreen(onNavigateToSettings = { navController.navigate(NavigationGraph.Settings.route) })
        }
        composable(NavigationGraph.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}

@Composable
fun RememberAppNavHost(startDestination: String = NavigationGraph.Chat.route): NavHostController {
    val navController = rememberNavController()
    AppNavHost(navController, startDestination)
    return navController
}

object NavigationGraph {
    object Chat {
        const val route = "chat"
    }
    object Settings {
        const val route = "settings"
    }
}