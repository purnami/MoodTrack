package com.example.moodtrack.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.moodtrack.ui.screen.SplashScreen
import com.example.moodtrack.ui.screen.auth.LoginScreen
import com.example.moodtrack.ui.screen.auth.RegisScreen

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        route = Screen.AuthRoot.route,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.MainRoot.route) {
                        popUpTo(Screen.AuthRoot.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegisScreen(
                onRegisSuccess = {
                    navController.navigate(Screen.MainRoot.route) {
                        popUpTo(Screen.AuthRoot.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }
    }
}
