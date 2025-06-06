package com.example.jvent.screen

import com.example.jvent.viewmodel.EventViewModel
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.jvent.R
import com.example.jvent.components.DefaultTopBar

@Composable
fun MakeEvent(
    viewModel: EventViewModel = viewModel(),
    navigateToDashboard: () -> Unit,
) {
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
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                        Text(stringResource(id = R.string.upload_image), color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }

            item {
                EventTextField(
                    stringResource(id = R.string.event_name),
                    viewModel.eventName
                ) { viewModel.eventName = it }
            }
            item {
                EventTextField(
                    stringResource(id = R.string.ticket_category),
                    viewModel.ticketCategory
                ) { viewModel.ticketCategory = it }
            }
            item {
                EventTextField(
                    stringResource(id = R.string.date_time),
                    viewModel.dateTime
                ) { viewModel.dateTime = it }
            }
            item {
                EventTextField(
                    stringResource(id = R.string.location),
                    viewModel.location
                ) { viewModel.location = it }
            }
            item {
                EventTextField(
                    stringResource(id = R.string.contact_person),
                    viewModel.organizer
                ) { viewModel.organizer = it }
            }
            item {
                EventTextField(
                    stringResource(id = R.string.platform_link),
                    viewModel.platformLink
                ) { viewModel.platformLink = it }
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
                        if (viewModel.validateForm()) {
                            // Here you would typically save the event to a repository
                            navigateToDashboard()
                            viewModel.resetForm()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(stringResource(id = R.string.create_event_now))
                }
            }
        }
    }
}

@Composable
fun EventTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
    )
}