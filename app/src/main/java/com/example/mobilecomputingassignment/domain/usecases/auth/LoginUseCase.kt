package com.example.mobilecomputingassignment.domain.usecases.auth

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class LoginUseCase @Inject constructor(private val auth: FirebaseAuth) {
  suspend fun execute(email: String, password: String): Result<Unit> {
    return try {
      auth.signInWithEmailAndPassword(email, password).await()
      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
