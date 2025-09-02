package com.example.mobilecomputingassignment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mobilecomputingassignment.domain.repository.IUserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PointsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: IUserRepository
) : ViewModel() {

    /**
     * Adds [earned] points to the user's current total.
     * If [currentPointsHint] is null, it fetches the latest from Firestore.
     * Returns Result with the NEW total on success.
     */
    suspend fun awardPoints(earned: Int, currentPointsHint: Long? = null): Result<Long> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("Not authenticated"))

        val base = currentPointsHint ?: (userRepository.getUserById(uid)?.points ?: 0L)
        val newTotal = base + earned

        val updateRes = userRepository.updateUserPoints(uid, newTotal)
        return if (updateRes.isSuccess) Result.success(newTotal)
        else Result.failure(updateRes.exceptionOrNull() ?: RuntimeException("Failed to update points"))
    }
}
