package com.example.jvent.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.jvent.EventCard
import com.example.jvent.R
import kotlinx.coroutines.launch

@Composable
fun LandingPage(
    navigateToRegistration: () -> Unit,
    navigateToExploreEvent: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToDetail: () -> Unit
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
                        .padding(16.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
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
                AppBarLanding(onMenuClick = { scope.launch { drawerState.open() } })
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
                item { PopularEventSection(navigateToDetail = navigateToDetail) }
                item { Spacer(modifier = Modifier.height(32.dp)) }
                item { CallToAction() }
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
fun HeroSection() {
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
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(stringResource(R.string.hero_button), color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}

@Composable
fun PopularEventSection(navigateToDetail: () -> Unit) {
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
                EventCard(navigateToDetail = navigateToDetail)
            }
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
            Text(stringResource(R.string.call_to_action_button), color = MaterialTheme.colorScheme.onSurface)
        }
    }
}