package com.example.jvent

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.jvent.ui.theme.Purple40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Detail() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Event") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Gambar Header dari URL
            Image(
                painter = rememberAsyncImagePainter("https://cdn.trii.global/Banner/NewsArticle/mobile/-1248124158.jpg"),
                contentDescription = "Foto Event",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Kartu Informasi Event
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Haru No Sakuragi", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("06 April 2025")
                    Text("Kota Cinema Mall, Banjarmasin")
                    Text("13:00 - 21:00 WITA")
                    Text("Gratis")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Diselenggarakan oleh")
                    Text("SHIKIFEST", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Beli Tiket
            Button(
                onClick = { /* TODO: Arahkan ke link eksternal */ },
                colors = ButtonDefaults.buttonColors(containerColor = Purple40),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("BELI TIKET")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Deskripsi Event
            Text("Deskripsi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Haru no Sakuragi adalah sebuah acara Kebudayaan Jepang yang akan digelar di Banjarmasin, Kalimantan Selatan pada 6 April 2025. Acara ini dirancang untuk menyambut musim semi dengan berbagai kegiatan yang mengangkat keindahan dan tradisi Budaya Jepang, termasuk mekarnya bunga sakura sebagai simbol harapan dan awal yang baru.\n\n" +
                        "Acara ini juga akan dimeriahkan dengan pertunjukan seni tradisional, seperti tarian khas Jepang, Performance dari berbagai Bintang Tamu, Kompetisi, hingga Cosplay yang akan menghadirkan karakter anime/manga/game/film favoritmu.\n\n" +
                        "Haru no Sakuragi menjadi kesempatan bagi kamu untuk menikmati suasana musim semi ala Jepang tanpa harus pergi jauh. Jangan lewatkan pengalaman penuh warna ini, yang akan membawa kehangatan dan keceriaan musim semi!"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Poster Event dari URL
            Image(
                painter = rememberAsyncImagePainter("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSIdbHugvMf7ner8p3PmeUEV9zKAb-BeU2CQg&s"),
                contentDescription = "Poster Event",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}