// SettingsScreen.kt
package com.example.jvent.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jvent.R
import com.example.jvent.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings() {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(context))

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
            LanguageSetting(viewModel)
            Spacer(modifier = Modifier.height(16.dp))
            DarkModeSetting(viewModel)
        }
    }
}

@Composable
private fun LanguageSetting(viewModel: SettingsViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.language))

        Box {
            TextButton(onClick = { viewModel.toggleLanguageDropdown() }) {
                Text(
                    text = when (viewModel.selectedLanguage) {
                        "id" -> stringResource(R.string.indonesia)
                        "en" -> stringResource(R.string.english)
                        else -> stringResource(R.string.indonesia)
                    }
                )
            }

            DropdownMenu(
                expanded = viewModel.isLanguageDropdownExpanded,
                onDismissRequest = { viewModel.toggleLanguageDropdown() }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.indonesia)) },
                    onClick = { viewModel.updateLanguage("id") }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.english)) },
                    onClick = { viewModel.updateLanguage("en") }
                )
            }
        }
    }
}

@Composable
private fun DarkModeSetting(viewModel: SettingsViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.dark_mode))

        Switch(
            checked = viewModel.isDarkMode,
            onCheckedChange = { viewModel.toggleDarkMode(it) }
        )
    }
}

class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}