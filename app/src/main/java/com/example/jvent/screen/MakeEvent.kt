package com.example.jvent.screen

import com.example.jvent.viewmodel.EventViewModel
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.jvent.R
import com.example.jvent.components.DefaultTopBar
import com.example.jvent.components.EventTextField

@Composable
fun MakeEvent(
    navigateToDashboard: () -> Unit,
) {
    val viewModel: EventViewModel = viewModel()
    val context = LocalContext.current

    // Launcher untuk memilih gambar dari gallery
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                viewModel.imageUri = it
            }
        }
    )

    // Tampilkan error jika ada
    LaunchedEffect(viewModel.error) {
        viewModel.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            DefaultTopBar(title = stringResource(id = R.string.app_name))
        }
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(2.dp, color = MaterialTheme.colorScheme.onSecondary, RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .clickable {
                            imagePicker.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (viewModel.imageUri != null) {
                        AsyncImage(
                            model = viewModel.imageUri,
                            contentDescription = "Selected image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                            Text(stringResource(id = R.string.upload_image), color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }

            item {
                EventTextField(
                    label = stringResource(id = R.string.event_name),
                    value = viewModel.eventName,
                    onValueChange = { viewModel.eventName = it }
                )
            }
            item {
                EventTextField(
                    label = stringResource(id = R.string.ticket_category),
                    value = viewModel.ticketCategory,
                    onValueChange = { viewModel.ticketCategory = it }
                )
            }
            item {
                EventTextField(
                    label = stringResource(id = R.string.date_time),
                    value = viewModel.dateTime,
                    onValueChange = { viewModel.dateTime = it }
                )
            }
            item {
                EventTextField(
                    label = stringResource(id = R.string.location),
                    value = viewModel.location,
                    onValueChange = { viewModel.location = it }
                )
            }
            item {
                EventTextField(
                    label = stringResource(id = R.string.contact_person),
                    value = viewModel.organizer,
                    onValueChange = { viewModel.organizer = it }
                )
            }
            item {
                EventTextField(
                    label = stringResource(id = R.string.platform_link),
                    value = viewModel.platformLink,
                    onValueChange = { viewModel.platformLink = it }
                )
            }

            item {
                OutlinedTextField(
                    value = viewModel.description,
                    onValueChange = { viewModel.description = it },
                    label = { Text(stringResource(id = R.string.description)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                )
            }

            item {
                Button(
                    onClick = {
                        viewModel.createEvent(
                            context = context,
                            onSuccess = {
                                navigateToDashboard()
                                viewModel.resetForm()
                            },
                            onError = {
                                // Error handling already done via LaunchedEffect
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(stringResource(id = R.string.create_event_now))
                    }
                }
            }
        }
    }
}