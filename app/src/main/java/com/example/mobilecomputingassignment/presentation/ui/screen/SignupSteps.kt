package com.example.mobilecomputingassignment.presentation.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mobilecomputingassignment.domain.models.Team

enum class PasswordStrength {
    WEAK,
    STRONG
}

@Composable
fun SignupEmailStep(
        onNextClick: (String) -> Unit,
        onBackClick: () -> Unit,
        isLoading: Boolean,
        errorMessage: String?,
        initialEmail: String = "",
        onEmailChange: ((String) -> Unit)? = null
) {
    var email by remember { mutableStateOf(initialEmail) }

    SignupLayout(
            title = "Enter your email",
            subtitle = "We'll check if this email is available",
            onBackClick = onBackClick,
            currentStep = 1,
            totalSteps = 6,
            buttonText = "Next",
            buttonEnabled = email.isNotEmpty() && errorMessage == null,
            isLoading = isLoading,
            onButtonClick = { onNextClick(email) },
            errorMessage = errorMessage
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
                value = email,
                    onValueChange = {
                        email = it
                        onEmailChange?.invoke(it)
                    },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorMessage != null
        )

            // Show error message below the input field
            if (errorMessage != null) {
                Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun SignupPasswordStep(
        onNextClick: (String, String) -> Unit,
        onBackClick: () -> Unit,
        errorMessage: String?,
        initialPassword: String = "",
        initialConfirmPassword: String = ""
) {
    var password by remember { mutableStateOf(initialPassword) }
    var confirmPassword by remember { mutableStateOf(initialConfirmPassword) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Password strength validation
    val passwordStrength =
            remember(password) {
                when {
                    password.length < 7 -> PasswordStrength.WEAK
                    !password.any { it.isUpperCase() } -> PasswordStrength.WEAK
                    !password.any { it.isDigit() } -> PasswordStrength.WEAK
                    else -> PasswordStrength.STRONG
                }
            }

    val passwordsMatch =
            password.isNotEmpty() && confirmPassword.isNotEmpty() && password == confirmPassword
    val isFormValid = passwordStrength == PasswordStrength.STRONG && passwordsMatch

    SignupLayout(
            title = "Create a password",
            subtitle =
                    "Your password must be at least 7 characters with 1 capital letter and 1 number",
            onBackClick = onBackClick,
            currentStep = 2,
            totalSteps = 5,
            buttonText = "Next",
            buttonEnabled = isFormValid,
            onButtonClick = { onNextClick(password, confirmPassword) },
            errorMessage = errorMessage
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Password field with strength indicator
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(text = if (passwordVisible) "👁️" else "🙈", fontSize = 16.sp)
                        }
                    },
                    visualTransformation =
                            if (passwordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                        isError =
                                errorMessage != null ||
                                        (password.isNotEmpty() &&
                                                passwordStrength == PasswordStrength.WEAK)
                )

                // Password strength requirements
                if (password.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        PasswordRequirement(
                                text = "At least 7 characters",
                                isMet = password.length >= 7
                        )
                        PasswordRequirement(
                                text = "Contains uppercase letter",
                                isMet = password.any { it.isUpperCase() }
                        )
                        PasswordRequirement(
                                text = "Contains number",
                                isMet = password.any { it.isDigit() }
                        )
                    }
                }
            }

            // Confirm password field
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                            IconButton(
                                    onClick = { confirmPasswordVisible = !confirmPasswordVisible }
                            ) {
                            Text(
                                    text = if (confirmPasswordVisible) "👁️" else "🙈",
                                    fontSize = 16.sp
                            )
                        }
                    },
                    visualTransformation =
                            if (confirmPasswordVisible) VisualTransformation.None
                            else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                        isError =
                                errorMessage != null ||
                                        (confirmPassword.isNotEmpty() &&
                                                password.isNotEmpty() &&
                                                !passwordsMatch)
                )

                // Password match indicator
                if (confirmPassword.isNotEmpty() && password.isNotEmpty()) {
                    Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                                imageVector =
                                        if (passwordsMatch) Icons.Default.Check
                                        else Icons.Default.Close,
                                contentDescription = null,
                                tint =
                                        if (passwordsMatch) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                        )
                        Text(
                                text =
                                        if (passwordsMatch) "Passwords match"
                                        else "Passwords don't match",
                                style = MaterialTheme.typography.bodySmall,
                                color =
                                        if (passwordsMatch) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PasswordRequirement(text: String, isMet: Boolean) {
    Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
                imageVector = if (isMet) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint =
                        if (isMet) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
        )
        Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color =
                        if (isMet) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun SignupUsernameAndAgeStep(
        onNextClick: (String, String, Boolean) -> Unit,
        onBackClick: () -> Unit,
        isLoading: Boolean,
        errorMessage: String?,
        initialUsername: String = "",
        initialBirthdate: String = "",
        initialAgeConfirmed: Boolean = false,
        onUsernameChange: ((String) -> Unit)? = null
) {
    var username by remember { mutableStateOf(initialUsername) }
    var birthdate by remember { mutableStateOf(initialBirthdate) }
    var ageConfirmed by remember { mutableStateOf(initialAgeConfirmed) }
    var showDatePicker by remember { mutableStateOf(false) }

    val isFormValid =
            username.length >= 3 && birthdate.isNotEmpty() && ageConfirmed && errorMessage == null

    SignupLayout(
            title = "Complete your profile",
            subtitle = "Enter your username and birthdate to continue",
            onBackClick = onBackClick,
            currentStep = 3,
            totalSteps = 5,
            buttonText = "Next",
            buttonEnabled = isFormValid,
            isLoading = isLoading,
            onButtonClick = { onNextClick(username, birthdate, ageConfirmed) },
            errorMessage = errorMessage
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Username field
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                    value = username,
                        onValueChange = {
                            username = it
                            onUsernameChange?.invoke(it)
                        },
                    label = { Text("Username") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                        isError =
                                errorMessage != null ||
                                        (username.isNotEmpty() && username.length < 3)
            )

            if (username.isNotEmpty() && username.length < 3) {
                Text(
                        text = "Username must be at least 3 characters",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                )
            }

                // Show validation error from Firebase check
                if (errorMessage != null && errorMessage.contains("username", ignoreCase = true)) {
                    Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Birthdate field
        OutlinedTextField(
                value = birthdate,
                onValueChange = { birthdate = it },
                label = { Text("Enter your birthdate") },
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                placeholder = { Text("25/12/1990") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorMessage != null,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select date")
                    }
                }
        )

            // Age confirmation checkbox
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Checkbox(checked = ageConfirmed, onCheckedChange = { ageConfirmed = it })
                Text(
                        text = "I confirm that I am 18 years or older",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                )
            }

        // Note: For a full implementation, you would add a DatePickerDialog here
        // This is a simplified version to match the design structure
        if (showDatePicker) {
            // DatePickerDialog would go here in a real implementation
            showDatePicker = false
            }
        }
    }
}

