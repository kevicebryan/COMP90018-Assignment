package com.example.mobilecomputingassignment.presentation.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
    onCreateMatch: (homeTeam: String, awayTeam: String) -> Unit,
    currentHomeTeam: String? = null,
    currentAwayTeam: String? = null
) {
    var selectedHomeTeam by remember { 
        mutableStateOf<Team?>(
            currentHomeTeam?.let { homeTeamName ->
                availableTeams.find { it.name == homeTeamName }
            }
        )
    }
    var selectedAwayTeam by remember { 
        mutableStateOf<Team?>(
            currentAwayTeam?.let { awayTeamName ->
                availableTeams.find { it.name == awayTeamName }
            }
        )
    }
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
                .fillMaxWidth(0.95f)
                .heightIn(max = 600.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Create Custom Match",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    IconButton(
                        onClick = onDismiss,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                HorizontalDivider()

                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = "Select the teams for your custom match:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        if (selectedHomeTeam != null && selectedAwayTeam != null) {
                            // Show selected teams with logos
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Custom Match",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 20.dp)
                                    )
                                    
                                    // Teams display
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Home Team
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            selectedHomeTeam?.localLogoRes?.let { logoRes ->
                                                Image(
                                                    painter = painterResource(id = logoRes),
                                                    contentDescription = selectedHomeTeam?.name,
                                                    modifier = Modifier.size(60.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = selectedHomeTeam?.name ?: "",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }
                                        
                                        // VS
                                        Text(
                                            text = "VS",
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                        
                                        // Away Team
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            selectedAwayTeam?.localLogoRes?.let { logoRes ->
                                                Image(
                                                    painter = painterResource(id = logoRes),
                                                    contentDescription = selectedAwayTeam?.name,
                                                    modifier = Modifier.size(60.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = selectedAwayTeam?.name ?: "",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }
                                    }
                                    
                                    // Change Teams Button
                                    Spacer(modifier = Modifier.height(16.dp))
                                    OutlinedButton(
                                        onClick = {
                                            selectedHomeTeam = null
                                            selectedAwayTeam = null
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.primary
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Change Teams")
                                    }
                                }
                            }
                        } else {
                            // Team Selection Interface
                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Home Team Selection
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp)
                                    ) {
                                        Text(
                                            text = "Home Team",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(bottom = 12.dp)
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
                                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                                                    focusedTextColor = MaterialTheme.colorScheme.onSurface
                                                ),
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
                                                                    Image(
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

                                // Away Team Selection
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp)
                                    ) {
                                        Text(
                                            text = "Away Team",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(bottom = 12.dp)
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
                                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                                                    focusedTextColor = MaterialTheme.colorScheme.onSurface
                                                ),
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
                                                                    Image(
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
                    }
                }

                // Action buttons - Fixed at bottom
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Cancel",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
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
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Save Match",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    }
                }
            }
        }
    }
}
