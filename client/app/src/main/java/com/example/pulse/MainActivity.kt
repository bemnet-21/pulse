package com.example.pulse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pulse.ui.screens.DetailScreen
import com.example.pulse.ui.screens.FeedScreen
import com.example.pulse.ui.screens.NavRoute
import com.example.pulse.ui.screens.WelcomeScreen
import com.example.pulse.ui.theme.PulseTheme
import com.yourname.devpulse.ui.screens.BookmarksScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PulseTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "welcome") {
                    composable("welcome") {
                        WelcomeScreen(
                            onNavigateToFeed = {
                                navController.navigate("feed") {
                                    popUpTo("welcome") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("feed") {
                        FeedScreen(
                            onArticleClick = { articleId ->
                                navController.navigate("article/$articleId")
                            },
                            onNavigateBottomBar = { route ->
                                if(route == NavRoute.Bookmarks) navController.navigate("bookmarks")
                                if(route == NavRoute.Profile) navController.navigate("profile")
                            }
                        )
                    }

                    composable(
                        route = "article/{articleId}",
                        arguments = listOf(navArgument("articleId") { type = NavType.IntType })
                    ) { backStackEntry ->

                        val articleId = backStackEntry.arguments?.getInt("articleId") ?: return@composable

                        DetailScreen(
                            articleId = articleId,
                            onBackClick = { navController.popBackStack() }
                        )

                    }

                    composable("bookmarks") {
                        BookmarksScreen(
                            onArticleClick = { articleId ->
                                navController.navigate("article/$articleId")
                            },
                            onNavigateBottomBar = { route ->
                                if(route == NavRoute.Home) navController.navigate("feed")
                                if(route == NavRoute.Profile) navController.navigate("profile")
                            }
                        )
                    }
                }
            }
        }
    }
}

