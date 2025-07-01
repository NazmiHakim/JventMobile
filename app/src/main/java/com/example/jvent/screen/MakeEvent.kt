package com.example.jvent.screen

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeEvent(
    navigateToDashboard: () -> Unit,
) {
    val viewModel: EventViewModel = viewModel()
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    // Launcher untuk hasil dari UCrop
    val uCropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            resultUri?.let {
                viewModel.imageUri = it
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(result.data!!)
            Toast.makeText(context, "Crop error: ${cropError?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher untuk memilih gambar dari galeri
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { sourceUri ->
        sourceUri?.let {
            val destinationFileName = "cropped_image_${System.currentTimeMillis()}.jpg"
            val destinationUri = Uri.fromFile(File(context.cacheDir, destinationFileName))

            val options = UCrop.Options().apply {
                setCompressionQuality(90)
                setFreeStyleCropEnabled(true)
            }

            val uCrop = UCrop.of(it, destinationUri)
                .withAspectRatio(16F, 9F)
                .withMaxResultSize(1080, 720)
                .withOptions(options)

            val intent = uCrop.getIntent(context)
            uCropLauncher.launch(intent)
        }
    }

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

    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                viewModel.dateTime = "$dayOfMonth/${month + 1}/$year"
                timePickerDialog.show()
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnDismissListener { showDatePicker = false }
            show()
        }
    }


    val paidEventString = stringResource(id = R.string.paid_event)

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
                        .border(
                            2.dp,
                            color = MaterialTheme.colorScheme.onSecondary,
                            RoundedCornerShape(12.dp)
                        )
                        .background(MaterialTheme.colorScheme.background)
                        .clickable {
                            imagePicker.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (viewModel.imageUri != null) {
                        AsyncImage(
                            model = viewModel.imageUri,
                            contentDescription = stringResource(id = R.string.selected_image_desc),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                stringResource(id = R.string.upload_image),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
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
                    label = stringResource(id = R.string.event_organizer_label),
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
                        .clickable { showDatePicker = true },
                    enabled = false,
                    trailingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = stringResource(id = R.string.select_date_desc)
                        )
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

            item {
                val eventTypes = listOf(stringResource(id = R.string.free_event), paidEventString)
                var expanded by rememberSaveable { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = viewModel.eventType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.event_type_label)) },
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

            if (viewModel.eventType == paidEventString) {
                item {
                    EventTextField(
                        label = stringResource(id = R.string.event_price_label),
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
                                Toast.makeText(
                                    context,
                                    R.string.event_created_success,
                                    Toast.LENGTH_SHORT
                                ).show()
                                navigateToDashboard()
                                viewModel.resetForm(context)
                            },
                            onError = {
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