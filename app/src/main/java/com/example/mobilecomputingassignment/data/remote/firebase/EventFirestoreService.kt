package com.example.mobilecomputingassignment.data.remote.firebase

import android.util.Log
import com.example.mobilecomputingassignment.data.models.EventDto
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp
import java.util.Date


@Singleton
class EventFirestoreService @Inject constructor(private val firestore: FirebaseFirestore) {
    companion object {
        private const val EVENTS_COLLECTION = "events"
        private const val TAG = "EventFirestoreService"
        // noise snapshots subcollection
        private const val NOISE_SNAPSHOTS = "noiseSnapshots"
    }

    // === NEW: noise snapshot write ===
    suspend fun addNoiseSnapshot(eventId: String, userId: String, dbfs: Double): Result<Unit> {
        return try {
            val snapRef = firestore
                .collection(EVENTS_COLLECTION)
                .document(eventId)
                .collection(NOISE_SNAPSHOTS)
                .document()

            val data = mapOf(
                "userId" to userId,
                "dbfs" to dbfs,
                "capturedAt" to FieldValue.serverTimestamp()
            )

            snapRef.set(data).await()
            Log.d(TAG, "Noise snapshot added: event=$eventId user=$userId dbfs=$dbfs")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding noise snapshot", e)
            Result.failure(e)
        }
    }

