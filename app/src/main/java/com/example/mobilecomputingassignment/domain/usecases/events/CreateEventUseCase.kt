package com.example.mobilecomputingassignment.domain.usecases.events

import com.example.mobilecomputingassignment.domain.models.Event
import com.example.mobilecomputingassignment.domain.repository.IEventRepository
import com.example.mobilecomputingassignment.domain.usecases.notifications.ManageFavoriteTeamNotificationsUseCase
import java.util.Date
import javax.inject.Inject

class CreateEventUseCase @Inject constructor(
    private val eventRepository: IEventRepository,
    private val manageFavoriteTeamNotificationsUseCase: ManageFavoriteTeamNotificationsUseCase
) {
  suspend fun execute(event: Event): Result<String> {
    // Validate event data
    if (event.title.isBlank()) {
      return Result.failure(Exception("Event title is required"))
    }

    if (event.location.name.isBlank()) {
      return Result.failure(Exception("Location name is required"))
    }

    if (event.location.address.isBlank()) {
      return Result.failure(Exception("Location address is required"))
    }

    if (event.capacity <= 0) {
      return Result.failure(Exception("Capacity must be greater than 0"))
    }

    if (event.date.before(Date())) {
      return Result.failure(Exception("Event date must be in the future"))
    }

    if (event.checkInTime.after(event.date)) {
      return Result.failure(Exception("Check-in time must be before event date"))
    }

    // Set timestamps
    val now = Date()
    val eventToCreate = event.copy(createdAt = now, updatedAt = now, isActive = true)

    // Create the event
    val result = eventRepository.createEvent(eventToCreate)
    
    // If event creation was successful, trigger favorite team notifications
    result.onSuccess { eventId ->
      try {
        // Create a copy of the event with the generated ID for notifications
        val eventWithId = eventToCreate.copy(id = eventId)
        manageFavoriteTeamNotificationsUseCase.onNewEventCreated(eventWithId)
      } catch (e: Exception) {
        // Log error but don't fail the event creation
        android.util.Log.e("CreateEventUseCase", "Failed to trigger favorite team notifications", e)
      }
    }

    return result
  }
}
