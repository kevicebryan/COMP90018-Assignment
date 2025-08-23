package com.example.mobilecomputingassignment.domain.models

data class Team(
        val id: Int,
        val name: String,
        val abbreviation: String,
        val logoUrl: String? = null, // Keep for API compatibility
        val localLogoRes: Int? = null // Local drawable resource ID
)
