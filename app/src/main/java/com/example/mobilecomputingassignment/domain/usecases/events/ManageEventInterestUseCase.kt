package com.example.mobilecomputingassignment.domain.usecases.events

import com.example.mobilecomputingassignment.domain.repository.IEventRepository
import javax.inject.Inject

class ManageEventInterestUseCase
@Inject
constructor(private val eventRepository: IEventRepository) {
  suspend fun addInterest(eventId: String, userId: String): Result<Unit> {
    return eventRepository.addUserInterest(eventId, userId)
  }

  suspend fun removeInterest(eventId: String, userId: String): Result<Unit> {
    return eventRepository.removeUserInterest(eventId, userId)
  }

  suspend fun checkIn(eventId: String, userId: String): Result<Unit> {
    return eventRepository.addAttendee(eventId, userId)
  }

  suspend fun checkOut(eventId: String, userId: String): Result<Unit> {
    return eventRepository.removeAttendee(eventId, userId)
  }
}
