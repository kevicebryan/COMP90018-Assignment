package com.example.mobilecomputingassignment.data.remote.firebase

import android.util.Log
import com.example.mobilecomputingassignment.data.models.UserDto
import com.example.mobilecomputingassignment.domain.models.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class FirestoreService
@Inject
constructor(private val firestore: FirebaseFirestore, private val auth: FirebaseAuth) {

    suspend fun createUser(
            email: String,
            username: String,
            birthdate: Date,
            leagues: List<String>,
            teams: List<String>
    ): Result<User> {
        return try {
            val currentUser =
                    auth.currentUser ?: return Result.failure(Exception("User not authenticated"))

            // Use Firebase Auth UID as the document ID (UUID)
            val userId = currentUser.uid

            val userDto =
                    UserDto(
                            username = username,
                            email = email,
                            birthdate = Timestamp(birthdate),
                            leagues = leagues,
                            teams = teams,
                            points = 0L, // New users start with 0 points
                            createdAt = Timestamp.now(),
                            updatedAt = Timestamp.now()
                    )

            // Create document with specific ID (the Firebase Auth UID)
            firestore
                    .collection("users")
                    .document(userId) // This becomes the UUID
                    .set(userDto)
                    .await()

            // Also create entries for uniqueness checking
            firestore
                    .collection("usernames")
                    .document(username)
                    .set(mapOf("userId" to userId, "createdAt" to Timestamp.now()))
                    .await()

            firestore
                    .collection("emails")
                    .document(email)
                    .set(mapOf("userId" to userId, "createdAt" to Timestamp.now()))
                    .await()

            // Return the user with the document ID as UUID
            Result.success(userDto.toDomainModel(userId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(userId: String): Result<User?> {
        return try {
            val document = firestore.collection("users").document(userId).get().await()

            if (document.exists()) {
                val userDto = document.toObject(UserDto::class.java) ?: return Result.success(null)
                Result.success(userDto.toDomainModel(document.id))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkUsernameExists(username: String): Boolean {
        return try {
            Log.d("FirestoreService", "Checking if username exists: $username")
            val document = firestore.collection("usernames").document(username).get().await()
            val exists = document.exists()
            Log.d("FirestoreService", "Username '$username' exists: $exists")
            exists
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error checking username existence", e)
            false
        }
    }

    suspend fun checkEmailExists(email: String): Boolean {
        return try {
            Log.d("FirestoreService", "=== EMAIL CHECK START ===")
            Log.d("FirestoreService", "Checking if email exists: '$email'")
            Log.d("FirestoreService", "Email length: ${email.length}")
            Log.d("FirestoreService", "Querying collection: emails, document: '$email'")

            val document = firestore.collection("emails").document(email).get().await()
            val exists = document.exists()

            Log.d("FirestoreService", "Document exists: $exists")
            if (exists) {
                Log.d("FirestoreService", "Document data: ${document.data}")
            }
            Log.d("FirestoreService", "=== EMAIL CHECK END ===")

            exists
        } catch (e: Exception) {
            Log.e("FirestoreService", "=== EMAIL CHECK ERROR ===", e)
            Log.e("FirestoreService", "Error details: ${e.message}")
            Log.e("FirestoreService", "Error cause: ${e.cause}")
            false
        }
    }

    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            val userDto =
                    UserDto(
                            username = user.username,
                            email = user.email,
                            birthdate = user.birthdate?.let { Timestamp(it) },
                            leagues = user.leagues,
                            teams = user.teams,
                            points = user.points,
                            createdAt = user.createdAt?.let { Timestamp(it) },
                            updatedAt = Timestamp.now()
                    )

            firestore.collection("users").document(user.id).set(userDto).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserPoints(userId: String, newPoints: Long): Result<Unit> {
        return try {
            firestore
                    .collection("users")
                    .document(userId)
                    .update(mapOf("points" to newPoints, "updatedAt" to Timestamp.now()))
                    .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            val userDoc = firestore.collection("users").document(userId).get().await()
            if (userDoc.exists()) {
                val userData = userDoc.toObject(UserDto::class.java)
                userData?.let {
                    // Delete from users collection
                    firestore.collection("users").document(userId).delete().await()

                    // Delete from usernames collection
                    firestore.collection("usernames").document(it.username).delete().await()

                    // Delete from emails collection
                    firestore.collection("emails").document(it.email).delete().await()
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
