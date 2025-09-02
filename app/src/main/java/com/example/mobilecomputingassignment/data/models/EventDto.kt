// com/example/mobilecomputingassignment/data/models/EventDto.kt
package com.example.mobilecomputingassignment.data.models

import com.example.mobilecomputingassignment.domain.models.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import java.util.Date

data class EventDto(
    val id: String = "",
    val hostUserId: String = "",
    val hostUsername: String = "",
    val date: Timestamp = Timestamp.now(),
    val checkInTime: Timestamp = Timestamp.now(),
    val matchId: String = "",
    val matchDetails: MatchDetailsDto? = null,
    val location: EventLocationDto = EventLocationDto(),
    val capacity: Int = 0,
    val contactNumber: String = "",
    val amenities: EventAmenitiesDto = EventAmenitiesDto(),
    val accessibility: EventAccessibilityDto = EventAccessibilityDto(),

    // 🔸 IMPORTANT: tolerate legacy string OR new array
    val attendees: Any? = null,
    val attendeesCount: Long? = null,

    val interestedUsers: List<String> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    @get:PropertyName("isActive") @set:PropertyName("isActive")
    var isActive: Boolean = true,
    val volume: Int = 1
) {
    fun toDomain(): Event {
        val attendeesList: List<String> = when (attendees) {
            is List<*> -> attendees.filterIsInstance<String>()
            is String  -> if (attendees.isNotBlank()) listOf(attendees) else emptyList()
            else       -> emptyList()
        }
        val normalizedCount = attendeesCount ?: attendeesList.size.toLong()

        return Event(
            id = id,
            hostUserId = hostUserId,
            hostUsername = hostUsername,
            date = date.toDate(),
            checkInTime = checkInTime.toDate(),
            matchId = matchId,
            matchDetails = matchDetails?.toDomain(),
            location = location.toDomain(),
            capacity = capacity,
            contactNumber = contactNumber,
            amenities = amenities.toDomain(),
            accessibility = accessibility.toDomain(),
            attendees = "",                         // keep legacy string unused
            attendeesCount = normalizedCount,       // 🔸 map to domain
            interestedUsers = interestedUsers,
            createdAt = createdAt.toDate(),
            updatedAt = updatedAt.toDate(),
            isActive = isActive,
            volume = volume
        )
    }

    companion object {
        fun fromDomain(event: Event): EventDto {
            return EventDto(
                id = event.id,
                hostUserId = event.hostUserId,
                hostUsername = event.hostUsername,
                date = Timestamp(event.date),
                checkInTime = Timestamp(event.checkInTime),
                matchId = event.matchId,
                matchDetails = event.matchDetails?.let { MatchDetailsDto.fromDomain(it) },
                location = EventLocationDto.fromDomain(event.location),
                capacity = event.capacity,
                contactNumber = event.contactNumber,
                amenities = EventAmenitiesDto.fromDomain(event.amenities),
                accessibility = EventAccessibilityDto.fromDomain(event.accessibility),

                // write new schema going forward
                attendees = emptyList<String>(),
                attendeesCount = event.attendeesCount,   // 🔸 keep round-tripping this

                interestedUsers = event.interestedUsers,
                createdAt = Timestamp(event.createdAt),
                updatedAt = Timestamp(event.updatedAt),
                isActive = event.isActive,
                volume = event.volume
            )
        }
    }
}

data class EventLocationDto(
    val name: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {
    fun toDomain() = EventLocation(name, address, latitude, longitude)
    companion object { fun fromDomain(location: EventLocation) =
        EventLocationDto(location.name, location.address, location.latitude, location.longitude) }
}

data class EventAmenitiesDto(
    val isIndoor: Boolean = false,
    val isOutdoor: Boolean = false,
    val isChildFriendly: Boolean = false,
    val isPetFriendly: Boolean = false,
    val hasParking: Boolean = false,
    val hasFood: Boolean = false,
    val hasToilet: Boolean = false,
    val hasWifi: Boolean = false
) {
    fun toDomain() = EventAmenities(isIndoor, isOutdoor, isChildFriendly, isPetFriendly, hasParking, hasFood, hasToilet, hasWifi)
    companion object { fun fromDomain(a: EventAmenities) =
        EventAmenitiesDto(a.isIndoor, a.isOutdoor, a.isChildFriendly, a.isPetFriendly, a.hasParking, a.hasFood, a.hasToilet, a.hasWifi) }
}

data class EventAccessibilityDto(
    val isWheelchairAccessible: Boolean = false,
    val hasAccessibleToilets: Boolean = false,
    val hasAccessibleParking: Boolean = false,
    val hasSignLanguageSupport: Boolean = false,
    val hasAudioSupport: Boolean = false
) {
    fun toDomain() = EventAccessibility(isWheelchairAccessible, hasAccessibleToilets, hasAccessibleParking, hasSignLanguageSupport, hasAudioSupport)
    companion object { fun fromDomain(a: EventAccessibility) =
        EventAccessibilityDto(a.isWheelchairAccessible, a.hasAccessibleToilets, a.hasAccessibleParking, a.hasSignLanguageSupport, a.hasAudioSupport) }
}

data class MatchDetailsDto(
    val id: String = "",
    val homeTeam: String = "",
    val awayTeam: String = "",
    val competition: String = "",
    val venue: String = "",
    val matchTime: Timestamp = Timestamp.now(),
    val round: String = "",
    val season: Int = 2025
) {
    fun toDomain() = MatchDetails(id, homeTeam, awayTeam, competition, venue, matchTime.toDate(), round, season)
    companion object { fun fromDomain(m: MatchDetails) =
        MatchDetailsDto(m.id, m.homeTeam, m.awayTeam, m.competition, m.venue, Timestamp(m.matchTime), m.round, m.season) }
}
