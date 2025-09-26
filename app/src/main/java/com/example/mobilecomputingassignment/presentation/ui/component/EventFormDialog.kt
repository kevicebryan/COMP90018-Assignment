package com.example.mobilecomputingassignment.presentation.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable // <<< ADD THIS LINE
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.domain.models.Team
import com.example.mobilecomputingassignment.presentation.utils.TimezoneUtils
import com.example.mobilecomputingassignment.presentation.viewmodel.EventViewModel
import java.util.*
import java.util.Calendar
import java.text.SimpleDateFormat

@Composable
private fun EventFormSection(
        title: String,
        isExpanded: Boolean,
        onHeaderClick: () -> Unit,
        content: @Composable ColumnScope.() -> Unit
) {
        Column {
                // Clickable Header Row
                Row(
                        modifier =
                                Modifier.fillMaxWidth()
                                .clickable(
                                                interactionSource =
                                                        remember { MutableInteractionSource() },
                                        indication = null
                                ) { onHeaderClick() }
                                .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Icon(
                                imageVector =
                                        if (isExpanded) Icons.Filled.KeyboardArrowDown
                                        else Icons.Filled.KeyboardArrowRight,
                                contentDescription =
                                        if (isExpanded) "Collapse $title" else "Expand $title",
                                tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                        )
                }

                // Conditionally display content based on expansion state
                if (isExpanded) {
                        Column(
                                modifier =
                                        Modifier.padding(
                                                top = 8.dp
                                        ), // Add some space above content
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                content = content
                        )
                }
        }
}

@Composable
private fun EventFormSection(
        title: String,
        content: @Composable ColumnScope.() -> Unit // Original simple signature
) {
        Column {
                Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp) // Standard padding for title
                )
                Column(
                        verticalArrangement =
                                Arrangement.spacedBy(6.dp), // Spacing for content items
                        content = content
                )
        }
}

@Composable
fun TeamVsTeamDisplay(homeTeamName: String?, awayTeamName: String?) {
        if (homeTeamName == null && awayTeamName == null) return
        if (homeTeamName == null || awayTeamName == null) return // need both logos

        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
        ) {
                // Home Team Logo
                TeamLogo(teamName = homeTeamName)

                // VS
                Text(
                        text = "VS",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Away Team Logo
                TeamLogo(teamName = awayTeamName)
        }
}

/*
@Composable
fun TeamVsTeamDisplay(
        homeTeamName: String?,
        awayTeamName: String?
) {
        if (homeTeamName == null && awayTeamName == null) return

        val bothTeamsPresent = homeTeamName != null && awayTeamName != null

        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
        ) {
                // Home Team
                homeTeamName?.let { name ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                TeamLogo(teamName = name)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                        text = name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                )
                        }
                }

                // VS
                if (bothTeamsPresent) {
                        Text(
                                text = " VS ",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 16.dp)
                        )
                }

                // Away Team
                awayTeamName?.let { name ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                        text = name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                TeamLogo(teamName = name)
                        }
                }
        }
}
 */

