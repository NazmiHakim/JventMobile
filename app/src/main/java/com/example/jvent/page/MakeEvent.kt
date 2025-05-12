package com.example.jvent.page

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.jvent.DefaultTopBar
import com.example.jvent.R

@Composable
fun MakeEvent(
    navigateToDashboard: () -> Unit,
) {
    var eventName by rememberSaveable { mutableStateOf("") }
    var ticketCategory by rememberSaveable { mutableStateOf("") }
    var dateTime by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var organizer by rememberSaveable { mutableStateOf("") }
    var platformLink by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

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

            item { EventTextField(stringResource(id = R.string.event_name), eventName) { eventName = it } }
            item { EventTextField(stringResource(id = R.string.ticket_category), ticketCategory) { ticketCategory = it } }
            item { EventTextField(stringResource(id = R.string.date_time), dateTime) { dateTime = it } }
            item { EventTextField(stringResource(id = R.string.location), location) { location = it } }
            item { EventTextField(stringResource(id = R.string.contact_person), organizer) { organizer = it } }
            item { EventTextField(stringResource(id = R.string.platform_link), platformLink) { platformLink = it } }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(id = R.string.description)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    )

            }

            item {
                Button(
                    onClick = { navigateToDashboard() },
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