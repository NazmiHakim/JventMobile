package com.example.jvent.screen

import android.content.Intent
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
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import coil.compose.rememberAsyncImagePainter
import com.example.jvent.R
import com.example.jvent.components.DefaultTopBar
import com.example.jvent.model.Event
import com.example.jvent.viewmodel.EventViewModel
import com.example.jvent.workers.NotificationWorker
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun Detail(
    eventId: String,
    navigateToEdit: () -> Unit,
    onEventDeleted: () -> Unit
) {
    val db = Firebase.firestore
    val auth = Firebase.auth
    val viewModel: EventViewModel = viewModel()
    var event by remember { mutableStateOf<Event?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var isReminded by remember { mutableStateOf(false) }

    DisposableEffect(eventId) {
        isLoading = true
        val listener = db.collection("events").document(eventId)
            .addSnapshotListener { snapshot, e ->
                isLoading = false
                if (e != null) {
                    error = "Gagal memuat data: ${e.message}"
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    event = snapshot.toObject(Event::class.java)?.copy(id = snapshot.id)
                } else {
                    error = "Event tidak ditemukan."
                }
            }
        onDispose {
            listener.remove()
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Event") },
            text = { Text("Apakah Anda yakin ingin menghapus event ini secara permanen?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEvent(
                            eventId = eventId,
                            onSuccess = {
                                Toast.makeText(context, "Event berhasil dihapus", Toast.LENGTH_SHORT).show()
                                showDeleteDialog = false
                                onEventDeleted()
                            },
                            onError = { errorMsg ->
                                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                showDeleteDialog = false
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
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
                        if (evt.platformLink.isNotEmpty()) {
                            Button(
                                onClick = {
                                    try {
                                        val intent =
                                            Intent(Intent.ACTION_VIEW, evt.platformLink.toUri())
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Tidak dapat membuka link.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = stringResource(id = R.string.buy_ticket))
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = {
                                isReminded = true
                                Toast.makeText(context, "Pengingat diaktifkan!", Toast.LENGTH_SHORT).show()

                                val eventDateStr = evt.dateTime
                                val eventName = evt.title

                                val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                val eventDate = try {
                                    format.parse(eventDateStr)
                                } catch (e: Exception) {
                                    null
                                }
                                val currentTime = System.currentTimeMillis()

                                if (eventDate != null) {
                                    val timeDiff = eventDate.time - currentTime

                                    val countdowns = listOf(
                                        Triple(3, TimeUnit.DAYS, "3 hari"),
                                        Triple(1, TimeUnit.DAYS, "1 hari"),
                                        Triple(12, TimeUnit.HOURS, "12 jam"),
                                        Triple(1, TimeUnit.HOURS, "1 jam"),
                                        Triple(5, TimeUnit.MINUTES, "5 menit"),
                                        Triple(1, TimeUnit.MINUTES, "1 menit")
                                    )

                                    for ((value, unit, countdownText) in countdowns) {
                                        val delay = timeDiff - unit.toMillis(value.toLong())
                                        if (delay > 0) {
                                            val data = Data.Builder()
                                                .putString("EVENT_NAME", eventName)
                                                .putString("COUNTDOWN", countdownText)
                                                .build()

                                            val reminderWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                                                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                                                .setInputData(data)
                                                .build()

                                            WorkManager.getInstance(context).enqueue(reminderWorkRequest)
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isReminded,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isReminded) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(text = if (isReminded) "Diingatkan" else "Ingatkan Saya")
                        }
                    }
                }

                if (auth.currentUser != null && auth.currentUser?.uid == evt.userId) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = navigateToEdit,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Edit Event")
                        }
                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Delete Event")
                        }
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