package com.example.jvent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard() {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Event Aktif", "Event Lalu")

    val dummyEvents = listOf(
        "World Music Festival",
        "World Music Festival",
        "World Music Festival"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Saya", fontSize = 20.sp) },
                actions = {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A49FF))
                    ) {
                        Text("Buat Event")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                // Search bar
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Cari event disini") },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Tab Event Aktif / Lalu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tabs.forEachIndexed { index, title ->
                        TextButton(onClick = { selectedTab = index }) {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) Color.White else Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                // Event Horizontal List
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(dummyEvents) { event ->
                        EventCard(title = event)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { /* Buat Event */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A49FF))
                ) {
                    Text("Buat Event")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Hai, terima kasih telah menggunakan Jvent",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    fontSize = 16.sp
                )

                Text(
                    "Silahkan Buat Event Mu dengan klik Button Diatas!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    fontSize = 14.sp,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun EventCard(title: String) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(260.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D3A))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Gambar (placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color.DarkGray)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text("25-27 July, 2025 · Valencia, ES", color = Color.LightGray, fontSize = 12.sp)
                Text("Rp. 100.000", color = Color.White, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Imagi", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}