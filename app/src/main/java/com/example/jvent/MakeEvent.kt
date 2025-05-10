// MakeEvent.kt
package com.example.jvent

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeEvent() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Event") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Form Buat Event akan di sini", style = MaterialTheme.typography.titleMedium)
            // Tambahkan form event di sini
        }
    }
}