package com.example.moodtrack.ui.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Register : Screen("register")
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object Mood : Screen("mood")
    data object Statistics : Screen("statistics")
    data object Profile : Screen("profile")

    data object Recommendations : Screen("recommendations/{mood}/{note}") {
        fun createRoute(mood: Int, note: String) = "recommendations/$mood/${Uri.encode(note)}"
    }
    data object SelfAssessment : Screen("self_assessment")

    object AuthRoot : Screen("auth_root")
    object MainRoot : Screen("main_root")
}
