package com.example.mobilecomputingassignment.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobilecomputingassignment.domain.models.Event
import com.example.mobilecomputingassignment.presentation.ui.component.EventCard
import com.example.mobilecomputingassignment.presentation.ui.component.EventFormDialog
import com.example.mobilecomputingassignment.presentation.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(modifier: Modifier = Modifier, eventViewModel: EventViewModel = hiltViewModel()) {
  val uiState by eventViewModel.uiState.collectAsState()

  LaunchedEffect(Unit) { eventViewModel.refreshEvents() }

  Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
    // Header
    Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
              text = "Events",
              style = MaterialTheme.typography.headlineMedium,
//              fontWeight = FontWeight.Bold
      )

      // Add button (only show on hosted events tab)
      if (uiState.selectedTab == 1) {
        FloatingActionButton(
                onClick = { eventViewModel.startCreatingEvent() },
                modifier = Modifier.size(48.dp),
                containerColor = MaterialTheme.colorScheme.primary
        ) {
          Icon(
                  imageVector = Icons.Default.Add,
                  contentDescription = "Create Event",
                  tint = MaterialTheme.colorScheme.onPrimary
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Tab Row
    TabRow(
            selectedTabIndex = uiState.selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
    ) {
      Tab(
              selected = uiState.selectedTab == 0,
              onClick = { eventViewModel.setSelectedTab(0) },
              text = {
                Text(
                        "Interested",
                        color =
                                if (uiState.selectedTab == 0) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
      )
      Tab(
              selected = uiState.selectedTab == 1,
              onClick = { eventViewModel.setSelectedTab(1) },
              text = {
                Text(
                        "Hosted",
                        color =
                                if (uiState.selectedTab == 1) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Content based on selected tab
    when (uiState.selectedTab) {
      0 ->
              InterestedEventsContent(
                      events = uiState.interestedEvents,
                      isLoading = uiState.isLoading,
                      onToggleInterest = { eventId, isInterested ->
                        eventViewModel.toggleEventInterest(eventId, isInterested)
                      },
                      onCheckIn = { eventId -> eventViewModel.checkInToEvent(eventId) }
              )
      1 ->
              HostedEventsContent(
                      events = uiState.hostedEvents,
                      isLoading = uiState.isLoading,
                      onEditEvent = { event -> eventViewModel.startEditingEvent(event) },
                      onDeleteEvent = { eventId -> eventViewModel.deleteEvent(eventId) }
              )
    }

    // Error message
    uiState.errorMessage?.let { error ->
      Card(
              modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
              colors =
                      CardDefaults.cardColors(
                              containerColor = MaterialTheme.colorScheme.errorContainer
                      )
      ) {
        Text(
                text = error,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onErrorContainer
        )
      }
    }
  }

  // Show event form if creating or editing
  if (uiState.isCreatingEvent || uiState.isEditingEvent) {
    EventFormDialog(
            isEditing = uiState.isEditingEvent,
            onDismiss = { eventViewModel.cancelEventForm() },
            onSave = {
              if (uiState.isEditingEvent) {
                eventViewModel.updateEvent()
              } else {
                eventViewModel.createEvent()
              }
            },
            eventViewModel = eventViewModel
    )
  }
}

@Composable
private fun InterestedEventsContent(
        events: List<Event>,
        isLoading: Boolean,
        onToggleInterest: (String, Boolean) -> Unit,
        onCheckIn: (String) -> Unit
) {
  if (isLoading) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
  } else if (events.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
                text = "No interested events",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
                text = "Explore events to find ones you're interested in!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  } else {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(events) { event ->
        EventCard(
                event = event,
                isHosted = false,
                onToggleInterest = { onToggleInterest(event.id, true) },
                onCheckIn = { onCheckIn(event.id) }
        )
      }
    }
  }
}

@Composable
private fun HostedEventsContent(
        events: List<Event>,
        isLoading: Boolean,
        onEditEvent: (Event) -> Unit,
        onDeleteEvent: (String) -> Unit
) {
  if (isLoading) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
  } else if (events.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
                text = "No hosted events",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
                text = "Create your first event using the + button!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  } else {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(events) { event ->
        EventCard(
                event = event,
                isHosted = true,
                onEditEvent = { onEditEvent(event) },
                onDeleteEvent = { onDeleteEvent(event.id) }
        )
      }
    }
  }
}
