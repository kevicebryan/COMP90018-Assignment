package com.example.mobilecomputingassignment.data.models

import com.example.mobilecomputingassignment.domain.models.User
import com.google.firebase.Timestamp

data class UserDto(
        // Note: No id field here since it's the document ID
        val username: String = "",
        val email: String = "",
        val birthdate: Timestamp? = null,
        val leagues: List<String> = emptyList(),
        val teams: List<String> = emptyList(),
        val points: Long = 0L, // User points
        val selectedAvatar: String = "avatar_default",
        val unlockedAvatars: List<String> = listOf("avatar_default"),
        val createdAt: Timestamp? = null,
        val updatedAt: Timestamp? = null
) {
        fun toDomainModel(documentId: String): User {
                return User(
                        id = documentId, // Use the Firestore document ID as the UUID
                        username = username,
                        email = email,
                        birthdate = birthdate?.toDate(),
                        leagues = leagues,
                        teams = teams,
                        points = points,
                        selectedAvatar = selectedAvatar,
                        unlockedAvatars = unlockedAvatars,
                        createdAt = createdAt?.toDate(),
                        updatedAt = updatedAt?.toDate()
                )
        }
}

fun User.toDto(): UserDto {
        return UserDto(
                username = username,
                email = email,
                birthdate = birthdate?.let { Timestamp(it) },
                leagues = leagues,
                teams = teams,
                points = points,
                selectedAvatar = selectedAvatar,
                unlockedAvatars = unlockedAvatars,
                createdAt = createdAt?.let { Timestamp(it) },
                updatedAt = updatedAt?.let { Timestamp(it) }
        )
}
