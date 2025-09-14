package com.example.mobilecomputingassignment.presentation.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mobilecomputingassignment.R
import androidx.compose.foundation.layout.statusBarsPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileLeagueSelectionScreen(
    initialSelectedLeagues: Set<String> = emptySet(),
    isLoading: Boolean = false,
    onBackClick: () -> Unit,
    onSaveClick: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLeagues by remember(initialSelectedLeagues) {
        mutableStateOf(initialSelectedLeagues)
    }

    // Use SAME Scaffold + TopAppBar pattern as other profile sub-screens
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select League") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                modifier = Modifier.statusBarsPadding()
            )
        },
        bottomBar = {
            // Show save button only if changes made
            if (selectedLeagues != initialSelectedLeagues) {
                Button(
                    onClick = { onSaveClick(selectedLeagues.toList()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = !isLoading
                ) {
                    Text("Save Changes")
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // League selection grid (same as before)
                    val availableLeagues = listOf("AFL", "A-League", "Premier League", "NBA")
                    val enabledLeagues = setOf("AFL")
                    val leagueImages = mapOf(
                        "AFL" to R.drawable.league_afl,
                        "A-League" to R.drawable.league_a_league,
                        "Premier League" to R.drawable.league_premier_league,
                        "NBA" to R.drawable.league_nba
                    )

                    items(availableLeagues.chunked(3)) { leagueRow ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            leagueRow.forEach { league ->
                                val isEnabled = enabledLeagues.contains(league)
                                val isSelected = selectedLeagues.contains(league)

                                Card(
                                    onClick = {
                                        if (isEnabled) {
                                            selectedLeagues = if (isSelected) {
                                                selectedLeagues - league
                                            } else {
                                                selectedLeagues + league
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f).aspectRatio(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = when {
                                            isSelected && isEnabled -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                            isEnabled -> MaterialTheme.colorScheme.surface
                                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                        }
                                    ),
                                    border = when {
                                        isSelected && isEnabled -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                        isEnabled -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
                                    }
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        leagueImages[league]?.let { imageRes ->
                                            Image(
                                                painter = painterResource(id = imageRes),
                                                contentDescription = "$league logo",
                                                modifier = Modifier.size(48.dp).weight(1f),
                                                colorFilter = if (!isEnabled) ColorFilter.colorMatrix(
                                                    ColorMatrix().apply { setToSaturation(0f) }
                                                ) else null,
                                                alpha = if (!isEnabled) 0.4f else 1f
                                            )
                                        } ?: Box(
                                            modifier = Modifier.size(48.dp).weight(1f)
                                                .background(
                                                    MaterialTheme.colorScheme.surfaceVariant,
                                                    RoundedCornerShape(8.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(league.take(3), style = MaterialTheme.typography.labelMedium)
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = league,
                                            style = MaterialTheme.typography.bodySmall,
                                            textAlign = TextAlign.Center,
                                            maxLines = 2,
                                            color = when {
                                                isSelected && isEnabled -> MaterialTheme.colorScheme.primary
                                                isEnabled -> MaterialTheme.colorScheme.onSurface
                                                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                            }
                                        )
                                    }
                                }
                            }

                            repeat(3 - leagueRow.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
