package com.example.mobilecomputingassignment.presentation.ui.component

import android.service.autofill.FieldClassification
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.presentation.ui.component.CustomMatchDialog
import com.example.mobilecomputingassignment.presentation.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.foundation.clickable // <<< ADD THIS LINE
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color // For Color(0xFFFFA500)
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.foundation.BorderStroke
import com.example.mobilecomputingassignment.domain.models.Team
import java.util.*

@Composable
private fun EventFormSection(
        title: String,
        isExpanded: Boolean,
        onHeaderClick: () -> Unit,
        content: @Composable ColumnScope.() -> Unit
) {
        Column {
                //Clickable Header Row
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                ) { onHeaderClick() }
                                .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Icon(
                                imageVector = if (isExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowRight,
                                contentDescription = if (isExpanded) "Collapse $title" else "Expand $title",
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
                                modifier = Modifier.padding(top = 8.dp), // Add some space above content
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
                        verticalArrangement = Arrangement.spacedBy(6.dp), // Spacing for content items
                        content = content
                )
        }
}

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

@Composable
private fun CheckboxRow(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        text: String,
        modifier: Modifier = Modifier
) {
        Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
        ) {
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
                        modifier = Modifier
                                .fillMaxWidth(0.95f)
                                .fillMaxHeight(0.9f),
                        shape = RoundedCornerShape(16.dp),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                )
                ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                                // Header
                                Row(
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
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
                                                Modifier
                                                        .weight(1f)
                                                        .verticalScroll(rememberScrollState())
                                                        .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                        // Load matches when date changes
                                        LaunchedEffect(formData.date) {
                                                eventViewModel.loadMatchesForDate(formData.date)

                                                eventViewModel.updateFormData(
                                                        formData.copy(
                                                                selectedMatch = null,       // clear selected match
                                                                locationName = "",          // clear location name
                                                                locationAddress = "",       // clear address
                                                                capacity = 0             // clear capacity if you have this field
                                                        )
                                                )
                                        }

                                        // Date and Time
                                        EventFormSection(title = "Date & Time") {
                                                var showDatePicker by remember { mutableStateOf(false) }
                                                var showTimePicker by remember { mutableStateOf(false) }
                                                Row(
                                                        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {

                                                        // Date picker
                                                        OutlinedTextField(
                                                                value = SimpleDateFormat(
                                                                        "yyyy-MM-dd",
                                                                        Locale.getDefault()
                                                                ).format(formData.date),
                                                                onValueChange = {},
                                                                label = { Text("Event Date *") },
                                                                modifier = Modifier.weight(1f),
                                                                readOnly = true,
                                                                trailingIcon = {
                                                                        IconButton(onClick = {
                                                                                showDatePicker =
                                                                                        true
                                                                        }) {
                                                                                Icon(
                                                                                        painter = painterResource(
                                                                                                id = R.drawable.ic_calendar
                                                                                        ),
                                                                                        contentDescription = "Select date",
                                                                                        tint = MaterialTheme.colorScheme.primary
                                                                                )
                                                                        }
                                                                }
                                                        )

                                                        // Time picker
                                                        OutlinedTextField(
                                                                value = SimpleDateFormat(
                                                                        "HH:mm",
                                                                        Locale.getDefault()
                                                                ).format(formData.checkInTime),
                                                                onValueChange = {},
                                                                label = { Text("Match Time *") },
                                                                modifier = Modifier.weight(1f),
                                                                readOnly = true,
                                                                trailingIcon = {
                                                                        IconButton(onClick = {
                                                                                showTimePicker =
                                                                                        true
                                                                        }) {
                                                                                Icon(
                                                                                        painter = painterResource(
                                                                                                id = R.drawable.ic_clock
                                                                                        ),
                                                                                        contentDescription = "Select time",
                                                                                        tint = MaterialTheme.colorScheme.primary
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
                                                                                                ?.let { millis
                                                                                                        ->
                                                                                                        val newDate =
                                                                                                                Date(
                                                                                                                        millis
                                                                                                                )
                                                                                                        eventViewModel
                                                                                                                .updateFormData(
                                                                                                                        formData.copy(
                                                                                                                                date =
                                                                                                                                        newDate
                                                                                                                        )
                                                                                                                )
                                                                                                }
                                                                                        showDatePicker =
                                                                                                false
                                                                                },
                                                                                colors = ButtonDefaults.textButtonColors(
                                                                                        contentColor = MaterialTheme.colorScheme.primary
                                                                                )
                                                                        ) { Text("OK") }
                                                                },
                                                                dismissButton = {
                                                                        TextButton(
                                                                                onClick = {
                                                                                        showDatePicker =
                                                                                                false
                                                                                },
                                                                                colors = ButtonDefaults.textButtonColors(
                                                                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                                                )
                                                                        ) { Text("Cancel") }
                                                                },
                                                                colors = DatePickerDefaults.colors(
                                                                        containerColor = MaterialTheme.colorScheme.surface,
                                                                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                                                                        headlineContentColor = MaterialTheme.colorScheme.onSurface,
                                                                        weekdayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                        subheadContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                        yearContentColor = MaterialTheme.colorScheme.onSurface,
                                                                        currentYearContentColor = MaterialTheme.colorScheme.primary,
                                                                        selectedYearContentColor = MaterialTheme.colorScheme.onPrimary,
                                                                        selectedYearContainerColor = MaterialTheme.colorScheme.primary,
                                                                        dayContentColor = MaterialTheme.colorScheme.onSurface,
                                                                        disabledDayContentColor = MaterialTheme.colorScheme.onSurface.copy(
                                                                                alpha = 0.38f
                                                                        ),
                                                                        selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                                                                        disabledSelectedDayContentColor = MaterialTheme.colorScheme.onPrimary.copy(
                                                                                alpha = 0.38f
                                                                        ),
                                                                        selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                                                                        disabledSelectedDayContainerColor = MaterialTheme.colorScheme.primary.copy(
                                                                                alpha = 0.12f
                                                                        ),
                                                                        todayContentColor = MaterialTheme.colorScheme.primary,
                                                                        todayDateBorderColor = MaterialTheme.colorScheme.primary,
                                                                        dayInSelectionRangeContentColor = MaterialTheme.colorScheme.onPrimary,
                                                                        dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.primary.copy(
                                                                                alpha = 0.12f
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
                                                        val calendar = Calendar.getInstance()
                                                        calendar.time = formData.checkInTime

                                                        val timePickerState =
                                                                rememberTimePickerState(
                                                                        initialHour =
                                                                                calendar.get(
                                                                                        Calendar.HOUR_OF_DAY
                                                                                ),
                                                                        initialMinute =
                                                                                calendar.get(
                                                                                        Calendar.MINUTE
                                                                                )
                                                                )

                                                        AlertDialog(
                                                                onDismissRequest = {
                                                                        showTimePicker = false
                                                                },
                                                                title = {
                                                                        Text(
                                                                                "Select Check-in Time",
                                                                                color = MaterialTheme.colorScheme.onSurface
                                                                        )
                                                                },
                                                                text = {
                                                                        TimePicker(
                                                                                state = timePickerState,
                                                                                colors = TimePickerDefaults.colors(
                                                                                        clockDialColor = MaterialTheme.colorScheme.surfaceVariant,
                                                                                        clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                                                                                        clockDialUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                                        selectorColor = MaterialTheme.colorScheme.primary,
                                                                                        periodSelectorBorderColor = MaterialTheme.colorScheme.outline,
                                                                                        periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                                                                                        periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surface,
                                                                                        periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                                                                                        periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurface,
                                                                                        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                                                                                        timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surface,
                                                                                        timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                                                                                        timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurface
                                                                                )
                                                                        )
                                                                },
                                                                confirmButton = {
                                                                        TextButton(
                                                                                onClick = {
                                                                                        val newCalendar =
                                                                                                Calendar.getInstance()
                                                                                        newCalendar
                                                                                                .set(
                                                                                                        Calendar.HOUR_OF_DAY,
                                                                                                        timePickerState
                                                                                                                .hour
                                                                                                )
                                                                                        newCalendar
                                                                                                .set(
                                                                                                        Calendar.MINUTE,
                                                                                                        timePickerState
                                                                                                                .minute
                                                                                                )
                                                                                        newCalendar
                                                                                                .set(
                                                                                                        Calendar.SECOND,
                                                                                                        0
                                                                                                )
                                                                                        newCalendar
                                                                                                .set(
                                                                                                        Calendar.MILLISECOND,
                                                                                                        0
                                                                                                )

                                                                                        eventViewModel
                                                                                                .updateFormData(
                                                                                                        formData.copy(
                                                                                                                checkInTime =
                                                                                                                        newCalendar
                                                                                                                                .time
                                                                                                        )
                                                                                                )
                                                                                        showTimePicker =
                                                                                                false
                                                                                },
                                                                                colors = ButtonDefaults.textButtonColors(
                                                                                        contentColor = MaterialTheme.colorScheme.primary
                                                                                )
                                                                        ) { Text("OK") }
                                                                },
                                                                dismissButton = {
                                                                        TextButton(
                                                                                onClick = {
                                                                                        showTimePicker =
                                                                                                false
                                                                                },
                                                                                colors = ButtonDefaults.textButtonColors(
                                                                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                                                )
                                                                        ) { Text("Cancel") }
                                                                },
                                                                containerColor = MaterialTheme.colorScheme.surface,
                                                                titleContentColor = MaterialTheme.colorScheme.onSurface,
                                                                textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                }
                                        }

                                        // Match Selection
                                        EventFormSection(title = "Match") {
                                                // State to track whether "Live" or "Custom" mode is selected
                                                // true for Live, false for Custom
                                                var isLiveMatchMode by remember(uiState.availableMatches) {
                                                        mutableStateOf(uiState.availableMatches.isNotEmpty())
                                                }

                                                LaunchedEffect(uiState.availableMatches) {
                                                        if (uiState.availableMatches.isEmpty()) {
                                                                isLiveMatchMode = false
                                                        }
                                                }

                                                Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(
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
                                                                        colors = ButtonDefaults.buttonColors(
                                                                                containerColor = MaterialTheme.colorScheme.primary, // Or your orange Color(0xFFFFA500)
                                                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                                                        ),
                                                                        modifier = Modifier.weight(
                                                                                1f
                                                                        )
                                                                ) {
                                                                        Text("Live")
                                                                }
                                                        } else {
                                                                // Unselected state: Outlined Button
                                                                OutlinedButton(
                                                                        onClick = {
                                                                                isLiveMatchMode =
                                                                                        true
                                                                        },
                                                                        colors = ButtonDefaults.outlinedButtonColors(
                                                                                contentColor = MaterialTheme.colorScheme.primary // Text color for outlined button
                                                                        ),
                                                                        border = BorderStroke(
                                                                                1.dp,
                                                                                MaterialTheme.colorScheme.primary
                                                                        ), // Outline color
                                                                        modifier = Modifier.weight(
                                                                                1f
                                                                        )
                                                                ) {
                                                                        Text("Live")
                                                                }
                                                        }

                                                        // --- Custom Button ---
                                                        if (!isLiveMatchMode) { // Note the negation here
                                                                // Selected state: Filled Button
                                                                Button(
                                                                        onClick = {
                                                                                isLiveMatchMode =
                                                                                        false
                                                                        },
                                                                        colors = ButtonDefaults.buttonColors(
                                                                                containerColor = MaterialTheme.colorScheme.primary, // Or your desired selected color
                                                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                                                        ),
                                                                        modifier = Modifier.weight(
                                                                                1f
                                                                        )
                                                                ) {
                                                                        Text("Custom")
                                                                }
                                                        } else {
                                                                // Unselected state: Outlined Button
                                                                OutlinedButton(
                                                                        onClick = {
                                                                                isLiveMatchMode =
                                                                                        false
                                                                        },
                                                                        colors = ButtonDefaults.outlinedButtonColors(
                                                                                contentColor = MaterialTheme.colorScheme.primary // Text color
                                                                        ),
                                                                        border = BorderStroke(
                                                                                1.dp,
                                                                                MaterialTheme.colorScheme.primary
                                                                        ), // Outline color
                                                                        modifier = Modifier.weight(
                                                                                1f
                                                                        )
                                                                ) {
                                                                        Text("Custom")
                                                                }
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
                                                                        modifier = Modifier.fillMaxWidth()
                                                                ) {
                                                                        OutlinedTextField(
                                                                                value = formData.selectedMatch // <<< THIS IS THE KEY CHANGE
                                                                                        ?.let { currentSelectedMatch -> // If a match is selected in formData...
                                                                                                val home =
                                                                                                        currentSelectedMatch.homeTeam
                                                                                                                ?: "TBD"
                                                                                                val away =
                                                                                                        currentSelectedMatch.awayTeam
                                                                                                                ?: "TBD"
                                                                                                val venueInfo =
                                                                                                        currentSelectedMatch.venue?.let { " - $it" }
                                                                                                                ?: ""
                                                                                                "$home vs $away$venueInfo" // ...display its details.
                                                                                        }
                                                                                        ?: "Select an available live match *", // Default if formData.selectedMatch is null
                                                                                onValueChange = {}, // Not directly editable
                                                                                readOnly = true,
                                                                                label = { Text("Selected Match / Available Live Matches *") }, // Updated label
                                                                                trailingIcon = {
                                                                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                                                                                expanded = expanded
                                                                                        )
                                                                                },
                                                                                modifier = Modifier
                                                                                        .fillMaxWidth()
                                                                                        .menuAnchor()
                                                                        )

                                                                        ExposedDropdownMenu(
                                                                                expanded = expanded,
                                                                                onDismissRequest = {
                                                                                        expanded =
                                                                                                false
                                                                                }
                                                                        ) {
                                                                                if (uiState.isLoadingMatches && uiState.availableMatches.isEmpty()) {
                                                                                        DropdownMenuItem(
                                                                                                onClick = {},
                                                                                                enabled = false,
                                                                                                text = {
                                                                                                        Text(
                                                                                                                "Still loading..."
                                                                                                        )
                                                                                                }
                                                                                        )
                                                                                }
                                                                                uiState.availableMatches.forEach { matchInList ->
                                                                                        DropdownMenuItem(
                                                                                                onClick = {
                                                                                                        eventViewModel.selectMatch(
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
                                                                formData.selectedMatch?.let { selected ->
                                                                        Spacer(
                                                                                modifier = Modifier.height(
                                                                                        12.dp
                                                                                )
                                                                        )

                                                                        TeamVsTeamDisplay(
                                                                                homeTeamName = selected.homeTeam,
                                                                                awayTeamName = selected.awayTeam
                                                                        )
                                                                }


                                                        } else { // No matches available and not loading
                                                                Text(
                                                                        text = "There are no live matches available for the selected date. Please select another day or create a custom match.",
                                                                        style = MaterialTheme.typography.bodyMedium,
                                                                        color = MaterialTheme.colorScheme.error,
                                                                        textAlign = TextAlign.Center,
                                                                        modifier = Modifier
                                                                                .fillMaxWidth()
                                                                                .padding(
                                                                                        horizontal = 16.dp,
                                                                                        vertical = 8.dp
                                                                                )
                                                                )
                                                        }
                                                        // Custom Matches
                                                } else {
                                                        // --- Custom Match Selector with Form Boxes ---
                                                        var homeTeam by remember(uiState.availableTeams) { mutableStateOf(uiState.availableTeams.firstOrNull()) }
                                                        var awayTeam by remember(uiState.availableTeams) { mutableStateOf(uiState.availableTeams.getOrNull(1)) }
                                                        var expandedHomeTeam by remember { mutableStateOf(false) }
                                                        var expandedAwayTeam by remember { mutableStateOf(false) }

                                                        Column(
                                                                modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .padding(vertical = 8.dp),
                                                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                                                ) {
                                                                        // Home Team Dropdown
                                                                        ExposedDropdownMenuBox(
                                                                                expanded = expandedHomeTeam,
                                                                                onExpandedChange = { expandedHomeTeam = !expandedHomeTeam }
                                                                        ) {
                                                                                OutlinedTextField(
                                                                                        value = homeTeam?.name ?: "",
                                                                                        onValueChange = {},
                                                                                        readOnly = true,
                                                                                        label = { Text("Home Team") },
                                                                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedHomeTeam) },
                                                                                        modifier = Modifier.fillMaxWidth()
                                                                                )

                                                                                ExposedDropdownMenu(
                                                                                        expanded = expandedHomeTeam,
                                                                                        onDismissRequest = { expandedHomeTeam = false }
                                                                                ) {
                                                                                        uiState.availableTeams.forEach { team ->
                                                                                                DropdownMenuItem(
                                                                                                        text = { Text(team.name) },
                                                                                                        onClick = {
                                                                                                                homeTeam = team
                                                                                                                // If the away team is the same as selected home, reset it
                                                                                                                if (awayTeam == team) awayTeam = null
                                                                                                                expandedHomeTeam = false
                                                                                                        }
                                                                                                )
                                                                                        }
                                                                                }
                                                                        }

                                                                        // Away Team Dropdown
                                                                        ExposedDropdownMenuBox(
                                                                                expanded = expandedAwayTeam,
                                                                                onExpandedChange = { expandedAwayTeam = !expandedAwayTeam }
                                                                        ) {
                                                                                OutlinedTextField(
                                                                                        value = awayTeam?.name ?: "",
                                                                                        onValueChange = {},
                                                                                        readOnly = true,
                                                                                        label = { Text("Away Team") },
                                                                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedAwayTeam) },
                                                                                        modifier = Modifier.fillMaxWidth()
                                                                                )

                                                                                ExposedDropdownMenu(
                                                                                        expanded = expandedAwayTeam,
                                                                                        onDismissRequest = { expandedAwayTeam = false }
                                                                                ) {
                                                                                        uiState.availableTeams.filter { it != homeTeam }.forEach { team ->
                                                                                                DropdownMenuItem(
                                                                                                        text = { Text(team.name) },
                                                                                                        onClick = {
                                                                                                                awayTeam = team
                                                                                                                expandedAwayTeam = false
                                                                                                        }
                                                                                                )
                                                                                        }
                                                                                }
                                                                        }

                                                                        // Optional Preview
                                                                        if (homeTeam != null && awayTeam != null) {
                                                                                Spacer(modifier = Modifier.height(16.dp))
                                                                                Text(
                                                                                        text = "Preview: ${homeTeam!!.name} vs ${awayTeam!!.name}",
                                                                                        style = MaterialTheme.typography.titleSmall,
                                                                                        fontWeight = FontWeight.SemiBold
                                                                                )
                                                                        }
                                                                }

                                                }

                                                val showEventInfoSection =
                                                        (!isLiveMatchMode) || (isLiveMatchMode && uiState.availableMatches.isNotEmpty());

                                                if (showEventInfoSection) {
                                                        // Location
                                                        EventFormSection(title = "Location") {
                                                                OutlinedTextField(
                                                                        value = formData.locationName,
                                                                        onValueChange = {
                                                                                eventViewModel.updateFormData(
                                                                                        formData.copy(
                                                                                                locationName = it
                                                                                        )
                                                                                )
                                                                        },
                                                                        label = { Text("Location Name *") },
                                                                        modifier = Modifier.fillMaxWidth(),
                                                                        singleLine = true
                                                                )
                                                                OutlinedTextField(
                                                                        value = formData.locationAddress,
                                                                        onValueChange = {
                                                                                eventViewModel.updateFormData(
                                                                                        formData.copy(
                                                                                                locationAddress = it
                                                                                        )
                                                                                )
                                                                        },
                                                                        label = { Text("Address *") },
                                                                        modifier = Modifier.fillMaxWidth(),
                                                                        maxLines = 2
                                                                )

                                                                // Map picker button
                                                                Button(
                                                                        onClick = {
                                                                                eventViewModel.showMapPicker()
                                                                        },
                                                                        modifier = Modifier.fillMaxWidth(),
                                                                        colors =
                                                                                ButtonDefaults.buttonColors(
                                                                                        containerColor =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .primary
                                                                                )
                                                                ) {
                                                                        Icon(
                                                                                painter = painterResource(
                                                                                        id = R.drawable.ic_location
                                                                                ),
                                                                                contentDescription = "Select location on map",
                                                                                tint = MaterialTheme.colorScheme.onPrimary
                                                                        )
                                                                        Spacer(
                                                                                modifier = Modifier.width(
                                                                                        8.dp
                                                                                )
                                                                        )
                                                                        Text("Select Map Location")
                                                                }
                                                                // Show selected coordinates if available
                                                                if (formData.latitude != 0.0 &&
                                                                        formData.longitude != 0.0
                                                                ) {
                                                                        Card(
                                                                                modifier = Modifier.fillMaxWidth(),
                                                                                colors =
                                                                                        CardDefaults.cardColors(
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
                                                                                                text = "Selected Location:",
                                                                                                style = MaterialTheme.typography.titleSmall,
                                                                                                fontWeight = FontWeight.SemiBold
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
                                                                                                style = MaterialTheme.typography.bodySmall,
                                                                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                                                                        )
                                                                                }
                                                                        }
                                                                }
                                                        }

                                                        // Capacity and Contact
                                                        EventFormSection(title = "Details") {
                                                                var capacityText by remember { mutableStateOf(formData.capacity.toString()) }
                                                                OutlinedTextField(
                                                                        value = capacityText,
                                                                        onValueChange = { newText ->
                                                                                // Allow empty string for deletion
                                                                                if (newText.isEmpty() || newText.all { it.isDigit() }) {
                                                                                        capacityText = newText
                                                                                        // Update numeric capacity, default 0 if empty
                                                                                        val capacityInt = newText.toIntOrNull() ?: 0
                                                                                        eventViewModel.updateFormData(formData.copy(capacity = capacityInt))
                                                                                }
                                                                        },
                                                                        label = { Text("Capacity *") },
                                                                        modifier = Modifier.fillMaxWidth(),
                                                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                                                )

                                                                OutlinedTextField(
                                                                        value = formData.contactNumber,
                                                                        onValueChange = {
                                                                                eventViewModel.updateFormData(
                                                                                        formData.copy(
                                                                                                contactNumber = it
                                                                                        )
                                                                                )
                                                                        },
                                                                        label = {
                                                                                Text("Contact Number")
                                                                        },
                                                                        modifier = Modifier.fillMaxWidth(),
                                                                        keyboardOptions =
                                                                                KeyboardOptions(
                                                                                        keyboardType =
                                                                                                KeyboardType.Phone
                                                                                )
                                                                )
                                                        }
                                                        var amenitiesExpanded by remember {
                                                                mutableStateOf(
                                                                        false
                                                                )
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
                                                                        checked = formData.amenities.isIndoor,
                                                                        onCheckedChange = {
                                                                                eventViewModel.updateFormData(
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
                                                                        checked = formData.amenities.isOutdoor,
                                                                        onCheckedChange = {
                                                                                eventViewModel.updateFormData(
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
                                                                                formData.amenities.isChildFriendly,
                                                                        onCheckedChange = {
                                                                                eventViewModel.updateFormData(
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
                                                                        checked = formData.amenities.isPetFriendly,
                                                                        onCheckedChange = {
                                                                                eventViewModel.updateFormData(
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
                                                                        checked = formData.amenities.hasParking,
                                                                        onCheckedChange = {
                                                                                eventViewModel.updateFormData(
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
                                                                        checked = formData.amenities.hasFood,
                                                                        onCheckedChange = {
                                                                                eventViewModel.updateFormData(
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
                                                                        checked = formData.amenities.hasToilet,
                                                                        onCheckedChange = {
                                                                                eventViewModel.updateFormData(
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
                                                                        checked = formData.amenities.hasWifi,
                                                                        onCheckedChange = {
                                                                                eventViewModel.updateFormData(
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
                                                                mutableStateOf(
                                                                        false
                                                                )
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
                                                                        checked = formData.accessibility.isWheelchairAccessible,
                                                                        onCheckedChange = {
                                                                                eventViewModel.updateFormData(
                                                                                        formData.copy(
                                                                                                accessibility = formData.accessibility.copy(
                                                                                                        isWheelchairAccessible = it
                                                                                                )
                                                                                        )
                                                                                )
                                                                        },
                                                                        text = "Wheelchair Accessible"
                                                                )

                                                                CheckboxRow(
                                                                        checked =
                                                                                formData.accessibility
                                                                                        .hasAccessibleToilets,
                                                                        onCheckedChange = {
                                                                                eventViewModel.updateFormData(
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
                                                                modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .padding(16.dp),
                                                                horizontalArrangement = Arrangement.spacedBy(
                                                                        8.dp
                                                                )
                                                        ) {
                                                                OutlinedButton(
                                                                        onClick = onDismiss,
                                                                        modifier = Modifier.weight(
                                                                                1f
                                                                        )
                                                                ) { Text("Cancel") }

                                                                Button(
                                                                        onClick = onSave,
                                                                        modifier = Modifier.weight(
                                                                                1f
                                                                        ),
                                                                        enabled =
                                                                                !uiState.isLoading &&
                                                                                        formData.selectedMatch != null &&
                                                                                        formData.locationName
                                                                                                .isNotBlank() &&
                                                                                        formData.locationAddress
                                                                                                .isNotBlank()
                                                                ) {
                                                                        if (uiState.isLoading) {
                                                                                CircularProgressIndicator(
                                                                                        modifier = Modifier.size(
                                                                                                16.dp
                                                                                        ),
                                                                                        color =
                                                                                                MaterialTheme.colorScheme
                                                                                                        .onPrimary
                                                                                )
                                                                        } else {
                                                                                Text(if (isEditing) "Update" else "Create")
                                                                        }
                                                                }
                                                        }
                                                }
                                        }


                                        // Custom Match Dialog
                                        if (uiState.showCustomMatchDialog) {
                                                CustomMatchDialog(
                                                        availableTeams = uiState.availableTeams,
                                                        isLoadingTeams = uiState.isLoadingTeams,
                                                        onDismiss = { eventViewModel.hideCustomMatchDialog() },
                                                        onCreateMatch = { homeTeam, awayTeam ->
                                                                eventViewModel.createCustomMatch(
                                                                        homeTeam,
                                                                        awayTeam
                                                                )
                                                        },
                                                        currentHomeTeam = formData.selectedMatch?.homeTeam,
                                                        currentAwayTeam = formData.selectedMatch?.awayTeam
                                                )
                                        }
                                }
                        }
                }
        }
}







// Make sure your UiStateType and Team data class are defined
// data class YourUiStateType( /* ..., */ val availableTeams: List<Team> = emptyList(), /* ... */ )
// data class Team(val name: String, val localLogoRes: Int? = null) // Example
// data class Match( /* ..., */ val homeTeam: String?, val awayTeam: String?, val isLiveMatch: Boolean = false)





