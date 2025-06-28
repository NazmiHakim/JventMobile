package com.example.jvent.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jvent.JventApplication
import com.example.jvent.components.DefaultTopBar
import com.example.jvent.components.EventCard
import com.example.jvent.viewmodel.EventListViewModel
import com.example.jvent.viewmodel.EventViewModelFactory

@Composable
fun FavoritesScreen(
    navigateToDetail: (String) -> Unit
) {
    val context = LocalContext.current
    val eventListViewModel: EventListViewModel = viewModel(
        factory = EventViewModelFactory((context.applicationContext as JventApplication).repository)
    )
    val favoriteEvents by eventListViewModel.favoriteEvents.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            DefaultTopBar(title = "Favorit")
        }
    ) { innerPadding ->
        if (favoriteEvents.isEmpty()) {
            Text(
                text = "Belum ada event favorit.",
                modifier = Modifier.padding(innerPadding).padding(16.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                items(favoriteEvents) { event ->
                    EventCard(
                        event = event,
                        navigateToDetail = { navigateToDetail(event.id) }
                    )
                }
            }
        }
    }
}