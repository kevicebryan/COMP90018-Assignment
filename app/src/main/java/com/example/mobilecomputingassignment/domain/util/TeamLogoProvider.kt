package com.example.mobilecomputingassignment.domain.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.mobilecomputingassignment.R

object TeamLogoProvider {
  private val teamLogoMap =
          mapOf(
                  "Manchester United" to R.drawable.ic_team_manchester_united,
                  "Sheffield Wednesday" to R.drawable.ic_team_sheffield_wednesday,
                  // Add more team mappings here
                  )

  @Composable
  fun getTeamLogo(teamName: String): Painter {
    val resourceId = teamLogoMap[teamName] ?: R.drawable.ic_team_default
    return painterResource(id = resourceId)
  }

  fun getTeamLogoResourceId(teamName: String): Int {
    return teamLogoMap[teamName] ?: R.drawable.ic_team_default
  }
}
