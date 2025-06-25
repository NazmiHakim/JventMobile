package com.example.jvent.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.jvent.R
import com.example.jvent.components.DefaultTopBar
import com.example.jvent.components.EventTextField
import com.example.jvent.viewmodel.EventViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class) // Diperlukan untuk ExposedDropdownMenuBox
@Composable
fun MakeEvent(
    navigateToDashboard: () -> Unit,
) {
    val viewModel: EventViewModel = viewModel()
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                viewModel.imageUri = it
            }
        }
    )

    // Tampilkan error menggunakan Toast, karena validasi sekarang ada di ViewModel
    LaunchedEffect(viewModel.error) {
        viewModel.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            viewModel.dateTime += " ${String.format("%02d:%02d", hourOfDay, minute)}"
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            viewModel.dateTime = "$dayOfMonth/${month + 1}/$year"
            timePickerDialog.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )


    Scaffold(
        topBar = {
            DefaultTopBar(title = stringResource(id = R.string.make_event))
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
                    label = "Penyelenggara Event", // Langsung ganti atau gunakan string resource baru
                    value = viewModel.organizer,
                    onValueChange = { viewModel.organizer = it }
                )
            }
            item {
                OutlinedTextField(
                    value = viewModel.dateTime,
                    onValueChange = { viewModel.dateTime = it },
                    label = { Text(stringResource(id = R.string.date_time)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    enabled = false, // Agar keyboard tidak muncul
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            item {
                EventTextField(
                    label = stringResource(id = R.string.location),
                    value = viewModel.location,
                    onValueChange = { viewModel.location = it }
                )
            }

            // + Tambahkan Dropdown untuk Tipe Event
            item {
                val eventTypes = listOf("Gratis", "Berbayar")
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = viewModel.eventType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipe Event") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
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

            // + Tambahkan Input Harga jika "Berbayar"
            if (viewModel.eventType == "Berbayar") {
                item {
                    EventTextField(
                        label = "Harga Event (Contoh: Rp 25.000)",
                        value = viewModel.price,
                        onValueChange = { viewModel.price = it },
                    )
                }
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
                                Toast.makeText(context, "Event berhasil dibuat!", Toast.LENGTH_SHORT).show()
                                navigateToDashboard()
                                viewModel.resetForm()
                            },
                            onError = {
                                // Toast sudah ditangani oleh LaunchedEffect
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