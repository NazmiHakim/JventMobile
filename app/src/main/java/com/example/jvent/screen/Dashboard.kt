package com.example.jvent.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jvent.JventApplication
import com.example.jvent.R
import com.example.jvent.components.DefaultTopBar
import com.example.jvent.components.EventCard
import com.example.jvent.viewmodel.EventListViewModel
import com.example.jvent.viewmodel.EventViewModelFactory

@Composable
fun Dashboard(
    navigateToDetail: (String) -> Unit,
    navigateToMakeEvent: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val eventListViewModel: EventListViewModel = viewModel(
        factory = EventViewModelFactory((context.applicationContext as JventApplication).repository)
    )
    val events by eventListViewModel.allEvents.collectAsState(initial = emptyList())

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.active_event),
        stringResource(R.string.past_event)
    )

    // The LaunchedEffect for fetching data is no longer needed.
    // The UI will automatically update when 'events' changes.

    Scaffold(
        topBar = {
            DefaultTopBar(title = stringResource(id = R.string.app_name))
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Search field dan tab
            item {
                Column {
                    var searchQuery by rememberSaveable { mutableStateOf("") }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(stringResource(R.string.search_event_here)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        tabs.forEachIndexed { index, title ->
                            TextButton(onClick = { selectedTab = index }) {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Event list horizontal
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 260.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(events) { event ->
                        EventCard(
                            event = event,
                            navigateToDetail = { navigateToDetail(event.id) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { navigateToMakeEvent() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(stringResource(R.string.create_event))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.thank_you_message), fontSize = 16.sp)
                    Text(
                        stringResource(R.string.create_event_prompt),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Logout")
                }
            }
        }
    }
}