package com.example.mobilecomputingassignment.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.presentation.ui.component.ShakeToReveal
import com.example.mobilecomputingassignment.presentation.ui.component.rememberVibrate



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInCompleteScreen(
    onBackClick: () -> Unit,
    eventId: String,
    onRevealPointsClick: () -> Unit // NEW
) {
    val vibrate = rememberVibrate()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Check-in Complete") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            ShakeToReveal(onShake = {
                onRevealPointsClick()
                vibrate()
            })

            Spacer(Modifier.height(32.dp))

            // Optional illustration if you have it (R.drawable.image_32)
            runCatching {
                Image(
                    painter = painterResource(id = R.drawable.image_32),
                    contentDescription = null,
                    modifier = Modifier.size(256.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Check In Complete!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Redeem your points by shaking your phone or tapping the button below",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    onRevealPointsClick()
                    vibrate()
                },
                modifier = Modifier
                    .width(178.dp)
                    .height(40.dp)
            ) {
                Text("Reveal points", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Event ID: $eventId",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

