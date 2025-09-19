package com.example.mobilecomputingassignment.domain.repository

import com.example.mobilecomputingassignment.domain.models.Event

interface IEventRepository {
  suspend fun createEvent(event: Event): Result<String>
  suspend fun updateEvent(event: Event): Result<Unit>
  suspend fun deleteEvent(eventId: String): Result<Unit>
  suspend fun getEventById(eventId: String): Result<Event?>
  suspend fun getEventsByHost(hostUserId: String): Result<List<Event>>
  suspend fun getInterestedEvents(userId: String): Result<List<Event>>
  suspend fun getAllActiveEvents(): Result<List<Event>>
  suspend fun addUserInterest(eventId: String, userId: String): Result<Unit>
  suspend fun removeUserInterest(eventId: String, userId: String): Result<Unit>
  suspend fun addAttendee(eventId: String, userId: String): Result<Unit>
  suspend fun removeAttendee(eventId: String, userId: String): Result<Unit>
  suspend fun isUserAttending(eventId: String, userId: String): Result<Boolean>

  suspend fun addNoiseSnapshot(eventId: String, userId: String, dbfs: Double): Result<Unit>

  suspend fun computeRecentNoiseAverage(eventId: String, windowMinutes: Long = 20): Result<Double>

  /**
   * Get events within a geographic viewport (bounding box)
   * @param southWestLat South-west corner latitude
   * @param southWestLng South-west corner longitude
   * @param northEastLat North-east corner latitude
   * @param northEastLng North-east corner longitude
   * @return List of events within the viewport
   */
  suspend fun getEventsInViewport(
          southWestLat: Double,
          southWestLng: Double,
          northEastLat: Double,
          northEastLng: Double
  ): Result<List<Event>>
}
