package com.hmx.aios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hmx.aios.ui.navigation.HMxNavHost
import com.hmx.aios.ui.navigation.Screen
import com.hmx.aios.ui.components.BottomNavBar
import com.hmx.aios.ui.components.AnimatedCyberBackground
import com.hmx.aios.ui.theme.DarkBackground

@Composable
fun HMxApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        AnimatedCyberBackground()
        
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        HMxNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
    }
    }
}
