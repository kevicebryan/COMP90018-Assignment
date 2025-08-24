package com.example.mobilecomputingassignment.domain.models

import java.util.Date

data class Event(
        val id: String = "",
        val hostUserId: String = "",
        val hostUsername: String = "",
        val title: String = "",
        val description: String = "",
        val date: Date = Date(),
        val checkInTime: Date = Date(),
        val matchId: String = "",
        val matchDetails: MatchDetails? = null,
        val location: EventLocation = EventLocation(),
        val capacity: Int = 0,
        val contactNumber: String = "",
        val amenities: EventAmenities = EventAmenities(),
        val accessibility: EventAccessibility = EventAccessibility(),
        val attendees: List<String> = emptyList(), // User IDs
        val interestedUsers: List<String> = emptyList(), // User IDs
        val createdAt: Date = Date(),
        val updatedAt: Date = Date(),
        val isActive: Boolean = true
)

data class EventLocation(
        val name: String = "",
        val address: String = "",
        val latitude: Double = 0.0,
        val longitude: Double = 0.0
)

data class EventAmenities(
        val isIndoor: Boolean = false,
        val isOutdoor: Boolean = false,
        val isChildFriendly: Boolean = false,
        val isPetFriendly: Boolean = false,
        val hasParking: Boolean = false,
        val hasFood: Boolean = false,
        val hasDrinks: Boolean = false,
        val hasWifi: Boolean = false
)

data class EventAccessibility(
        val isWheelchairAccessible: Boolean = false,
        val hasAccessibleToilets: Boolean = false,
        val hasAccessibleParking: Boolean = false,
        val hasSignLanguageSupport: Boolean = false,
        val hasAudioSupport: Boolean = false
)

data class MatchDetails(
        val id: String = "",
        val homeTeam: String = "",
        val awayTeam: String = "",
        val competition: String = "",
        val venue: String = "",
        val matchTime: Date = Date(),
        val round: String = "",
        val season: String = ""
)
