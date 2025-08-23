package com.example.mobilecomputingassignment.domain.usecases.auth

import com.example.mobilecomputingassignment.data.remote.firebase.FirestoreService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class GoogleSignInUseCase
@Inject
constructor(private val auth: FirebaseAuth, private val firestoreService: FirestoreService) {
  suspend fun execute(idToken: String): Result<Unit> {
    return try {
      val credential = GoogleAuthProvider.getCredential(idToken, null)
      val authResult = auth.signInWithCredential(credential).await()
      val firebaseUser =
              authResult.user ?: return Result.failure(Exception("Failed to sign in with Google"))

      // Check if user profile exists, if not create one
      val existingUser = firestoreService.getUserById(firebaseUser.uid)
      if (existingUser.getOrNull() == null) {
        // Create profile for new Google user
        firestoreService.createUser(
                email = firebaseUser.email ?: "",
                username = firebaseUser.displayName ?: "User${System.currentTimeMillis()}",
                birthdate = Date(0), // Will need to be updated
                leagues = emptyList(),
                teams = emptyList()
        )
      }

      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