@Composable
private fun CheckboxRow(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        text: String,
        modifier: Modifier = Modifier
) {
        Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                        checked = checked,
                        onCheckedChange = onCheckedChange,
                        colors =
                                CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary
                                )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(text = text, style = MaterialTheme.typography.bodyMedium)
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFormDialog(
        isEditing: Boolean,
        onDismiss: () -> Unit,
        onSave: () -> Unit,
        eventViewModel: EventViewModel
) {
        val formData by eventViewModel.formData.collectAsState()
        val uiState by eventViewModel.uiState.collectAsState()

        // Show map picker if requested
        if (uiState.showMapPicker) {
                MapPickerDialog(
                        onDismiss = { eventViewModel.hideMapPicker() },
                        onLocationSelected = { latLng, address ->
                                eventViewModel.onLocationSelected(latLng, address)
                        },
                        initialLatLng =
                                if (formData.latitude != 0.0 && formData.longitude != 0.0) {
                                        com.google.android.gms.maps.model.LatLng(
                                                formData.latitude,
                                                formData.longitude
                                        )
                                } else null
                )
        }

        Dialog(
                onDismissRequest = onDismiss,
                properties =
                        DialogProperties(
                                dismissOnBackPress = true,
                                dismissOnClickOutside = false,
                                usePlatformDefaultWidth = false
                        )
        ) {
                Card(
                        modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f),
                        shape = RoundedCornerShape(16.dp),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                )
                ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                                // Header
                                Row(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Text(
                                                text =
                                                        if (isEditing) "Edit Event"
                                                        else "Create Event",
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.Bold
                                        )

                                        IconButton(onClick = onDismiss) {
                                                Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Close"
                                                )
                                        }
                                }

                                HorizontalDivider()

                                // Form content
                                Column(
                                        modifier =
                                                Modifier.weight(1f)
                                                        .verticalScroll(rememberScrollState())
                                                        .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                        // Load matches when date changes
                                        LaunchedEffect(formData.date) {
                                                eventViewModel.loadMatchesForDate(formData.date)
                                        }

                                        // Date and Time
                                        EventFormSection(title = "Date & Time") {
                                                var showDatePicker by remember {
                                                        mutableStateOf(false)
                                                }
                                                var showTimePicker by remember {
                                                        mutableStateOf(false)
                                                }
                                                Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(8.dp)
                                                ) {

                                                        // Date picker
                                                        OutlinedTextField(
                                                                value = TimezoneUtils.formatPreservedDate(formData.date),
                                                                onValueChange = {},
                                                                label = { Text("Event Date *") },
                                                                modifier = Modifier.weight(1f),
                                                                readOnly = true,
                                                                trailingIcon = {
                                                                        IconButton(
                                                                                onClick = {
                                                                                showDatePicker =
                                                                                        true
                                                                                }
                                                                        ) {
                                                                                Icon(
                                                                                        painter =
                                                                                                painterResource(
                                                                                                        id =
                                                                                                                R.drawable
                                                                                                                        .ic_calendar
                                                                                                ),
                                                                                        contentDescription =
                                                                                                "Select date",
                                                                                        tint =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .primary
                                                                                )
                                                                        }
                                                                }
                                                        )

                                                        // Time picker
                                                        OutlinedTextField(
                                                                value = TimezoneUtils.formatPreservedTime(formData.checkInTime),
                                                                onValueChange = {},
                                                                label = { Text("Match Time *") },
                                                                modifier = Modifier.weight(1f),
                                                                readOnly = true,
                                                                trailingIcon = {
                                                                        IconButton(
                                                                                onClick = {
                                                                                showTimePicker =
                                                                                        true
                                                                                }
                                                                        ) {
                                                                                Icon(
                                                                                        painter =
                                                                                                painterResource(
                                                                                                        id =
                                                                                                                R.drawable
                                                                                                                        .ic_clock
                                                                                                ),
                                                                                        contentDescription =
                                                                                                "Select time",
                                                                                        tint =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .primary
                                                                                )
                                                                        }
                                                                }
                                                        )
                                                }

                                                // Date picker dialog
                                                if (showDatePicker) {
                                                        val datePickerState =
                                                                rememberDatePickerState(
                                                                        initialSelectedDateMillis =
                                                                                formData.date.time
                                                                )

                                                        DatePickerDialog(
                                                                onDismissRequest = {
                                                                        showDatePicker = false
                                                                },
                                                                confirmButton = {
                                                                        TextButton(
                                                                                onClick = {
                                                                                        datePickerState
                                                                                                .selectedDateMillis
                                                                                                ?.let {
                                                                                                                        millis
                                                                                                        ->
                                                                                                        // Create date in Australian timezone to match validation logic
                                                                                                        val calendar =
                                                                                                                Calendar.getInstance(
                                                                                                                        TimezoneUtils
                                                                                                                                .AUSTRALIAN_TIMEZONE
                                                                                                                )
                                                                                                        calendar.timeInMillis =
                                                                                                                millis
                                                                                                        val newDate =
                                                                                                                calendar.time
                                                                                                        if (newDate !=
                                                                                                                        formData.date
                                                                                                        ) {
                                                                                                                // Only reset selectedMatch when the user actually picked a different date
                                                                                                                eventViewModel
                                                                                                                        .updateFormData(
                                                                                                                        formData.copy(
                                                                                                                                        date =
                                                                                                                                                newDate,
                                                                                                                                        selectedMatch =
                                                                                                                                                null
                                                                                                                        )
                                                                                                                )
                                                                                                        }
                                                                                                }
                                                                                        showDatePicker =
                                                                                                false
                                                                                },
                                                                                colors =
                                                                                        ButtonDefaults
                                                                                                .textButtonColors(
                                                                                                        contentColor =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .primary
                                                                                )
                                                                        ) { Text("OK") }
                                                                },
                                                                dismissButton = {
                                                                        TextButton(
                                                                                onClick = {
                                                                                        showDatePicker =
                                                                                                false
                                                                                },
                                                                                colors =
                                                                                        ButtonDefaults
                                                                                                .textButtonColors(
                                                                                                        contentColor =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .onSurfaceVariant
                                                                                )
                                                                        ) { Text("Cancel") }
                                                                },
                                                                colors =
                                                                        DatePickerDefaults.colors(
                                                                                containerColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .surface,
                                                                                titleContentColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurface,
                                                                                headlineContentColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurface,
                                                                                weekdayContentColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurfaceVariant,
                                                                                subheadContentColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurfaceVariant,
                                                                                yearContentColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurface,
                                                                                currentYearContentColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary,
                                                                                selectedYearContentColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onPrimary,
                                                                                selectedYearContainerColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary,
                                                                                dayContentColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurface,
                                                                                disabledDayContentColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurface
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.38f
                                                                                                ),
                                                                                selectedDayContentColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onPrimary,
                                                                                disabledSelectedDayContentColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onPrimary
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.38f
                                                                                                ),
                                                                                selectedDayContainerColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary,
                                                                                disabledSelectedDayContainerColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.12f
                                                                                                ),
                                                                                todayContentColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary,
                                                                                todayDateBorderColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary,
                                                                                dayInSelectionRangeContentColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onPrimary,
                                                                                dayInSelectionRangeContainerColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.12f
                                                                        )
                                                                )
                                                        ) {
                                                                DatePicker(
                                                                        state = datePickerState,
                                                                        showModeToggle = false
                                                                )
                                                        }
                                                }

                                                // Time picker dialog
                                                if (showTimePicker) {
                                                        // Read time directly without timezone conversion
                                                        val calendar = Calendar.getInstance()
                                                        calendar.time = formData.checkInTime
                                                        val hour = calendar.get(Calendar.HOUR_OF_DAY)
                                                        val minute = calendar.get(Calendar.MINUTE)

                                                        val timePickerState =
                                                                rememberTimePickerState(
                                                                        initialHour = hour,
                                                                        initialMinute = minute
                                                                )

                                                        AlertDialog(
                                                                onDismissRequest = {
                                                                        showTimePicker = false
                                                                },
                                                                title = {
                                                                        Text(
                                                                                "Select Check-in Time",
                                                                                color =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurface
                                                                        )
                                                                },
                                                                text = {
                                                                        TimePicker(
                                                                                state =
                                                                                        timePickerState,
                                                                                colors =
                                                                                        TimePickerDefaults
                                                                                                .colors(
                                                                                                        clockDialColor =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .surfaceVariant,
                                                                                                        clockDialSelectedContentColor =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .onPrimary,
                                                                                                        clockDialUnselectedContentColor =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .onSurfaceVariant,
                                                                                                        selectorColor =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .primary,
                                                                                                        periodSelectorBorderColor =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .outline,
                                                                                                        periodSelectorSelectedContainerColor =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .primary,
                                                                                                        periodSelectorUnselectedContainerColor =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .surface,
                                                                                                        periodSelectorSelectedContentColor =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .onPrimary,
                                                                                                        periodSelectorUnselectedContentColor =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .onSurface,
                                                                                                        timeSelectorSelectedContainerColor =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .primary,
                                                                                                        timeSelectorUnselectedContainerColor =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .surface,
                                                                                                        timeSelectorSelectedContentColor =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .onPrimary,
                                                                                                        timeSelectorUnselectedContentColor =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .onSurface
                                                                                )
                                                                        )
                                                                },
                                                                confirmButton = {
                                                                        TextButton(
                                                                                onClick = {
                                                                                        // Create time directly without timezone conversion
                                                                                        val calendar = Calendar.getInstance()
                                                                                        calendar.time = formData.date
                                                                                        calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                                                                        calendar.set(Calendar.MINUTE, timePickerState.minute)
                                                                                        calendar.set(Calendar.SECOND, 0)
                                                                                        calendar.set(Calendar.MILLISECOND, 0)
                                                                                        val newTime = calendar.time
                                                                                        
                                                                                        // Debug logging
                                                                                        android.util.Log.d("EventFormDialog", "Time picker selected: ${timePickerState.hour}:${timePickerState.minute}")
                                                                                        android.util.Log.d("EventFormDialog", "New time created: ${newTime}")
                                                                                        android.util.Log.d("EventFormDialog", "New time formatted: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(newTime)}")

                                                                                        eventViewModel
                                                                                                .updateFormData(
                                                                                                        formData.copy(
                                                                                                                checkInTime =
                                                                                                                        newTime
                                                                                                        )
                                                                                                )
                                                                                        showTimePicker =
                                                                                                false
                                                                                },
                                                                                colors =
                                                                                        ButtonDefaults
                                                                                                .textButtonColors(
                                                                                                        contentColor =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .primary
                                                                                )
                                                                        ) { Text("OK") }
                                                                },
                                                                dismissButton = {
                                                                        TextButton(
                                                                                onClick = {
                                                                                        showTimePicker =
                                                                                                false
                                                                                },
                                                                                colors =
                                                                                        ButtonDefaults
                                                                                                .textButtonColors(
                                                                                                        contentColor =
                                                                                                                MaterialTheme
                                                                                                                        .colorScheme
                                                                                                                        .onSurfaceVariant
                                                                                )
                                                                        ) { Text("Cancel") }
                                                                },
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .surface,
                                                                titleContentColor =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurface,
                                                                textContentColor =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant
                                                        )
                                                }
                                        }

                                        // Match Selection
                                        EventFormSection(title = "Match") {
                                                // State to track whether "Live" or "Custom" mode is
                                                // selected
                                                // true for Live, false for Custom
                                                var isLiveMatchMode by
                                                        remember(
                                                                uiState.availableMatches,
                                                                formData.selectedMatch
                                                        ) {
                                                                mutableStateOf(
                                                                        // If editing and has a selected match, check if it's a live match
                                                                        formData.selectedMatch?.let { match ->
                                                                                // Custom matches have "Custom Match" as round, live matches have actual round numbers
                                                                                match.round != "Custom Match" && 
                                                                                uiState.availableMatches.isNotEmpty()
                                                                        } ?: uiState.availableMatches.isNotEmpty()
                                                                )
                                                }

                                                val availableTeams = uiState.availableTeams

                                                var selectedHomeTeam by
                                                        remember(
                                                                availableTeams,
                                                                formData.selectedMatch
                                                        ) {
                                                                mutableStateOf<Team?>(
                                                                        // Initialize with team from
                                                                        // selected match if
                                                                        // available
                                                                        formData.selectedMatch
                                                                                ?.homeTeam?.let {
                                                                                teamName ->
                                                        availableTeams
                                                                                        .find {
                                                                                                it.name ==
                                                                                                        teamName
                                                                                        }
                                                                        }
                                                                )
                                                        }
                                                var selectedAwayTeam by
                                                        remember(
                                                                availableTeams,
                                                                formData.selectedMatch
                                                        ) {
                                                                mutableStateOf<Team?>(
                                                                        // Initialize with team from
                                                                        // selected match if
                                                                        // available
                                                                        formData.selectedMatch
                                                                                ?.awayTeam?.let {
                                                                                teamName ->
                                                                                availableTeams
                                                                                        .find {
                                                                                                it.name ==
                                                                                                        teamName
                                                                                        }
                                                                        }
                                                                )
                                                }

                                                LaunchedEffect(uiState.availableMatches) {
                                                        if (uiState.availableMatches.isEmpty()) {
                                                                isLiveMatchMode = false
                                                        }
                                                }

                                                Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(
                                                                16.dp
                                                        ) // Add space between buttons
                                                ) {
                                                        // --- Live Button ---
                                                        if (isLiveMatchMode) {
                                                                // Selected state: Filled Button
                                                                Button(
                                                                        onClick = {
                                                                                isLiveMatchMode =
                                                                                        true
                                                                        },
                                                                        colors =
                                                                                ButtonDefaults
                                                                                        .buttonColors(
                                                                                                containerColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .primary, // Or your orange Color(0xFFFFA500)
                                                                                                contentColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onPrimary
                                                                                        ),
                                                                        modifier =
                                                                                Modifier.weight(1f)
                                                                ) { Text("Live") }
                                                        } else {
                                                                // Unselected state: Outlined Button
                                                                OutlinedButton(
                                                                        onClick = {
                                                                                isLiveMatchMode =
                                                                                        true
                                                                        },
                                                                        colors =
                                                                                ButtonDefaults
                                                                                        .outlinedButtonColors(
                                                                                                contentColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .primary // Text color for outlined button
                                                                                        ),
                                                                        border =
                                                                                BorderStroke(
                                                                                1.dp,
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary
                                                                        ), // Outline color
                                                                        modifier =
                                                                                Modifier.weight(1f)
                                                                ) { Text("Live") }
                                                        }

                                                        // --- Custom Button ---
                                                        if (!isLiveMatchMode
                                                        ) { // Note the negation here
                                                                // Selected state: Filled Button
                                                                Button(
                                                                        onClick = {
                                                                                isLiveMatchMode =
                                                                                        false
                                                                        },
                                                                        colors =
                                                                                ButtonDefaults
                                                                                        .buttonColors(
                                                                                                containerColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .primary, // Or your desired selected color
                                                                                                contentColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onPrimary
                                                                                        ),
                                                                        modifier =
                                                                                Modifier.weight(1f)
                                                                ) { Text("Custom") }
                                                        } else {
                                                                // Unselected state: Outlined Button
                                                                OutlinedButton(
                                                                        onClick = {
                                                                                isLiveMatchMode =
                                                                                        false
                                                                        },
                                                                        colors =
                                                                                ButtonDefaults
                                                                                        .outlinedButtonColors(
                                                                                                contentColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .primary // Text color
                                                                                        ),
                                                                        border =
                                                                                BorderStroke(
                                                                                1.dp,
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary
                                                                        ), // Outline color
                                                                        modifier =
                                                                                Modifier.weight(1f)
                                                                ) { Text("Custom") }
                                                        }
                                                }
                                                if (isLiveMatchMode) {
                                                        // --- UI for LIVE Match Mode ---
                                                        if (uiState.availableMatches.isNotEmpty()) {
                                                                var expanded by remember {
                                                                        mutableStateOf(false)
                                                                }

                                                                ExposedDropdownMenuBox(
                                                                        expanded = expanded,
                                                                        onExpandedChange = {
                                                                                expanded = !expanded
                                                                        },
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
                                                                ) {
                                                                        OutlinedTextField(
                                                                                value =
                                                                                        formData.selectedMatch // <<< THIS IS THE KEY CHANGE
                                                                                                ?.let {
                                                                                                        currentSelectedMatch
                                                                                                        -> // If a match is selected in formData...
                                                                                                val home =
                                                                                                                currentSelectedMatch
                                                                                                                        .homeTeam
                                                                                                                ?: "TBD"
                                                                                                val away =
                                                                                                                currentSelectedMatch
                                                                                                                        .awayTeam
                                                                                                                ?: "TBD"
                                                                                                val venueInfo =
                                                                                                                currentSelectedMatch
                                                                                                                        .venue
                                                                                                                        ?.let {
                                                                                                                                " - $it"
                                                                                                                        }
                                                                                                                ?: ""
                                                                                                "$home vs $away$venueInfo" // ...display its details.
                                                                                        }
                                                                                        ?: "Select a live match *", // Default if formData.selectedMatch is null
                                                                                onValueChange = {
                                                                                }, // Not directly
                                                                                // editable
                                                                                readOnly = true,
                                                                                label = {
                                                                                        Text(
                                                                                                "Selected Live Match *"
                                                                                        )
                                                                                }, // Updated label
                                                                                trailingIcon = {
                                                                                        ExposedDropdownMenuDefaults
                                                                                                .TrailingIcon(
                                                                                                        expanded =
                                                                                                                expanded
                                                                                                )
                                                                                },
                                                                                modifier =
                                                                                        Modifier.fillMaxWidth()
                                                                                        .menuAnchor()
                                                                        )

                                                                        ExposedDropdownMenu(
                                                                                expanded = expanded,
                                                                                onDismissRequest = {
                                                                                        expanded =
                                                                                                false
                                                                                }
                                                                        ) {
                                                                                if (uiState.isLoadingMatches &&
                                                                                                uiState.availableMatches
                                                                                                        .isEmpty()
                                                                                ) {
                                                                                        DropdownMenuItem(
                                                                                                onClick = {
                                                                                                },
                                                                                                enabled =
                                                                                                        false,
                                                                                                text = {
                                                                                                        Text(
                                                                                                                "Still loading..."
                                                                                                        )
                                                                                                }
                                                                                        )
                                                                                }
                                                                                uiState.availableMatches
                                                                                        .forEach {
                                                                                                matchInList
                                                                                                ->
                                                                                        DropdownMenuItem(
                                                                                                onClick = {
                                                                                                                eventViewModel
                                                                                                                        .selectMatch(
                                                                                                                matchInList
                                                                                                        )
                                                                                                        expanded =
                                                                                                                false
                                                                                                },
                                                                                                text = {
                                                                                                        Text(
                                                                                                                "${matchInList.homeTeam ?: "TBD"} vs ${matchInList.awayTeam ?: "TBD"}"
                                                                                                        )
                                                                                                }
                                                                                        )
                                                                                }
                                                                        }
                                                                }
                                                                formData.selectedMatch?.let {
                                                                        selected ->
                                                                        Spacer(
                                                                                modifier =
                                                                                        Modifier.height(
                                                                                        12.dp
                                                                                )
                                                                        )

                                                                        TeamVsTeamDisplay(
                                                                                homeTeamName =
                                                                                        selected.homeTeam,
                                                                                awayTeamName =
                                                                                        selected.awayTeam
                                                                        )
                                                                }
                                                        } else { // No matches available and not
                                                                // loading
                                                                Text(
                                                                        text =
                                                                                "There are no live matches available for the selected date. Please select another day or create a custom match.",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodyMedium,
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .error,
                                                                        textAlign =
                                                                                TextAlign.Center,
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
                                                                                .padding(
                                                                                                horizontal =
                                                                                                        16.dp,
                                                                                                vertical =
                                                                                                        8.dp
                                                                                )
                                                                )
                                                        }
                                                } // --- Custom Matches ---
                                                else {

                                                        // Add this LaunchedEffect to load teams
                                                        // when entering custom mode
                                                        LaunchedEffect(Unit) {
                                                                eventViewModel
                                                                        .loadAflTeams() // You need
                                                                // to make
                                                                // this
                                                                // method
                                                                // public
                                                        }

                                                        var expandedHomeTeam by remember {
                                                                mutableStateOf(false)
                                                        }
                                                        var expandedAwayTeam by remember {
                                                                mutableStateOf(false)
                                                        }

                                                        Column(
                                                                modifier =
                                                                        Modifier.fillMaxWidth()
                                                                                .padding(
                                                                                        vertical =
                                                                                                8.dp
                                                                                ),
                                                                horizontalAlignment =
                                                                        Alignment
                                                                                .CenterHorizontally,
                                                                verticalArrangement =
                                                                        Arrangement.spacedBy(12.dp)
                                                        ) {
                                                                Row(
                                                                        modifier =
                                                                                Modifier.fillMaxWidth(),
                                                                        horizontalArrangement =
                                                                                Arrangement
                                                                                        .spacedBy(
                                                                                                16.dp
                                                                                        ), // gap
                                                                        // between fields
                                                                        verticalAlignment =
                                                                                Alignment.Top
                                                                ) {
                                                                        // --- Home Team Dropdown
                                                                        // ---
                                                                        Column(
                                                                                modifier =
                                                                                        Modifier.weight(
                                                                                                1f
                                                                                        )
                                                                        ) {
                                                                                Text(
                                                                                        text =
                                                                                                "Home Team *",
                                                                                        style =
                                                                                                MaterialTheme
                                                                                                        .typography
                                                                                                        .titleMedium,
                                                                                        fontWeight =
                                                                                                FontWeight
                                                                                                        .Bold,
                                                                                        modifier =
                                                                                                Modifier.padding(
                                                                                                        bottom =
                                                                                                                8.dp
                                                                                        )
                                                                                )

                                                                                ExposedDropdownMenuBox(
                                                                                        expanded =
                                                                                                expandedHomeTeam,
                                                                                        onExpandedChange = {
                                                                                                expandedHomeTeam =
                                                                                                        !expandedHomeTeam
                                                                                        }
                                                                                ) {
                                                                                        OutlinedTextField(
                                                                                                value =
                                                                                                        selectedHomeTeam
                                                                                                                ?.name
                                                                                                        ?: "Home team",
                                                                                                onValueChange = {
                                                                                                },
                                                                                                readOnly =
                                                                                                        true,
                                                                                                trailingIcon = {
                                                                                                        ExposedDropdownMenuDefaults
                                                                                                                .TrailingIcon(
                                                                                                                expandedHomeTeam
                                                                                                        )
                                                                                                },
                                                                                                modifier =
                                                                                                        Modifier.fillMaxWidth()
                                                                                                        .menuAnchor()
                                                                                        )

                                                                                        ExposedDropdownMenu(
                                                                                                expanded =
                                                                                                        expandedHomeTeam,
                                                                                                onDismissRequest = {
                                                                                                        expandedHomeTeam =
                                                                                                                false
                                                                                                }
                                                                                        ) {
                                                                                                if (availableTeams
                                                                                                                .isEmpty()
                                                                                                ) {
                                                                                                        DropdownMenuItem(
                                                                                                                text = {
                                                                                                                        Text(
                                                                                                                                "No teams available"
                                                                                                                        )
                                                                                                                },
                                                                                                                onClick = {
                                                                                                                }
                                                                                                        )
                                                                                                } else {
                                                                                                        availableTeams
                                                                                                                .forEach {
                                                                                                                        team
                                                                                                                        ->
                                                                                                                DropdownMenuItem(
                                                                                                                        text = {
                                                                                                                                        Row(
                                                                                                                                                verticalAlignment =
                                                                                                                                                        Alignment
                                                                                                                                                                .CenterVertically
                                                                                                                                        ) {
                                                                                                                                                team.localLogoRes
                                                                                                                                                        ?.let {
                                                                                                                                                                logoRes
                                                                                                                                                                ->
                                                                                                                                                Image(
                                                                                                                                                                        painter =
                                                                                                                                                                                painterResource(
                                                                                                                                                                                        id =
                                                                                                                                                                                                logoRes
                                                                                                                                                                                ),
                                                                                                                                                                        contentDescription =
                                                                                                                                                                                team.name,
                                                                                                                                                                        modifier =
                                                                                                                                                                                Modifier.size(
                                                                                                                                                                24.dp
                                                                                                                                                        )
                                                                                                                                                )
                                                                                                                                                Spacer(
                                                                                                                                                                        modifier =
                                                                                                                                                                                Modifier.width(
                                                                                                                                                                8.dp
                                                                                                                                                        )
                                                                                                                                                )
                                                                                                                                        }
                                                                                                                                                Text(
                                                                                                                                                        team.name
                                                                                                                                                )
                                                                                                                                }
                                                                                                                        },
                                                                                                                        // In the home team onClick:
                                                                                                                        onClick = {
                                                                                                                                selectedHomeTeam =
                                                                                                                                        team
                                                                                                                                expandedHomeTeam =
                                                                                                                                        false
                                                                                                                        }
                                                                                                                )
                                                                                                        }
                                                                                                }
                                                                                        }
                                                                                }
                                                                        }

                                                                        // --- Away Team Dropdown
                                                                        // ---
                                                                        Column(
                                                                                modifier =
                                                                                        Modifier.weight(
                                                                                                1f
                                                                                        )
                                                                        ) {
                                                                                Text(
                                                                                        text =
                                                                                                "Away Team *",
                                                                                        style =
                                                                                                MaterialTheme
                                                                                                        .typography
                                                                                                        .titleMedium,
                                                                                        fontWeight =
                                                                                                FontWeight
                                                                                                        .Bold,
                                                                                        modifier =
                                                                                                Modifier.padding(
                                                                                                        bottom =
                                                                                                                8.dp
                                                                                        )
                                                                                )

                                                                                ExposedDropdownMenuBox(
                                                                                        expanded =
                                                                                                expandedAwayTeam,
                                                                                        onExpandedChange = {
                                                                                                expandedAwayTeam =
                                                                                                        !expandedAwayTeam
                                                                                        }
                                                                                ) {
                                                                                        OutlinedTextField(
                                                                                                value =
                                                                                                        selectedAwayTeam
                                                                                                                ?.name
                                                                                                        ?: "Away team",
                                                                                                onValueChange = {
                                                                                                },
                                                                                                readOnly =
                                                                                                        true,
                                                                                                trailingIcon = {
                                                                                                        ExposedDropdownMenuDefaults
                                                                                                                .TrailingIcon(
                                                                                                                expandedAwayTeam
                                                                                                        )
                                                                                                },
                                                                                                modifier =
                                                                                                        Modifier.fillMaxWidth()
                                                                                                        .menuAnchor()
                                                                                        )

                                                                                        ExposedDropdownMenu(
                                                                                                expanded =
                                                                                                        expandedAwayTeam,
                                                                                                onDismissRequest = {
                                                                                                        expandedAwayTeam =
                                                                                                                false
                                                                                                }
                                                                                        ) {
                                                                                                availableTeams
                                                                                                        .filter {
                                                                                                                it !=
                                                                                                                        selectedHomeTeam
                                                                                                        }
                                                                                                        .forEach {
                                                                                                                team
                                                                                                                ->
                                                                                                                DropdownMenuItem(
                                                                                                                        text = {
                                                                                                                                Row(
                                                                                                                                        verticalAlignment =
                                                                                                                                                Alignment
                                                                                                                                                        .CenterVertically
                                                                                                                                ) {
                                                                                                                                        team.localLogoRes
                                                                                                                                                ?.let {
                                                                                                                                                        logoRes
                                                                                                                                                        ->
                                                                                                                                                Image(
                                                                                                                                                                painter =
                                                                                                                                                                        painterResource(
                                                                                                                                                                                id =
                                                                                                                                                                                        logoRes
                                                                                                                                                                        ),
                                                                                                                                                                contentDescription =
                                                                                                                                                                        team.name,
                                                                                                                                                                modifier =
                                                                                                                                                                        Modifier.size(
                                                                                                                                                                24.dp
                                                                                                                                                        )
                                                                                                                                                )
                                                                                                                                                Spacer(
                                                                                                                                                                modifier =
                                                                                                                                                                        Modifier.width(
                                                                                                                                                                8.dp
                                                                                                                                                        )
                                                                                                                                                )
                                                                                                                                        }
                                                                                                                                        Text(
                                                                                                                                                team.name
                                                                                                                                        )
                                                                                                                                }
                                                                                                                        },
                                                                                                                        // In the away team onClick:
                                                                                                                        onClick = {
                                                                                                                                selectedAwayTeam =
                                                                                                                                        team
                                                                                                                                expandedAwayTeam =
                                                                                                                                        false
                                                                                                                        }
                                                                                                                )
                                                                                                        }
                                                                                        }
                                                                                }
                                                                        }
                                                                }

                                                                // --- Preview of selected match ---
                                                                if (selectedHomeTeam != null &&
                                                                                selectedAwayTeam !=
                                                                                        null
                                                                ) {
                                                                        Spacer(
                                                                                modifier =
                                                                                        Modifier.height(
                                                                                        16.dp
                                                                                )
                                                                        )
                                                                        TeamVsTeamDisplay(
                                                                                homeTeamName =
                                                                                        selectedHomeTeam!!
                                                                                                .name,
                                                                                awayTeamName =
                                                                                        selectedAwayTeam!!
                                                                                                .name
                                                                        )
                                                                }
                                                        }
                                                }

                                                val showEventInfoSection =
                                                        (!isLiveMatchMode) ||
                                                                (isLiveMatchMode &&
                                                                        uiState.availableMatches
                                                                                .isNotEmpty())

                                                if (showEventInfoSection) {
                                                        // Location
                                                        EventFormSection(title = "Location") {
                                                                OutlinedTextField(
                                                                        value =
                                                                                formData.locationName,
                                                                        onValueChange = {
                                                                                eventViewModel
                                                                                        .updateFormData(
                                                                                        formData.copy(
                                                                                                        locationName =
                                                                                                                it
                                                                                                )
                                                                                        )
                                                                        },
                                                                        label = {
                                                                                Text(
                                                                                        "Location Name *"
                                                                                )
                                                                        },
                                                                        modifier =
                                                                                Modifier.fillMaxWidth(),
                                                                        singleLine = true
                                                                )
                                                                OutlinedTextField(
                                                                        value =
                                                                                formData.locationAddress,
                                                                        onValueChange = {
                                                                                eventViewModel
                                                                                        .updateFormData(
                                                                                        formData.copy(
                                                                                                        locationAddress =
                                                                                                                it
                                                                                                )
                                                                                        )
                                                                        },
                                                                        label = {
                                                                                Text("Address *")
                                                                        },
                                                                        modifier =
                                                                                Modifier.fillMaxWidth(),
                                                                        maxLines = 2
                                                                )

                                                                // Map picker button
                                                                Button(
                                                                        onClick = {
                                                                                eventViewModel
                                                                                        .showMapPicker()
                                                                        },
                                                                        modifier =
                                                                                Modifier.fillMaxWidth(),
                                                                        colors =
                                                                                ButtonDefaults
                                                                                        .buttonColors(
                                                                                        containerColor =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .primary
                                                                                )
                                                                ) {
                                                                        Icon(
                                                                                painter =
                                                                                        painterResource(
                                                                                                id =
                                                                                                        R.drawable
                                                                                                                .ic_location
                                                                                        ),
                                                                                contentDescription =
                                                                                        "Select location on map",
                                                                                tint =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onPrimary
                                                                        )
                                                                        Spacer(
                                                                                modifier =
                                                                                        Modifier.width(
                                                                                        8.dp
                                                                                )
                                                                        )
                                                                        Text("Select Map Location")
                                                                }
                                                                // Show selected coordinates if
                                                                // available
                                                                if (formData.latitude != 0.0 &&
                                                                                formData.longitude !=
                                                                                        0.0
                                                                ) {
                                                                        Card(
                                                                                modifier =
                                                                                        Modifier.fillMaxWidth(),
                                                                                colors =
                                                                                        CardDefaults
                                                                                                .cardColors(
                                                                                                containerColor =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .surfaceVariant
                                                                                        )
                                                                        ) {
                                                                                Column(
                                                                                        modifier =
                                                                                                Modifier.padding(
                                                                                                        12.dp
                                                                                                )
                                                                                ) {
                                                                                        Text(
                                                                                                text =
                                                                                                        "Selected Location:",
                                                                                                style =
                                                                                                        MaterialTheme
                                                                                                                .typography
                                                                                                                .titleSmall,
                                                                                                fontWeight =
                                                                                                        FontWeight
                                                                                                                .SemiBold
                                                                                        )
                                                                                        Spacer(
                                                                                                modifier =
                                                                                                        Modifier.height(
                                                                                                                4.dp
                                                                                                        )
                                                                                        )
                                                                                        Text(
                                                                                                text =
                                                                                                        "Coordinates: ${formData.latitude}, ${formData.longitude}",
                                                                                                style =
                                                                                                        MaterialTheme
                                                                                                                .typography
                                                                                                                .bodySmall,
                                                                                                color =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onSurfaceVariant
                                                                                        )
                                                                                }
                                                                        }
                                                                }
                                                        }

                                                        // Capacity and Contact
                                                        EventFormSection(title = "Details") {
                                                                var capacityText by remember {
                                                                        mutableStateOf(
                                                                                formData.capacity
                                                                                        .toString()
                                                                        )
                                                                }
                                                                OutlinedTextField(
                                                                        value = capacityText,
                                                                        onValueChange = { newText ->
                                                                                // Allow empty
                                                                                // string for
                                                                                // deletion
                                                                                if (newText.isEmpty() ||
                                                                                                newText
                                                                                                        .all {
                                                                                                                it.isDigit()
                                                                                                        }
                                                                                ) {
                                                                                        capacityText =
                                                                                                newText
                                                                                        // Update
                                                                                        // numeric
                                                                                        // capacity,
                                                                                        // default 0
                                                                                        // if empty
                                                                                        val capacityInt =
                                                                                                newText.toIntOrNull()
                                                                                                        ?: 0
                                                                                        eventViewModel
                                                                                                .updateFormData(
                                                                                                formData.copy(
                                                                                                                capacity =
                                                                                                                        capacityInt
                                                                                                )
                                                                                        )
                                                                                }
                                                                        },
                                                                        label = {
                                                                                Text("Capacity *")
                                                                        },
                                                                        modifier =
                                                                                Modifier.fillMaxWidth(),
                                                                        keyboardOptions =
                                                                                KeyboardOptions(
                                                                                        keyboardType =
                                                                                                KeyboardType
                                                                                                        .Number
                                                                        )
                                                                )

                                                                OutlinedTextField(
                                                                        value =
                                                                                formData.contactNumber,
                                                                        onValueChange = {
                                                                                eventViewModel
                                                                                        .updateFormData(
                                                                                        formData.copy(
                                                                                                        contactNumber =
                                                                                                                it
                                                                                        )
                                                                                )
                                                                        },
                                                                        label = {
                                                                                Text(
                                                                                        "Contact Number"
                                                                                )
                                                                        },
                                                                        modifier =
                                                                                Modifier.fillMaxWidth(),
                                                                        keyboardOptions =
                                                                                KeyboardOptions(
                                                                                        keyboardType =
                                                                                                KeyboardType
                                                                                                        .Phone
                                                                                )
                                                                )
                                                        }
                                                        var amenitiesExpanded by remember {
                                                                mutableStateOf(false)
                                                        } // Default to collapsed

                                                        // Amenities
                                                        EventFormSection(
                                                                title = "Amenities",
                                                                isExpanded = amenitiesExpanded,
                                                                onHeaderClick = {
                                                                        amenitiesExpanded =
                                                                                !amenitiesExpanded
                                                                }
                                                        ) {
                                                                CheckboxRow(
                                                                        checked =
                                                                                formData.amenities
                                                                                        .isIndoor,
                                                                        onCheckedChange = {
                                                                                eventViewModel
                                                                                        .updateFormData(
                                                                                        formData.copy(
                                                                                                amenities =
                                                                                                        formData.amenities
                                                                                                                .copy(
                                                                                                                        isIndoor =
                                                                                                                                it
                                                                                                                )
                                                                                        )
                                                                                )
                                                                        },
                                                                        text = "Indoor"
                                                                )

                                                                CheckboxRow(
                                                                        checked =
                                                                                formData.amenities
                                                                                        .isOutdoor,
                                                                        onCheckedChange = {
                                                                                eventViewModel
                                                                                        .updateFormData(
                                                                                        formData.copy(
                                                                                                amenities =
                                                                                                        formData.amenities
                                                                                                                .copy(
                                                                                                                        isOutdoor =
                                                                                                                                it
                                                                                                                )
                                                                                        )
                                                                                )
                                                                        },
                                                                        text = "Outdoor"
                                                                )

                                                                CheckboxRow(
                                                                        checked =
                                                                                formData.amenities
                                                                                        .isChildFriendly,
                                                                        onCheckedChange = {
                                                                                eventViewModel
                                                                                        .updateFormData(
                                                                                        formData.copy(
                                                                                                amenities =
                                                                                                        formData.amenities
                                                                                                                .copy(
                                                                                                                        isChildFriendly =
                                                                                                                                it
                                                                                                                )
                                                                                        )
                                                                                )
                                                                        },
                                                                        text = "Child Friendly"
                                                                )

                                                                CheckboxRow(
                                                                        checked =
                                                                                formData.amenities
                                                                                        .isPetFriendly,
                                                                        onCheckedChange = {
                                                                                eventViewModel
                                                                                        .updateFormData(
                                                                                        formData.copy(
                                                                                                amenities =
                                                                                                        formData.amenities
                                                                                                                .copy(
                                                                                                                        isPetFriendly =
                                                                                                                                it
                                                                                                                )
                                                                                        )
                                                                                )
                                                                        },
                                                                        text = "Pet Friendly"
                                                                )

                                                                CheckboxRow(
                                                                        checked =
                                                                                formData.amenities
                                                                                        .hasParking,
                                                                        onCheckedChange = {
                                                                                eventViewModel
                                                                                        .updateFormData(
                                                                                        formData.copy(
                                                                                                amenities =
                                                                                                        formData.amenities
                                                                                                                .copy(
                                                                                                                        hasParking =
                                                                                                                                it
                                                                                                                )
                                                                                        )
                                                                                )
                                                                        },
                                                                        text = "Parking"
                                                                )

                                                                CheckboxRow(
                                                                        checked =
                                                                                formData.amenities
                                                                                        .hasFood,
                                                                        onCheckedChange = {
                                                                                eventViewModel
                                                                                        .updateFormData(
                                                                                        formData.copy(
                                                                                                amenities =
                                                                                                        formData.amenities
                                                                                                                .copy(
                                                                                                                        hasFood =
                                                                                                                                it
                                                                                                                )
                                                                                        )
                                                                                )
                                                                        },
                                                                        text = "Food"
                                                                )

                                                                CheckboxRow(
                                                                        checked =
                                                                                formData.amenities
                                                                                        .hasToilet,
                                                                        onCheckedChange = {
                                                                                eventViewModel
                                                                                        .updateFormData(
                                                                                        formData.copy(
                                                                                                amenities =
                                                                                                        formData.amenities
                                                                                                                .copy(
                                                                                                                        hasToilet =
                                                                                                                                it
                                                                                                                )
                                                                                        )
                                                                                )
                                                                        },
                                                                        text = "Toilet"
                                                                )

                                                                CheckboxRow(
                                                                        checked =
                                                                                formData.amenities
                                                                                        .hasWifi,
                                                                        onCheckedChange = {
                                                                                eventViewModel
                                                                                        .updateFormData(
                                                                                        formData.copy(
                                                                                                amenities =
                                                                                                        formData.amenities
                                                                                                                .copy(
                                                                                                                        hasWifi =
                                                                                                                                it
                                                                                                                )
                                                                                        )
                                                                                )
                                                                        },
                                                                        text = "WiFi"
                                                                )
                                                        }

                                                        var accessibilityExpanded by remember {
                                                                mutableStateOf(false)
                                                        }

                                                        // Accessibility
                                                        EventFormSection(
                                                                title = "Accessibility",
                                                                isExpanded = accessibilityExpanded,
                                                                onHeaderClick = {
                                                                        accessibilityExpanded =
                                                                                !accessibilityExpanded
                                                                }
                                                        ) {
                                                                CheckboxRow(
                                                                        checked =
                                                                                formData.accessibility
                                                                                        .isWheelchairAccessible,
                                                                        onCheckedChange = {
                                                                                eventViewModel
                                                                                        .updateFormData(
                                                                                        formData.copy(
                                                                                                        accessibility =
                                                                                                                formData.accessibility
                                                                                                                        .copy(
                                                                                                                                isWheelchairAccessible =
                                                                                                                                        it
                                                                                                )
                                                                                        )
                                                                                )
                                                                        },
                                                                        text =
                                                                                "Wheelchair Accessible"
                                                                )

                                                                CheckboxRow(
                                                                        checked =
                                                                                formData.accessibility
                                                                                        .hasAccessibleToilets,
                                                                        onCheckedChange = {
                                                                                eventViewModel
                                                                                        .updateFormData(
                                                                                        formData.copy(
                                                                                                accessibility =
                                                                                                        formData.accessibility
                                                                                                                .copy(
                                                                                                                        hasAccessibleToilets =
                                                                                                                                it
                                                                                                                )
                                                                                        )
                                                                                )
                                                                        },
                                                                        text = "Accessible Toilets"
                                                                )
                                                        }

                                                        // Footer buttons
                                                        HorizontalDivider()

                                                        Row(
                                                                modifier =
                                                                        Modifier.fillMaxWidth()
                                                                        .padding(16.dp),
                                                                horizontalArrangement =
                                                                        Arrangement.spacedBy(8.dp)
                                                        ) {
                                                                OutlinedButton(
                                                                        onClick = onDismiss,
                                                                        modifier =
                                                                                Modifier.weight(1f)
                                                                ) { Text("Cancel") }

                                                                Button(
                                                                        onClick = {
                                                                                if (!isLiveMatchMode &&
                                                                                                selectedHomeTeam !=
                                                                                                        null &&
                                                                                                selectedAwayTeam !=
                                                                                                        null
                                                                                ) {
                                                                                        eventViewModel
                                                                                                .createCustomMatch(
                                                                                                        selectedHomeTeam!!
                                                                                                                .name,
                                                                                                        selectedAwayTeam!!
                                                                                                                .name
                                                                                        )
                                                                                }
                                                                                onSave()
                                                                        },
                                                                        modifier =
                                                                                Modifier.weight(1f),
                                                                        enabled =
                                                                                when {
                                                                                        uiState.isLoading ->
                                                                                                false
                                                                                        isLiveMatchMode ->
                                                                                                formData.selectedMatch !=
                                                                                                        null &&
                                                                                                        formData.locationName
                                                                                                                .isNotBlank() &&
                                                                                                        formData.locationAddress
                                                                                                                .isNotBlank()
                                                                                        else ->
                                                                                                selectedHomeTeam !=
                                                                                                        null &&
                                                                                                        selectedAwayTeam !=
                                                                                                                null && // Check local state for custom matches
                                                                                                        formData.locationName
                                                                                                                .isNotBlank() &&
                                                                                                        formData.locationAddress
                                                                                                                .isNotBlank()
                                                                        }
                                                                ) {
                                                                        if (uiState.isLoading) {
                                                                                CircularProgressIndicator(
                                                                                        modifier =
                                                                                                Modifier.size(
                                                                                                16.dp
                                                                                        ),
                                                                                        color =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .onPrimary
                                                                                )
                                                                        } else {
                                                                                Text(
                                                                                        if (isEditing
                                                                                        )
                                                                                                "Update"
                                                                                        else
                                                                                                "Create"
                                                                                )
                                                                        }
                                                                }
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
}
