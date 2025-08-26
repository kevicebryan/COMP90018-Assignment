package com.example.mobilecomputingassignment.data.remote

import com.example.mobilecomputingassignment.data.models.SquiggleTeamsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * SquiggleApiService - External API Integration
 *
 * This service handles communication with the Squiggle API, which provides official AFL (Australian
 * Football League) match and team data.
 *
 * API Endpoint: https://api.squiggle.com.au/
 *
 * Key Concepts:
 * - Retrofit: HTTP client library for API calls
 * - Response<T>: Wrapper for HTTP responses with status codes
 * - @GET: HTTP GET request annotation
 * - @Query: URL query parameter annotation
 *
 * Data Flow:
 * 1. App makes request to Squiggle API
 * 2. API returns JSON response
 * 3. Retrofit converts JSON to Kotlin objects
 * 4. App uses the data for event creation
 */

/**
 * API Interface Definition
 *
 * Defines the HTTP endpoints and parameters for the Squiggle API. Retrofit uses this interface to
 * generate the actual HTTP client.
 */
interface SquiggleApiService {

  /**
   * Get AFL Teams
   *
   * Retrieves all AFL teams with their details.
   *
   * @param query Always "teams" for this endpoint
   * @param year Season year (default: 2024)
   * @param format Response format (default: "json")
   * @return Response containing team data
   */
  @GET("/")
  suspend fun getTeams(
          @Query("q") query: String = "teams",
          @Query("year") year: Int = 2024,
          @Query("format") format: String = "json"
  ): Response<SquiggleTeamsResponse>

  /**
   * Get AFL Games/Matches
   *
   * Retrieves AFL matches for event creation.
   *
   * @param query Always "games" for this endpoint
   * @param complete "!100" means get incomplete games (future matches only)
   * @param year Season year (optional)
   * @return Response containing match data
   */
  @GET("/")
  suspend fun getGames(
          @Query("q") query: String = "games",
          @Query("complete") complete: String = "!100", // Get incomplete games (future matches)
          @Query("year") year: Int? = null
  ): Response<SquiggleGamesResponse>
}

data class SquiggleGamesResponse(val games: List<SquiggleGame>)

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
