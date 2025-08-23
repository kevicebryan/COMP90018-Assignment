package com.example.mobilecomputingassignment.domain.usecases.auth

import com.example.mobilecomputingassignment.data.remote.firebase.FirestoreService
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class RegisterUseCase
@Inject
constructor(private val auth: FirebaseAuth, private val firestoreService: FirestoreService) {
  suspend fun execute(
          email: String,
          password: String,
          username: String,
          birthdateString: String,
          leagues: List<String>,
          teams: List<String>
  ): Result<Unit> {
    return try {
      // Parse birthdate
      val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
      val birthdate =
              dateFormat.parse(birthdateString)
                      ?: return Result.failure(Exception("Invalid birthdate format"))

      // Check if user is over 18
      val age = (System.currentTimeMillis() - birthdate.time) / (365.25 * 24 * 60 * 60 * 1000)
      if (age < 18) {
        return Result.failure(Exception("You must be 18 or older to use this app"))
      }

      // Create Firebase Auth account
      val authResult = auth.createUserWithEmailAndPassword(email, password).await()
      val firebaseUser =
              authResult.user ?: return Result.failure(Exception("Failed to create user account"))

      // Create user profile in Firestore
      firestoreService
              .createUser(
                      email = email,
                      username = username,
                      birthdate = birthdate,
                      leagues = leagues,
                      teams = teams
              )
              .getOrThrow()

      Result.success(Unit)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
