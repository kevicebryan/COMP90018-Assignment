package com.example.mobilecomputingassignment.data.repository

import com.example.mobilecomputingassignment.data.remote.firebase.FirestoreService
import com.example.mobilecomputingassignment.domain.models.User
import com.example.mobilecomputingassignment.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow // <-- Add this import
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val firestoreService: FirestoreService) :
    IUserRepository {

    override suspend fun getUserById(userId: String): User? {
        return firestoreService.getUserById(userId).getOrNull()
    }

    override suspend fun createUser(
        email: String,
        username: String,
        birthdate: java.util.Date,
        leagues: List<String>,
        teams: List<String>
    ): Result<User> {
        return firestoreService.createUser(email, username, birthdate, leagues, teams)
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return firestoreService.updateUser(user)
    }

    override suspend fun updateUserPoints(userId: String, newPoints: Long): Result<Unit> {
        return firestoreService.updateUserPoints(userId, newPoints)
    }

    override suspend fun checkUsernameExists(username: String): Boolean {
        return firestoreService.checkUsernameExists(username)
    }

    override suspend fun checkEmailExists(email: String): Boolean {
        return firestoreService.checkEmailExists(email)
    }

    override suspend fun deleteUser(userId: String): Result<Unit> {
        return firestoreService.deleteUser(userId)
    }

    // 👇 ADD THE IMPLEMENTATIONS FOR THE NEW FUNCTIONS 👇
    override fun getUserTeams(): Flow<List<String>> {
        return firestoreService.getUserTeams()
    }

    override suspend fun updateUserTeams(teams: List<String>): Result<Unit> {
        return firestoreService.updateUserTeams(teams)
    }
}