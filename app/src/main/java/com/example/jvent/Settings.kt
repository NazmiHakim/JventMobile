package com.example.jvent

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings() {
    val context = LocalContext.current

    // Mengambil bahasa yang disimpan, jika tidak ada set default 'id'
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val savedLang = prefs.getString("language", "id") ?: "id"

    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf(savedLang) }

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
                                "id" -> "Indonesia"
                                "en" -> "English"
                                else -> "Indonesia"
                            }
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        // Item untuk memilih Bahasa Indonesia
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.indonesia)) },
                            onClick = {
                                selectedLanguage = "id"
                                updateLanguage(context, "id") // Langsung update dan restart
                                expanded = false
                            }
                        )
                        // Item untuk memilih Bahasa Inggris
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.english)) },
                            onClick = {
                                selectedLanguage = "en"
                                updateLanguage(context, "en") // Langsung update dan restart
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

fun updateLanguage(context: Context, language: String) {
    // Simpan bahasa yang dipilih
    saveLanguagePref(context, language)
    // Set locale menggunakan utility fungsi setLocale()
    setLocale(context, language)  // Gunakan fungsi ini dari LocaleUtils.kt
    // Restart aplikasi untuk menerapkan bahasa baru
    restartApp(context)
}

fun saveLanguagePref(context: Context, language: String) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("language", language).apply()
}

fun restartApp(context: Context) {
    val activity = context as? Activity ?: return
    val intent = activity.intent
    activity.finish()
    activity.startActivity(intent)
}
