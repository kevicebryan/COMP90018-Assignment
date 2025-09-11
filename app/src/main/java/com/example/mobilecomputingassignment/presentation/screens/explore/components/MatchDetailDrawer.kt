package com.example.mobilecomputingassignment.presentation.screens.explore.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.domain.models.Event
import com.example.mobilecomputingassignment.presentation.ui.component.TeamLogo
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailDrawer(
    event: Event,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onGetDirections: (Event) -> Unit,
    onToggleInterest: (Event) -> Unit,
    currentUserId: String? = null,
    isUpdatingInterest: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Animation for drawer visibility
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "drawer_alpha"
    )

    val animatedOffset by animateFloatAsState(
        targetValue = if (isVisible) 0f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "drawer_offset"
    )

    if (isVisible) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .zIndex(10f) // Higher z-index than the nearest events drawer
        ) {
            // Background overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = animatedAlpha * 0.3f))
                    .zIndex(1f)
            )

            // Drawer content
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .heightIn(min = 400.dp, max = 700.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
                    .zIndex(2f)
                    .offset(y = (animatedOffset * 100).dp)
            ) {
                // Header with close button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Match Details",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    thickness = 1.dp
                )

                // Scrollable content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Teams section
                    item {
                        TeamsSection(event = event)
                    }

                    // Date and time section
                    item {
                        DateTimeSection(event = event)
                    }

                    // Venue section
                    item {
                        VenueSection(event = event)
                    }

                    // Amenities section
                    item {
                        AmenitiesSection(event = event)
                    }

                    // Accessibility section
                    item {
                        AccessibilitySection(event = event)
                    }

                    // Interest count section
                    item {
                        InterestCountSection(event = event)
                    }

                    // Noise level section (for ongoing events)
                    item {
                        NoiseLevelSection(event = event)
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Directions button
                    Button(
                        onClick = { onGetDirections(event) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_direction),
                            contentDescription = "Get directions",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Directions")
                    }

                    // Interest button
                    val isInterested = event.interestedUsers.contains(currentUserId)
                    val canInterest = currentUserId != null && currentUserId != event.hostUserId
                    
                    // Debug logging
                    android.util.Log.d("MatchDetailDrawer", "Interest button debug:")
                    android.util.Log.d("MatchDetailDrawer", "  currentUserId: $currentUserId")
                    android.util.Log.d("MatchDetailDrawer", "  event.hostUserId: ${event.hostUserId}")
                    android.util.Log.d("MatchDetailDrawer", "  canInterest: $canInterest")
                    android.util.Log.d("MatchDetailDrawer", "  isInterested: $isInterested")
                    
                    OutlinedButton(
                        onClick = { 
                            if (canInterest && !isUpdatingInterest) {
                                android.util.Log.d("MatchDetailDrawer", "Interest button clicked")
                                onToggleInterest(event) 
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = canInterest && !isUpdatingInterest,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isInterested) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                    ) {
                        if (isUpdatingInterest) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = if (isInterested) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isInterested) "Remove interest" else "Add interest",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            when {
                                isUpdatingInterest -> "Updating..."
                                isInterested -> "Interested"
                                else -> "Interest"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamsSection(event: Event) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Match",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home team
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TeamLogo(teamName = event.matchDetails?.homeTeam ?: "TBD")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = event.matchDetails?.homeTeam ?: "TBD",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "VS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Away team
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TeamLogo(teamName = event.matchDetails?.awayTeam ?: "TBD")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = event.matchDetails?.awayTeam ?: "TBD",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun DateTimeSection(event: Event) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Date",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(event.date),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            // Time
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_clock),
                    contentDescription = "Time",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(event.checkInTime),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun VenueSection(event: Event) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_location),
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Venue",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = event.location.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = event.location.address,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (event.contactNumber.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_phone),
                        contentDescription = "Contact",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = event.contactNumber,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun AmenitiesSection(event: Event) {
    val amenities = listOf(
        "Indoor" to event.amenities.isIndoor,
        "Outdoor" to event.amenities.isOutdoor,
        "Child Friendly" to event.amenities.isChildFriendly,
        "Pet Friendly" to event.amenities.isPetFriendly,
        "Parking" to event.amenities.hasParking,
        "Food" to event.amenities.hasFood,
        "Toilet" to event.amenities.hasToilet,
        "WiFi" to event.amenities.hasWifi
    ).filter { it.second }

    if (amenities.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Amenities",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(amenities) { (amenity, _) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = amenity,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = amenity,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AccessibilitySection(event: Event) {
    val accessibility = listOf(
        "Wheelchair Accessible" to event.accessibility.isWheelchairAccessible,
        "Accessible Toilets" to event.accessibility.hasAccessibleToilets,
        "Accessible Parking" to event.accessibility.hasAccessibleParking,
        "Sign Language Support" to event.accessibility.hasSignLanguageSupport,
        "Audio Support" to event.accessibility.hasAudioSupport
    ).filter { it.second }

    if (accessibility.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Accessibility",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(accessibility) { (feature, _) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_accessible),
                                contentDescription = feature,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = feature,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InterestCountSection(event: Event) {
    // Debug logging for interest count
    android.util.Log.d("InterestCountSection", "Event ${event.id}: ${event.interestedUsers.size} interested users")
    android.util.Log.d("InterestCountSection", "Interested users: ${event.interestedUsers}")
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Interested users",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${event.interestedUsers.size} people interested",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun NoiseLevelSection(event: Event) {
    val currentTime = Date()
    val eventTime = event.date
    val timeDifference = kotlin.math.abs(currentTime.time - eventTime.time)
    val oneHourInMillis = 60 * 60 * 1000L
    
    // Check if event is ongoing (within 1 hour range)
    val isOngoing = timeDifference <= oneHourInMillis
    
    if (isOngoing) {
        val noiseLevel = when {
            event.volume <= 40 -> Pair("Quiet", R.drawable.ic_volume)
            event.volume <= 70 -> Pair("Moderate", R.drawable.ic_volume)
            else -> Pair("Loud", R.drawable.ic_volume)
        }
        
        val backgroundColor = when {
            event.volume <= 40 -> Color.Green.copy(alpha = 0.1f)
            event.volume <= 70 -> Color(0xFFFF9800).copy(alpha = 0.1f) // Orange
            else -> Color.Red.copy(alpha = 0.1f)
        }
        
        val textColor = when {
            event.volume <= 40 -> Color.Green
            event.volume <= 70 -> Color(0xFFFF9800) // Orange
            else -> Color.Red
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = noiseLevel.second),
                    contentDescription = "Noise level",
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Noise Level: ${noiseLevel.first} (${event.volume}dB)",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
            }
        }
    }
}
