package com.example.jvent.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.jvent.R
import com.example.jvent.components.DefaultTopBar
import com.example.jvent.model.Event
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

@Composable
fun Detail(eventId: String) {
    val db = Firebase.firestore
    var event by remember { mutableStateOf<Event?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(eventId) {
        isLoading = true
        try {
            val snapshot = db.collection("events")
                .document(eventId)
                .get()
                .await()

            event = Event(
                id = snapshot.id,
                title = snapshot.getString("title") ?: "",
                description = snapshot.getString("description") ?: "",
                dateTime = snapshot.getString("dateTime") ?: "",
                location = snapshot.getString("location") ?: "",
                organizer = snapshot.getString("organizer") ?: "",
                platformLink = snapshot.getString("platformLink") ?: "",
                ticketCategory = snapshot.getString("ticketCategory") ?: "",
                imageUrl = snapshot.getString("imageUrl") ?: "",
                userId = snapshot.getString("userId") ?: ""
            )
        } catch (e: Exception) {
            error = e.message ?: "Gagal memuat data"
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            DefaultTopBar(title = stringResource(id = R.string.app_name))
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        event?.let { evt ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = rememberAsyncImagePainter(evt.imageUrl),
                    contentDescription = stringResource(R.string.description),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = evt.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = evt.location,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = evt.dateTime,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = evt.ticketCategory,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.event_organizer),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = evt.organizer,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = { /* Tambahkan aksi beli tiket jika diperlukan */ },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = stringResource(id = R.string.buy_ticket))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.description),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    color = MaterialTheme.colorScheme.onPrimary,
                    text = evt.description
                )

                if (evt.platformLink.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Platform Link: ${evt.platformLink}",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Image(
                    painter = rememberAsyncImagePainter("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSIdbHugvMf7ner8p3PmeUEV9zKAb-BeU2CQg&s"),
                    contentDescription = stringResource(R.string.poster),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}