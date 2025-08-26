package com.example.mobilecomputingassignment.domain.usecases.events

import com.example.mobilecomputingassignment.domain.models.Event
import com.example.mobilecomputingassignment.domain.repository.IEventRepository
import java.util.Date
import javax.inject.Inject

class UpdateEventUseCase @Inject constructor(private val eventRepository: IEventRepository) {
      suspend fun execute(event: Event): Result<Unit> {
        // Validate event data
        if (event.matchId.isBlank()) {
            return Result.failure(Exception("Please select a match"))
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

    // Update timestamp
    val updatedEvent = event.copy(updatedAt = Date())

    return eventRepository.updateEvent(updatedEvent)
  }

  suspend fun deleteEvent(eventId: String): Result<Unit> {
    return eventRepository.deleteEvent(eventId)
  }
}
