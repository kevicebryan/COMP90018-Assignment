package com.example.mobilecomputingassignment.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobilecomputingassignment.domain.util.TeamLogoProvider

@Composable
fun TeamLogo(teamName: String) {
  Card(
          modifier = Modifier.size(40.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Image(
              painter = TeamLogoProvider.getTeamLogo(teamName),
              contentDescription = "$teamName logo",
              modifier = Modifier.fillMaxSize().padding(4.dp)
      )
    }
  }
}
