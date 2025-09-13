package com.example.mobilecomputingassignment.data.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Singleton
class NetworkConnectivityService @Inject constructor(@ApplicationContext private val context: Context) {
  companion object {
    private const val TAG = "NetworkConnectivityService"
  }

  private val connectivityManager =
          context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

  /** Get current network connectivity status */
  fun isNetworkAvailable(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      val network = connectivityManager.activeNetwork
      val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
      networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
              networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    } else {
      @Suppress("DEPRECATION") val networkInfo = connectivityManager.activeNetworkInfo
      networkInfo?.isConnected == true
    }
  }

  /** Check if device has internet connectivity (not just network connection) */
  fun hasInternetConnectivity(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      val network = connectivityManager.activeNetwork
      val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
      networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
              networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    } else {
      @Suppress("DEPRECATION") val networkInfo = connectivityManager.activeNetworkInfo
      networkInfo?.isConnected == true && networkInfo.isAvailable
    }
  }

  /** Flow that emits network connectivity status changes */
  fun networkConnectivityFlow(): Flow<Boolean> =
          callbackFlow {
                    val callback =
                            object : ConnectivityManager.NetworkCallback() {
                              override fun onAvailable(network: Network) {
                                Log.d(TAG, "Network available")
                                trySend(true)
                              }

                              override fun onLost(network: Network) {
                                Log.d(TAG, "Network lost")
                                trySend(false)
                              }

                              override fun onCapabilitiesChanged(
                                      network: Network,
                                      networkCapabilities: NetworkCapabilities
                              ) {
                                val hasInternet =
                                        networkCapabilities.hasCapability(
                                                NetworkCapabilities.NET_CAPABILITY_INTERNET
                                        ) &&
                                                networkCapabilities.hasCapability(
                                                        NetworkCapabilities.NET_CAPABILITY_VALIDATED
                                                )
                                Log.d(
                                        TAG,
                                        "Network capabilities changed - has internet: $hasInternet"
                                )
                                trySend(hasInternet)
                              }
                            }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                      connectivityManager.registerDefaultNetworkCallback(callback)
                    } else {
                      @Suppress("DEPRECATION")
                      val request =
                              NetworkRequest.Builder()
                                      .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                                      .build()
                      connectivityManager.registerNetworkCallback(request, callback)
                    }

                    // Emit current status
                    trySend(hasInternetConnectivity())

                    awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
                  }
                  .distinctUntilChanged()

  /** Get network type information for debugging */
  fun getNetworkInfo(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      val network = connectivityManager.activeNetwork
      val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

      if (networkCapabilities == null) {
        "No network"
      } else {
        val hasInternet =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val hasValidated =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        val isWifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val isCellular = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        val isEthernet = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)

        buildString {
          append("Internet: $hasInternet, Validated: $hasValidated")
          append(", Wifi: $isWifi, Cellular: $isCellular, Ethernet: $isEthernet")
        }
      }
    } else {
      @Suppress("DEPRECATION") val networkInfo = connectivityManager.activeNetworkInfo
      if (networkInfo == null) {
        "No network"
      } else {
        "Type: ${networkInfo.typeName}, Connected: ${networkInfo.isConnected}, Available: ${networkInfo.isAvailable}"
      }
    }
  }
}
