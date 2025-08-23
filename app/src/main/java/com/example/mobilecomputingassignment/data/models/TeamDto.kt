package com.example.mobilecomputingassignment.data.models

import com.google.gson.annotations.SerializedName

data class TeamDto(
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String,
        @SerializedName("abbrev") val abbreviation: String,
        @SerializedName("logo") val logo: String?
)

data class SquiggleTeamsResponse(@SerializedName("teams") val teams: List<TeamDto>)
