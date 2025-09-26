package com.example.mobilecomputingassignment.presentation.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.res.painterResource
import com.example.mobilecomputingassignment.R
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mobilecomputingassignment.domain.models.Event
import com.example.mobilecomputingassignment.presentation.utils.EventDateUtils
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCard(
    event: Event,
    isHosted: Boolean = false,
    onToggleInterest: (() -> Unit)? = null,
    onCheckIn: (() -> Unit)? = null,
    onGetDirections: (() -> Unit)? = null,
    onEditEvent: (() -> Unit)? = null,
    onDeleteEvent: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Check if event is in the past for opacity styling
    val isEventInPast = EventDateUtils.isEventInPast(event)
    val cardAlpha = if (isEventInPast) 0.33f else 1.0f

    // Separate the "- [Event Venue]" from event.title (since it repeats in event.description)
    val cleanTitle = event.title.split("-").first().trim().replace(" vs ", " vs \n")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(cardAlpha),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(
                            color =
                                MaterialTheme.colorScheme.primaryContainer.copy(
                                    alpha = 0.3f
                                ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = cleanTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (event.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = event.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Action buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (isHosted && !isEventInPast) {
                            // Edit button with circular background
                            Box(
                                modifier =
                                    Modifier
                                        .size(40.dp)
                                        .background(
                                            MaterialTheme.colorScheme
                                                .primaryContainer,
                                            CircleShape
                                        ),
                                contentAlignment = Alignment.Center
                            ) {
                                IconButton(onClick = { onEditEvent?.invoke() }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Event",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Delete button with circular background
                            Box(
                                modifier =
                                    Modifier
                                        .size(40.dp)
                                        .background(
                                            MaterialTheme.colorScheme
                                                .errorContainer,
                                            CircleShape
                                        ),
                                contentAlignment = Alignment.Center
                            ) {
                                IconButton(onClick = { showDeleteDialog = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Event",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
//                        } else {
//                            // Interest button with circular background [REMOVED FOR NOW]
//                            Box(
//                                    modifier =
//                                            Modifier.size(40.dp)
//                                                    .background(
//                                                            MaterialTheme.colorScheme
//                                                                    .tertiaryContainer,
//                                                            CircleShape
//                                                    ),
//                                    contentAlignment = Alignment.Center
//                            ) {
//                                IconButton(onClick = { onToggleInterest?.invoke() }) {
//                                    Icon(
//                                            imageVector = Icons.Default.Favorite,
//                                            contentDescription = "Toggle Interest",
//                                            tint = MaterialTheme.colorScheme.tertiary,
//                                            modifier = Modifier.size(20.dp)
//                                    )
//                                }
//                            }
                        }
                    }
                }
            }

            if (!isEventInPast) {
                Spacer(modifier = Modifier.height(12.dp))

                // Team logos before event details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TeamLogo(
                        teamName = event.matchDetails?.homeTeam ?: "TBD",
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "vs",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    TeamLogo(
                        teamName = event.matchDetails?.awayTeam ?: "TBD",
                        modifier = Modifier.size(64.dp)
                    )
                }
                // Event details in nested card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.surfaceVariant.copy(
                                    alpha = 0.3f
                                )
                        ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = event.location.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        if (event.location.address.isNotBlank()) {
                            Text(
                                text = event.location.address,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        EventDetailRow(
                            icon = Icons.Default.DateRange,
                            label = "Date",
                            value =
                                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                    .format(event.date)
                        )

                        EventDetailRow(
                            icon = Icons.Default.Info,
                            label = "Time",
                            value =
                                SimpleDateFormat("HH:mm", Locale.getDefault())
                                    .format(event.checkInTime)
                        )

                        EventDetailRow(
                            icon = Icons.Default.Person,
                            label = "Capacity",
                            value = "${event.attendeesCount}/${event.capacity}"
                        )
                        if (event.contactNumber.isNotBlank()) {
                            EventDetailRow(
                                icon = Icons.Default.Phone,
                                label = "Contact",
                                value = event.contactNumber
                            )
                        }
                    }
                }
            }

            // Amenities and accessibility indicators
            if (event.amenities.isIndoor ||
                event.amenities.isOutdoor ||
                event.amenities.isChildFriendly ||
                event.amenities.isPetFriendly
            ) {

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (event.amenities.isIndoor) {
                        AmenityChip("Indoor")
                    }
                    if (event.amenities.isOutdoor) {
                        AmenityChip("Outdoor")
                    }
                    if (event.amenities.isChildFriendly) {
                        AmenityChip("Child Friendly")
                    }
                    if (event.amenities.isPetFriendly) {
                        AmenityChip("Pet Friendly")
                    }
                }
            }

            // Action buttons at bottom - only show for future events
            if (!isHosted && !isEventInPast) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onToggleInterest?.invoke() },
                        modifier = Modifier.weight(1f)
                    ) { Text("Uninterest") }

                    Button(
                        onClick = { onGetDirections?.invoke() ?: onCheckIn?.invoke() },
                        modifier = Modifier.weight(1f)
                    ) { Text(if (onGetDirections != null) "Directions" else "Check In") }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Event") },
            text = {
                Text(
                    "Are you sure you want to delete this event? This action cannot be undone."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteEvent?.invoke()
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun EventDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier =
                Modifier
                    .size(32.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        CircleShape
                    ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun AmenityChip(text: String, modifier: Modifier = Modifier) {
    AssistChip(
        onClick = {},
        label = { Text(text = text, style = MaterialTheme.typography.labelSmall) },
        modifier = modifier,
        colors =
            AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
    )
}

// Overloaded version that accepts drawable resource ID
@Composable
private fun EventDetailRow(
    iconResId: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier =
                Modifier
                    .size(20.dp)
                    .padding(end = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
