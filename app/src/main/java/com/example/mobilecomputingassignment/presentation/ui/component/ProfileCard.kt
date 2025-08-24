package com.example.mobilecomputingassignment.presentation.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.domain.models.User

@Composable
fun ProfileCard(user: User?, isLoading: Boolean, modifier: Modifier = Modifier) {
    Card(
            modifier = modifier.fillMaxWidth(),
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            // Avatar
            Image(
                    painter = painterResource(id = R.drawable.avatar),
                    contentDescription = "Profile Avatar",
                    modifier =
                            Modifier.size(64.dp)
                                    .border(
                                            width = 2.dp,
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = CircleShape
                                    )
                                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // User Info
            Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else if (user != null) {
                    UserInfo(user = user)
                } else {
                    DefaultUserInfo()
                }
            }
        }
    }
}

@Composable
private fun UserInfo(user: User) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
                text = "@${user.username.ifEmpty { "username" }}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
        )
        Text(
                text = user.email.ifEmpty { "email@example.com" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        PointsDisplay(points = user.points)
    }
}

@Composable
private fun DefaultUserInfo() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
                text = "@username",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
        )
        Text(
                text = "email@example.com",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        PointsDisplay(points = 0)
    }
}

@Composable
private fun PointsDisplay(points: Long) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
                painter = painterResource(id = R.drawable.ic_points),
                contentDescription = "Points",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
                text = "$points points",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
        )
    }
}
