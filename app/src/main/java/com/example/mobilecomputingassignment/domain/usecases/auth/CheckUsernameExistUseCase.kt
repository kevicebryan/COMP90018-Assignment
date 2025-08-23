package com.example.mobilecomputingassignment.domain.usecases.auth

import com.example.mobilecomputingassignment.data.remote.firebase.FirestoreService
import javax.inject.Inject

class CheckUsernameExistsUseCase
@Inject
constructor(private val firestoreService: FirestoreService) {
  suspend fun execute(username: String): Boolean {
    return firestoreService.checkUsernameExists(username)
  }
}
