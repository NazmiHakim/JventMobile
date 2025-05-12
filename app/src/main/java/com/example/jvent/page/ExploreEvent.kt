package com.example.jvent.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.jvent.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExploreEvent(navigateToDetail: () -> Unit) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

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
                    text = stringResource(R.string.search_result_prefix).format(searchQuery),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(4) {
                        ExploreEventCard(navigateToDetail = navigateToDetail)
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
fun ExploreEventCard(navigateToDetail: () -> Unit = {}) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        modifier = Modifier
            .width(220.dp)
            .height(240.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = navigateToDetail
    ) {
        Box {
            Image(
                painter = rememberAsyncImagePainter("https://cdn.trii.global/Banner/NewsArticle/mobile/-1248124158.jpg"),
                contentDescription = stringResource(R.string.event_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = stringResource(R.string.favorite),
                tint = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
        }
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = stringResource(R.string.event_title),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.event_date),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.free_event),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun FilterChip(text: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.onSecondary,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(text = text, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}