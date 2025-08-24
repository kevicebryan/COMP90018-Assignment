package com.example.mobilecomputingassignment.presentation.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mobilecomputingassignment.utils.QRCodeGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRCodeScreen(username: String, onBackClick: () -> Unit) {
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(username) { qrBitmap = QRCodeGenerator.generateQRCode(username, 400, 400) }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("My QR Code") },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                )
                            }
                        }
                )
            }
    ) { paddingValues ->
        Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
            Text(
                    text = "Share Your Profile",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                    text = "Let others scan this QR code to check in on your hosted events",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 32.dp)
            )

            // QR Code Container
            Card(
                    modifier = Modifier.size(320.dp).padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                ) {
                    qrBitmap?.let { bitmap ->
                        Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "QR Code for $username",
                                modifier =
                                        Modifier.size(200.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.surface)
                        )
                    }
                            ?: run {
                                Box(
                                        modifier =
                                                Modifier.size(200.dp)
                                                        .background(
                                                                MaterialTheme.colorScheme
                                                                        .surfaceVariant,
                                                                RoundedCornerShape(8.dp)
                                                        ),
                                        contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator() }
                            }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                            text = "@$username",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
