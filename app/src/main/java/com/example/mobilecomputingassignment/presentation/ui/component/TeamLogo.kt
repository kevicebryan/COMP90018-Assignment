package com.example.mobilecomputingassignment.presentation.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.data.utils.TeamLogoMapper

@Composable
fun TeamLogo(teamName: String) {
  Card(
          modifier = Modifier.size(40.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Image(
              painter =
                      painterResource(
                              TeamLogoMapper.getTeamLogoByName(teamName) ?: R.drawable.ic_sports
                      ),
              contentDescription = "$teamName logo",
              modifier = Modifier.fillMaxSize().padding(4.dp)
      )
    }
  }
}
