package com.example.mobilecomputingassignment

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.example.mobilecomputingassignment.ui.theme.WatchmatesTheme

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
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var currentUser by remember { mutableStateOf(auth.currentUser) }
    var isLoading by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("Firebase status: Checking...") }

    // Google Sign-In setup
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            auth.signInWithCredential(credential)
                .addOnCompleteListener { authResult ->
                    isLoading = false
                    if (authResult.isSuccessful) {
                        currentUser = auth.currentUser
                        Toast.makeText(context, "Sign in successful!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Sign in failed: ${authResult.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        } catch (e: ApiException) {
            isLoading = false
            Toast.makeText(context, "Google sign-in failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Test Firebase connections
    LaunchedEffect(currentUser) {
        try {
            val authStatus = if (currentUser != null) "✅ Auth: Connected" else "❌ Auth: Not signed in"

            // Test Firestore
            firestore.collection("test").limit(1).get()
                .addOnSuccessListener {
                    statusText = "$authStatus\n✅ Firestore: Connected"
                }
                .addOnFailureListener { e ->
                    statusText = "$authStatus\n❌ Firestore: ${e.message}"
                }
        } catch (e: Exception) {
            statusText = "❌ Firebase setup error: ${e.message}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App title using Racing Sans One (headers)
        Text(
            text = "🎬 WatchMates Firebase Test",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Status header using Racing Sans One
                Text(
                    text = "Status:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                // Status content using Noto Sans
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium
                )

                currentUser?.let { user ->
                    Spacer(modifier = Modifier.height(8.dp))
                    // User info using Noto Sans
                    Text(
                        text = "User: ${user.displayName ?: user.email}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (currentUser == null) {
            Button(
                onClick = {
                    isLoading = true
                    val signInIntent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,  // Orange theme color
                    contentColor = MaterialTheme.colorScheme.onPrimary   // White text
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    // Button text using Noto Sans
                    Text(
                        text = "Signing in...",
                        style = MaterialTheme.typography.labelLarge
                    )
                } else {
                    // Button text using Noto Sans
                    Text(
                        text = "🔐 Sign in with Google",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        } else {
            Button(
                onClick = {
                    auth.signOut()
                    currentUser = null
                    Toast.makeText(context, "Signed out", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,    // Red for sign out
                    contentColor = MaterialTheme.colorScheme.onError     // White text
                )
            ) {
                // Button text using Noto Sans
                Text(
                    text = "Sign Out",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Help text using Noto Sans
        Text(
            text = "If both Auth and Firestore show ✅, Firebase is working!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}