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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.domain.models.Team
import com.example.mobilecomputingassignment.presentation.viewmodel.AuthUiState
import java.util.Calendar
import androidx.compose.material3.ExperimentalMaterial3Api // Add this
import androidx.compose.material3.Scaffold // Add this
import androidx.compose.material3.TopAppBar // Add this

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
                totalSteps = 6, // Assuming 6 steps now with league and team as separate for signup
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
                                leadingIcon = {
                                        Icon(Icons.Default.Email, contentDescription = null)
                                },
                                keyboardOptions =
                                        KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = errorMessage != null
                        )

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
                totalSteps = 6, // Assuming 6 total steps
                buttonText = "Next",
                buttonEnabled = isFormValid,
                onButtonClick = { onNextClick(password, confirmPassword) },
                errorMessage = errorMessage
        ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                        value = password,
                                        onValueChange = { password = it },
                                        label = { Text("Password") },
                                        leadingIcon = {
                                                Icon(Icons.Default.Lock, contentDescription = null)
                                        },
                                        trailingIcon = {
                                                IconButton(
                                                        onClick = {
                                                                passwordVisible = !passwordVisible
                                                        }
                                                ) {
                                                        Icon(
                                                                painter =
                                                                        painterResource(
                                                                                if (passwordVisible) R.drawable.visibility_icon
                                                                                else R.drawable.visibility_off_icon
                                                                        ),
                                                                contentDescription =
                                                                        if (passwordVisible) "Hide password"
                                                                        else "Show password"
                                                        )
                                                }
                                        },
                                        visualTransformation =
                                                if (passwordVisible) VisualTransformation.None
                                                else PasswordVisualTransformation(),
                                        keyboardOptions =
                                                KeyboardOptions(
                                                        keyboardType = KeyboardType.Password
                                                ),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        isError =
                                                errorMessage != null ||
                                                        (password.isNotEmpty() &&
                                                                passwordStrength == PasswordStrength.WEAK)
                                )

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

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                        value = confirmPassword,
                                        onValueChange = { confirmPassword = it },
                                        label = { Text("Confirm Password") },
                                        leadingIcon = {
                                                Icon(Icons.Default.Lock, contentDescription = null)
                                        },
                                        trailingIcon = {
                                                IconButton(
                                                        onClick = {
                                                                confirmPasswordVisible = !confirmPasswordVisible
                                                        }
                                                ) {
                                                        Icon(
                                                                painter =
                                                                        painterResource(
                                                                                if (confirmPasswordVisible) R.drawable.visibility_icon
                                                                                else R.drawable.visibility_off_icon
                                                                        ),
                                                                contentDescription =
                                                                        if (confirmPasswordVisible) "Hide password"
                                                                        else "Show password"
                                                        )
                                                }
                                        },
                                        visualTransformation =
                                                if (confirmPasswordVisible) VisualTransformation.None
                                                else PasswordVisualTransformation(),
                                        keyboardOptions =
                                                KeyboardOptions(
                                                        keyboardType = KeyboardType.Password
                                                ),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        isError =
                                                errorMessage != null ||
                                                        (confirmPassword.isNotEmpty() &&
                                                                password.isNotEmpty() &&
                                                                !passwordsMatch)
                                )

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

fun calculateAge(birthdateString: String): Int {
        return try {
                val parts = birthdateString.split("/")
                if (parts.size != 3) return 0
                val day = parts[0].toInt()
                val month = parts[1].toInt() // Month is 1-based in input
                val year = parts[2].toInt()

                val today = Calendar.getInstance()
                val birthCalendar = Calendar.getInstance()
                birthCalendar.set(year, month - 1, day) // Calendar month is 0-based

                var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
                if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                        age--
                }
                age
        } catch (e: Exception) {
                0
        }
}

