package com.example.mobilecomputingassignment.presentation.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mobilecomputingassignment.domain.models.Event
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCard(
        event: Event,
        isHosted: Boolean = false,
        onToggleInterest: (() -> Unit)? = null,
        onCheckIn: (() -> Unit)? = null,
        onEditEvent: (() -> Unit)? = null,
        onDeleteEvent: (() -> Unit)? = null,
        modifier: Modifier = Modifier
) {
  var showDeleteDialog by remember { mutableStateOf(false) }

  Card(
          modifier = modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors =
                  CardDefaults.cardColors(
                          containerColor = MaterialTheme.colorScheme.surface,
                          contentColor = MaterialTheme.colorScheme.onSurface
                  ),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header with title and actions
      Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
                  text = event.title,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis
          )

          if (event.description.isNotBlank()) {
            Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
            )
          }
        }

        // Action buttons
        Row {
          if (isHosted) {
            // Edit button
            IconButton(onClick = { onEditEvent?.invoke() }) {
              Icon(
                      imageVector = Icons.Default.Edit,
                      contentDescription = "Edit Event",
                      tint = MaterialTheme.colorScheme.primary
              )
            }

            // Delete button
            IconButton(onClick = { showDeleteDialog = true }) {
              Icon(
                      imageVector = Icons.Default.Delete,
                      contentDescription = "Delete Event",
                      tint = MaterialTheme.colorScheme.error
              )
            }
          } else {
            // Interest button
            IconButton(onClick = { onToggleInterest?.invoke() }) {
              Icon(
                      imageVector = Icons.Default.Favorite,
                      contentDescription = "Toggle Interest",
                      tint = MaterialTheme.colorScheme.primary
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Event details
      EventDetailRow(
              icon = Icons.Default.DateRange,
              label = "Date",
              value = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(event.date)
      )

      EventDetailRow(
              icon = Icons.Default.Info,
              label = "Check-in",
              value = SimpleDateFormat("HH:mm", Locale.getDefault()).format(event.checkInTime)
      )

      EventDetailRow(
              icon = Icons.Default.LocationOn,
              label = "Location",
              value = event.location.name
      )

      if (event.location.address.isNotBlank()) {
        EventDetailRow(
                icon = Icons.Default.Place,
                label = "Address",
                value = event.location.address
        )
      }

            EventDetailRow(
                icon = Icons.Default.Person,
                label = "Capacity",
                value = "${event.attendeesCount}/${event.capacity}"
            )

      if (event.contactNumber.isNotBlank()) {
        EventDetailRow(icon = Icons.Default.Phone, label = "Contact", value = event.contactNumber)
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

      // Action buttons at bottom
      if (!isHosted) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(onClick = { onToggleInterest?.invoke() }, modifier = Modifier.weight(1f)) {
            Text("Remove Interest")
          }

          Button(onClick = { onCheckIn?.invoke() }, modifier = Modifier.weight(1f)) {
            Text("Check In")
          }
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
              Text("Are you sure you want to delete this event? This action cannot be undone.")
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
          modifier = modifier.fillMaxWidth().padding(vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.width(8.dp))

    Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
