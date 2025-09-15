package com.example.mobilecomputingassignment.presentation.ui.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.presentation.ui.component.ShakeToReveal
import com.example.mobilecomputingassignment.presentation.ui.component.rememberVibrate

// NEW: imports to talk to ViewModel + do background sampling
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobilecomputingassignment.presentation.viewmodel.CheckInViewModel
import com.example.mobilecomputingassignment.util.NoiseSampler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInCompleteScreen(
    onBackClick: () -> Unit,
    eventId: String,
    onRevealPointsClick: () -> Unit // NEW
) {
    // microphone permission
    val context = LocalContext.current
    var hasMicPermission by remember { mutableStateOf(false) }

    val micPermLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
    }

    LaunchedEffect(Unit) {
        hasMicPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (!hasMicPermission) {
            micPermLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // NEW: get the VM and, once permission is granted, sample & upload one snapshot
    val checkInVm: CheckInViewModel = hiltViewModel()
    LaunchedEffect(hasMicPermission) {
        if (hasMicPermission) {
            // Sample the microphone off the main thread
            val sample = withContext(Dispatchers.Default) { NoiseSampler.sampleDbFs() }
            sample?.let { result ->
                // Send snapshot and update rolling 20-min average
                checkInVm.captureNoiseSnapshot(eventId, result.dbfs)
            }
        }
    }

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
                style = MaterialTheme.typography.headlineSmall
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


