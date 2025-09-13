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

@Singleton
class EventNotificationService
@Inject
constructor(@ApplicationContext private val context: Context) {
  companion object {
    private const val CHANNEL_ID = "event_reminders"
    private const val CHANNEL_NAME = "Event Reminders"
    private const val CHANNEL_DESCRIPTION = "Notifications for events you're interested in"

    // Notification IDs
    private const val EVENT_DAY_NOTIFICATION_ID = 1000
    private const val EVENT_30MIN_NOTIFICATION_ID = 2000

    // Request codes for pending intents
    private const val EVENT_DAY_REQUEST_CODE = 1001
    private const val EVENT_30MIN_REQUEST_CODE = 2001
  }

  private val notificationManager =
          context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

  init {
    createNotificationChannel()
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel =
              NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
                      .apply {
                        description = CHANNEL_DESCRIPTION
                        enableVibration(true)
                        setShowBadge(true)
                      }
      notificationManager.createNotificationChannel(channel)
    }
  }

  fun showEventDayNotification(event: Event) {
    val intent =
            Intent(context, MainActivity::class.java).apply {
              flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

    val pendingIntent =
            PendingIntent.getActivity(
                    context,
                    EVENT_DAY_REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

    val dateFormatter = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())

    val notification =
            NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Event Today! 🏈")
                    .setContentText(
                            "${event.matchDetails?.homeTeam ?: "Team A"} vs ${event.matchDetails?.awayTeam ?: "Team B"} at ${event.location.name}"
                    )
                    .setStyle(
                            NotificationCompat.BigTextStyle()
                                    .bigText(
                                            "Your event is happening today at ${timeFormatter.format(event.checkInTime)}!\n\n" +
                                                    "📍 ${event.location.name}\n" +
                                                    "🏠 ${event.location.address}\n" +
                                                    "📞 ${if (event.contactNumber.isNotBlank()) event.contactNumber else "No contact provided"}"
                                    )
                    )
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setVibrate(longArrayOf(0, 500, 250, 500))
                    .build()

    notificationManager.notify(EVENT_DAY_NOTIFICATION_ID + event.id.hashCode(), notification)
  }

  fun showEvent30MinNotification(event: Event) {
    val intent =
            Intent(context, MainActivity::class.java).apply {
              flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

    val pendingIntent =
            PendingIntent.getActivity(
                    context,
                    EVENT_30MIN_REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

    val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())

    val notification =
            NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Event Starting Soon! ⏰")
                    .setContentText(
                            "${event.matchDetails?.homeTeam ?: "Team A"} vs ${event.matchDetails?.awayTeam ?: "Team B"} starts at ${timeFormatter.format(event.checkInTime)}"
                    )
                    .setStyle(
                            NotificationCompat.BigTextStyle()
                                    .bigText(
                                            "Your event starts in 30 minutes!\n\n" +
                                                    "🏈 ${event.matchDetails?.homeTeam ?: "Team A"} vs ${event.matchDetails?.awayTeam ?: "Team B"}\n" +
                                                    "📍 ${event.location.name}\n" +
                                                    "🏠 ${event.location.address}\n" +
                                                    "⏰ Check-in time: ${timeFormatter.format(event.checkInTime)}"
                                    )
                    )
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setVibrate(longArrayOf(0, 300, 200, 300))
                    .build()

    notificationManager.notify(EVENT_30MIN_NOTIFICATION_ID + event.id.hashCode(), notification)
  }

  fun cancelEventNotifications(eventId: String) {
    notificationManager.cancel(EVENT_DAY_NOTIFICATION_ID + eventId.hashCode())
    notificationManager.cancel(EVENT_30MIN_NOTIFICATION_ID + eventId.hashCode())
  }
}
