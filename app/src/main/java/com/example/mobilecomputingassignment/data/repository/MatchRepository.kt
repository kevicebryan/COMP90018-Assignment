package com.example.mobilecomputingassignment.data.repository

import android.util.Log
import com.example.mobilecomputingassignment.data.remote.SquiggleApiService
import com.example.mobilecomputingassignment.domain.models.MatchDetails
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchRepository @Inject constructor(private val squiggleApiService: SquiggleApiService) {
  companion object {
    private const val TAG = "MatchRepository"
    private val API_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
  }

  suspend fun getMatchesForDate(date: Date): Result<List<MatchDetails>> {
    return try {
      val calendar = Calendar.getInstance().apply { time = date }
      val year = calendar.get(Calendar.YEAR)

      val response = squiggleApiService.getGames(year = year)

      if (response.isSuccessful) {
        val games = response.body()?.games ?: emptyList()

        // Filter games by the selected date
        val targetDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)

        val matchingGames =
                games.filter { game ->
                  val gameDate = game.date.substring(0, 10) // Extract "yyyy-MM-dd" part
                  gameDate == targetDateString && game.complete == 0 // Only future/incomplete games
                }

        val matches =
                matchingGames.map { game ->
                  MatchDetails(
                          id = game.id.toString(),
                          homeTeam = game.hteam,
                          awayTeam = game.ateam,
                          competition = "AFL",
                          venue = game.venue,
                          matchTime = parseApiDate(game.date),
                          round = game.roundname,
                          season = game.year.toString()
                  )
                }

        Log.d(TAG, "Found ${matches.size} matches for date: $targetDateString")
        Result.success(matches)
      } else {
        Log.e(TAG, "API request failed: ${response.code()} - ${response.message()}")
        Result.failure(Exception("Failed to fetch matches: ${response.message()}"))
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error fetching matches", e)
      Result.failure(e)
    }
  }

  suspend fun getAllUpcomingMatches(): Result<List<MatchDetails>> {
    return try {
      val currentYear = Calendar.getInstance().get(Calendar.YEAR)
      val response = squiggleApiService.getGames(year = currentYear)

      if (response.isSuccessful) {
        val games = response.body()?.games ?: emptyList()

        // Filter for upcoming games only
        val upcomingGames =
                games.filter { game ->
                  game.complete == 0 // Only future/incomplete games
                }

        val matches =
                upcomingGames.map { game ->
                  MatchDetails(
                          id = game.id.toString(),
                          homeTeam = game.hteam,
                          awayTeam = game.ateam,
                          competition = "AFL",
                          venue = game.venue,
                          matchTime = parseApiDate(game.date),
                          round = game.roundname,
                          season = game.year.toString()
                  )
                }

        Log.d(TAG, "Found ${matches.size} upcoming matches")
        Result.success(matches)
      } else {
        Log.e(TAG, "API request failed: ${response.code()} - ${response.message()}")
        Result.failure(Exception("Failed to fetch matches: ${response.message()}"))
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error fetching upcoming matches", e)
      Result.failure(e)
    }
  }

  private fun parseApiDate(dateString: String): Date {
    return try {
      API_DATE_FORMAT.parse(dateString) ?: Date()
    } catch (e: Exception) {
      Log.w(TAG, "Failed to parse date: $dateString", e)
      Date()
    }
  }
}
