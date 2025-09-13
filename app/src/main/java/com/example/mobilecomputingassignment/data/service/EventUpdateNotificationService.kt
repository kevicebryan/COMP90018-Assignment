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
class EventUpdateNotificationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val CHANNEL_ID = "event_update_notifications"
        private const val CHANNEL_NAME = "Event Updates"
        private const val CHANNEL_DESCRIPTION = "Notifications when events you're interested in get updated"
        
        // Notification IDs
        private const val EVENT_UPDATE_NOTIFICATION_ID = 5000
        
        // Request codes for pending intents
        private const val EVENT_UPDATE_REQUEST_CODE = 5001
    }
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun showEventUpdateNotification(event: Event, updatedFields: List<String>) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Pass event ID to navigate to specific event details
            putExtra("eventId", event.id)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            EVENT_UPDATE_REQUEST_CODE + event.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val dateFormatter = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
        
        // Create a summary of what was updated
        val updateSummary = when {
            updatedFields.size == 1 -> "Updated: ${updatedFields[0]}"
            updatedFields.size <= 3 -> "Updated: ${updatedFields.joinToString(", ")}"
            else -> "Updated: ${updatedFields.take(2).joinToString(", ")} and ${updatedFields.size - 2} more"
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Event Updated! 📝")
            .setContentText("${event.matchDetails?.homeTeam ?: "Team A"} vs ${event.matchDetails?.awayTeam ?: "Team B"} has been updated")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("📝 The event you're interested in has been updated!\n\n" +
                        "🏈 ${event.matchDetails?.homeTeam ?: "Team A"} vs ${event.matchDetails?.awayTeam ?: "Team B"}\n" +
                        "📅 ${dateFormatter.format(event.date)}\n" +
                        "⏰ ${timeFormatter.format(event.checkInTime)}\n" +
                        "📍 ${event.location.name}\n" +
                        "🏠 ${event.location.address}\n\n" +
                        "🔧 $updateSummary\n\n" +
                        "Tap to view the changes!")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 300, 150, 300))
            .build()
        
        notificationManager.notify(EVENT_UPDATE_NOTIFICATION_ID + event.id.hashCode(), notification)
    }
    
    fun cancelEventUpdateNotifications(eventId: String) {
        notificationManager.cancel(EVENT_UPDATE_NOTIFICATION_ID + eventId.hashCode())
    }
}
