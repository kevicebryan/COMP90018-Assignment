package com.example.mobilecomputingassignment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mobilecomputingassignment.domain.repository.IEventRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val eventRepository: IEventRepository
) : ViewModel() {

    suspend fun hasAlreadyCheckedIn(eventId: String): Result<Boolean> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("Not authenticated"))
        return eventRepository.isUserAttending(eventId, uid)
    }

    suspend fun checkInToEvent(eventId: String): Result<Unit> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("Not authenticated"))
        return eventRepository.addAttendee(eventId, uid)
    }
}
