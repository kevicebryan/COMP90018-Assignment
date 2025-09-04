package com.example.mobilecomputingassignment.presentation.screens.explore.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.domain.models.Event
import com.example.mobilecomputingassignment.presentation.ui.components.TeamLogo
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyEventsBottomSheet(events: List<Event>, onEventClick: (Event) -> Unit) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surface
  ) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
      Text(
        text = "Matches Near You",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp),
        color = MaterialTheme.colorScheme.onSurface
      )

      if (events.isEmpty()) {
        // Empty state
        Column(
          modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "We can't find any nearby matches near you",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
          )
          Text(
            text = "Try zooming out on the map or check back later",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
          )
        }
      } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          items(events) { event -> EventCard(event = event, onClick = { onEventClick(event) }) }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCard(event: Event, onClick: () -> Unit) {
  Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
      // Teams
      Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
      ) {
        TeamLogo(teamName = event.matchDetails?.homeTeam ?: "TBD")
        Text(
                text = "VS",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
        )
        TeamLogo(teamName = event.matchDetails?.awayTeam ?: "TBD")
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Date and Time
      Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
                painter = painterResource(id = R.drawable.ic_calendar),
                contentDescription = "Date",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
                text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(event.date),
                style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
                painter = painterResource(id = R.drawable.ic_clock),
                contentDescription = "Time",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
                text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(event.checkInTime),
                style = MaterialTheme.typography.bodyMedium
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Venue
      Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
                painter = painterResource(id = R.drawable.ic_location),
                contentDescription = "Location",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column {
          Text(
                  text = event.location.name,
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Medium
          )
          Text(
                  text = event.location.address,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Accessibility indicator if needed
      if (event.accessibility.isWheelchairAccessible) {
        Spacer(modifier = Modifier.height(8.dp))
        Icon(
                painter = painterResource(id = R.drawable.ic_accessible),
                contentDescription = "Wheelchair accessible",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}
