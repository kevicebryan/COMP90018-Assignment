package com.example.mobilecomputingassignment.domain.models

import java.util.Date

data class User(
        val id: String = "", // This IS the UUID (Firestore document ID)
        val username: String = "", // Unique username
        val email: String = "", // Unique email
        val birthdate: Date? = null, // For age verification
        val leagues: List<String> = emptyList(), // Array of league names
        val teams: List<String> = emptyList(), // Array of team names
        val points: Long = 0L, // User points for gamification
        val selectedAvatar: String = "avatar_default", // Selected avatar drawable name
        val unlockedAvatars: List<String> =
                listOf("avatar_default"), // List of unlocked avatar names
        val createdAt: Date? = null,
        val updatedAt: Date? = null
) {
    fun isOver18(): Boolean {
        return birthdate?.let { birth ->
            val age = (System.currentTimeMillis() - birth.time) / (365.25 * 24 * 60 * 60 * 1000)
            age >= 18
        }
                ?: false
    }

    // The document ID serves as the UUID for QR codes
    fun getQRCodeData(): String = id

    // Points-related helper functions
    fun canAffordReward(cost: Long): Boolean = points >= cost

    fun getPointsLevel(): String =
            when {
                points < 100 -> "Rookie Fan"
                points < 500 -> "Regular Supporter"
                points < 1000 -> "Dedicated Fan"
                points < 2500 -> "Super Fan"
                points < 5000 -> "Ultimate Fan"
                else -> "Legend"
            }
}
