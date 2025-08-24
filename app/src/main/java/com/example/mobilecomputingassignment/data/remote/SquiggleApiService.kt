package com.example.mobilecomputingassignment.data.remote

import com.example.mobilecomputingassignment.data.models.SquiggleTeamsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SquiggleApiService {
  @GET("/")
  suspend fun getTeams(
          @Query("q") query: String = "teams",
          @Query("year") year: Int = 2024,
          @Query("format") format: String = "json"
  ): Response<SquiggleTeamsResponse>

  @GET("/")
  suspend fun getGames(
          @Query("q") query: String = "games",
          @Query("complete") complete: String = "!100", // Get incomplete games (future matches)
          @Query("year") year: Int? = null
  ): Response<SquiggleGamesResponse>
}

data class SquiggleGamesResponse(
    val games: List<SquiggleGame>
)

data class SquiggleGame(
    val id: Int,
    val venue: String,
    val hteam: String, // Home team
    val ateam: String, // Away team
    val hteamid: Int,
    val ateamid: Int,
    val date: String, // "2025-08-24 12:20:00"
    val localtime: String,
    val unixtime: Long,
    val timestr: String?,
    val round: Int,
    val roundname: String,
    val year: Int,
    val complete: Int, // 0 = not started, 100 = finished
    val is_final: Int,
    val is_grand_final: Int,
    val winner: String?,
    val winnerteamid: Int?,
    val hscore: Int?,
    val ascore: Int?,
    val hgoals: Int?,
    val agoals: Int?,
    val hbehinds: Int?,
    val abehinds: Int?,
    val updated: String,
    val tz: String
)
