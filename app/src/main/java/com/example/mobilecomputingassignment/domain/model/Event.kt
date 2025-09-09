package com.example.mobilecomputingassignment.domain.model

import java.util.Date

data class Event(
        val id: String,
        val teamA: String,
        val teamB: String,
        val date: Date,
        val checkInTime: Date,
        val venueName: String,
        val locationAddress: String,
        val latitude: Double,
        val longitude: Double,
        val isAccessible: Boolean,
        val contactNumber: String,
        val hostUserId: String,
        val hostUsername: String,
        val isActive: Boolean = true,
        val interestedUsers: List<String> = emptyList()
)
