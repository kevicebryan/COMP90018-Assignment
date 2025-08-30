package com.example.mobilecomputingassignment.data.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

class LocationService(private val context: Context) {

  private val fusedLocationClient: FusedLocationProviderClient =
          LocationServices.getFusedLocationProviderClient(context)

  suspend fun getCurrentLocation(): Location? {
    return suspendCancellableCoroutine { continuation ->
      // Check permissions
      if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                      PackageManager.PERMISSION_GRANTED &&
                      ActivityCompat.checkSelfPermission(
                              context,
                              Manifest.permission.ACCESS_COARSE_LOCATION
                      ) != PackageManager.PERMISSION_GRANTED
      ) {
        continuation.resume(null)
        return@suspendCancellableCoroutine
      }

      try {
        fusedLocationClient
                .getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        object : CancellationToken() {
                          override fun onCanceledRequested(listener: OnTokenCanceledListener) =
                                  CancellationTokenSource().token
                          override fun isCancellationRequested() = false
                        }
                )
                .addOnSuccessListener { location -> continuation.resume(location) }
                .addOnFailureListener { exception -> continuation.resumeWithException(exception) }
      } catch (e: Exception) {
        continuation.resumeWithException(e)
      }
    }
  }

  fun hasLocationPermission(): Boolean {
    return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
  }
}
