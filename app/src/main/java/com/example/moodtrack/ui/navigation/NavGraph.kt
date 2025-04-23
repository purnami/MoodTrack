package com.example.moodtrack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun NavGraph(
    navController: NavHostController,
    isLoggedIn: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.MainRoot.route else Screen.AuthRoot.route
    ) {
        authNavGraph(navController)
        mainNavGraph(navController)
    }
}