package com.example.moodtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.moodtrack.ui.navigation.NavGraph
import com.example.moodtrack.ui.theme.MoodTrackTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoodTrackTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
