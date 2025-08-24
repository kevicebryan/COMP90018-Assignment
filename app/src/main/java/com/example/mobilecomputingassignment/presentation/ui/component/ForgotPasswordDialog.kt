package com.example.mobilecomputingassignment.presentation.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun ForgotPasswordDialog(
        onSendResetEmail: (String) -> Unit,
        onDismiss: () -> Unit,
        isLoading: Boolean = false
) {
  var email by remember { mutableStateOf("") }

  AlertDialog(
          onDismissRequest = onDismiss,
          containerColor = MaterialTheme.colorScheme.surface,
          titleContentColor = MaterialTheme.colorScheme.onSurface,
          textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
          icon = {
            Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
            )
          },
          title = {
            Text(
                    text = "Reset Password",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
            )
          },
          text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
              Text(
                      text =
                              "Enter your email address and we'll send you a link to reset your password.",
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
              )

              OutlinedTextField(
                      value = email,
                      onValueChange = { email = it },
                      label = { Text("Email Address") },
                      placeholder = { Text("Enter your email") },
                      keyboardOptions =
                              androidx.compose.foundation.text.KeyboardOptions(
                                      keyboardType = KeyboardType.Email
                              ),
                      singleLine = true,
                      modifier = Modifier.fillMaxWidth(),
                      shape = RoundedCornerShape(12.dp),
                      colors =
                              OutlinedTextFieldDefaults.colors(
                                      focusedBorderColor = MaterialTheme.colorScheme.primary,
                                      focusedLabelColor = MaterialTheme.colorScheme.primary
                              )
              )
            }
          },
          confirmButton = {
            Button(
                    onClick = {
                      if (email.isNotBlank()) {
                        onSendResetEmail(email.trim())
                      }
                    },
                    enabled = !isLoading && email.isNotBlank(),
                    colors =
                            ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                            )
            ) {
              if (isLoading) {
                CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                )
              } else {
                Text("Send Reset Email")
              }
            }
          },
          dismissButton = {
            TextButton(
                    onClick = onDismiss,
                    colors =
                            ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                            )
            ) { Text("Cancel") }
          }
  )
}
