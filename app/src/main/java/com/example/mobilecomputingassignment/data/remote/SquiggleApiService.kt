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
}
