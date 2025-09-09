package com.example.mobilecomputingassignment.presentation.screens.explore.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobilecomputingassignment.domain.models.Event
import com.example.mobilecomputingassignment.presentation.ui.component.NearbyMatchCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyEventsBottomSheet(events: List<Event>, onEventClick: (Event) -> Unit) {
  Surface(modifier = Modifier.fillMaxWidth(), color = Color.White) {
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
          items(events) { event ->
            NearbyMatchCard(event = event, onClick = { onEventClick(event) })
          }
        }
      }
    }
  }
}
