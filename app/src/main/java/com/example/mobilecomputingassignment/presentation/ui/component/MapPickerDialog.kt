package com.example.mobilecomputingassignment.presentation.ui.component

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.mobilecomputingassignment.data.service.LocationService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPickerDialog(
        onDismiss: () -> Unit,
        onLocationSelected: (LatLng, String) -> Unit,
        initialLatLng: LatLng? = null
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val locationService = remember { LocationService(context) }

  // Default to Melbourne, Australia if no initial location
  val defaultLocation = LatLng(-37.7963, 144.9614)
  var selectedLocation by remember { mutableStateOf(initialLatLng ?: defaultLocation) }
  var address by remember { mutableStateOf("") }
  var isLoadingAddress by remember { mutableStateOf(false) }
  var hasLocationPermission by remember { mutableStateOf(locationService.hasLocationPermission()) }
  var isInitialized by remember { mutableStateOf(false) }

  // Camera position state
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(selectedLocation, 15f)
  }

  // Permission launcher
  val permissionLauncher =
          rememberLauncherForActivityResult(
                  contract = ActivityResultContracts.RequestMultiplePermissions()
          ) { permissions ->
            hasLocationPermission = permissions.values.all { it }
            if (hasLocationPermission) {
              // Get current location when permission is granted
              scope.launch {
                val currentLocation = locationService.getCurrentLocation()
                currentLocation?.let { location ->
                  val newLatLng = LatLng(location.latitude, location.longitude)
                  selectedLocation = newLatLng
                  cameraPositionState.animate(
                          update = CameraUpdateFactory.newLatLngZoom(newLatLng, 15f)
                  )
                  // Get address for current location
                  isLoadingAddress = true
                  address = getAddressFromLatLng(context, newLatLng)
                  isLoadingAddress = false
                }
              }
            }
          }

  // Auto-get current location when dialog opens (only if no initial location provided)
  LaunchedEffect(Unit) {
    if (initialLatLng == null && !isInitialized) {
      isInitialized = true
      if (hasLocationPermission) {
        val currentLocation = locationService.getCurrentLocation()
        currentLocation?.let { location ->
          val newLatLng = LatLng(location.latitude, location.longitude)
          selectedLocation = newLatLng
          cameraPositionState.animate(
                  update = CameraUpdateFactory.newLatLngZoom(newLatLng, 15f)
          )
          // Get address for current location
          isLoadingAddress = true
          address = getAddressFromLatLng(context, newLatLng)
          isLoadingAddress = false
        }
      } else {
        // Request permission for location
        permissionLauncher.launch(
                arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
        )
      }
    }
  }

  // Map properties
  val mapProperties by remember {
    mutableStateOf(
            MapProperties(isMyLocationEnabled = hasLocationPermission, mapType = MapType.NORMAL)
    )
  }

  // UI properties
  val uiSettings by remember {
    mutableStateOf(
            MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = hasLocationPermission,
                    mapToolbarEnabled = false
            )
    )
  }

  Dialog(
          onDismissRequest = onDismiss,
          properties =
                  DialogProperties(
                          dismissOnBackPress = true,
                          dismissOnClickOutside = false,
                          usePlatformDefaultWidth = false
                  )
  ) {
    Card(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
                  text = "Select Location",
                  style = MaterialTheme.typography.headlineSmall,
                  fontWeight = FontWeight.Bold
          )

          Row {
            // Current location button
            IconButton(
                    onClick = {
                      if (hasLocationPermission) {
                        scope.launch {
                          val currentLocation = locationService.getCurrentLocation()
                          currentLocation?.let { location ->
                            val newLatLng = LatLng(location.latitude, location.longitude)
                            selectedLocation = newLatLng
                            cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngZoom(newLatLng, 15f)
                            )
                            isLoadingAddress = true
                            address = getAddressFromLatLng(context, newLatLng)
                            isLoadingAddress = false
                          }
                        }
                      } else {
                        permissionLauncher.launch(
                                arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                        )
                      }
                    }
            ) {
              Icon(
                      imageVector = Icons.Default.LocationOn,
                      contentDescription = "Use current location",
                      tint =
                              if (hasLocationPermission) MaterialTheme.colorScheme.primary
                              else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            IconButton(onClick = onDismiss) {
              Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
          }
        }

        Divider()

        // Map
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
          GoogleMap(
                  modifier = Modifier.fillMaxSize(),
                  cameraPositionState = cameraPositionState,
                  properties = mapProperties,
                  uiSettings = uiSettings,
                  onMapClick = { latLng ->
                    selectedLocation = latLng
                    scope.launch {
                      isLoadingAddress = true
                      address = getAddressFromLatLng(context, latLng)
                      isLoadingAddress = false
                    }
                  }
          ) {
            // Marker for selected location
            Marker(
                    state = MarkerState(position = selectedLocation),
                    title = "Selected Location",
                    snippet = address.ifEmpty { "Loading address..." }
            )
          }

          // Center indicator
          /* Box(
                  modifier = Modifier.fillMaxSize().padding(16.dp),
                  contentAlignment = Alignment.Center
          ) {
            Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Center",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
            )
          } */
        }

        // Address display
        Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors =
                        CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
                    text = "Selected Address:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoadingAddress) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Loading address...")
              }
            } else {
              Text(
                      text = address.ifEmpty { "Tap on map to select location" },
                      style = MaterialTheme.typography.bodyMedium
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                    text =
                            "Coordinates: ${selectedLocation.latitude}, ${selectedLocation.longitude}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Action buttons
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }

          Button(
                  onClick = {
                    onLocationSelected(selectedLocation, address)
                    onDismiss()
                  },
                  modifier = Modifier.weight(1f),
                  enabled = address.isNotEmpty()
          ) { Text("Select Location") }
        }
      }
    }
  }
}

// Reverse geocoding function using Google Geocoding API
private suspend fun getAddressFromLatLng(context: android.content.Context, latLng: LatLng): String {
  return try {
    val geocoder = android.location.Geocoder(context)
    val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

    if (!addresses.isNullOrEmpty()) {
      val address = addresses[0]
      val addressParts = mutableListOf<String>()

      // Build address string
      address.thoroughfare?.let { addressParts.add(it) }
      address.subThoroughfare?.let { addressParts.add(it) }
      address.locality?.let { addressParts.add(it) }
      address.adminArea?.let { addressParts.add(it) }
      address.postalCode?.let { addressParts.add(it) }

      addressParts.joinToString(", ")
    } else {
      "Unknown location"
    }
  } catch (e: Exception) {
    "Error getting address"
  }
}
