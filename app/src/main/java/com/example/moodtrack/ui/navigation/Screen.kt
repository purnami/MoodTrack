package com.example.moodtrack.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Register : Screen("register")
    data object Login : Screen("login")
    data object Home : Screen("home")
}