package com.example.mobilecomputingassignment.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobilecomputingassignment.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsEarnedScreen(
    points: Int,
    onBackClick: () -> Unit,
    onSeePoints: () -> Unit,   // ⬅️ keep this callback
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Points Earned") },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 🔹 Added image_31 from drawable
            Image(
                painter = painterResource(id = R.drawable.image_31),
                contentDescription = "Points earned illustration",
                modifier = Modifier.size(width = 240.dp, height = 384.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "🎉 Congrats!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "You have earned points from this event!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "+ ${points} pts",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onSeePoints,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("See Points")
            }
        }
    }
}
