package com.example.mobilecomputingassignment.presentation.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(val label: String, val icon: ImageVector)

@Composable
fun WatchMatesBottomNavigation(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        val tabs =
                listOf(
                        NavigationItem("Profile", Icons.Default.Person),
                        NavigationItem("Explore", Icons.Default.Search),
                        NavigationItem("Events", Icons.Default.DateRange),
                        NavigationItem("Check-in", Icons.Default.LocationOn)
                )

        tabs.forEachIndexed { index, item ->
            NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) },
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    colors =
                            NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor =
                                            MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
            )
        }
    }
}
