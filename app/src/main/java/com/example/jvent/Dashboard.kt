// Registraition.kt
package com.example.jvent

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("test") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Pengaturan pengguna akan ditampilkan di sini", style = MaterialTheme.typography.titleMedium)
            Button(onClick = {}) {

            }
        }
    }
}