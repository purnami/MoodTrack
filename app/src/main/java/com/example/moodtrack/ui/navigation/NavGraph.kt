package com.example.moodtrack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.moodtrack.ui.screen.auth.LoginScreen
import com.example.moodtrack.ui.screen.SplashScreen
import com.example.moodtrack.ui.screen.auth.RegisScreen
import com.example.moodtrack.ui.screen.home.HomeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Register.route) {
            RegisScreen(
                onRegisSuccess = { navController.navigate(Screen.Home.route) },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.Home.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Home.route) { HomeScreen(navController) }
    }
}
