package com.example.mobilecomputingassignment.data.repository

import com.example.mobilecomputingassignment.data.models.EventDto
import com.example.mobilecomputingassignment.data.remote.firebase.EventFirestoreService
import com.example.mobilecomputingassignment.domain.models.Event
import com.example.mobilecomputingassignment.domain.repository.IEventRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository
@Inject
constructor(private val eventFirestoreService: EventFirestoreService) : IEventRepository {

  override suspend fun createEvent(event: Event): Result<String> {
    val eventDto = EventDto.fromDomain(event)
    return eventFirestoreService.createEvent(eventDto)
  }

  override suspend fun updateEvent(event: Event): Result<Unit> {
    val eventDto = EventDto.fromDomain(event)
    return eventFirestoreService.updateEvent(eventDto)
  }

  override suspend fun deleteEvent(eventId: String): Result<Unit> {
    return eventFirestoreService.deleteEvent(eventId)
  }

  override suspend fun getEventById(eventId: String): Result<Event?> {
    return eventFirestoreService.getEventById(eventId).map { eventDto -> eventDto?.toDomain() }
  }

  override suspend fun getEventsByHost(hostUserId: String): Result<List<Event>> {
    return try {
      // Log the hostUserId to verify it's correct
      println("Getting events for host: $hostUserId")

      val result = eventFirestoreService.getEventsByHost(hostUserId)

      // Log the result
      result
              .onSuccess { eventDtos ->
                println("Found ${eventDtos.size} events for host $hostUserId")
              }
              .onFailure { error ->
                println("Error getting events for host $hostUserId: ${error.message}")
              }

      result.map { eventDtos -> eventDtos.map { it.toDomain() } }
    } catch (e: Exception) {
      println("Exception getting events for host $hostUserId: ${e.message}")
      Result.failure(e)
    }
  }

  override suspend fun getInterestedEvents(userId: String): Result<List<Event>> {
    return eventFirestoreService.getInterestedEvents(userId).map { eventDtos ->
      eventDtos.map { it.toDomain() }
    }
  }

  override suspend fun getAllActiveEvents(): Result<List<Event>> {
    return eventFirestoreService.getAllActiveEvents().map { eventDtos ->
      eventDtos.map { it.toDomain() }
    }
  }

  override suspend fun addUserInterest(eventId: String, userId: String): Result<Unit> {
    return eventFirestoreService.addUserInterest(eventId, userId)
  }

  override suspend fun removeUserInterest(eventId: String, userId: String): Result<Unit> {
    return eventFirestoreService.removeUserInterest(eventId, userId)
  }

  override suspend fun addAttendee(eventId: String, userId: String): Result<Unit> {
    return eventFirestoreService.addAttendee(eventId, userId)
  }

  // inside EventRepository class
  override suspend fun isUserAttending(eventId: String, userId: String): Result<Boolean> {
    return eventFirestoreService.isUserAttending(eventId, userId)
  }


  override suspend fun removeAttendee(eventId: String, userId: String): Result<Unit> {
    return eventFirestoreService.removeAttendee(eventId, userId)
  }
}
