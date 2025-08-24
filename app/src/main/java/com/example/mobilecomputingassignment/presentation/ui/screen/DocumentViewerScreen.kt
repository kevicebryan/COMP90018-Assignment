package com.example.mobilecomputingassignment.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentViewerScreen(title: String, content: String, onBackClick: () -> Unit) {
        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text(title) },
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
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(paddingValues)
                                        .padding(16.dp)
                                        .verticalScroll(rememberScrollState())
                ) {
                        Text(
                                text = title,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Text(
                                text =
                                        "Last updated: ${java.text.SimpleDateFormat("MMMM dd, yyyy").format(java.util.Date())}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // Document content
                        Text(
                                text = content,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                                modifier = Modifier.padding(bottom = 32.dp)
                        )

                        // Contact information
                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                                text = "Contact Us",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                                text =
                                        "If you have any questions about this document, please contact us at:\n\n" +
                                                "Email: kevinbryanreligion@gmail.com\n" +
                                                "Website: www.kevbry.in\n" +
                                                "Address: University of Melbourne, Carlton, VIC, 3053",
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                        )
                }
        }
}
