package com.example.jvent

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jvent.ui.theme.JventTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySavedLocale()
        setContent {
            JventTheme {
                JventApp()
            }
        }
    }

    private fun applySavedLocale() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val lang = prefs.getString("language", "id") ?: "id"
        setLocale(this, lang)
    }
}

@Composable
fun JventApp() {
    val navController = rememberNavController()
    val isLoading = remember { mutableStateOf(false) }

    Box {
        NavHost(navController, startDestination = "splash") {
            composable("splash") {
                AppSplashScreen {
                    navController.navigate("landing") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }
            composable("landing") {
                LandingPage(
                    navigateToRegistration = {
                        navigateWithLoading(isLoading, navController, "registration")
                    },
                    navigateToExploreEvent = {
                        navigateWithLoading(isLoading, navController, "explore")
                    },
                    navigateToSettings = {
                        navigateWithLoading(isLoading, navController, "settings")
                    },
                    navigateToDetail = {
                        navigateWithLoading(isLoading, navController, "detail")
                    }
                )
            }
            composable("registration") {
                Registration(navigateToLogin = {
                    navigateWithLoading(isLoading, navController,"login") })
            }
            composable("explore") {
                ExploreEvent(navigateToDetail = {
                    navigateWithLoading(isLoading, navController,"detail") })
            }
            composable("settings") {
                Settings()
            }
            composable("login") {
                Login(
                    navigateToMakeEvent = {
                        navigateWithLoading(isLoading, navController,"make_event") },
                    navigateToRegistration = {
                        navigateWithLoading(isLoading, navController,"registration") }
                )
            }
            composable("make_event") {
                MakeEvent(navigateToDashboard = {
                    navigateWithLoading(isLoading, navController,"dashboard") })
            }
            composable("dashboard") {
                Dashboard()
            }
            composable("detail") {
                Detail()
            }
        }

        // Hanya panggil NavigateWithLoading dalam composable context
        NavigateWithLoading(isLoading.value)
    }
}

