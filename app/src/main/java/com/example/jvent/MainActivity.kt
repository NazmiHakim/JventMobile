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
import com.example.jvent.components.NavigateWithLoading
import com.example.jvent.components.navigateWithLoading
import com.example.jvent.repository.AuthRepository
import com.example.jvent.screen.AppSplashScreen
import com.example.jvent.screen.Dashboard
import com.example.jvent.screen.Detail
import com.example.jvent.screen.EditEvent
import com.example.jvent.screen.ExploreEvent
import com.example.jvent.screen.LandingPage
import com.example.jvent.screen.LoginScreen
import com.example.jvent.screen.MakeEvent
import com.example.jvent.screen.RegistrationScreen
import com.example.jvent.screen.Settings
import com.example.jvent.ui.theme.JventTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        val isDarkMode = getDarkModePref()
        setContent {
            JventTheme(darkTheme = isDarkMode) {
                JventApp(auth)
            }
        }
    }

    private fun getDarkModePref(): Boolean {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("dark_mode", false)
    }
}

@Composable
fun JventApp(auth: FirebaseAuth) {
    val navController = rememberNavController()
    val isLoading = remember { mutableStateOf(false) }
    val isUserLoggedIn = remember { mutableStateOf(auth.currentUser != null) }

    Box {
        NavHost(navController, startDestination = "splash") {
            composable("splash") {
                AppSplashScreen {
                    val destination = "landing"
                    navController.navigate(destination) {
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
                    navigateToDetail = { eventId ->
                        navController.navigate("detail/$eventId")  // Pass eventId to route
                    },
                    navigateToDashboard = {
                        navigateWithLoading(isLoading, navController, "dashboard")
                    },
                    isLoggedIn = isUserLoggedIn.value
                )
            }
            composable("registration") {
                RegistrationScreen(navController)
            }
            composable("settings") {
                Settings(
                    navigateToLogin = {
                        navigateWithLoading(isLoading, navController, "login")
                    },
                    isLoggedIn = isUserLoggedIn.value,
                    onLogout = {
                        AuthRepository.logout()
                        isUserLoggedIn.value = false
                        navController.navigate("landing") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                    }
                )
            }

            composable("explore") {
                ExploreEvent(navigateToDetail = { eventId ->
                    navController.navigate("detail/$eventId")
                })
            }
            composable("login") {
                LoginScreen(
                    navController = navController,
                    onLoginSuccess = {
                        isUserLoggedIn.value = true
                        navController.navigate("dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    navigateToRegistration = {
                        navigateWithLoading(isLoading, navController, "registration")
                    }
                )
            }
            composable("make_event") {
                MakeEvent(
                    navigateToDashboard = {
                        navigateWithLoading(isLoading, navController, "dashboard")
                    }
                )
            }
            composable("dashboard") {
                if (isUserLoggedIn.value) {
                    Dashboard(
                        navigateToDetail = { eventId ->
                            navController.navigate("detail/$eventId")
                        },
                        navigateToMakeEvent = {
                            navigateWithLoading(isLoading, navController, "make_event")
                        },
                        onLogout = {
                            AuthRepository.logout()
                            isUserLoggedIn.value = false
                            navController.navigate("landing")
                        }
                    )
                } else {
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            }
            composable("detail/{eventId}") { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
                Detail(
                    eventId = eventId,
                    onEdit = { passedEventId ->
                        // Navigate to the new EditEvent screen
                        navController.navigate("edit_event/$passedEventId")
                    },
                    onDeleted = {
                        // After deletion, navigate back to the previous screen
                        navController.popBackStack()
                    }
                )
            }

            composable("edit_event/{eventId}") { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
                EditEvent(
                    eventId = eventId,
                    navigateToDashboard = {
                        navController.navigate("dashboard") {
                            // Correctly pop the back stack up to the dashboard
                            popUpTo("dashboard") {
                                inclusive = true
                            }
                        }
                    }
                )
                NavigateWithLoading(isLoading.value)
            }
        }
    }
}