package com.example.moodtrack.ui.screen.home

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moodtrack.core.utils.toMoodLabel
import com.example.moodtrack.ui.navigation.BottomNavBar
import com.example.moodtrack.ui.navigation.Screen
import com.example.moodtrack.ui.screen.mood.MoodInputScreen
import com.example.moodtrack.ui.screen.profile.ProfileScreen
import com.example.moodtrack.ui.screen.recomendation.RecommendationScreen
import com.example.moodtrack.ui.screen.selfassessment.SelfAssessmentScreen
import com.example.moodtrack.ui.screen.statistics.MoodStatisticsScreen
import com.example.moodtrack.ui.viewmodel.MoodViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    moodViewModel: MoodViewModel = hiltViewModel(),
    rootNavController: NavHostController
){
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val context = LocalContext.current
    var backPressedTime by remember { mutableStateOf(0L) }
    val backPressThreshold = 2000L

    LaunchedEffect(Unit) {
        moodViewModel.scheduleMoodNotifications(context)
    }

    BackHandler {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < backPressThreshold) {
            (context as? Activity)?.finish()
        } else {
            backPressedTime = currentTime
            Toast.makeText(context, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Mood.route,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(Screen.Mood.route) { MoodInputScreen(
                onNavigateToRecommendation = { mood, note ->
                    navController.navigate(Screen.Recommendations.createRoute(mood, note))
                }
            ) }
            composable(Screen.Statistics.route) { MoodStatisticsScreen() }
            composable(Screen.SelfAssessment.route) { SelfAssessmentScreen() }
            composable(Screen.Profile.route) { ProfileScreen(
                onLogoutSuccess = {
                    rootNavController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToSettings = {},
                onNavigateToRecommendation = {
                    navController.navigate(Screen.Recommendations.route)
                }

            ) }

            composable(
                route = Screen.Recommendations.route,
                arguments = listOf(
                    navArgument("mood") { type = NavType.IntType },
                    navArgument("note") { type = NavType.StringType }
                )
            ) {
                val mood = it.arguments?.getInt("mood") ?: 0
                val note = it.arguments?.getString("note") ?: ""
                RecommendationScreen(
                    navController = navController,
                    mood = mood,
                    note = note
                )
            }
        }
    }
}