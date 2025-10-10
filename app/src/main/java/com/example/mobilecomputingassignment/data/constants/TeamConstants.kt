package com.example.mobilecomputingassignment.data.constants

import com.example.mobilecomputingassignment.data.utils.TeamLogoMapper
import com.example.mobilecomputingassignment.domain.models.Team

/**
 * Constant team data for AFL teams based on Squiggle API response This eliminates the need to fetch
 * team data from external APIs
 */
object TeamConstants {

        /**
         * All AFL teams as constant data Based on Squiggle API response from
         * https://api.squiggle.com.au/?q=teams
         */
        val AFL_TEAMS =
                listOf(
                        Team(
                                id = 1,
                                name = "Adelaide",
                                abbreviation = "ADE",
                                league = "AFL",
                                localLogoRes = TeamLogoMapper.getTeamLogoByName("Adelaide")
                        ),
                        Team(
                                id = 2,
                                name = "Brisbane Lions",
                                abbreviation = "BRI",
                                league = "AFL",
                                localLogoRes = TeamLogoMapper.getTeamLogoByName("Brisbane Lions")
                        ),
                        Team(
                                id = 3,
                                name = "Carlton",
                                abbreviation = "CAR",
                                league = "AFL",
                                localLogoRes = TeamLogoMapper.getTeamLogoByName("Carlton")
                        ),
                        Team(
                                id = 4,
                                name = "Collingwood",
                                abbreviation = "COL",
                                league = "AFL",
                                localLogoRes = TeamLogoMapper.getTeamLogoByName("Collingwood")
                        ),
                        Team(
                                id = 5,
                                name = "Essendon",
                                abbreviation = "ESS",
                                league = "AFL",
                                localLogoRes = TeamLogoMapper.getTeamLogoByName("Essendon")
                        ),
                        Team(
                                id = 6,
                                name = "Fremantle",
                                abbreviation = "FRE",
                                league = "AFL",
                                localLogoRes = TeamLogoMapper.getTeamLogoByName("Fremantle")
                        ),
                        Team(
                                id = 7,
                                name = "Geelong",
                                abbreviation = "GEE",
                                league = "AFL",
                                localLogoRes = TeamLogoMapper.getTeamLogoByName("Geelong")
                        ),
                        Team(
                                id = 8,
                                name = "Gold Coast",
                                abbreviation = "GCS",
                                league = "AFL",
                                localLogoRes = TeamLogoMapper.getTeamLogoByName("Gold Coast")
                        ),
                        Team(
                                id = 9,
                                name = "Greater Western Sydney",
                                abbreviation = "GWS",
                                league = "AFL",
                                localLogoRes =
                                        TeamLogoMapper.getTeamLogoByName("Greater Western Sydney")
                        ),
                        Team(
                                id = 10,
                                name = "Hawthorn",
                                abbreviation = "HAW",
                                league = "AFL",
                                localLogoRes = TeamLogoMapper.getTeamLogoByName("Hawthorn")
                        ),
                        Team(
                                id = 11,
                                name = "Melbourne",
                                abbreviation = "MEL",
                                league = "AFL",
                                localLogoRes = TeamLogoMapper.getTeamLogoByName("Melbourne")
                        ),
                        Team(
                                id = 12,
                                name = "North Melbourne",
                                abbreviation = "NOR",
                                league = "AFL",
                                localLogoRes = TeamLogoMapper.getTeamLogoByName("North Melbourne")
                        ),
                        Team(
                                id = 13,
                                name = "Port Adelaide",
                                abbreviation = "POR",
                                league = "AFL",
                                localLogoRes = TeamLogoMapper.getTeamLogoByName("Port Adelaide")
                        ),
                        Team(
                                id = 14,
                                name = "Richmond",
                                abbreviation = "RIC",
                                league = "AFL",
                                localLogoRes = TeamLogoMapper.getTeamLogoByName("Richmond")
                        ),
                        Team(
                                id = 15,
                                name = "St Kilda",
                                abbreviation = "STK",
                                league = "AFL",
                                localLogoRes = TeamLogoMapper.getTeamLogoByName("St Kilda")
                        ),
                        Team(
                                id = 16,
                                name = "Sydney",
                                abbreviation = "SYD",
                                league = "AFL",
                                localLogoRes = TeamLogoMapper.getTeamLogoByName("Sydney")
                        ),
                        Team(
                                id = 17,
                                name = "West Coast",
                                abbreviation = "WCE",
                                league = "AFL",
                                localLogoRes = TeamLogoMapper.getTeamLogoByName("West Coast")
                        ),
                        Team(
                                id = 18,
                                name = "Western Bulldogs",
                                abbreviation = "WBD",
                                league = "AFL",
                                localLogoRes = TeamLogoMapper.getTeamLogoByName("Western Bulldogs")
                        )
                )

        /** List of all F1 Grand Prix races in the 2024 season */
        val F1_GRAND_PRIX =
                arrayOf(
                        "Bahrain Grand Prix",
                        "Saudi Arabian Grand Prix",
                        "Australian Grand Prix",
                        "Japanese Grand Prix",
                        "Chinese Grand Prix",
                        "Miami Grand Prix",
                        "Emilia Romagna Grand Prix",
                        "Monaco Grand Prix",
                        "Canadian Grand Prix",
                        "Spanish Grand Prix",
                        "Austrian Grand Prix",
                        "British Grand Prix",
                        "Hungarian Grand Prix",
                        "Belgian Grand Prix",
                        "Dutch Grand Prix",
                        "Italian Grand Prix",
                        "Azerbaijan Grand Prix",
                        "Singapore Grand Prix",
                        "United States Grand Prix",
                        "Mexico City Grand Prix",
                        "São Paulo Grand Prix",
                        "Las Vegas Grand Prix",
                        "Qatar Grand Prix",
                        "Abu Dhabi Grand Prix"
                )

        /**
         * Get teams by league name
         * @param leagueName The league name (e.g., "AFL")
         * @return List of teams in the specified league
         */
        fun getTeams(leagueName: String): List<Team> {
                return when (leagueName.uppercase()) {
                        "AFL" -> AFL_TEAMS
                        else -> emptyList()
                }
        }

        /**
         * Get all available leagues
         * @return Set of available league names
         */
        fun getAvailableLeagues(): Set<String> {
                return setOf("AFL", "F1")
        }

        /**
         * Get team by ID
         * @param teamId The team ID
         * @return Team if found, null otherwise
         */
        fun getTeamById(teamId: Int): Team? {
                return AFL_TEAMS.find { it.id == teamId }
        }

        /**
         * Get team by name
         * @param teamName The team name
         * @return Team if found, null otherwise
         */
        fun getTeamByName(teamName: String): Team? {
                return AFL_TEAMS.find { it.name.equals(teamName, ignoreCase = true) }
        }

        /**
         * Get team by abbreviation
         * @param abbreviation The team abbreviation
         * @return Team if found, null otherwise
         */
        fun getTeamByAbbreviation(abbreviation: String): Team? {
                return AFL_TEAMS.find { it.abbreviation.equals(abbreviation, ignoreCase = true) }
        }
}
