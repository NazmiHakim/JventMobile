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
import com.example.jvent.page.Dashboard
import com.example.jvent.page.Detail
import com.example.jvent.page.ExploreEvent
import com.example.jvent.page.LandingPage
import com.example.jvent.page.Login
import com.example.jvent.page.MakeEvent
import com.example.jvent.page.Registration
import com.example.jvent.page.Settings
import com.example.jvent.ui.theme.JventTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isDarkMode = getDarkModePref()
        setContent {
            JventTheme(darkTheme = isDarkMode) {
                JventApp()
            }
        }
    }

    private fun getDarkModePref(): Boolean {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("dark_mode", false)
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
                MakeEvent(
                    navigateToDashboard = {
                        navigateWithLoading(isLoading, navController,"dashboard") }
                )
            }
            composable("dashboard") {
                Dashboard(
                    navigateToDetail = {
                        navigateWithLoading(isLoading, navController, "detail")
                    },
                    navigateToMakeEvent = {
                        navigateWithLoading(isLoading, navController, "make_event")
                    }
                )
            }
            composable("detail") {
                Detail()
            }
        }

        NavigateWithLoading(isLoading.value)
    }
}

