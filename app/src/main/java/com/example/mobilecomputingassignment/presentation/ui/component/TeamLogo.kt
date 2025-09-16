package com.example.mobilecomputingassignment.presentation.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.data.utils.TeamLogoMapper

@Composable
fun TeamLogo(teamName: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.size(40.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Image(
                painter =
                    painterResource(
                        TeamLogoMapper.getTeamLogoByName(teamName) ?: R.drawable.ic_sports
                    ),
                contentDescription = "$teamName logo",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            )
        }
    }
}
