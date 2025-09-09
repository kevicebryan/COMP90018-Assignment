package com.example.mobilecomputingassignment.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.mobilecomputingassignment.domain.repository.LocationRepository
import com.google.android.gms.location.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine

class DefaultLocationRepository
@Inject
constructor(
        @ApplicationContext private val context: Context,
        private val fusedLocationClient: FusedLocationProviderClient
) : LocationRepository {

  override suspend fun getCurrentLocation(): Location? {
    if (!hasLocationPermission()) {
      return null
    }

    return suspendCancellableCoroutine { continuation ->
      try {
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location -> continuation.resume(location) }
                .addOnFailureListener { continuation.resume(null) }
      } catch (e: SecurityException) {
        continuation.resume(null)
      }
    }
  }

  override fun hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
  }

  override fun getLocationUpdates(): Flow<Location> = callbackFlow {
    if (!hasLocationPermission()) {
      close()
      return@callbackFlow
    }

    val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                    .setMinUpdateDistanceMeters(50f)
                    .build()

    val locationCallback =
            object : LocationCallback() {
              override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location -> trySend(location) }
              }
            }

    try {
      fusedLocationClient.requestLocationUpdates(
              locationRequest,
              locationCallback,
              Looper.getMainLooper()
      )
    } catch (e: SecurityException) {
      close()
      return@callbackFlow
    }

    awaitClose { fusedLocationClient.removeLocationUpdates(locationCallback) }
  }

  override suspend fun requestLocationPermission() {
    // This will be handled by the activity/fragment using the permissions API
    // Implementation depends on your UI layer's permission handling
  }
}
