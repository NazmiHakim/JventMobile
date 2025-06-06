package com.example.jvent.viewmodel// com.example.jvent.viewmodel.SettingsViewModel.kt
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jvent.utils.setLocale
import kotlinx.coroutines.launch

class SettingsViewModel(private val context: Context) : ViewModel() {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var selectedLanguage by mutableStateOf(prefs.getString("language", "id") ?: "id")
        private set

    var isDarkMode by mutableStateOf(prefs.getBoolean("dark_mode", false))
        private set

    var isLanguageDropdownExpanded by mutableStateOf(false)
        private set

    fun toggleLanguageDropdown() {
        isLanguageDropdownExpanded = !isLanguageDropdownExpanded
    }

    fun updateLanguage(language: String) {
        selectedLanguage = language
        viewModelScope.launch {
            saveLanguagePref(language)
            setLocale(context, language)
            restartApp(context)
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        isDarkMode = enabled
        viewModelScope.launch {
            saveDarkModePref(enabled)
            restartApp(context)
        }
    }

    private fun saveLanguagePref(language: String) {
        prefs.edit { putString("language", language) }
    }

    private fun saveDarkModePref(isDarkMode: Boolean) {
        prefs.edit { putBoolean("dark_mode", isDarkMode) }
    }

    private fun restartApp(context: Context) {
        (context as? android.app.Activity)?.let { activity ->
            activity.finish()
            activity.startActivity(activity.intent)
        }
    }
}