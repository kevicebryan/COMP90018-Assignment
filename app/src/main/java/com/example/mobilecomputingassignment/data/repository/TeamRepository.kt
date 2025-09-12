package com.example.mobilecomputingassignment.data.repository

import com.example.mobilecomputingassignment.data.constants.TeamConstants
import com.example.mobilecomputingassignment.domain.models.Team
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamRepository @Inject constructor() {

  /**
   * Get AFL teams from constant data (no API call needed)
   * @return Result containing list of AFL teams
   */
  suspend fun getAflTeams(): Result<List<Team>> {
    return try {
      val teams = TeamConstants.getTeams("AFL")
      Result.success(teams)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  /**
   * Get teams by league name
   * @param leagueName The league name (e.g., "AFL")
   * @return Result containing list of teams in the specified league
   */
  suspend fun getTeams(leagueName: String): Result<List<Team>> {
    return try {
      val teams = TeamConstants.getTeams(leagueName)
      Result.success(teams)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  /**
   * Get team by ID
   * @param teamId The team ID
   * @return Result containing the team if found
   */
  suspend fun getTeamById(teamId: Int): Result<Team?> {
    return try {
      val team = TeamConstants.getTeamById(teamId)
      Result.success(team)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  /**
   * Get team by name
   * @param teamName The team name
   * @return Result containing the team if found
   */
  suspend fun getTeamByName(teamName: String): Result<Team?> {
    return try {
      val team = TeamConstants.getTeamByName(teamName)
      Result.success(team)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  /**
   * Get team by abbreviation
   * @param abbreviation The team abbreviation
   * @return Result containing the team if found
   */
  suspend fun getTeamByAbbreviation(abbreviation: String): Result<Team?> {
    return try {
      val team = TeamConstants.getTeamByAbbreviation(abbreviation)
      Result.success(team)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  /**
   * Get all available leagues
   * @return Result containing set of available league names
   */
  suspend fun getAvailableLeagues(): Result<Set<String>> {
    return try {
      val leagues = TeamConstants.getAvailableLeagues()
      Result.success(leagues)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