    // === NEW: compute 20-min avg & store on event ===
    suspend fun computeAndUpdateRecentNoiseAverage(
        eventId: String,
        windowMinutes: Long = 20L
    ): Result<Double> {
        return try {
            val cutoff = Timestamp(Date(System.currentTimeMillis() - windowMinutes * 60_000))
            val col = firestore
                .collection(EVENTS_COLLECTION)
                .document(eventId)
                .collection(NOISE_SNAPSHOTS)

            // last 20 minutes ordered by time
            val snaps = col
                .whereGreaterThanOrEqualTo("capturedAt", cutoff)
                .orderBy("capturedAt")
                .get()
                .await()

            val values = snaps.documents.mapNotNull { it.getDouble("dbfs") }
            val avg = if (values.isNotEmpty()) values.average() else Double.NaN

            // Store on the event for quick reads (optional but handy)
            firestore.collection(EVENTS_COLLECTION)
                .document(eventId)
                .update(
                    mapOf(
                        "recentNoiseDbfs" to avg,
                        "recentNoiseUpdatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()

            Log.d(
                TAG,
                "Updated recentNoiseDbfs for event=$eventId avg=${if (avg.isNaN()) "NaN" else avg}"
            )
            Result.success(avg)
        } catch (e: Exception) {
            Log.e(TAG, "Error computing/updating recent noise average", e)
            Result.failure(e)
        }
    }

    suspend fun createEvent(event: EventDto): Result<String> {
        return try {
            val docRef = firestore.collection(EVENTS_COLLECTION).document()
            val eventWithId = event.copy(id = docRef.id)

            Log.d(
                TAG,
                "Creating event with data: hostUserId=${eventWithId.hostUserId}, hostUsername=${eventWithId.hostUsername}, isActive=${eventWithId.isActive}"
            )

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
            val event = if (document.exists()) document.toObject(EventDto::class.java) else null
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

            Log.d(TAG, "Raw documents for host $hostUserId:")
            querySnapshot.documents.forEach { doc ->
                Log.d(TAG, "Document ${doc.id}: hostUserId=${doc.getString("hostUserId")}, isActive=${doc.getBoolean("isActive")}")
            }

            val events = querySnapshot.documents.mapNotNull { it.toObject(EventDto::class.java) }
            Log.d(TAG, "Retrieved ${events.size} hosted events for user: $hostUserId")
            Result.success(events)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting events by host", e)
            Result.failure(e)
        }
    }

    suspend fun getInterestedEvents(userId: String): Result<List<EventDto>> {
        return try {
            Log.d(TAG, "Getting interested events for user: $userId")
            
            val querySnapshot =
                firestore
                    .collection(EVENTS_COLLECTION)
                    .whereArrayContains("interestedUsers", userId)
                    .whereEqualTo("isActive", true)
                    .orderBy("date", Query.Direction.ASCENDING)
                    .get()
                    .await()

            Log.d(TAG, "Raw documents for interested user $userId:")
            querySnapshot.documents.forEach { doc ->
                Log.d(TAG, "Document ${doc.id}: interestedUsers=${doc.get("interestedUsers")}, isActive=${doc.getBoolean("isActive")}")
            }

            val events = querySnapshot.documents.mapNotNull { it.toObject(EventDto::class.java) }
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

            val events = querySnapshot.documents.mapNotNull { it.toObject(EventDto::class.java) }
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
                .update("interestedUsers", FieldValue.arrayUnion(userId))
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
                .update("interestedUsers", FieldValue.arrayRemove(userId))
                .await()
            Log.d(TAG, "User $userId removed interest from event: $eventId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error removing user interest", e)
            Result.failure(e)
        }
    }

    /**
     * ADD ATTENDEE (atomic):
     * - Ensures 'attendees' is an array<String>
     * - Adds userId if not present
     * - Increments attendeesCount only when adding
     */
    suspend fun addAttendee(eventId: String, userId: String): Result<Unit> {
        return try {
            val events = firestore.collection(EVENTS_COLLECTION)
            firestore.runTransaction { txn ->
                val docRef = events.document(eventId)
                val snap = txn.get(docRef)
                if (!snap.exists()) throw IllegalStateException("Event not found: $eventId")

                val currentList: List<String> = when (val raw = snap.get("attendees")) {
                    is List<*> -> raw.filterIsInstance<String>()
                    is String  -> if (raw.isNotBlank()) listOf(raw) else emptyList() // legacy
                    else       -> emptyList()
                }
                val alreadyAttending = currentList.contains(userId)

                if (!alreadyAttending) {
                    val newList = currentList + userId
                    txn.update(docRef, "attendees", newList)
                    txn.update(docRef, "attendeesCount", FieldValue.increment(1))
                }
                null
            }.await()

            Log.d(TAG, "User $userId checked in to event: $eventId (count updated if new)")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding attendee", e)
            Result.failure(e)
        }
    }

    suspend fun isUserAttending(eventId: String, userId: String): Result<Boolean> {
        return try {
            val snap = firestore.collection(EVENTS_COLLECTION).document(eventId).get().await()
            if (!snap.exists()) return Result.failure(IllegalStateException("Event not found: $eventId"))

            // Support legacy schemas: attendees may be string or list (or missing)
            val attendees: List<String> = when (val raw = snap.get("attendees")) {
                is List<*> -> raw.filterIsInstance<String>()
                is String  -> listOf(raw)
                else       -> emptyList()
            }
            Result.success(attendees.contains(userId))
        } catch (e: Exception) {
            Log.e(TAG, "isUserAttending failed", e)
            Result.failure(e)
        }
    }

    /**
     * REMOVE ATTENDEE (atomic):
     * - Removes userId from attendees array
     * - Decrements attendeesCount only when removal happened (and not below zero)
     */
    suspend fun removeAttendee(eventId: String, userId: String): Result<Unit> {
        return try {
            val events = firestore.collection(EVENTS_COLLECTION)

            firestore.runTransaction { txn ->
                val docRef = events.document(eventId)
                val snap = txn.get(docRef)
                if (!snap.exists()) throw IllegalStateException("Event not found: $eventId")

                val currentList: List<String> = when (val raw = snap.get("attendees")) {
                    is List<*> -> raw.filterIsInstance<String>()
                    is String -> listOf(raw)  // migrate legacy
                    else -> emptyList()
                }

                if (currentList.contains(userId)) {
                    val newList = currentList.filterNot { it == userId }
                    txn.update(docRef, mapOf("attendees" to newList))

                    val currentCount = snap.getLong("attendeesCount") ?: currentList.size.toLong()
                    if (currentCount > 0) {
                        txn.update(docRef, "attendeesCount", FieldValue.increment(-1))
                    }
                }
                null
            }.await()

            Log.d(TAG, "User $userId removed from event: $eventId (count updated)")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error removing attendee", e)
            Result.failure(e)
        }
    }
}
