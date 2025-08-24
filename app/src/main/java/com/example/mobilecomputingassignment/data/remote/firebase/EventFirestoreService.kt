package com.example.mobilecomputingassignment.data.remote.firebase

import android.util.Log
import com.example.mobilecomputingassignment.data.models.EventDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class EventFirestoreService @Inject constructor(private val firestore: FirebaseFirestore) {
  companion object {
    private const val EVENTS_COLLECTION = "events"
    private const val TAG = "EventFirestoreService"
  }

  suspend fun createEvent(event: EventDto): Result<String> {
    return try {
      val docRef = firestore.collection(EVENTS_COLLECTION).document()
      val eventWithId = event.copy(id = docRef.id)
      docRef.set(eventWithId).await()
      Log.d(TAG, "Event created successfully with ID: ${docRef.id}")
      Result.success(docRef.id)
    } catch (e: Exception) {
      Log.e(TAG, "Error creating event", e)
      Result.failure(e)
    }
  }

  suspend fun updateEvent(event: EventDto): Result<Unit> {
    return try {
      firestore.collection(EVENTS_COLLECTION).document(event.id).set(event).await()
      Log.d(TAG, "Event updated successfully: ${event.id}")
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Error updating event", e)
      Result.failure(e)
    }
  }

  suspend fun deleteEvent(eventId: String): Result<Unit> {
    return try {
      firestore.collection(EVENTS_COLLECTION).document(eventId).update("isActive", false).await()
      Log.d(TAG, "Event deleted successfully: $eventId")
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Error deleting event", e)
      Result.failure(e)
    }
  }

  suspend fun getEventById(eventId: String): Result<EventDto?> {
    return try {
      val document = firestore.collection(EVENTS_COLLECTION).document(eventId).get().await()

      val event =
              if (document.exists()) {
                document.toObject(EventDto::class.java)
              } else {
                null
              }
      Result.success(event)
    } catch (e: Exception) {
      Log.e(TAG, "Error getting event by ID", e)
      Result.failure(e)
    }
  }

  suspend fun getEventsByHost(hostUserId: String): Result<List<EventDto>> {
    return try {
      val querySnapshot =
              firestore
                      .collection(EVENTS_COLLECTION)
                      .whereEqualTo("hostUserId", hostUserId)
                      .whereEqualTo("isActive", true)
                      .orderBy("date", Query.Direction.ASCENDING)
                      .get()
                      .await()

      val events =
              querySnapshot.documents.mapNotNull { document ->
                document.toObject(EventDto::class.java)
              }
      Log.d(TAG, "Retrieved ${events.size} hosted events for user: $hostUserId")
      Result.success(events)
    } catch (e: Exception) {
      Log.e(TAG, "Error getting events by host", e)
      Result.failure(e)
    }
  }

  suspend fun getInterestedEvents(userId: String): Result<List<EventDto>> {
    return try {
      val querySnapshot =
              firestore
                      .collection(EVENTS_COLLECTION)
                      .whereArrayContains("interestedUsers", userId)
                      .whereEqualTo("isActive", true)
                      .orderBy("date", Query.Direction.ASCENDING)
                      .get()
                      .await()

      val events =
              querySnapshot.documents.mapNotNull { document ->
                document.toObject(EventDto::class.java)
              }
      Log.d(TAG, "Retrieved ${events.size} interested events for user: $userId")
      Result.success(events)
    } catch (e: Exception) {
      Log.e(TAG, "Error getting interested events", e)
      Result.failure(e)
    }
  }

  suspend fun getAllActiveEvents(): Result<List<EventDto>> {
    return try {
      val querySnapshot =
              firestore
                      .collection(EVENTS_COLLECTION)
                      .whereEqualTo("isActive", true)
                      .orderBy("date", Query.Direction.ASCENDING)
                      .get()
                      .await()

      val events =
              querySnapshot.documents.mapNotNull { document ->
                document.toObject(EventDto::class.java)
              }
      Log.d(TAG, "Retrieved ${events.size} active events")
      Result.success(events)
    } catch (e: Exception) {
      Log.e(TAG, "Error getting all active events", e)
      Result.failure(e)
    }
  }

  suspend fun addUserInterest(eventId: String, userId: String): Result<Unit> {
    return try {
      firestore
              .collection(EVENTS_COLLECTION)
              .document(eventId)
              .update(
                      "interestedUsers",
                      com.google.firebase.firestore.FieldValue.arrayUnion(userId)
              )
              .await()
      Log.d(TAG, "User $userId added interest to event: $eventId")
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Error adding user interest", e)
      Result.failure(e)
    }
  }

  suspend fun removeUserInterest(eventId: String, userId: String): Result<Unit> {
    return try {
      firestore
              .collection(EVENTS_COLLECTION)
              .document(eventId)
              .update(
                      "interestedUsers",
                      com.google.firebase.firestore.FieldValue.arrayRemove(userId)
              )
              .await()
      Log.d(TAG, "User $userId removed interest from event: $eventId")
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Error removing user interest", e)
      Result.failure(e)
    }
  }

  suspend fun addAttendee(eventId: String, userId: String): Result<Unit> {
    return try {
      firestore
              .collection(EVENTS_COLLECTION)
              .document(eventId)
              .update("attendees", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
              .await()
      Log.d(TAG, "User $userId added as attendee to event: $eventId")
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Error adding attendee", e)
      Result.failure(e)
    }
  }

  suspend fun removeAttendee(eventId: String, userId: String): Result<Unit> {
    return try {
      firestore
              .collection(EVENTS_COLLECTION)
              .document(eventId)
              .update("attendees", com.google.firebase.firestore.FieldValue.arrayRemove(userId))
              .await()
      Log.d(TAG, "User $userId removed as attendee from event: $eventId")
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Error removing attendee", e)
      Result.failure(e)
    }
  }
}
