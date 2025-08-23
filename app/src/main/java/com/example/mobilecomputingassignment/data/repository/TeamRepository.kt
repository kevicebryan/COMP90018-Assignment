package com.example.mobilecomputingassignment.data.repository

import com.example.mobilecomputingassignment.data.remote.SquiggleApiService
import com.example.mobilecomputingassignment.data.utils.TeamLogoMapper
import com.example.mobilecomputingassignment.domain.models.Team
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamRepository @Inject constructor(private val squiggleApi: SquiggleApiService) {
  suspend fun getAflTeams(): Result<List<Team>> {
    return try {
      val response = squiggleApi.getTeams()
      if (response.isSuccessful) {
        val teams =
                response.body()?.teams?.map { teamDto ->
                  Team(
                          id = teamDto.id,
                          name = teamDto.name,
                          abbreviation = teamDto.abbreviation,
                          logoUrl = teamDto.logo?.let { "https://api.squiggle.com.au$it" },
                          localLogoRes = TeamLogoMapper.getTeamLogoByName(teamDto.name)
                  )
                }
                        ?: emptyList()
        Result.success(teams)
      } else {
        Result.failure(Exception("Failed to fetch teams: ${response.message()}"))
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
