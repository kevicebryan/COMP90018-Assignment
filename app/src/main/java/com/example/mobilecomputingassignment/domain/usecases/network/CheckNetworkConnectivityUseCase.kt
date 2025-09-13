package com.example.mobilecomputingassignment.domain.usecases.network

import com.example.mobilecomputingassignment.data.service.NetworkConnectivityService
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class CheckNetworkConnectivityUseCase
@Inject
constructor(private val networkConnectivityService: NetworkConnectivityService) {

  fun isNetworkAvailable(): Boolean {
    return networkConnectivityService.isNetworkAvailable()
  }

  fun hasInternetConnectivity(): Boolean {
    return networkConnectivityService.hasInternetConnectivity()
  }

  fun networkConnectivityFlow(): Flow<Boolean> {
    return networkConnectivityService.networkConnectivityFlow()
  }

  fun getNetworkInfo(): String {
    return networkConnectivityService.getNetworkInfo()
  }

  fun getConnectivityErrorMessage(): String {
    return when {
      !networkConnectivityService.isNetworkAvailable() ->
              "No network connection available. Please check your internet connection."
      !networkConnectivityService.hasInternetConnectivity() ->
              "Unable to connect to servers. Please check your internet connection."
      else -> "Connection issue. Please try again."
    }
  }
}
