package com.example.mobilecomputingassignment.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupEmailStep(
    onNextClick: (String) -> Unit,
    onBackClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    var email by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StepHeader(
            title = "Enter your email",
            subtitle = "We'll check if this email is available",
            onBackClick = onBackClick,
            currentStep = 1,
            totalSteps = 6
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = errorMessage != null
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onNextClick(email) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && email.isNotEmpty()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Next")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupPasswordStep(
    onNextClick: (String, String) -> Unit,
    onBackClick: () -> Unit,
    errorMessage: String?
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StepHeader(
            title = "Create a password",
            subtitle = "Your password must be at least 6 characters",
            onBackClick = onBackClick,
            currentStep = 2,
            totalSteps = 6
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = errorMessage != null
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                    )
                }
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = errorMessage != null
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onNextClick(password, confirmPassword) },
            modifier = Modifier.fillMaxWidth(),
            enabled = password.isNotEmpty() && confirmPassword.isNotEmpty()
        ) {
            Text("Next")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupUsernameStep(
    onNextClick: (String) -> Unit,
    onBackClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    var username by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StepHeader(
            title = "Choose a username",
            subtitle = "This is how other users will see you",
            onBackClick = onBackClick,
            currentStep = 3,
            totalSteps = 6
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = errorMessage != null
        )

        if (username.isNotEmpty() && username.length < 3) {
            Text(
                text = "Username must be at least 3 characters",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onNextClick(username) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && username.length >= 3
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Next")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupAgeStep(
    onNextClick: (String) -> Unit,
    onBackClick: () -> Unit,
    errorMessage: String?
) {
    var birthdate by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StepHeader(
            title = "Enter your birthdate",
            subtitle = "You must be 18 or older to use this app",
            onBackClick = onBackClick,
            currentStep = 4,
            totalSteps = 6
        )

        OutlinedTextField(
            value = birthdate,
            onValueChange = { birthdate = it },
            label = { Text("Birthdate (DD/MM/YYYY)") },
            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            placeholder = { Text("25/12/1990") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = errorMessage != null
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onNextClick(birthdate) },
            modifier = Modifier.fillMaxWidth(),
            enabled = birthdate.isNotEmpty()
        ) {
            Text("Next")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupLeagueStep(
    onNextClick: (List<String>) -> Unit,
    onSkipClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var selectedLeagues by remember { mutableStateOf(setOf<String>()) }
    
    val availableLeagues = listOf(
        "Premier League",
        "La Liga",
        "Serie A",
        "Bundesliga",
        "Ligue 1",
        "Champions League",
        "NBA",
        "NFL",
        "MLB",
        "NHL",
        "Formula 1",
        "Tennis"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StepHeader(
            title = "Pick your favorite leagues",
            subtitle = "Optional - you can skip this step",
            onBackClick = onBackClick,
            currentStep = 5,
            totalSteps = 6
        )

        LazyColumn {
            items(availableLeagues.chunked(2)) { leagueRow ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    leagueRow.forEach { league ->
                        FilterChip(
                            onClick = {
                                selectedLeagues = if (selectedLeagues.contains(league)) {
                                    selectedLeagues - league
                                } else {
                                    selectedLeagues + league
                                }
                            },
                            label = { Text(league) },
                            selected = selectedLeagues.contains(league),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (leagueRow.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onSkipClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Skip")
            }
            
            Button(
                onClick = { onNextClick(selectedLeagues.toList()) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Next")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupTeamStep(
    onNextClick: (List<String>) -> Unit,
    onSkipClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var selectedTeams by remember { mutableStateOf(setOf<String>()) }
    
    val availableTeams = listOf(
        "Manchester United", "Liverpool", "Arsenal", "Chelsea",
        "Barcelona", "Real Madrid", "Manchester City", "Bayern Munich",
        "Juventus", "AC Milan", "Inter Milan", "PSG",
        "Lakers", "Warriors", "Celtics", "Heat",
        "Cowboys", "Patriots", "Steelers", "Packers"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StepHeader(
            title = "Pick your favorite teams",
            subtitle = "Optional - you can skip this step",
            onBackClick = onBackClick,
            currentStep = 6,
            totalSteps = 6
        )

        LazyColumn {
            items(availableTeams.chunked(2)) { teamRow ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    teamRow.forEach { team ->
                        FilterChip(
                            onClick = {
                                selectedTeams = if (selectedTeams.contains(team)) {
                                    selectedTeams - team
                                } else {
                                    selectedTeams + team
                                }
                            },
                            label = { Text(team) },
                            selected = selectedTeams.contains(team),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (teamRow.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onSkipClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Skip")
            }
            
            Button(
                onClick = { onNextClick(selectedTeams.toList()) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Complete")
            }
        }
    }
}

@Composable
fun StepHeader(
    title: String,
    subtitle: String,
    onBackClick: () -> Unit,
    currentStep: Int,
    totalSteps: Int
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            
            Text(
                text = "$currentStep/$totalSteps",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LinearProgressIndicator(
            progress = currentStep.toFloat() / totalSteps.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}