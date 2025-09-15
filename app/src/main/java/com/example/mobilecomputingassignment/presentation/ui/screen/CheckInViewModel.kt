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

    /**
     * NEW: After you’ve sampled the mic in the UI and obtained `dbfs`,
     * call this to store the snapshot and update the 20-minute rolling average.
     */
    suspend fun captureNoiseSnapshot(eventId: String, dbfs: Double): Result<Unit> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("Not authenticated"))

        // 1) Write the snapshot
        val snapRes = eventRepository.addNoiseSnapshot(eventId, uid, dbfs)
        if (snapRes.isFailure) return snapRes

        // 2) Recompute & store recent average on the event (20 minutes)
        val avgRes = eventRepository.computeRecentNoiseAverage(eventId, 20)
        return if (avgRes.isSuccess) Result.success(Unit) else Result.failure(avgRes.exceptionOrNull()!!)
    }
}
