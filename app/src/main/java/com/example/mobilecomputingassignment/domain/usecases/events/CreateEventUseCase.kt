package com.example.mobilecomputingassignment.domain.usecases.events

import com.example.mobilecomputingassignment.domain.models.Event
import com.example.mobilecomputingassignment.domain.repository.IEventRepository
import java.util.Date
import javax.inject.Inject

class CreateEventUseCase @Inject constructor(private val eventRepository: IEventRepository) {
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

    return eventRepository.createEvent(eventToCreate)
  }
}
