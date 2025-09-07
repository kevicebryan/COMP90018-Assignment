package com.example.mobilecomputingassignment.presentation.screens.explore

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobilecomputingassignment.R
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

  // Location permission state
  var hasLocationPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
      ) == PackageManager.PERMISSION_GRANTED
    )
  }

  // Permission launcher
  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                           permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    
    if (hasLocationPermission) {
      // Update the viewModel with permission status
      viewModel.onLocationPermissionGranted()
      
      // Get current location and move camera
      scope.launch {
        val currentLocation = locationService.getCurrentLocation()
        currentLocation?.let { location ->
          val newLatLng = LatLng(location.latitude, location.longitude)
          cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(newLatLng, 15f)
          )
        }
      }
    }
  }

  // Request permission on first launch
  LaunchedEffect(Unit) {
    if (!hasLocationPermission) {
      permissionLauncher.launch(
        arrayOf(
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION
        )
      )
    } else {
      viewModel.onLocationPermissionGranted()
    }
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
            // Animate camera to event location
            cameraPositionState.animate(
              update = CameraUpdateFactory.newLatLngZoom(
                LatLng(event.location.latitude, event.location.longitude),
                15f
              )
            )
            // Collapse the bottom sheet to show more of the map
            bottomSheetScaffoldState.bottomSheetState.partialExpand()
          }
        }
      )
    },
    sheetPeekHeight = 120.dp,
    sheetContainerColor = Color.White,
    containerColor = MaterialTheme.colorScheme.background,
    contentColor = MaterialTheme.colorScheme.onBackground
  ) { paddingValues ->
    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
        uiSettings = MapUiSettings(
          zoomControlsEnabled = false, 
          myLocationButtonEnabled = false, // We'll create our own
          mapToolbarEnabled = false
        ),
        onMapClick = { latLng -> viewModel.onMapClick(latLng) }
        ) {
        // Debug: Log the number of visible events
        LaunchedEffect(uiState.visibleEvents.size) {
          println("DEBUG: ${uiState.visibleEvents.size} visible events on map")
          uiState.visibleEvents.forEach { event ->
            println("DEBUG: Event at ${event.location.latitude}, ${event.location.longitude} - ${event.matchDetails?.homeTeam} vs ${event.matchDetails?.awayTeam}")
          }
        }

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

        // Also show all events as markers (for debugging)
        uiState.allEvents.forEach { event ->
          Marker(
            state = MarkerState(position = LatLng(event.location.latitude, event.location.longitude)),
            title = event.matchDetails?.let { "${it.homeTeam} vs ${it.awayTeam}" } ?: "Watch Along Event",
            snippet = "${event.location.name} (All Events)",
            onClick = {
              viewModel.onMarkerClick(event)
              true
            }
          )
        }
      }

      // Header overlay (top)
      ExploreHeader(
        onFilterClick = { 
          // TODO: Implement filter functionality
        },
        modifier = Modifier.align(Alignment.TopCenter)
      )


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

@Composable
private fun ExploreHeader(
  onFilterClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = Color.White,
    shadowElevation = 4.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Explore",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface
      )
      
      IconButton(
        onClick = onFilterClick,
        modifier = Modifier
          .shadow(2.dp, CircleShape)
      ) {
        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.surface,
          modifier = Modifier.size(40.dp)
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
          ) {
            Icon(
              painter = painterResource(id = R.drawable.ic_filter),
              contentDescription = "Filter",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }
  }
}