@Composable
fun SignupUsernameAndAgeStep(
        onNextClick: (String, String, Boolean) -> Unit,
        onBackClick: () -> Unit,
        isLoading: Boolean,
        errorMessage: String?,
        initialUsername: String = "",
        initialBirthdate: String = "", // Expects "DD/MM/YYYY"
        initialAgeConfirmed: Boolean = false,
        onUsernameChange: ((String) -> Unit)? = null,
        uiState: AuthUiState // Assuming this is needed for some validation, otherwise can be removed
) {
        var username by remember { mutableStateOf(initialUsername) }
        var birthdate by remember { mutableStateOf(initialBirthdate) }
        var ageConfirmed by remember { mutableStateOf(initialAgeConfirmed) }
        var showDatePicker by remember { mutableStateOf(false) } // State for showing DatePickerDialog

        val calculatedAge = calculateAge(birthdate)
        val isBirthdateValidAndOldEnough = calculatedAge >= 18 && birthdate.matches(Regex("""\d{2}/\d{2}/\d{4}"""))

        val isFormValid =
                username.length >= 3 &&
                        isBirthdateValidAndOldEnough &&
                        ageConfirmed &&
                        (errorMessage == null || !errorMessage.contains("username", ignoreCase = true)) // Ensure username specific error doesn't block

        SignupLayout(
                title = "Complete your profile",
                subtitle = "Enter your username and birthdate to continue",
                onBackClick = onBackClick,
                currentStep = 3,
                totalSteps = 6, // Assuming 6 total steps
                buttonText = "Next",
                buttonEnabled = isFormValid,
                isLoading = isLoading,
                onButtonClick = { onNextClick(username, birthdate, ageConfirmed) },
                errorMessage = errorMessage
        ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                        value = username,
                                        onValueChange = {
                                                username = it
                                                onUsernameChange?.invoke(it)
                                        },
                                        label = { Text("Username") },
                                        leadingIcon = {
                                                Icon(Icons.Default.Person, contentDescription = null)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        isError =
                                                (errorMessage != null && errorMessage.contains("username", ignoreCase = true)) ||
                                                        (username.isNotEmpty() && username.length < 3)
                                )
                                if (username.isNotEmpty() && username.length < 3) {
                                        Text(
                                                text = "Username must be at least 3 characters",
                                                color = MaterialTheme.colorScheme.error,
                                                style = MaterialTheme.typography.bodySmall
                                        )
                                }
                                if (errorMessage != null && errorMessage.contains("username", ignoreCase = true)) {
                                        Text(
                                                text = errorMessage,
                                                color = MaterialTheme.colorScheme.error,
                                                style = MaterialTheme.typography.bodySmall
                                        )
                                }
                        }

                        OutlinedTextField(
                                value = birthdate,
                                onValueChange = { birthdate = it }, // Manual input, consider DatePicker for better UX
                                label = { Text("Birthdate (DD/MM/YYYY)") },
                                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                                placeholder = { Text("25/12/1990") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = birthdate.isNotEmpty() && !birthdate.matches(Regex("""\d{2}/\d{2}/\d{4}"""))
                                // Add DatePickerDialog trigger if you want a visual picker
                        )

                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                                Checkbox(
                                        checked = ageConfirmed,
                                        onCheckedChange = { ageConfirmed = it },
                                        enabled = isBirthdateValidAndOldEnough // Enable checkbox only if age is valid and >= 18
                                )
                                Text(
                                        text = "I confirm that I am 18 years or older",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isBirthdateValidAndOldEnough) MaterialTheme.colorScheme.onSurface
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                        }
                        if (birthdate.isNotEmpty() && !isBirthdateValidAndOldEnough && calculatedAge < 18 && calculatedAge != 0) {
                                Text(
                                        text = "You must be 18 years or older to sign up.",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                )
                        }
                }
        }
}

