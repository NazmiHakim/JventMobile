package com.example.jvent.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jvent.JventApplication
import com.example.jvent.R
import com.example.jvent.components.EventCard
import com.example.jvent.viewmodel.EventListViewModel
import com.example.jvent.viewmodel.EventViewModelFactory

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExploreEvent(
    navigateToDetail: (String) -> Unit
) {
    val context = LocalContext.current
    val eventListViewModel: EventListViewModel = viewModel(
        factory = EventViewModelFactory((context.applicationContext as JventApplication).repository)
    )
    val allEvents by eventListViewModel.allEvents.collectAsState(initial = emptyList())
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val events = remember(allEvents, searchQuery) {
        if (searchQuery.isBlank()) {
            allEvents
        } else {
            allEvents.filter {
                it.title.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val isLoading = false // No longer fetched directly, so loading state is simpler.

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(stringResource(R.string.search_placeholder)) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search_event))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = if (searchQuery.isEmpty()) {
                        stringResource(R.string.all_events)
                    } else {
                        stringResource(R.string.search_result_prefix).format(searchQuery)
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (events.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_events_found),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                item {
                    LazyRow(
                        modifier = Modifier.height(260.dp),
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
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(stringResource(R.string.search_by_time), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        stringResource(R.string.today),
                        stringResource(R.string.tomorrow),
                        stringResource(R.string.this_week),
                        stringResource(R.string.next_week),
                        stringResource(R.string.next_month)
                    ).forEach {
                        FilterChip(text = it)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(stringResource(R.string.search_by_price), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(text = stringResource(R.string.free_event), modifier = Modifier.weight(1f))
                    FilterChip(text = stringResource(R.string.paid_event), modifier = Modifier.weight(1f))
                }
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(text = text)
        },
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    )
}