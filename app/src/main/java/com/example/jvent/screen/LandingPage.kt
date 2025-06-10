package com.example.jvent.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.jvent.R
import com.example.jvent.components.EventCard
import com.example.jvent.model.Event
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LandingPage(
    navigateToRegistration: () -> Unit,
    navigateToExploreEvent: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToDetail: (String) -> Unit,
    navigateToDashboard: () -> Unit,
    isLoggedIn: Boolean
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Firestore dan state event
    val db = Firebase.firestore
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Fetch events from Firestore on launch
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val snapshot = db.collection("events").get().await()
            events = snapshot.documents.map { doc ->
                Event(
                    id = doc.id,
                    title = doc.getString("title") ?: "",
                    description = doc.getString("description") ?: "",
                    dateTime = doc.getString("dateTime") ?: "",
                    location = doc.getString("location") ?: "",
                    organizer = doc.getString("organizer") ?: "",
                    platformLink = doc.getString("platformLink") ?: "",
                    ticketCategory = doc.getString("ticketCategory") ?: "",
                    imageUrl = doc.getString("imageUrl") ?: "",
                    userId = doc.getString("userId") ?: "",
                    eventType = doc.getString("eventType") ?: "Gratis",
                    price = doc.getString("price") ?: ""
                )
            }
        } catch (e: Exception) {
            error = e.message ?: "Failed to load events"
            android.widget.Toast.makeText(context, error, android.widget.Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    stringResource(R.string.app_name),
                    modifier = Modifier.padding(16.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                if (isLoggedIn) {
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.admin_dashboard), color = MaterialTheme.colorScheme.onPrimary) },
                        icon = { Icon(Icons.Outlined.Dashboard, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navigateToDashboard()
                        }
                    )
                }
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
                AppBarLanding(onMenuClick = { scope.launch { drawerState.open() } })
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .clipToBounds()
            ) {
                item { HeroSection(navigateToExploreEvent = navigateToExploreEvent) }
                item { Spacer(modifier = Modifier.height(24.dp)) }
                item {
                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        PopularEventSection(
                            events = events,
                            navigateToDetail = navigateToDetail
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
                //item { CallToAction(navigateToRegistration = navigateToRegistration) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarLanding(onMenuClick: () -> Unit) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

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
fun HeroSection(navigateToExploreEvent: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter("https://cdn.trii.global/Banner/NewsArticle/mobile/-1248124158.jpg"),
            contentDescription = "Hero Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.tertiaryContainer,
                            MaterialTheme.colorScheme.background
                        ),
                        startY = 0f,
                        endY = 800f
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
                onClick = navigateToExploreEvent,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(stringResource(R.string.hero_button), color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}

@Composable
fun PopularEventSection(
    events: List<Event>,
    navigateToDetail: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .height(295.dp)
    ) {
        Text(
            stringResource(R.string.popular_event),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (events.isEmpty()) {
            Text("No events available", modifier = Modifier.padding(16.dp))
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
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
}

//@Composable
//fun CallToAction(navigateToRegistration: () -> Unit) {
    //Column(
        //modifier = Modifier
            //.fillMaxWidth()
            //.background(MaterialTheme.colorScheme.background)
            //.padding(24.dp),
        //horizontalAlignment = Alignment.CenterHorizontally
    //) {
        //Text(
            //(R.string.call_to_action_1),
            //color = MaterialTheme.colorScheme.onSurface,
            //fontWeight = FontWeight.Bold,
            //fontSize = 20.sp
        //)
        //Spacer(modifier = Modifier.height(12.dp))
        //Button(
            //onClick = navigateToRegistration,
            //colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSecondaryContainer)
        //) {
            //Text(stringResource(R.string.call_to_action_button), color = MaterialTheme.colorScheme.onSurface)
        //}
    //}
//}