// Add this annotation above the function
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupLeagueStep(
        onNextClick: (List<String>) -> Unit,
        onSkipClick: () -> Unit, // Retained for original signup flow
        onBackClick: () -> Unit,
        initialSelectedLeagues: Set<String> = emptySet(),
        isEditingMode: Boolean = false,
        onSaveLeagues: ((List<String>) -> Unit)? = null
) {
        // ----- START: ALL THE NEW CODE IS BELOW -----

        var selectedLeagues by remember { mutableStateOf(initialSelectedLeagues) }

        val actualButtonText = if (isEditingMode) "Save Changes" else "Next"
        val actualTitle = if (isEditingMode) "Edit Favourite Leagues" else "Pick your favorite leagues"

        val actualOnButtonClick = {
                if (isEditingMode) {
                        onSaveLeagues?.invoke(selectedLeagues.toList())
                } else {
                        onNextClick(selectedLeagues.toList())
                }
        }

        // This Scaffold structure mirrors your working TeamSelectionScreen
        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text(actualTitle) },
                                navigationIcon = {
                                        // THIS IS THE FIX: The IconButton now correctly calls onBackClick
                                        IconButton(onClick = onBackClick) {
                                                Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                        contentDescription = "Back"
                                                )
                                        }
                                }
                        )
                },
                bottomBar = {
                        // A bottom bar with a button for a consistent UI
                        Button(
                                onClick = { actualOnButtonClick() },
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                        ) {
                                Text(actualButtonText)
                        }
                }
        ) { innerPadding -> // This padding prevents content from hiding under the bars

                // Your existing UI for displaying leagues goes inside the Scaffold's content area
                val availableLeagues =
                        listOf(
                                "AFL",
                                "A-League",
                                "Premier League",
                                "NBA",
                        )
                val enabledLeagues = setOf("AFL")
                val leagueImages =
                        mapOf(
                                "AFL" to R.drawable.league_afl,
                                "A-League" to R.drawable.league_a_league,
                                "Premier League" to R.drawable.league_premier_league,
                                "NBA" to R.drawable.league_nba
                        )

                // The LazyColumn now uses the padding from the Scaffold
                LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(vertical = 8.dp) // Adds padding inside the list
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
                                                                                if (isSelected) selectedLeagues - league
                                                                                else selectedLeagues + league
                                                                }
                                                        },
                                                        modifier = Modifier
                                                                .weight(1f)
                                                                .aspectRatio(1f),
                                                        shape = RoundedCornerShape(12.dp),
                                                        colors = CardDefaults.cardColors(
                                                                containerColor = when {
                                                                        isSelected && isEnabled -> MaterialTheme.colorScheme.primary.copy(
                                                                                alpha = 0.1f
                                                                        )
                                                                        isEnabled -> MaterialTheme.colorScheme.surface
                                                                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(
                                                                                alpha = 0.3f
                                                                        )
                                                                }
                                                        ),
                                                        border = when {
                                                                isSelected && isEnabled -> BorderStroke(
                                                                        2.dp,
                                                                        MaterialTheme.colorScheme.primary
                                                                )
                                                                isEnabled -> BorderStroke(
                                                                        1.dp,
                                                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                                                )
                                                                else -> BorderStroke(
                                                                        1.dp,
                                                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)
                                                                )
                                                        }
                                                ) {
                                                        Column(
                                                                modifier = Modifier
                                                                        .fillMaxSize()
                                                                        .padding(12.dp),
                                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                                verticalArrangement = Arrangement.Center
                                                        ) {
                                                                leagueImages[league]?.let { imageRes ->
                                                                        Image(
                                                                                painter = painterResource(id = imageRes),
                                                                                contentDescription = "$league logo",
                                                                                modifier = Modifier
                                                                                        .size(48.dp)
                                                                                        .weight(1f),
                                                                                colorFilter = if (!isEnabled) ColorFilter.colorMatrix(
                                                                                        ColorMatrix().apply { setToSaturation(0f) }) else null,
                                                                                alpha = if (!isEnabled) 0.4f else 1f
                                                                        )
                                                                } ?: Box(
                                                                        modifier = Modifier
                                                                                .size(48.dp)
                                                                                .weight(1f)
                                                                                .background(
                                                                                        MaterialTheme.colorScheme.surfaceVariant,
                                                                                        RoundedCornerShape(8.dp)
                                                                                ),
                                                                        contentAlignment = Alignment.Center
                                                                ) {
                                                                        Text(league.take(3),
                                                                                style = MaterialTheme.typography.labelMedium)
                                                                }
                                                                Spacer(modifier = Modifier.height(8.dp))
                                                                Text(
                                                                        text = league,
                                                                        style = MaterialTheme.typography.bodySmall,
                                                                        textAlign = TextAlign.Center,
                                                                        maxLines = 2,
                                                                        color = when {
                                                                                isSelected && isEnabled -> MaterialTheme.colorScheme.primary
                                                                                isEnabled -> MaterialTheme.colorScheme.onSurface
                                                                                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                                                        alpha = 0.3f
                                                                                )
                                                                        }
                                                                )
                                                        }
                                                }
                                        }
                                        repeat(3 - leagueRow.size) {
                                                Spacer(modifier = Modifier.weight(1f))
                                        }
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
        availableTeams: List<Team> = emptyList(), // This should be provided by AuthViewModel
        isLoadingTeams: Boolean = false,
        onLoadTeams: () -> Unit = {} // AuthViewModel should handle loading
) {
        var selectedTeams by remember { mutableStateOf(setOf<String>()) }

        LaunchedEffect(Unit) {
                if (availableTeams.isEmpty() && !isLoadingTeams) {
                        onLoadTeams()
                }
        }

        SignupLayout(
                title = "Pick your favorite teams",
                subtitle = "This is to help recommend watch alongs for you",
                onBackClick = onBackClick,
                currentStep = 5, // Assuming this is step 5 of 6 in signup
                totalSteps = 6,
                buttonText = "Complete", // Or "Next" if there's a final summary/confirmation
                buttonEnabled = true,
                onButtonClick = { onNextClick(selectedTeams.toList()) },
                showSkip = true,
                onSkipClick = onSkipClick
        ) {
                if (isLoadingTeams) {
                        Box(modifier = Modifier.fillMaxWidth().height(320.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                        }
                } else {
                        LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.heightIn(min = 200.dp, max = 320.dp)
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
                                                                        selectedTeams =
                                                                                if (isSelected) selectedTeams - team.name
                                                                                else selectedTeams + team.name
                                                                },
                                                                modifier = Modifier.weight(1f).aspectRatio(1f),
                                                                shape = RoundedCornerShape(12.dp),
                                                                colors = CardDefaults.cardColors(
                                                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                                                        else MaterialTheme.colorScheme.surface
                                                                ),
                                                                border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                                                else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                                        ) {
                                                                Column(
                                                                        modifier = Modifier.fillMaxSize().padding(12.dp),
                                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                                        verticalArrangement = Arrangement.Center
                                                                ) {
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
                                                                                Box(
                                                                                        modifier = Modifier.size(48.dp).weight(1f)
                                                                                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
                                                                                        contentAlignment = Alignment.Center
                                                                                ) {
                                                                                        Text(team.abbreviation.ifEmpty { team.name.take(3) }, style = MaterialTheme.typography.labelMedium)
                                                                                }
                                                                        }
                                                                        Spacer(modifier = Modifier.height(8.dp))
                                                                        Text(
                                                                                text = team.name,
                                                                                style = MaterialTheme.typography.bodySmall,
                                                                                textAlign = TextAlign.Center,
                                                                                maxLines = 2,
                                                                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                                                                else MaterialTheme.colorScheme.onSurface
                                                                        )
                                                                }
                                                        }
                                                }
                                                repeat(3 - teamRow.size) {
                                                        Spacer(modifier = Modifier.weight(1f))
                                                }
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
        isEditingMode: Boolean = false, // <-- NEW PARAMETER
        content: @Composable () -> Unit
) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                // Pass isEditingMode to SignupHeader
                SignupHeader(
                        onBackClick = onBackClick,
                        currentStep = currentStep,
                        totalSteps = totalSteps,
                        isEditingMode = isEditingMode // <-- PASS IT HERE
                )

                Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = title, style = MaterialTheme.typography.headlineMedium)
                                Text(
                                        text = subtitle,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }
                        content()
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

                Column(
                        modifier = Modifier.padding(bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        // Only show skip button if showSkip is true AND it's not editing mode
                        if (showSkip && onSkipClick != null && !isEditingMode) {
                                Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                        Button( // Main action button (Next/Save/Complete)
                                                onClick = onButtonClick,
                                                enabled = buttonEnabled && !isLoading,
                                                modifier = Modifier.fillMaxWidth().height(40.dp),
                                                shape = RoundedCornerShape(16.dp)
                                        ) {
                                                if (isLoading) {
                                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                                                } else {
                                                        Text(buttonText, style = MaterialTheme.typography.labelLarge, fontSize = 16.sp)
                                                }
                                        }
                                        OutlinedButton( // Skip button
                                                onClick = onSkipClick,
                                                modifier = Modifier.fillMaxWidth().height(40.dp),
                                                shape = RoundedCornerShape(16.dp)
                                        ) {
                                                Text("Skip", style = MaterialTheme.typography.labelLarge, fontSize = 16.sp)
                                        }
                                }
                        } else { // Show only the main action button
                                Button(
                                        onClick = onButtonClick,
                                        enabled = buttonEnabled && !isLoading,
                                        modifier = Modifier.fillMaxWidth().height(40.dp),
                                        shape = RoundedCornerShape(16.dp)
                                ) {
                                        if (isLoading) {
                                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                                        } else {
                                                Text(buttonText, style = MaterialTheme.typography.labelLarge, fontSize = 16.sp)
                                        }
                                }
                        }
                }
        }
}

@Composable
private fun SignupHeader(
        onBackClick: () -> Unit,
        currentStep: Int,
        totalSteps: Int,
        isEditingMode: Boolean = false // <-- NEW PARAMETER
) {
        Column(
                modifier = Modifier.padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        IconButton(onClick = onBackClick) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                        // Only show "X of Y" if not in editing mode and steps are valid
                        if (!isEditingMode && currentStep > 0 && totalSteps > 0) {
                                Text(
                                        text = "$currentStep of $totalSteps",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }
                }
                // Only show progress bar if not in editing mode and steps are valid
                if (!isEditingMode && currentStep > 0 && totalSteps > 0) {
                        LinearProgressIndicator(
                                progress = currentStep.toFloat() / totalSteps.toFloat(),
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.primary,
                        )
                }
        }
}
