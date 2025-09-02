// In: app/src/main/java/com/example/mobilecomputingassignment/MainActivity.kt

package com.example.mobilecomputingassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobilecomputingassignment.presentation.ui.screen.MainAppScreen
import com.example.mobilecomputingassignment.presentation.ui.screen.OnboardingScreen
import com.example.mobilecomputingassignment.presentation.ui.theme.WatchmatesTheme
import com.example.mobilecomputingassignment.presentation.viewmodel.AuthViewModel
import com.example.mobilecomputingassignment.presentation.viewmodel.EventViewModel
import com.example.mobilecomputingassignment.presentation.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WatchmatesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WatchMatesApp()
                }
            }
        }
    }
}

@Composable
fun WatchMatesApp() {
    val auth = FirebaseAuth.getInstance()
    var currentUser by remember { mutableStateOf(auth.currentUser) }

    // Get ViewModels to clear their cached data on logout
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val eventViewModel: EventViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()

    // Listen to auth state changes to automatically update the UI and clear data
    LaunchedEffect(Unit) {
        auth.addAuthStateListener { firebaseAuth ->
            val newUser = firebaseAuth.currentUser
            // Check if the user state has actually changed
            if (currentUser != newUser) {
                if (currentUser != null && newUser == null) {
                    // User logged out, clear all cached data to prevent data leaks
                    profileViewModel.clearUserData()
                    eventViewModel.clearEventData()
                    authViewModel.clearAuthData()
                }
                // Update the state to trigger recomposition
                currentUser = newUser
            }
        }
    }

    if (currentUser == null) {
        // If no user is logged in, show the onboarding/login screen
        OnboardingScreen(
            onNavigateToMain = {
                // This is called after a successful login/signup
                currentUser = auth.currentUser
            }
        )
    } else {
        // If a user is logged in, show the main application
        MainAppScreen(
            onLogout = {
                auth.signOut()
                // The auth state listener above will handle the UI update
            }
        )
    }
}
