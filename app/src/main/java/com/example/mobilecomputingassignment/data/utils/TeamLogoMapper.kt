package com.example.mobilecomputingassignment.data.utils

import com.example.mobilecomputingassignment.R

/**
 * Utility class to map team names to their local drawable resources This replaces the need to fetch
 * logos from external APIs
 */
object TeamLogoMapper {

    /**
     * Maps team names (as they appear in Squiggle API) to local drawable resources Only includes
     * teams with available PNG logos
     */
    private val teamLogoMap = mapOf(
        "Adelaide" to R.drawable.team_adelaide,
        "Brisbane Lions" to R.drawable.team_brisbane_lions,
        "Carlton" to R.drawable.team_carlton,
        "Collingwood" to R.drawable.team_collingwood,
        "Essendon" to R.drawable.team_essendon,
        "Fremantle" to R.drawable.team_fremantle,
        "Geelong" to R.drawable.team_geelong,
        "Gold Coast" to R.drawable.team_gold_coast,
        "Greater Western Sydney" to R.drawable.team_greater_western_sydney,
        "Hawthorn" to R.drawable.team_hawthorn,
        "Melbourne" to R.drawable.team_melbourne,
        "North Melbourne" to R.drawable.team_north_melbourne,
        "Port Adelaide" to R.drawable.team_port_adelaide,
        "Richmond" to R.drawable.team_richmond,
        "St Kilda" to R.drawable.team_st_kilda,
        "Sydney" to R.drawable.team_sydney,
        "West Coast" to R.drawable.team_west_coast,
        "Western Bulldogs" to R.drawable.team_western_bulldogs
    )

    /**
     * Maps team abbreviations to local drawable resources Only includes teams with available PNG
     * logos
     */
    private val teamAbbrevLogoMap =
        mapOf(
            // Available PNG logos
            "ADE" to R.drawable.team_adelaide,
            "BRI" to R.drawable.team_brisbane_lions,
            "CAR" to R.drawable.team_carlton,
            "COL" to R.drawable.team_collingwood,
            "ESS" to R.drawable.team_essendon,
            "FRE" to R.drawable.team_fremantle,
            "GEE" to R.drawable.team_geelong,
            "GCS" to R.drawable.team_gold_coast,
            "GWS" to R.drawable.team_greater_western_sydney,
            "HAW" to R.drawable.team_hawthorn,
            "MEL" to R.drawable.team_melbourne,
            "NOR" to R.drawable.team_north_melbourne,
            "POR" to R.drawable.team_port_adelaide,
            "RIC" to R.drawable.team_richmond,
            "STK" to R.drawable.team_st_kilda,
            "SYD" to R.drawable.team_sydney,
            "WCE" to R.drawable.team_west_coast,
            "WBD" to R.drawable.team_western_bulldogs
        )

    /**
     * Get drawable resource ID for a team by name
     * @param teamName The full team name (e.g., "Adelaide", "Brisbane Lions")
     * @return Drawable resource ID or null if not found
     */
    fun getTeamLogoByName(teamName: String): Int? {
        return teamLogoMap[teamName]
    }

    /**
     * Get drawable resource ID for a team by abbreviation
     * @param abbreviation The team abbreviation (e.g., "ADE", "BRI")
     * @return Drawable resource ID or null if not found
     */
    fun getTeamLogoByAbbreviation(abbreviation: String): Int? {
        return teamAbbrevLogoMap[abbreviation]
    }

    /** Get all available team names */
    fun getAllTeamNames(): Set<String> {
        return teamLogoMap.keys
    }

    /** Get all available team abbreviations */
    fun getAllTeamAbbreviations(): Set<String> {
        return teamAbbrevLogoMap.keys
    }
}