@Composable
fun SignupLeagueStep(
        onNextClick: (List<String>) -> Unit,
        onSkipClick: () -> Unit,
        onBackClick: () -> Unit
) {
    var selectedLeagues by remember { mutableStateOf(setOf<String>()) }

    val availableLeagues =
            listOf(
                    "AFL",
                    "A-League",
                    "Premier League",
                    "NBA",
            )

    val enabledLeagues = setOf("AFL")

    SignupLayout(
            title = "Pick your favorite leagues",
            subtitle = "This is to help recommend watch alongs for you",
            onBackClick = onBackClick,
            currentStep = 4,
            totalSteps = 5,
            buttonText = "Next",
            buttonEnabled = true,
            onButtonClick = { onNextClick(selectedLeagues.toList()) },
            showSkip = true,
            onSkipClick = onSkipClick
    ) {
        // Map league names to their drawable resources
        val leagueImages =
                mapOf(
                        "AFL" to com.example.mobilecomputingassignment.R.drawable.league_afl,
                        "A-League" to
                                com.example.mobilecomputingassignment.R.drawable.league_a_league,
                        "Premier League" to
                                com.example
                                        .mobilecomputingassignment
                                        .R
                                        .drawable
                                        .league_premier_league,
                        "NBA" to com.example.mobilecomputingassignment.R.drawable.league_nba
                )

        LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(320.dp)
        ) {
            items(availableLeagues.chunked(3)) { leagueRow ->
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    leagueRow.forEach { league ->
                        val isEnabled = enabledLeagues.contains(league)
                        val isSelected = selectedLeagues.contains(league)
                        Card(
                                onClick = {
                                    if (isEnabled) {
                                    selectedLeagues =
                                            if (selectedLeagues.contains(league)) {
                                                selectedLeagues - league
                                            } else {
                                                selectedLeagues + league
                                                }
                                    }
                                },
                                modifier = Modifier.weight(1f).aspectRatio(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors =
                                        CardDefaults.cardColors(
                                                containerColor =
                                                        if (isSelected && isEnabled)
                                                                MaterialTheme.colorScheme.primary
                                                                        .copy(alpha = 0.1f)
                                                        else MaterialTheme.colorScheme.surface
                                        ),
                                border =
                                        if (isSelected && isEnabled)
                                                BorderStroke(
                                                        2.dp,
                                                        MaterialTheme.colorScheme.primary
                                                )
                                        else
                                                BorderStroke(
                                                        1.dp,
                                                        if (isEnabled)
                                                                MaterialTheme.colorScheme.outline
                                                                        .copy(alpha = 0.2f)
                                                        else
                                                                MaterialTheme.colorScheme.outline
                                                                        .copy(alpha = 0.1f)
                                                )
                        ) {
                            Column(
                                    modifier = Modifier.fillMaxSize().padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                            ) {
                                // League logo
                                leagueImages[league]?.let { imageRes ->
                                    Image(
                                            painter = painterResource(id = imageRes),
                                            contentDescription = "$league logo",
                                            modifier = Modifier.size(48.dp).weight(1f)
                                    )
                                }
                                        ?: run {
                                            // Placeholder if no image
                                            Box(
                                                    modifier =
                                                            Modifier.size(48.dp)
                                                                    .weight(1f)
                                                                    .background(
                                                                            MaterialTheme
                                                                                    .colorScheme
                                                                                    .surfaceVariant,
                                                                            RoundedCornerShape(8.dp)
                                                                    ),
                                                    contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                        text = league.take(3),
                                                        style = MaterialTheme.typography.labelMedium
                                                )
                                            }
                                        }

                                Spacer(modifier = Modifier.height(8.dp))

                                // League name
                                Text(
                                        text = league,
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                        color =
                                                if (isSelected && isEnabled)
                                                        MaterialTheme.colorScheme.primary
                                                else if (isEnabled)
                                                        MaterialTheme.colorScheme.onSurface
                                                else
                                                        MaterialTheme.colorScheme.onSurface.copy(
                                                                alpha = 0.4f
                                                        )
                                )
                            }
                        }
                    }
                    // Fill remaining columns with spacers
                    repeat(3 - leagueRow.size) { Spacer(modifier = Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
fun SignupTeamStep(
        onNextClick: (List<String>) -> Unit,
        onSkipClick: () -> Unit,
        onBackClick: () -> Unit,
        availableTeams: List<Team> = emptyList(),
        isLoadingTeams: Boolean = false,
        onLoadTeams: () -> Unit = {}
) {
    var selectedTeams by remember { mutableStateOf(setOf<String>()) }

    // Load teams when the composable is first created
    LaunchedEffect(Unit) {
        if (availableTeams.isEmpty() && !isLoadingTeams) {
            onLoadTeams()
        }
    }

    SignupLayout(
            title = "Pick your favorite teams",
            subtitle = "This is to help recommend watch alongs for you",
            onBackClick = onBackClick,
            currentStep = 5,
            totalSteps = 5,
            buttonText = "Complete",
            buttonEnabled = true,
            onButtonClick = { onNextClick(selectedTeams.toList()) },
            showSkip = true,
            onSkipClick = onSkipClick
    ) {
        if (isLoadingTeams) {
            Box(
                    modifier = Modifier.fillMaxWidth().height(320.dp),
                    contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else {
        LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(320.dp)
        ) {
                items(availableTeams.chunked(3)) { teamRow ->
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    teamRow.forEach { team ->
                            val isSelected = selectedTeams.contains(team.name)
                            Card(
                                onClick = {
                                        val teamName = team.name
                                    selectedTeams =
                                                if (selectedTeams.contains(teamName)) {
                                                    selectedTeams.minus(teamName)
                                            } else {
                                                    selectedTeams.plus(teamName)
                                                }
                                    },
                                    modifier = Modifier.weight(1f).aspectRatio(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors =
                                            CardDefaults.cardColors(
                                                    containerColor =
                                                            if (isSelected)
                                                                    MaterialTheme.colorScheme
                                                                            .primary.copy(
                                                                            alpha = 0.1f
                                                                    )
                                                            else MaterialTheme.colorScheme.surface
                                            ),
                                    border =
                                            if (isSelected)
                                                    BorderStroke(
                                                            2.dp,
                                                            MaterialTheme.colorScheme.primary
                                                    )
                                            else
                                                    BorderStroke(
                                                            1.dp,
                                                            MaterialTheme.colorScheme.outline.copy(
                                                                    alpha = 0.2f
                                                            )
                                                    )
                            ) {
                                Column(
                                        modifier = Modifier.fillMaxSize().padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                ) {
                                    // Team logo - prioritize local logo over API URL
                                    if (team.localLogoRes != null) {
                                        Image(
                                                painter = painterResource(id = team.localLogoRes),
                                                contentDescription = "${team.name} logo",
                                                modifier = Modifier.size(48.dp).weight(1f)
                                        )
                                    } else if (!team.logoUrl.isNullOrEmpty()) {
                                        AsyncImage(
                                                model = team.logoUrl,
                                                contentDescription = "${team.name} logo",
                                                modifier = Modifier.size(48.dp).weight(1f)
                                        )
                                    } else {
                                        // Placeholder if no logo
                                        Box(
                                                modifier =
                                                        Modifier.size(48.dp)
                                                                .weight(1f)
                                                                .background(
                                                                        MaterialTheme.colorScheme
                                                                                .surfaceVariant,
                                                                        RoundedCornerShape(8.dp)
                                                                ),
                                                contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                    text = team.abbreviation,
                                                    style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Team name
                                    Text(
                                            text = team.name,
                                            style = MaterialTheme.typography.bodySmall,
                                            textAlign = TextAlign.Center,
                                            maxLines = 2,
                                            color =
                                                    if (isSelected)
                                                            MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                        // Fill remaining columns with spacers
                        repeat(3 - teamRow.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SignupLayout(
        title: String,
        subtitle: String,
        onBackClick: () -> Unit,
        currentStep: Int,
        totalSteps: Int,
        buttonText: String,
        buttonEnabled: Boolean,
        onButtonClick: () -> Unit,
        isLoading: Boolean = false,
        errorMessage: String? = null,
        showSkip: Boolean = false,
        onSkipClick: (() -> Unit)? = null,
        content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        // Header with back button and progress
        SignupHeader(onBackClick = onBackClick, currentStep = currentStep, totalSteps = totalSteps)

        // Content area
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            // Title and subtitle
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium
                )

                Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Form content
            content()

            // Error message (only show for general errors, not field-specific ones)
            if (errorMessage != null &&
                            !errorMessage.contains("email", ignoreCase = true) &&
                            !errorMessage.contains("username", ignoreCase = true) &&
                            !errorMessage.contains("password", ignoreCase = true)
            ) {
                Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Bottom buttons
        Column(
                modifier = Modifier.padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (showSkip && onSkipClick != null) {
                Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                            onClick = onButtonClick,
                            enabled = buttonEnabled && !isLoading,
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            shape = RoundedCornerShape(16.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                    buttonText,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontSize = 16.sp
                            )
                        }
                    }

                    OutlinedButton(
                            onClick = onSkipClick,
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Skip", style = MaterialTheme.typography.labelLarge, fontSize = 16.sp)
                    }
                }
            } else {
                Button(
                        onClick = onButtonClick,
                        enabled = buttonEnabled && !isLoading,
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        shape = RoundedCornerShape(16.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                                buttonText,
                                style = MaterialTheme.typography.labelLarge,
                                fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SignupHeader(onBackClick: () -> Unit, currentStep: Int, totalSteps: Int) {
    Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back button and step indicator
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            Text(
                    text = "$currentStep of $totalSteps",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Progress bar
        LinearProgressIndicator(
                progress = { currentStep.toFloat() / totalSteps },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
        )
    }
}
