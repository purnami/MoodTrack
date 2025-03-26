package com.example.moodtrack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.moodtrack.ui.screen.auth.LoginScreen
import com.example.moodtrack.ui.screen.MainScreen
import com.example.moodtrack.ui.screen.auth.RegisScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Register : Screen("register")
    object Login : Screen("login")
    object Home : Screen("home")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Register.route) {
            RegisScreen(onRegisSuccess = { navController.navigate(Screen.Home.route) })
        }
        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = { navController.navigate(Screen.Home.route) })
        }
        composable(Screen.Home.route) { HomeScreen(navController) }
    }
}
