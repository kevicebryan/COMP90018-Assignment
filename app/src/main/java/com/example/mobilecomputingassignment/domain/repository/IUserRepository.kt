package com.example.mobilecomputingassignment.domain.repository

import com.example.mobilecomputingassignment.domain.models.User

interface IUserRepository {
  suspend fun getUserById(userId: String): User?
  suspend fun createUser(
          email: String,
          username: String,
          birthdate: java.util.Date,
          leagues: List<String>,
          teams: List<String>
  ): Result<User>
  suspend fun updateUser(user: User): Result<Unit>
  suspend fun updateUserPoints(userId: String, newPoints: Long): Result<Unit>
  suspend fun checkUsernameExists(username: String): Boolean
  suspend fun checkEmailExists(email: String): Boolean
  suspend fun deleteUser(userId: String): Result<Unit>
}
