package com.example.mobilecomputingassignment.presentation.screens.explore.components

import androidx.compose.foundation.layout.*
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
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.domain.models.Event
import com.example.mobilecomputingassignment.presentation.ui.component.TeamLogo
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventDetailsDialog(
        event: Event,
        currentUserId: String?,
        onDismiss: () -> Unit,
        onGetDirections: (Event) -> Unit,
        onToggleInterest: (Event) -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
      Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        // Header with close button
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
                  text = "Match Details",
                  style = MaterialTheme.typography.titleLarge,
                  fontWeight = FontWeight.Bold
          )
          IconButton(onClick = onDismiss) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Teams
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
          TeamLogo(teamName = event.matchDetails?.homeTeam ?: "TBD")
          Text(
                  text = "VS",
                  style = MaterialTheme.typography.titleLarge,
                  color = MaterialTheme.colorScheme.primary
          )
          TeamLogo(teamName = event.matchDetails?.awayTeam ?: "TBD")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Date and Time
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
          Icon(
                  painter = painterResource(id = R.drawable.ic_calendar),
                  contentDescription = "Date",
                  tint = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
                  text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(event.date),
                  style = MaterialTheme.typography.bodyLarge
          )
          Spacer(modifier = Modifier.width(16.dp))
          Icon(
                  painter = painterResource(id = R.drawable.ic_clock),
                  contentDescription = "Time",
                  tint = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
                  text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(event.checkInTime),
                  style = MaterialTheme.typography.bodyLarge
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Venue
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
          Icon(
                  painter = painterResource(id = R.drawable.ic_location),
                  contentDescription = "Location",
                  tint = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
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
          }
        }

        if (event.accessibility.isWheelchairAccessible) {
          Spacer(modifier = Modifier.height(8.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                    painter = painterResource(id = R.drawable.ic_accessible),
                    contentDescription = "Wheelchair accessible",
                    tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Wheelchair accessible", style = MaterialTheme.typography.bodyMedium)
          }
        }

        // Contact
        if (event.contactNumber.isNotBlank()) {
          Spacer(modifier = Modifier.height(16.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                    painter = painterResource(id = R.drawable.ic_phone),
                    contentDescription = "Contact",
                    tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = event.contactNumber, style = MaterialTheme.typography.bodyLarge)
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action buttons
        val isInterested = currentUserId?.let { event.interestedUsers.contains(it) } ?: false
        val canInterest = currentUserId != null && currentUserId != event.hostUserId

        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(onClick = { onGetDirections(event) }, modifier = Modifier.weight(1f)) {
            Icon(
                    painter = painterResource(id = R.drawable.ic_direction),
                    contentDescription = "Get directions"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Directions")
          }
          Button(
                  onClick = { if (canInterest) onToggleInterest(event) },
                  modifier = Modifier.weight(1f),
                  enabled = canInterest,
                  colors =
                          ButtonDefaults.buttonColors(
                                  containerColor =
                                          if (isInterested) {
                                            MaterialTheme.colorScheme.primary
                                          } else {
                                            MaterialTheme.colorScheme.surface
                                          },
                                  contentColor =
                                          if (isInterested) {
                                            MaterialTheme.colorScheme.onPrimary
                                          } else {
                                            MaterialTheme.colorScheme.primary
                                          }
                          )
          ) {
            Icon(
                    painter =
                            painterResource(
                                    id =
                                            if (isInterested) R.drawable.ic_like_filled
                                            else R.drawable.ic_like
                            ),
                    contentDescription =
                            if (isInterested) "Uninterest" else "Add to interested"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isInterested) "Interested" else "Interest")
          }
        }
      }
    }
  }
}
