// app/src/main/java/com/example/jvent/screen/EditEvent.kt
package com.example.jvent.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.jvent.R
import com.example.jvent.components.DefaultTopBar
import com.example.jvent.components.EventTextField
import com.example.jvent.viewmodel.EditEventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEvent(
    eventId: String,
    navigateToDashboard: () -> Unit,
    viewModel: EditEventViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    LaunchedEffect(viewModel.error) {
        viewModel.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            DefaultTopBar(title = "Edit Event")
        }
    ) { innerPadding ->
        if (viewModel.isLoading && viewModel.eventName.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
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
                                // Image picker logic can be added here
                                Toast.makeText(context, "Image editing is not implemented.", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (viewModel.imageUrl != null) {
                            AsyncImage(
                                model = viewModel.imageUrl,
                                contentDescription = "Current event image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                                Text("Select Image", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }

                item { EventTextField(label = stringResource(R.string.event_name), value = viewModel.eventName, onValueChange = { viewModel.eventName = it }) }
                item { EventTextField(label = "Penyelenggara Event", value = viewModel.organizer, onValueChange = { viewModel.organizer = it }) }
                item { EventTextField(label = stringResource(R.string.date_time), value = viewModel.dateTime, onValueChange = { viewModel.dateTime = it }) }
                item { EventTextField(label = stringResource(R.string.location), value = viewModel.location, onValueChange = { viewModel.location = it }) }

                // Event Type Dropdown
                item {
                    val eventTypes = listOf("Gratis", "Berbayar")
                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                        OutlinedTextField(
                            value = viewModel.eventType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipe Event") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            eventTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        viewModel.eventType = type
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (viewModel.eventType == "Berbayar") {
                    item { EventTextField(label = "Harga Event", value = viewModel.price, onValueChange = { viewModel.price = it }) }
                }

                item { EventTextField(label = stringResource(R.string.platform_link), value = viewModel.platformLink, onValueChange = { viewModel.platformLink = it }) }
                item { EventTextField(label = stringResource(R.string.ticket_category), value = viewModel.ticketCategory, onValueChange = { viewModel.ticketCategory = it }) }
                item {
                    OutlinedTextField(
                        value = viewModel.description,
                        onValueChange = { viewModel.description = it },
                        label = { Text(stringResource(R.string.description)) },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                    )
                }

                item {
                    Button(
                        onClick = {
                            viewModel.updateEvent(
                                context = context,
                                onSuccess = {
                                    Toast.makeText(context, "Event updated successfully!", Toast.LENGTH_SHORT).show()
                                    navigateToDashboard()
                                },
                                onError = { errorMsg ->
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        enabled = !viewModel.isLoading
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                        } else {
                            Text("Save Changes")
                        }
                    }
                }
            }
        }
    }
}