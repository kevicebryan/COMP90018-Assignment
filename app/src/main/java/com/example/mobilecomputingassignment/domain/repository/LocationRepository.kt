package com.example.mobilecomputingassignment.domain.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
  suspend fun getCurrentLocation(): Location?
  fun hasLocationPermission(): Boolean
  fun getLocationUpdates(): Flow<Location>
  suspend fun requestLocationPermission()
}
