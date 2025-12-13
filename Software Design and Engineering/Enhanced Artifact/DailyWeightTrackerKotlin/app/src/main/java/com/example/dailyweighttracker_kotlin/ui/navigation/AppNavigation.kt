package com.example.dailyweighttracker_kotlin.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dailyweighttracker_kotlin.ui.screens.history.WeightHistoryScreen
import com.example.dailyweighttracker_kotlin.ui.screens.home.HomeScreen
import com.example.dailyweighttracker_kotlin.ui.screens.login.LoginScreen
import com.example.dailyweighttracker_kotlin.ui.screens.settings.SettingsScreen

// Creates app navigation graph
@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {

    // Navigation Graph
    NavHost(navController = navController, startDestination = "login", modifier = modifier) {

        // Login Screen
        composable("login") {
            LoginScreen(onLoginSuccess = { navController.navigate("home") })
        }

        // Home Screen
        composable("home") {
            HomeScreen(
                onViewHistoryClick = { navController.navigate("history") },
                onAccountClick = { navController.navigate("settings") }
            )
        }

        // Weight History Screen
        composable("history") {
            WeightHistoryScreen()
        }

        // Settings Screen
        composable("settings") {
            SettingsScreen(onLogout = {
                navController.popBackStack("login", inclusive = true)
                navController.navigate("login")
            })
        }
    }
}
