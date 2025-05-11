package com.example.jvent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeEvent(
    navigateToDashboard: () -> Unit,
) {
    // rememberSaveable untuk menyimpan input saat rotasi/recomposition
    var namaEvent by rememberSaveable { mutableStateOf("") }
    var kategoriTiket by rememberSaveable { mutableStateOf("") }
    var tanggalWaktu by rememberSaveable { mutableStateOf("") }
    var lokasi by rememberSaveable { mutableStateOf("") }
    var narahubung by rememberSaveable { mutableStateOf("") }
    var linkPlatform by rememberSaveable { mutableStateOf("") }
    var deskripsi by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Jvent", color = Color.Cyan, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0E0E1C)
                )
            )
        },
        containerColor = Color(0xFF0E0E1C)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                // Upload gambar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(2.dp, Color.Gray, RoundedCornerShape(12.dp))
                        .background(Color(0xFF1C1C2E)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                        Text("Unggah gambar/poster/banner", color = Color.White)
                    }
                }
            }

            item { EventTextField("Nama Event", namaEvent) { namaEvent = it } }
            item { EventTextField("Kategori Tiket", kategoriTiket) { kategoriTiket = it } }
            item { EventTextField("Tanggal dan Waktu", tanggalWaktu) { tanggalWaktu = it } }
            item { EventTextField("Lokasi", lokasi) { lokasi = it } }
            item { EventTextField("Narahubung", narahubung) { narahubung = it } }
            item { EventTextField("Lokasi Platform Event", linkPlatform) { linkPlatform = it } }

            // Deskripsi (multiline)
            item {
                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi Event") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color(0xFF1C1C2E),
                        focusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray
                    )
                )
            }

            // Tombol Buat Event
            item {
                Button(
                    onClick = { navigateToDashboard() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                ) {
                    Text("Buat Event Sekarang")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = Color(0xFF1C1C2E),
            focusedTextColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.Gray
        )
    )
}