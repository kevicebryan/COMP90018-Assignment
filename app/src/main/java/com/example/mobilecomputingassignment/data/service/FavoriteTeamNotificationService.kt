package com.example.mobilecomputingassignment.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.mobilecomputingassignment.MainActivity
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.domain.models.Event
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

@Singleton
class FavoriteTeamNotificationService
@Inject
constructor(@ApplicationContext private val context: Context) {
  companion object {
    private const val CHANNEL_ID = "favorite_team_notifications"
    private const val CHANNEL_NAME = "Favorite Team Alerts"
    private const val CHANNEL_DESCRIPTION =
            "Notifications when your favorite teams have events nearby"

    // Notification IDs
    private const val FAVORITE_TEAM_NEARBY_ID = 3000
    private const val FAVORITE_TEAM_PROXIMITY_ID = 4000

    // Request codes for pending intents
    private const val FAVORITE_TEAM_NEARBY_REQUEST_CODE = 3001
    private const val FAVORITE_TEAM_PROXIMITY_REQUEST_CODE = 4001

    private const val NEARBY_DISTANCE_KM = 5.0 // 5km radius
    private const val PROXIMITY_DISTANCE_M = 100.0 // 100 meters for proximity alerts
  }

  private val notificationManager =
          context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

  init {
    createNotificationChannel()
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel =
              NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
                      .apply {
                        description = CHANNEL_DESCRIPTION
                        enableVibration(true)
                        setShowBadge(true)
                      }
      notificationManager.createNotificationChannel(channel)
    }
  }

  fun showFavoriteTeamNearbyNotification(
          event: Event,
          userLatitude: Double,
          userLongitude: Double,
          distanceKm: Double
  ) {
    val intent =
            Intent(context, MainActivity::class.java).apply {
              flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

    val pendingIntent =
            PendingIntent.getActivity(
                    context,
                    FAVORITE_TEAM_NEARBY_REQUEST_CODE + event.id.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

    val dateFormatter = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())

    val notification =
            NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Your Team is Playing Nearby! 🏈")
                    .setContentText(
                            "${event.matchDetails?.homeTeam ?: "Team A"} vs ${event.matchDetails?.awayTeam ?: "Team B"} is happening nearby"
                    )
                    .setStyle(
                            NotificationCompat.BigTextStyle()
                                    .bigText(
                                            "🎯 Your favorite team has an event nearby!\n\n" +
                                                    "🏈 ${event.matchDetails?.homeTeam ?: "Team A"} vs ${event.matchDetails?.awayTeam ?: "Team B"}\n" +
                                                    "📅 ${dateFormatter.format(event.date)}\n" +
                                                    "⏰ ${timeFormatter.format(event.checkInTime)}\n" +
                                                    "📍 ${event.location.name}\n" +
                                                    "🏠 ${event.location.address}\n" +
                                                    "📏 ${String.format("%.1f", distanceKm)} km away"
                                    )
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setVibrate(longArrayOf(0, 400, 200, 400))
                    .build()

    notificationManager.notify(FAVORITE_TEAM_NEARBY_ID + event.id.hashCode(), notification)
  }

  fun showFavoriteTeamProximityNotification(
          event: Event,
          userLatitude: Double,
          userLongitude: Double,
          distanceM: Double
  ) {
    val intent =
            Intent(context, MainActivity::class.java).apply {
              flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

    val pendingIntent =
            PendingIntent.getActivity(
                    context,
                    FAVORITE_TEAM_PROXIMITY_REQUEST_CODE + event.id.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

    val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())

    val notification =
            NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("You're Near Your Team's Event! 🎯")
                    .setContentText(
                            "${event.matchDetails?.homeTeam ?: "Team A"} vs ${event.matchDetails?.awayTeam ?: "Team B"} is right here!"
                    )
                    .setStyle(
                            NotificationCompat.BigTextStyle()
                                    .bigText(
                                            "🚶‍♂️ You're walking past your favorite team's event!\n\n" +
                                                    "🏈 ${event.matchDetails?.homeTeam ?: "Team A"} vs ${event.matchDetails?.awayTeam ?: "Team B"}\n" +
                                                    "⏰ Starts at ${timeFormatter.format(event.checkInTime)}\n" +
                                                    "📍 ${event.location.name}\n" +
                                                    "🏠 ${event.location.address}\n" +
                                                    "📏 ${String.format("%.0f", distanceM)}m away - you're right here!"
                                    )
                    )
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setVibrate(longArrayOf(0, 200, 100, 200, 100, 200))
                    .build()

    notificationManager.notify(FAVORITE_TEAM_PROXIMITY_ID + event.id.hashCode(), notification)
  }

  fun cancelFavoriteTeamNotifications(eventId: String) {
    notificationManager.cancel(FAVORITE_TEAM_NEARBY_ID + eventId.hashCode())
    notificationManager.cancel(FAVORITE_TEAM_PROXIMITY_ID + eventId.hashCode())
  }

  /** Calculate distance between two points using Haversine formula */
  fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371e3 // Earth's radius in meters
    val φ1 = lat1 * PI / 180
    val φ2 = lat2 * PI / 180
    val Δφ = (lat2 - lat1) * PI / 180
    val Δλ = (lon2 - lon1) * PI / 180

    val a = sin(Δφ / 2) * sin(Δφ / 2) + cos(φ1) * cos(φ2) * sin(Δλ / 2) * sin(Δλ / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return r * c // Distance in meters
  }

  /** Check if an event features any of the user's favorite teams */
  fun eventFeaturesFavoriteTeam(event: Event, favoriteTeams: List<String>): Boolean {
    val homeTeam = event.matchDetails?.homeTeam
    val awayTeam = event.matchDetails?.awayTeam

    return favoriteTeams.any { favoriteTeam ->
      favoriteTeam.equals(homeTeam, ignoreCase = true) ||
              favoriteTeam.equals(awayTeam, ignoreCase = true)
    }
  }

  /** Check if event is within nearby distance (5km) */
  fun isEventNearby(
          event: Event,
          userLatitude: Double,
          userLongitude: Double
  ): Pair<Boolean, Double> {
    val distanceM =
            calculateDistance(
                    userLatitude,
                    userLongitude,
                    event.location.latitude,
                    event.location.longitude
            )
    val distanceKm = distanceM / 1000.0
    return Pair(distanceKm <= NEARBY_DISTANCE_KM, distanceKm)
  }

  /** Check if event is within proximity distance (100m) */
  fun isEventInProximity(
          event: Event,
          userLatitude: Double,
          userLongitude: Double
  ): Pair<Boolean, Double> {
    val distanceM =
            calculateDistance(
                    userLatitude,
                    userLongitude,
                    event.location.latitude,
                    event.location.longitude
            )
    return Pair(distanceM <= PROXIMITY_DISTANCE_M, distanceM)
  }
}
