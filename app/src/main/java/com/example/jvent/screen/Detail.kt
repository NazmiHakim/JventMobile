package com.example.jvent.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.jvent.components.DefaultTopBar
import com.example.jvent.R

@Composable
fun Detail() {
    Scaffold(
        topBar = {
            DefaultTopBar(title = stringResource(id = R.string.app_name))
        }, containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = rememberAsyncImagePainter("https://cdn.trii.global/Banner/NewsArticle/mobile/-1248124158.jpg"),
                contentDescription = stringResource(R.string.description),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            )
            {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.event_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.event_location),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        stringResource(R.string.event_date),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        stringResource(R.string.free_event),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.event_organizer),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        stringResource(R.string.organizer_name),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.buy_ticket))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }


            Spacer(modifier = Modifier.height(24.dp))
                Text(text = stringResource(R.string.description), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    color = MaterialTheme.colorScheme.onPrimary,
                    text = stringResource(R.string.event_description)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Image(
                    painter = rememberAsyncImagePainter("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSIdbHugvMf7ner8p3PmeUEV9zKAb-BeU2CQg&s"),
                    contentDescription = stringResource(R.string.poster),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }