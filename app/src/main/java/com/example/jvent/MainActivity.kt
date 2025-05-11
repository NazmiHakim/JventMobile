package com.example.jvent

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jvent.ui.theme.JventTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Terapkan locale yang disimpan sebelum UI dibuat
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

    NavHost(navController, startDestination = "landing") {
        composable("landing") {
            LandingPage(
                navigateToRegistration = { navController.navigate("registration") },
                navigateToExploreEvent = { navController.navigate("explore") },
                navigateToSettings = { navController.navigate("settings") },
                navigateToDetail = {navController.navigate("detail")}

            )
        }
        composable("registration") {
            Registration(navigateToLogin = {navController.navigate("login")})
        }
        composable("explore") {
            ExploreEvent(navigateToDetail = {navController.navigate("detail")})
        }
        composable("settings") {
            Settings() // Halaman Settings untuk mengubah bahasa
        }
        composable("login") {
            Login(navigateToMakeEvent = {navController.navigate("make_event")},
                navigateToRegistration = {navController.navigate("registration")})
        }
        composable("make_event"){
            MakeEvent(navigateToDashboard = {navController.navigate("dashboard")})
        }
        composable("dashboard") {
            Dashboard()
        }
        composable("detail") {
            Detail()
        }
        composable("filter") {
            Filter()
        }
    }
}