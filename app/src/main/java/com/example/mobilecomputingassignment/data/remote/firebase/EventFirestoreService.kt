package com.example.mobilecomputingassignment.data.remote.firebase

import android.util.Log
import com.example.mobilecomputingassignment.data.models.EventDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

/**
 * EventFirestoreService - Firebase Firestore Operations
 *
 * This service handles all direct interactions with Firebase Firestore database. It provides CRUD
 * (Create, Read, Update, Delete) operations for events.
 *
 * Key Concepts:
 * - Firestore: Google's NoSQL cloud database
 * - Collections: Like tables in SQL, but for documents
 * - Documents: Individual records stored as JSON-like objects
 * - Queries: Ways to filter and retrieve documents
 *
 * Architecture:
 * - This is the Data Layer (closest to external services)
 * - Uses DTOs (EventDto) for data transfer
 * - Returns Result<T> for error handling
 * - Uses Kotlin Coroutines for async operations
 */
@Singleton
class EventFirestoreService @Inject constructor(private val firestore: FirebaseFirestore) {
  companion object {
    // Firestore collection name (like a table name in SQL)
    private const val EVENTS_COLLECTION = "events"

    // Log tag for debugging
    private const val TAG = "EventFirestoreService"
  }

  /**
   * CREATE OPERATION
   *
   * Creates a new event document in Firestore.
   *
   * Process:
   * 1. Generate a new document ID
   * 2. Add the ID to the event data
   * 3. Save the document to Firestore
   * 4. Return the document ID
   */
  suspend fun createEvent(event: EventDto): Result<String> {
    return try {
      // Generate a new document ID (Firestore auto-generates this)
      val docRef = firestore.collection(EVENTS_COLLECTION).document()

      // Add the generated ID to the event data
      val eventWithId = event.copy(id = docRef.id)

      // Log the event data before saving
      Log.d(TAG, "Creating event with data: hostUserId=${eventWithId.hostUserId}, hostUsername=${eventWithId.hostUsername}, isActive=${eventWithId.isActive}")

      // Save the document to Firestore (await() waits for completion)
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

  /**
   * READ OPERATION - Get Events by Host
   *
   * Retrieves all events hosted by a specific user.
   *
   * Firestore Query:
   * - whereEqualTo: Filters documents where field equals value
   * - orderBy: Sorts results by specified field
   * - get(): Executes the query
   * - await(): Waits for async operation to complete
   */
  suspend fun getEventsByHost(hostUserId: String): Result<List<EventDto>> {
    return try {
      // Build the query: find events where hostUserId matches AND isActive is true
      val querySnapshot =
              firestore
                      .collection(EVENTS_COLLECTION)
                      .whereEqualTo("hostUserId", hostUserId) // Filter by host
                      .whereEqualTo("isActive", true) // Only active events
                      .orderBy("date", Query.Direction.ASCENDING) // Sort by date
                      .get() // Execute query
                      .await() // Wait for completion

      // Log raw documents for debugging
      Log.d(TAG, "Raw documents for host $hostUserId:")
      querySnapshot.documents.forEach { doc ->
        Log.d(TAG, "Document ${doc.id}: hostUserId=${doc.getString("hostUserId")}, isActive=${doc.getBoolean("isActive")}")
      }

      // Convert Firestore documents to EventDto objects
      val events =
              querySnapshot.documents.mapNotNull { document ->
                document.toObject(EventDto::class.java) // Auto-convert JSON to object
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

  /**
   * UPDATE OPERATION - Add Attendee
   *
   * Updates the attendees field of an event document. In our schema, attendees is a single string
   * (user ID) rather than an array.
   *
   * Note: This replaces the previous attendee (single attendee model)
   */
  suspend fun addAttendee(eventId: String, userId: String): Result<Unit> {
    return try {
      // Update the attendees field with the new user ID
      firestore
              .collection(EVENTS_COLLECTION)
              .document(eventId) // Reference specific document
              .update("attendees", userId) // Set attendees field to user ID
              .await() // Wait for update to complete

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