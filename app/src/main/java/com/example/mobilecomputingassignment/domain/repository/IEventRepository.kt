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
}
