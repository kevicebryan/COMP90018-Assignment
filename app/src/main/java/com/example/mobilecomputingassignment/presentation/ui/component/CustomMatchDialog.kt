package com.example.mobilecomputingassignment.presentation.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.mobilecomputingassignment.domain.models.Team

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomMatchDialog(
    availableTeams: List<Team>,
    isLoadingTeams: Boolean,
    onDismiss: () -> Unit,
    onCreateMatch: (homeTeam: String, awayTeam: String) -> Unit
) {
    var selectedHomeTeam by remember { mutableStateOf<Team?>(null) }
    var selectedAwayTeam by remember { mutableStateOf<Team?>(null) }
    var expandedHomeTeam by remember { mutableStateOf(false) }
    var expandedAwayTeam by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Create Custom Match",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                HorizontalDivider()

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Select the teams for your custom match:",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    if (isLoadingTeams) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Loading teams...")
                            }
                        }
                    } else {
                        // Home Team Selection
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Home Team",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                
                                ExposedDropdownMenuBox(
                                    expanded = expandedHomeTeam,
                                    onExpandedChange = { expandedHomeTeam = !expandedHomeTeam }
                                ) {
                                    OutlinedTextField(
                                        value = selectedHomeTeam?.name ?: "Select home team",
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = expandedHomeTeam
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor()
                                    )

                                    ExposedDropdownMenu(
                                        expanded = expandedHomeTeam,
                                        onDismissRequest = { expandedHomeTeam = false }
                                    ) {
                                        availableTeams.forEach { team ->
                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        // Team logo
                                                        team.localLogoRes?.let { logoRes ->
                                                            Icon(
                                                                painter = painterResource(id = logoRes),
                                                                contentDescription = team.name,
                                                                modifier = Modifier.size(24.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                        }
                                                        
                                                        Text(
                                                            text = team.name,
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    selectedHomeTeam = team
                                                    expandedHomeTeam = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // VS Text
                        Text(
                            text = "VS",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        // Away Team Selection
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Away Team",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                
                                ExposedDropdownMenuBox(
                                    expanded = expandedAwayTeam,
                                    onExpandedChange = { expandedAwayTeam = !expandedAwayTeam }
                                ) {
                                    OutlinedTextField(
                                        value = selectedAwayTeam?.name ?: "Select away team",
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = expandedAwayTeam
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor()
                                    )

                                    ExposedDropdownMenu(
                                        expanded = expandedAwayTeam,
                                        onDismissRequest = { expandedAwayTeam = false }
                                    ) {
                                        availableTeams.filter { it != selectedHomeTeam }.forEach { team ->
                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        // Team logo
                                                        team.localLogoRes?.let { logoRes ->
                                                            Icon(
                                                                painter = painterResource(id = logoRes),
                                                                contentDescription = team.name,
                                                                modifier = Modifier.size(24.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                        }
                                                        
                                                        Text(
                                                            text = team.name,
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    selectedAwayTeam = team
                                                    expandedAwayTeam = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            selectedHomeTeam?.let { homeTeam ->
                                selectedAwayTeam?.let { awayTeam ->
                                    onCreateMatch(homeTeam.name, awayTeam.name)
                                }
                            }
                        },
                        enabled = selectedHomeTeam != null && selectedAwayTeam != null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Create Match")
                    }
                }
            }
        }
    }
}
