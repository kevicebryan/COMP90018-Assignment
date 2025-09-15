package com.example.mobilecomputingassignment.presentation.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mobilecomputingassignment.domain.models.Team
import androidx.compose.foundation.layout.statusBarsPadding


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamSelectionScreen(
        availableTeams: List<Team>, // All possible teams the user can pick from
        initiallySelectedTeamNames: Set<String>, // The names of teams the user has ALREADY selected
        isLoading: Boolean, // True if teams are currently being loaded/fetched
        onSaveClick:
                (updatedSelectedTeamNames: Set<String>) -> Unit, // Callback when user saves changes
        onBackClick: () -> Unit // Callback when user navigates back
) {
    var localSelectedTeamNames by
            remember(initiallySelectedTeamNames) { mutableStateOf(initiallySelectedTeamNames) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Favourite Teams") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                modifier = Modifier.statusBarsPadding() // ADD THIS LINE
            )
        },
            bottomBar = {
                Button(
                        onClick = { onSaveClick(localSelectedTeamNames) },
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        enabled = !isLoading && localSelectedTeamNames != initiallySelectedTeamNames
                ) { Text("Save Changes") }
            }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                Box(
                        modifier = Modifier.fillMaxSize(), // Changed from
                        // fillMaxWidth().height(320.dp) to fill
                        // screen
                        contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            } else if (availableTeams.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                            text = "No teams available to select.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                LazyColumn(
                        verticalArrangement =
                                Arrangement.spacedBy(8.dp), // Matched from SignupTeamStep
                        // If you want the fixed height like SignupTeamStep:
                        // modifier = Modifier.height(320.dp).padding(horizontal = 16.dp)
                        // If you want it to fill available space in Scaffold:
                        modifier =
                                Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(availableTeams.chunked(3)) { teamRow ->
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp) // Matched
                        ) {
                            teamRow.forEach { team ->
                                val isSelected = localSelectedTeamNames.contains(team.name)

                                TeamDisplayCard( // Using the common/matched card
                                        team = team,
                                        isSelected = isSelected,
                                        onTeamClick = {
                                            localSelectedTeamNames =
                                                    if (isSelected)
                                                            localSelectedTeamNames - team.name
                                                    else localSelectedTeamNames + team.name
                                        },
                                        modifier =
                                                Modifier.weight(1f)
                                                        .aspectRatio(
                                                                1f
                                                        ) // Apply weight and aspect ratio here
                                )
                            }
                            // Fill out missing columns
                            repeat(3 - teamRow.size) {
                                Spacer(
                                        Modifier.weight(1f).aspectRatio(1f)
                                ) // Ensure spacer maintains grid shape
                            }
                        }
                    }
                }
            }
        }
    }
}

// TeamDisplayCard composable as defined above
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDisplayCard(
        team: Team,
        isSelected: Boolean,
        onTeamClick: () -> Unit,
        modifier: Modifier =
                Modifier // Modifier is passed in, including .weight(1f).aspectRatio(1f)
) {
    Card(
            onClick = onTeamClick,
            modifier = modifier, // This modifier will now include .weight(1f) and .aspectRatio(1f)
            shape = RoundedCornerShape(12.dp),
            colors =
                    CardDefaults.cardColors(
                            containerColor =
                                    if (isSelected)
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    else MaterialTheme.colorScheme.surface
                    ),
            border =
                    if (isSelected) {
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    } else {
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    }
    ) {
        Column(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
            Box(
                    modifier =
                            Modifier.weight(1f) // Ensure image area can expand
                                    .size(48.dp), // Keep logo area consistent
                    contentAlignment = Alignment.Center
            ) {
                if (team.localLogoRes != null) {
                    Image(
                            painter = painterResource(id = team.localLogoRes),
                            contentDescription = "${team.name} logo",
                            modifier = Modifier.fillMaxSize() // Fill the 48dp Box
                    )
                } else if (!team.logoUrl.isNullOrEmpty()) {
                    AsyncImage(
                            model = team.logoUrl,
                            contentDescription = "${team.name} logo",
                            modifier = Modifier.fillMaxSize() // Fill the 48dp Box
                    )
                } else {
                    Box(
                            modifier =
                                    Modifier.fillMaxSize()
                                            .background(
                                                    MaterialTheme.colorScheme.surfaceVariant,
                                                    RoundedCornerShape(8.dp)
                                            ),
                            contentAlignment = Alignment.Center
                    ) {
                        Text(text = team.abbreviation, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                    text = team.name,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    color =
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
