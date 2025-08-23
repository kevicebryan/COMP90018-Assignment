package com.example.mobilecomputingassignment.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobilecomputingassignment.presentation.viewmodel.AuthViewModel
import com.example.mobilecomputingassignment.presentation.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(onLogout: () -> Unit, viewModel: AuthViewModel = hiltViewModel()) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val signupData by viewModel.signupData.collectAsState()
  val currentUser = FirebaseAuth.getInstance().currentUser

  Scaffold(
          bottomBar = {
            NavigationBar {
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
                        onClick = { selectedTab = index }
                )
              }
            }
          }
  ) { innerPadding ->
    Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
    ) {
      when (selectedTab) {
        0 -> ProfileScreen(onLogout = onLogout)
        1 -> ExploreScreen()
        2 -> EventsScreen()
        3 -> CheckInScreen()
      }
    }
  }
}

@Composable
fun ProfileScreen(onLogout: () -> Unit, profileViewModel: ProfileViewModel = hiltViewModel()) {
  val profileState by profileViewModel.uiState.collectAsState()
  Column(
          modifier = Modifier.fillMaxSize().padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
    )

    Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
    ) {
      Column(
              modifier = Modifier.padding(20.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        if (profileState.isLoading) {
          Box(
                  modifier = Modifier.fillMaxWidth().height(200.dp),
                  contentAlignment = Alignment.Center
          ) { CircularProgressIndicator() }
        } else if (profileState.user != null) {
          val user = profileState.user!!
          ProfileItem(label = "Username", value = user.username.ifEmpty { "Not set" })
          ProfileItem(label = "Email", value = user.email.ifEmpty { "Not set" })
          ProfileItem(label = "Points", value = "${user.points}")
          ProfileItem(label = "Level", value = user.getPointsLevel())
          ProfileItem(
                  label = "Supported Teams",
                  value =
                          if (user.teams.isNotEmpty()) user.teams.joinToString(", ")
                          else "None selected"
          )
          ProfileItem(
                  label = "Leagues",
                  value =
                          if (user.leagues.isNotEmpty()) user.leagues.joinToString(", ")
                          else "None selected"
          )
        } else {
          // Error or no user data
          Text(
                  text = profileState.errorMessage ?: "No profile data available",
                  color = MaterialTheme.colorScheme.error,
                  style = MaterialTheme.typography.bodyMedium
          )
        }
      }
    }

    Spacer(modifier = Modifier.weight(1f))

    // Logout button at the bottom
    OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors =
                    ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                    )
    ) {
      Icon(
              imageVector = Icons.AutoMirrored.Filled.ExitToApp,
              contentDescription = "Logout",
              modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text("Logout")
    }
  }
}

@Composable
fun ProfileItem(label: String, value: String) {
  Column {
    Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
  }
}

@Composable
fun ExploreScreen() {
  Text(text = "Hello World - Explore", style = MaterialTheme.typography.headlineMedium)
}

@Composable
fun EventsScreen() {
  Text(text = "Hello World - Events", style = MaterialTheme.typography.headlineMedium)
}

@Composable
fun CheckInScreen() {
  Text(text = "Hello World - Check-in", style = MaterialTheme.typography.headlineMedium)
}

data class NavigationItem(
        val label: String,
        val icon: androidx.compose.ui.graphics.vector.ImageVector
)
