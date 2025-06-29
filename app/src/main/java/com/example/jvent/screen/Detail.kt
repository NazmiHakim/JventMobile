package com.example.jvent.screen

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.example.jvent.JventApplication
import com.example.jvent.R
import com.example.jvent.components.DefaultTopBar
import com.example.jvent.model.Event
import com.example.jvent.viewmodel.EventViewModel
import com.example.jvent.viewmodel.EventViewModelFactory
import com.example.jvent.workers.NotificationWorker
import com.google.firebase.auth.ktx.auth
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
    val auth = Firebase.auth
    val context = LocalContext.current
    val viewModel: EventViewModel = viewModel(
        factory = EventViewModelFactory((context.applicationContext as JventApplication).repository)
    )

    val event by viewModel.getEventById(eventId).collectAsState(initial = null)

    var showDeleteDialog by remember { mutableStateOf(false) }

    val sharedPrefs = context.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)

    var isReminded by remember(eventId) {
        mutableStateOf(sharedPrefs.getBoolean(eventId, false))
    }

    val workManager = WorkManager.getInstance(context)

    fun setReminder(event: Event) {
        val eventDateStr = event.dateTime
        val eventName = event.title
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
                        .addTag(eventId)
                        .build()

                    workManager.enqueue(reminderWorkRequest)
                }
            }
        }
    }

    fun cancelReminder() {
        workManager.cancelAllWorkByTag(eventId)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(id = R.string.delete_event_dialog_title)) },
            text = { Text(stringResource(id = R.string.delete_event_dialog_text)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEvent(
                            eventId = eventId,
                            onSuccess = {
                                Toast.makeText(context, R.string.event_deleted_success, Toast.LENGTH_SHORT).show()
                                showDeleteDialog = false
                                onEventDeleted()
                            },
                            onError = { errorMsg ->
                                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                showDeleteDialog = false
                            },
                            context = context
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(id = R.string.delete_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(id = R.string.cancel_button))
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
        val currentEvent = event

        if (currentEvent == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = rememberAsyncImagePainter(currentEvent.imageUrl),
                    contentDescription = stringResource(R.string.description),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.description),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    color = MaterialTheme.colorScheme.onPrimary,
                    text = currentEvent.description
                )

                Spacer(modifier = Modifier.height(24.dp))


                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = currentEvent.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentEvent.location,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = currentEvent.dateTime,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.event_organizer),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = currentEvent.organizer,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (currentEvent.platformLink.isNotEmpty()) {
                            Button(
                                onClick = {
                                    try {
                                        val intent =
                                            Intent(Intent.ACTION_VIEW, currentEvent.platformLink.toUri())
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            R.string.link_open_error,
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
                                viewModel.updateFavoriteStatus(currentEvent, !currentEvent.isFavorite, context)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(text = if (currentEvent.isFavorite) stringResource(id = R.string.cancel_favorite) else stringResource(id = R.string.add_to_favorites))
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = {
                                val newState = !isReminded
                                isReminded = newState

                                with(sharedPrefs.edit()) {
                                    putBoolean(eventId, newState)
                                    apply()
                                }

                                if (newState) {
                                    setReminder(currentEvent)
                                    Toast.makeText(context, R.string.reminder_activated, Toast.LENGTH_SHORT).show()
                                } else {
                                    cancelReminder()
                                    Toast.makeText(context, R.string.reminder_cancelled, Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isReminded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(text = if (isReminded) stringResource(id = R.string.cancel_reminder) else stringResource(id = R.string.remind_me))
                        }
                    }
                }

                if (auth.currentUser != null && auth.currentUser?.uid == currentEvent.userId) {
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
                            Text(stringResource(id = R.string.edit_event))
                        }
                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text(stringResource(id = R.string.delete_button))
                        }
                    }
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