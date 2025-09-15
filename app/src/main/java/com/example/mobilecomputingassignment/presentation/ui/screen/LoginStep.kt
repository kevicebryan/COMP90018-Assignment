package com.example.mobilecomputingassignment.presentation.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.presentation.ui.component.ForgotPasswordDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginStep(
        onLoginClick: (String, String) -> Unit,
        onGoogleSignInClick: (String) -> Unit,
        onForgotPasswordClick: (String) -> Unit,
        onBackClick: () -> Unit,
        isLoading: Boolean,
        errorMessage: String?
) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        var showForgotPasswordDialog by remember { mutableStateOf(false) }
        val context = LocalContext.current

        val googleSignInLauncher =
                rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        try {
                                val account = task.getResult(ApiException::class.java)
                                account.idToken?.let { idToken -> onGoogleSignInClick(idToken) }
                        } catch (e: ApiException) {
                                // Handle error
                        }
                }

        SignupLayout(
                title = "Welcome back",
                subtitle = "Sign in to your WatchMates account",
                onBackClick = onBackClick,
                currentStep = 0,
                totalSteps = 0,
                buttonText = "Log in",
                buttonEnabled = !isLoading && email.isNotEmpty() && password.isNotEmpty(),
                isLoading = isLoading,
                onButtonClick = { onLoginClick(email, password) },
                errorMessage = errorMessage
        ) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                placeholder = { Text("Your Email") },
                                leadingIcon = {
                                        Icon(Icons.Default.Email, contentDescription = null)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = errorMessage != null && email.isEmpty()
                        )

                        OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                placeholder = { Text("Your Password") },
                                leadingIcon = {
                                        Icon(Icons.Default.Lock, contentDescription = null)
                                },
                                trailingIcon = {
                                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                                Icon(
                                                        painter = painterResource(
                                                                if (passwordVisible) R.drawable.visibility_icon
                                                                else R.drawable.visibility_off_icon
                                                        ),
                                                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                                )
                                        }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = errorMessage != null && password.isEmpty()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Divider with "or" text
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                HorizontalDivider(modifier = Modifier.weight(1f))
                                Text(
                                        text = "or",
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                HorizontalDivider(modifier = Modifier.weight(1f))
                        }

                        OutlinedButton(
                                onClick = {
                                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                .requestIdToken(context.getString(R.string.default_web_client_id))
                                                .requestEmail()
                                                .build()

                                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                                },
                                modifier = Modifier.fillMaxWidth().height(40.dp),
                                enabled = !isLoading,
                                shape = RoundedCornerShape(16.dp)
                        ) {
                                Text(
                                        "Continue with Google",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontSize = 16.sp
                                )
                        }

                        TextButton(
                                onClick = { showForgotPasswordDialog = true },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                                Text(
                                        "Forgot Password?",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                )
                        }
                }
        }

        if (showForgotPasswordDialog) {
                ForgotPasswordDialog(
                        onSendResetEmail = { resetEmail ->
                                onForgotPasswordClick(resetEmail)
                                showForgotPasswordDialog = false
                        },
                        onDismiss = { showForgotPasswordDialog = false },
                        isLoading = isLoading
                )
        }
}

