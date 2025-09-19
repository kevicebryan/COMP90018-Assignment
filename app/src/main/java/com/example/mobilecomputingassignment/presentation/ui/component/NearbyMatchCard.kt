package com.example.mobilecomputingassignment.presentation.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.domain.models.Event
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyMatchCard(event: Event, onClick: () -> Unit) {
    Card(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            colors =
                    CardDefaults.cardColors(
                            containerColor = Color.White,
                            contentColor = MaterialTheme.colorScheme.onSurface
                    ),
            border = BorderStroke(width = 0.dp, color = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Teams Column (Left side)
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Home Team
                    Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(40.dp)
                    ) {
                        TeamLogo(teamName = event.matchDetails?.homeTeam ?: "TBD")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                                text = event.matchDetails?.homeTeam ?: "TBD",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                        )
                    }

                    // Away Team
                    Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(40.dp)
                    ) {
                        TeamLogo(teamName = event.matchDetails?.awayTeam ?: "TBD")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                                text = event.matchDetails?.awayTeam ?: "TBD",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Date and Time Column (Right side)
                Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Smart Date Display - Enhanced for today's events
                    val isToday = isEventToday(event.date)
                    Text(
                            text = getSmartDateText(event.date),
                            style =
                                    if (isToday) {
                                        MaterialTheme.typography.titleMedium
                                    } else {
                                        MaterialTheme.typography.bodyMedium
                                    },
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            color =
                                    if (isToday) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                    )

                    // Time - Enhanced for today's events
                    Text(
                            text =
                                    SimpleDateFormat("HH:mm", Locale.getDefault())
                                            .format(event.checkInTime),
                            style =
                                    if (isToday) {
                                        MaterialTheme.typography.titleMedium
                                    } else {
                                        MaterialTheme.typography.bodyMedium
                                    },
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            color =
                                    if (isToday) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Venue
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                        painter = painterResource(id = R.drawable.ic_location),
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                            text = event.location.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                    )
                    Text(
                            text = event.location.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (event.accessibility.isWheelchairAccessible) {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                            painter = painterResource(id = R.drawable.ic_accessible),
                            contentDescription = "Wheelchair accessible",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Bottom Divider
        HorizontalDivider(
                color = MaterialTheme.colorScheme.surfaceVariant,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun getSmartDateText(date: Date): String {
    val today = Calendar.getInstance()
    val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
    val eventCal = Calendar.getInstance().apply { time = date }

    return when {
        isSameDay(today, eventCal) -> "Today"
        isSameDay(tomorrow, eventCal) -> "Tomorrow"
        else -> SimpleDateFormat("d MMM", Locale.getDefault()).format(date)
    }
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun isEventToday(eventDate: Date): Boolean {
    val today = Calendar.getInstance()
    val eventCal = Calendar.getInstance().apply { time = eventDate }
    return isSameDay(today, eventCal)
}
