package com.example.mobilecomputingassignment.domain.usecases.events

import com.example.mobilecomputingassignment.domain.models.Event
import com.example.mobilecomputingassignment.domain.repository.IEventRepository
import javax.inject.Inject

class GetUserEventsUseCase @Inject constructor(private val eventRepository: IEventRepository) {
  suspend fun getHostedEvents(userId: String): Result<List<Event>> {
    return eventRepository.getEventsByHost(userId)
  }

  suspend fun getInterestedEvents(userId: String): Result<List<Event>> {
    return eventRepository.getInterestedEvents(userId)
  }

  suspend fun getAllActiveEvents(): Result<List<Event>> {
    return eventRepository.getAllActiveEvents()
  }
}
