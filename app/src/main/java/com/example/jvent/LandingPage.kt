package com.example.jvent

import android.provider.CalendarContract.Colors
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun LandingPage(
    navigateToRegistration: () -> Unit,
    navigateToExploreEvent: () -> Unit,
    navigateToSettings: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    stringResource(R.string.app_name),
                    modifier = Modifier
                        .padding(16.dp)
                        .background(MaterialTheme.colorScheme.secondary),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.make_event), color = MaterialTheme.colorScheme.onPrimary) },
                    icon = { Icon(Icons.Outlined.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navigateToRegistration()
                    },
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.explore), color = MaterialTheme.colorScheme.onPrimary) },
                    icon = { Icon(Icons.Outlined.Explore, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navigateToExploreEvent()
                    }
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.settings), color = MaterialTheme.colorScheme.onPrimary) },
                    icon = { Icon(Icons.Outlined.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navigateToSettings()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                AppBar(onMenuClick = { scope.launch { drawerState.open() } })
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .clipToBounds()
            ) {
                item { HeroSection() }
                item { Spacer(modifier = Modifier.height(24.dp)) }
                item { PopularEventSection() }
                item { Spacer(modifier = Modifier.height(32.dp)) }
                item { CallToAction() }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(onMenuClick: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    TopAppBar(
        title = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                placeholder = { Text(stringResource(R.string.search_event)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(53.dp),
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
fun HeroSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        AsyncImage(
            model = "https://i.ibb.co/PtTZ3Vf/cosplay.jpg",
            contentDescription = "Hero Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0x99000000), Color(0xCC000000)),
                        startY = 0f,
                        endY = 1000f
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(24.dp)
        ) {
            Text(
                stringResource(R.string.hero_1),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(R.string.hero_2),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { /* TODO */ },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(stringResource(R.string.hero_button), color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
fun PopularEventSection() {
    Column(modifier = Modifier
        .padding(16.dp)
        .background(MaterialTheme.colorScheme.background)
    ) {
        Text(stringResource(R.string.popular_event), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 260.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(5) {
                EventCard()
            }
        }
    }
}

@Composable
fun EventCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        modifier = Modifier
            .width(220.dp)
            .height(240.dp),
        shape = RoundedCornerShape(12.dp)

    ) {
        Box {
            AsyncImage(
                model = "https://i.ibb.co/Wt2KxP0/musicfestival.jpg",
                contentDescription = "Event Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Favorite",
                tint = Color.Red,
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
fun CallToAction() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.call_to_action_1),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { /* TODO */ },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSecondaryContainer)
        ) {
            Text(stringResource(R.string.call_to_action_button), color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}