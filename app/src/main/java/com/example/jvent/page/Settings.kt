package com.example.jvent.page

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.jvent.R
import com.example.jvent.setLocale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings() {
    val context = LocalContext.current

    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val savedLang = prefs.getString("language", "id") ?: "id"
    val savedDarkMode = prefs.getBoolean("dark_mode", false)

    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf(savedLang) }
    var isDarkMode by remember { mutableStateOf(savedDarkMode) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.settings)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.language))

                Box {
                    TextButton(onClick = { expanded = true }) {
                        Text(
                            text = when (selectedLanguage) {
                                "id" -> stringResource(R.string.indonesia)
                                "en" -> stringResource(R.string.english)
                                else -> stringResource(R.string.indonesia)
                            }
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.indonesia)) },
                            onClick = {
                                selectedLanguage = "id"
                                updateLanguage(context, "id")
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.english)) },
                            onClick = {
                                selectedLanguage = "en"
                                updateLanguage(context, "en")
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.dark_mode))

                Switch(
                    checked = isDarkMode,
                    onCheckedChange = {
                        isDarkMode = it
                        saveDarkModePref(context, it)
                        restartApp(context)
                    }
                )
            }
        }
    }
}

fun updateLanguage(context: Context, language: String) {
    saveLanguagePref(context, language)
    setLocale(context, language)
    restartApp(context)
}

fun saveLanguagePref(context: Context, language: String) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("language", language).apply()
}

fun saveDarkModePref(context: Context, isDarkMode: Boolean) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    prefs.edit().putBoolean("dark_mode", isDarkMode).apply()
}

fun restartApp(context: Context) {
    val activity = context as? Activity ?: return
    val intent = activity.intent
    activity.finish()
    activity.startActivity(intent)
}