package com.example.mobilecomputingassignment.presentation.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.presentation.viewmodel.ProfileViewModel
import java.text.NumberFormat
import java.util.Locale

// Available avatars with their costs
data class AvatarInfo(
        val drawableName: String,
        val displayName: String,
        val cost: Int, // 0 for default
        val drawableResId: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarSelectionScreen(
        onBackClick: () -> Unit,
        profileViewModel: ProfileViewModel = hiltViewModel()
) {
  val profileState by profileViewModel.uiState.collectAsState()
  val user = profileState.user

  // All available avatars
  val availableAvatars =
          listOf(
                  AvatarInfo("avatar_default", "Default", 0, R.drawable.avatar_default),
                  AvatarInfo("avatar_100", "Bronze Fan", 100, R.drawable.avatar_100),
                  AvatarInfo("avatar_200", "Silver Fan", 200, R.drawable.avatar_200),
                  AvatarInfo("avatar_300", "Gold Fan", 300, R.drawable.avatar_300),
                  AvatarInfo("avatar_400", "Platinum Fan", 400, R.drawable.avatar_400),
                  AvatarInfo("avatar_500", "Diamond Fan", 500, R.drawable.avatar_500)
          )

  var showUnlockDialog by remember { mutableStateOf(false) }
  var avatarToUnlock by remember { mutableStateOf<AvatarInfo?>(null) }

  Scaffold(
          topBar = {
            TopAppBar(
                    title = { Text("Select Avatar") },
                    navigationIcon = {
                      IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                      }
                    }
            )
          }
  ) { paddingValues ->
    Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
      if (profileState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          CircularProgressIndicator()
        }
      } else if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text(
                  text = "Unable to load user data",
                  style = MaterialTheme.typography.bodyLarge,
                  textAlign = TextAlign.Center
          )
        }
      } else {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
          // Points display
          val formattedPoints =
                  NumberFormat.getNumberInstance(Locale.getDefault()).format(user.points)

          Card(
                  modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                  colors =
                          CardDefaults.cardColors(
                                  containerColor = MaterialTheme.colorScheme.primaryContainer
                          )
          ) {
            Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = "Your Points",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                        text = formattedPoints,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                )
              }
            }
          }

          // Avatar grid
          LazyColumn(
                  verticalArrangement = Arrangement.spacedBy(12.dp),
                  modifier = Modifier.fillMaxSize()
          ) {
            items(availableAvatars.chunked(3).size) { rowIndex ->
              val avatarRow = availableAvatars.chunked(3)[rowIndex]
              Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                avatarRow.forEach { avatar ->
                  val isUnlocked = user.unlockedAvatars.contains(avatar.drawableName)
                  val isSelected = user.selectedAvatar == avatar.drawableName

                  AvatarCard(
                          avatar = avatar,
                          isUnlocked = isUnlocked,
                          isSelected = isSelected,
                          userPoints = user.points,
                          onAvatarClick = {
                            if (isUnlocked) {
                              // Select this avatar
                              profileViewModel.updateUserAvatar(avatar.drawableName)
                            } else {
                              // Show unlock dialog
                              avatarToUnlock = avatar
                              showUnlockDialog = true
                            }
                          },
                          modifier = Modifier.weight(1f)
                  )
                }

                // Fill empty columns
                repeat(3 - avatarRow.size) { Spacer(Modifier.weight(1f)) }
              }
            }
          }
        }
      }
    }
  }

  // Unlock confirmation dialog
  if (showUnlockDialog && avatarToUnlock != null) {
    val avatar = avatarToUnlock!!
    val canAfford = user?.points ?: 0L >= avatar.cost
    val formattedCost = NumberFormat.getNumberInstance(Locale.getDefault()).format(avatar.cost)
    val formattedUserPoints =
            NumberFormat.getNumberInstance(Locale.getDefault()).format(user?.points ?: 0)

    AlertDialog(
            onDismissRequest = { showUnlockDialog = false },
            title = { Text("Unlock ${avatar.displayName}") },
            text = {
              Column {
                Text("Cost: $formattedCost points")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Your points: $formattedUserPoints")

                if (!canAfford) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                          "You don't have enough points to unlock this avatar.",
                          color = MaterialTheme.colorScheme.error,
                          style = MaterialTheme.typography.bodySmall
                  )
                }
              }
            },
            confirmButton = {
              if (canAfford) {
                TextButton(
                        onClick = {
                          profileViewModel.unlockAvatar(avatar.drawableName, avatar.cost)
                          showUnlockDialog = false
                          avatarToUnlock = null
                        }
                ) { Text("Unlock") }
              }
            },
            dismissButton = {
              TextButton(
                      onClick = {
                        showUnlockDialog = false
                        avatarToUnlock = null
                      }
              ) { Text("Cancel") }
            }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarCard(
        avatar: AvatarInfo,
        isUnlocked: Boolean,
        isSelected: Boolean,
        userPoints: Long,
        onAvatarClick: () -> Unit,
        modifier: Modifier = Modifier
) {
  val canAfford = userPoints >= avatar.cost

  Card(
          onClick = onAvatarClick,
          modifier = modifier.aspectRatio(1f),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
  ) {
    Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
    ) {
      // Avatar image in circle with 1:1 aspect ratio
      Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
        Image(
                painter = painterResource(id = avatar.drawableResId),
                contentDescription = avatar.displayName,
                modifier =
                        Modifier.fillMaxSize()
                                .clip(CircleShape)
                                .then(
                                        if (isSelected) {
                                          Modifier.border(
                                                  3.dp,
                                                  Color(0xFFFF9800), // Orange border
                                                  CircleShape
                                          )
                                        } else {
                                          Modifier
                                        }
                                ),
                contentScale = ContentScale.Crop,
                alpha = if (isUnlocked) 1f else 0.5f
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Avatar name
      Text(
              text = avatar.displayName,
              style = MaterialTheme.typography.labelSmall,
              textAlign = TextAlign.Center,
              maxLines = 1,
              fontWeight = FontWeight.Medium
      )

      // Cost or status
      if (isSelected) {
        Text(
                text = "Selected",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFFF9800),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
        )
      } else {
        val formattedCost = NumberFormat.getNumberInstance(Locale.getDefault()).format(avatar.cost)
        if (!isUnlocked) {
          Text(
                  text = "$formattedCost pts",
                  style = MaterialTheme.typography.labelSmall,
                  color =
                          if (canAfford) MaterialTheme.colorScheme.primary
                          else MaterialTheme.colorScheme.error,
                  textAlign = TextAlign.Center
          )
        } else {
          Text(
                  text = "Unlocked",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                  textAlign = TextAlign.Center
          )
        }
      }
    }
  }
}
