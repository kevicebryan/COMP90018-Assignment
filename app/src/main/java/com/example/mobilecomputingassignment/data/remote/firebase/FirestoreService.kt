// In: app/src/main/java/com/example/mobilecomputingassignment/data/remote/firebase/FirestoreService.kt

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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class FirestoreService
@Inject
constructor(private val firestore: FirebaseFirestore, private val auth: FirebaseAuth) {

    // A reference to the "users" collection to avoid repeating the string
    private val usersCollection = firestore.collection("users")

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

            val userId = currentUser.uid

            val userDto =
                UserDto(
                    username = username,
                    email = email,
                    birthdate = Timestamp(birthdate),
                    leagues = leagues,
                    teams = teams,
                    points = 0L,
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                )

            usersCollection.document(userId).set(userDto).await()

            firestore.collection("usernames").document(username)
                .set(mapOf("userId" to userId, "createdAt" to Timestamp.now())).await()
            firestore.collection("emails").document(email)
                .set(mapOf("userId" to userId, "createdAt" to Timestamp.now())).await()

            Result.success(userDto.toDomainModel(userId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(userId: String): Result<User?> {
        return try {
            val document = usersCollection.document(userId).get().await()

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
            val document = firestore.collection("emails").document(email).get().await()
            val exists = document.exists()
            Log.d("FirestoreService", "Document exists: $exists")
            Log.d("FirestoreService", "=== EMAIL CHECK END ===")
            exists
        } catch (e: Exception) {
            Log.e("FirestoreService", "=== EMAIL CHECK ERROR ===", e)
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
            usersCollection.document(user.id).set(userDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserPoints(userId: String, newPoints: Long): Result<Unit> {
        return try {
            usersCollection.document(userId)
                .update(mapOf("points" to newPoints, "updatedAt" to Timestamp.now())).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            val userDoc = usersCollection.document(userId).get().await()
            if (userDoc.exists()) {
                val userData = userDoc.toObject(UserDto::class.java)
                userData?.let {
                    usersCollection.document(userId).delete().await()
                    firestore.collection("usernames").document(it.username).delete().await()
                    firestore.collection("emails").document(it.email).delete().await()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- NEW FUNCTIONS FOR TEAM SELECTION ---

    /**
     * Listens for real-time updates to the current user's team list from Firestore.
     * Returns a Flow that emits the list of team names whenever it changes.
     */
    fun getUserTeams(): Flow<List<String>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(emptyList()) // If no user is logged in, send an empty list and close.
            close()
            return@callbackFlow
        }

        val userDocRef = usersCollection.document(userId)

        // This listener is called every time the user's document changes in Firestore.
        val listener = userDocRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error) // Close the flow if there's an error.
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                // The field name "teams" is based on your createUser function.
                val teams = snapshot.get("teams") as? List<String> ?: emptyList()
                trySend(teams) // Send the latest list of teams to the ViewModel.
            } else {
                trySend(emptyList()) // The document doesn't exist or has no teams.
            }
        }

        // When the ViewModel stops listening, remove the Firestore listener to prevent memory leaks.
        awaitClose { listener.remove() }
    }

    /**
     * Updates the "teams" field in the current user's document in Firestore.
     */
    suspend fun updateUserTeams(teams: List<String>): Result<Unit> = runCatching {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

        // Find the user's document and update only the "teams" field with the new list.
        usersCollection.document(userId).update("teams", teams).await()
    }
}