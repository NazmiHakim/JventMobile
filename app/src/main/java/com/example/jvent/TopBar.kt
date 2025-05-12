package com.example.jvent

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopBar(
    title: String,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.tertiary
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = titleColor,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            MaterialTheme.colorScheme.background
        )
    )
}
