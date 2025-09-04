package com.example.mobilecomputingassignment.presentation.screens.explore

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobilecomputingassignment.data.service.LocationService
import com.example.mobilecomputingassignment.presentation.screens.explore.components.EventDetailsDialog
import com.example.mobilecomputingassignment.presentation.screens.explore.components.NearbyEventsBottomSheet
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
        viewModel: ExploreViewModel = hiltViewModel(),
        onNavigateToEvent: (String) -> Unit
) {
  val uiState by viewModel.uiState.collectAsState()
  val context = LocalContext.current
  val locationService = remember { LocationService(context) }
  val scope = rememberCoroutineScope()
  val defaultLocation = LatLng(-37.7963, 144.9614) // Melbourne location
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(defaultLocation, 13f)
  }

  // Bottom sheet state for nearby events
  val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
    bottomSheetState = rememberStandardBottomSheetState(
      initialValue = SheetValue.PartiallyExpanded
    )
  )

  BottomSheetScaffold(
    scaffoldState = bottomSheetScaffoldState,
    sheetContent = {
      NearbyEventsBottomSheet(
        events = uiState.nearbyEvents,
        onEventClick = { event ->
          viewModel.onEventSelected(event)
          scope.launch {
            cameraPositionState.animate(
              update = CameraUpdateFactory.newLatLngZoom(
                LatLng(event.location.latitude, event.location.longitude),
                15f
              )
            )
          }
        }
      )
    },
    sheetPeekHeight = 120.dp,
    containerColor = MaterialTheme.colorScheme.background,
    contentColor = MaterialTheme.colorScheme.onBackground
  ) { paddingValues ->
    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
      GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = uiState.isLocationPermissionGranted),
        uiSettings = MapUiSettings(
          zoomControlsEnabled = false, 
          myLocationButtonEnabled = false, // We'll create our own
          mapToolbarEnabled = false
        ),
        onMapClick = { latLng -> viewModel.onMapClick(latLng) }
      ) {
        // Markers for events
        uiState.visibleEvents.forEach { event ->
          Marker(
            state = MarkerState(position = LatLng(event.location.latitude, event.location.longitude)),
            title = event.matchDetails?.let { "${it.homeTeam} vs ${it.awayTeam}" } ?: "Watch Along Event",
            snippet = event.location.name,
            onClick = {
              viewModel.onMarkerClick(event)
              true
            }
          )
        }
      }

      // Action buttons column
      Column(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(16.dp)
          .padding(bottom = 140.dp), // Account for bottom sheet peek height
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Refresh events button
        FloatingActionButton(
          onClick = {
            viewModel.refreshEvents()
          },
          containerColor = MaterialTheme.colorScheme.surface,
          contentColor = MaterialTheme.colorScheme.primary
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Refresh events"
          )
        }
        
        // Current location button
        FloatingActionButton(
          onClick = {
            scope.launch {
              val currentLocation = locationService.getCurrentLocation()
              currentLocation?.let { location ->
                val newLatLng = LatLng(location.latitude, location.longitude)
                cameraPositionState.animate(
                  update = CameraUpdateFactory.newLatLngZoom(newLatLng, 15f)
                )
              }
            }
          },
          containerColor = MaterialTheme.colorScheme.surface,
          contentColor = MaterialTheme.colorScheme.primary
        ) {
          Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Go to current location"
          )
        }
      }
    }
  }

  // Event details dialog
  uiState.selectedEvent?.let { selectedEvent ->
    EventDetailsDialog(
            event = selectedEvent,
            onDismiss = { viewModel.onEventDeselected() },
            onGetDirections = { event -> viewModel.openGoogleMapsDirections(event) },
            onAddToInterested = { event -> viewModel.addEventToInterested(event) }
    )
  }
}
