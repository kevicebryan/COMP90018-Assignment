package com.example.mobilecomputingassignment.domain.usecases.auth

import com.example.mobilecomputingassignment.data.remote.firebase.FirestoreService
import javax.inject.Inject

class CheckEmailExistsUseCase @Inject constructor(private val firestoreService: FirestoreService) {
  suspend fun execute(email: String): Boolean {
    return firestoreService.checkEmailExists(email)
  }
}
