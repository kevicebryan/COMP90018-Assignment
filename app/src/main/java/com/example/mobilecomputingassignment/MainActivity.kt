package com.example.mobilecomputingassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.mobilecomputingassignment.presentation.ui.screen.MainAppScreen
import com.example.mobilecomputingassignment.presentation.ui.screen.OnboardingScreen
import com.example.mobilecomputingassignment.presentation.ui.theme.WatchmatesTheme
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

    // Listen to auth state changes
    LaunchedEffect(Unit) {
        auth.addAuthStateListener { firebaseAuth -> currentUser = firebaseAuth.currentUser }
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
                    currentUser = null
                }
        )
    }
}
