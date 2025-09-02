package com.example.mobilecomputingassignment.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mobilecomputingassignment.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInLanding(
    // Pass null when used as a bottom-tab (no back arrow). Pass a lambda to show the arrow.
    onBackClick: (() -> Unit)? = null,
    // Called when the user taps the “Tap to scan” button
    onTapScan: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(120.dp), // taller bar
                title = {
                    Image(
                        painter = painterResource(R.drawable.checkin_font),
                        contentDescription = null,
                        modifier = Modifier.height(120.dp) // bigger logo (try 48–64dp)
                    )
                },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
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
            Spacer(Modifier.height(46.dp)) // matches your reference top margin

            // === Circular “image box” with orange stroke (#FE8F00) ===
            Box(
                modifier = Modifier
                    .size(256.dp)
                    .clip(CircleShape)
                    .border(width = 8.dp, color = Color(0xFFFE8F00), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Option A: use your vector at res/drawable/image_30.xml
                // (You included it in your reference)
                Image(
                    painter = painterResource(id = R.drawable.image_30),
                    contentDescription = "Check-in illustration",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Option B (fallback): leave empty or put an icon/text placeholder
                // Text("QR", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))

            // === Helper text (centered) ===
            Text(
                text = "Scan the host's QR code to check in for the event.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(24.dp))

            // === “Tap to scan” button — dark background (#221A14) & rounded corners ===
            Button(
                onClick = onTapScan,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF221A14)),
                modifier = Modifier
                    .width(193.dp)
                    .height(40.dp)
            ) {
                Text(
                    text = "Tap to scan",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}