package com.example.mobilecomputingassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
                ) { WatchMatesApp() }
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

    // Listen to auth state changes
    LaunchedEffect(Unit) {
        auth.addAuthStateListener { firebaseAuth ->
            val newUser = firebaseAuth.currentUser
            // If user changed (logout or new login), clear cached data
            if (currentUser != newUser) {
                if (currentUser != null && newUser == null) {
                    // User logged out, clear all cached data
                    android.util.Log.d("MainActivity", "User logged out, clearing cached data")
                    profileViewModel.clearUserData()
                    eventViewModel.clearEventData()
                    authViewModel.clearAuthData()
                } else if (currentUser == null && newUser != null) {
                    // New user logged in, refresh data
                    android.util.Log.d(
                            "MainActivity",
                            "New user logged in, refreshing data: ${newUser.uid}"
                    )
                    profileViewModel.loadUserProfile()
                    eventViewModel.loadUserEvents()
                }
                currentUser = newUser
            }
        }
    }

    if (currentUser == null) {
        OnboardingScreen(
                onNavigateToMain = {
                    // User successfully authenticated, update state
                    currentUser = auth.currentUser
                }
        )
    } else {
        // User is authenticated, show main app
        MainAppScreen(
                onLogout = {
                    auth.signOut()
                    // Note: currentUser will be set to null by the auth state listener
                }
        )
    }
}
