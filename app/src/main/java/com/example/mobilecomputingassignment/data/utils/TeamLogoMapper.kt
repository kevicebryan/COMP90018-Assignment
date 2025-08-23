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
  private val teamLogoMap =
          mapOf(
                  // Available PNG logos
                  "Brisbane Lions" to R.drawable.team_brisbane_lions,
                  "Collingwood" to R.drawable.team_collingwood,
                  "Essendon" to R.drawable.team_essendon,
                  "Fremantle" to R.drawable.team_fremantle,
                  "Greater Western Sydney" to R.drawable.team_greater_western_sydney,
                  "Hawthorn" to R.drawable.team_hawthorn,
                  "Melbourne" to R.drawable.team_melbourne,
                  "North Melbourne" to R.drawable.team_north_melbourne,
                  "Port Adelaide" to R.drawable.team_port_adelaide,
                  "St Kilda" to R.drawable.team_st_kilda,
                  "Sydney" to R.drawable.team_sydney,
                  "West Coast" to R.drawable.team_west_coast,
                  "Western Bulldogs" to R.drawable.team_western_bulldogs
                  // Missing logos (need PNG conversion): Adelaide, Carlton, Geelong, Gold Coast,
                  // Richmond
                  )

  /**
   * Maps team abbreviations to local drawable resources Only includes teams with available PNG
   * logos
   */
  private val teamAbbrevLogoMap =
          mapOf(
                  // Available PNG logos
                  "BRI" to R.drawable.team_brisbane_lions,
                  "COL" to R.drawable.team_collingwood,
                  "ESS" to R.drawable.team_essendon,
                  "FRE" to R.drawable.team_fremantle,
                  "GWS" to R.drawable.team_greater_western_sydney,
                  "HAW" to R.drawable.team_hawthorn,
                  "MEL" to R.drawable.team_melbourne,
                  "NOR" to R.drawable.team_north_melbourne,
                  "POR" to R.drawable.team_port_adelaide,
                  "STK" to R.drawable.team_st_kilda,
                  "SYD" to R.drawable.team_sydney,
                  "WCE" to R.drawable.team_west_coast,
                  "WBD" to R.drawable.team_western_bulldogs
                  // Missing logos (need PNG conversion): ADE, CAR, GEE, GCS, RIC
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
