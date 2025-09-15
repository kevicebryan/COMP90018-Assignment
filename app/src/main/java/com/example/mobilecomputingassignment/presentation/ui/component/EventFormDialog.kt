package com.example.mobilecomputingassignment.presentation.ui.component

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
import androidx.compose.ui.Alignment
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import java.util.*

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
                                                var showDatePicker by remember { mutableStateOf(false) }
                                                var showTimePicker by remember { mutableStateOf(false) }

                                                Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {

                                                        // Date picker
                                                        OutlinedTextField(
                                                                value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(formData.date),
                                                                onValueChange = {},
                                                                label = { Text("Event Date *") },
                                                                modifier = Modifier.weight(1f),
                                                                readOnly = true,
                                                                trailingIcon = {
                                                                        IconButton(onClick = { showDatePicker = true }) {
                                                                                Icon(
                                                                                        painter = painterResource(id = R.drawable.ic_calendar),
                                                                                        contentDescription = "Select date",
                                                                                        tint = MaterialTheme.colorScheme.primary
                                                                                )
                                                                        }
                                                                }
                                                        )

                                                        // Time picker
                                                        OutlinedTextField(
                                                                value = SimpleDateFormat("HH:mm", Locale.getDefault()).format(formData.checkInTime),
                                                                onValueChange = {},
                                                                label = { Text("Match Time *") },
                                                                modifier = Modifier.weight(1f),
                                                                readOnly = true,
                                                                trailingIcon = {
                                                                        IconButton(onClick = { showTimePicker = true }) {
                                                                                Icon(
                                                                                        painter = painterResource(id = R.drawable.ic_clock),
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
                                                                                                ?.let {
                                                                                                        millis
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
                                                                        disabledDayContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                                                                        selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                                                                        disabledSelectedDayContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.38f),
                                                                        selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                                                                        disabledSelectedDayContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                                                        todayContentColor = MaterialTheme.colorScheme.primary,
                                                                        todayDateBorderColor = MaterialTheme.colorScheme.primary,
                                                                        dayInSelectionRangeContentColor = MaterialTheme.colorScheme.onPrimary,
                                                                        dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
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
                                                // Match dropdown
                                                if (uiState.isLoadingMatches) {
                                                        Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement =
                                                                        Arrangement.Center,
                                                                verticalAlignment =
                                                                        Alignment.CenterVertically
                                                        ) {
                                                                CircularProgressIndicator(
                                                                        modifier =
                                                                                Modifier.size(
                                                                                        20.dp
                                                                                ),
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .primary
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.width(8.dp)
                                                                )
                                                                Text("Loading matches...")
                                                        }
                                                } else if (uiState.availableMatches.isEmpty() && formData.selectedMatch == null) {
                                                        Column(
                                                                verticalArrangement = Arrangement.spacedBy(12.dp)
                                                        ) {
                                                                Text(
                                                                        text = "There are no live matches on the selected date",
                                                                        style = MaterialTheme.typography.bodyMedium,
                                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                                )
                                                                
                                                                Button(
                                                                        onClick = { eventViewModel.showCustomMatchDialog() },
                                                                        modifier = Modifier.fillMaxWidth()
                                                                ) {
                                                                        Text("Create Custom Match")
                                                                }
                                                        }
                                                } else if (formData.selectedMatch != null) {
                                                        // Show selected custom match - minimalist design
                                                        Column(
                                                                verticalArrangement = Arrangement.spacedBy(12.dp)
                                                        ) {
                                                                // Teams display with logos
                                                                Row(
                                                                        modifier = Modifier.fillMaxWidth(),
                                                                        horizontalArrangement = Arrangement.Center,
                                                                        verticalAlignment = Alignment.CenterVertically
                                                                ) {
                                                                        // Home Team with logo
                                                                        Row(
                                                                                verticalAlignment = Alignment.CenterVertically
                                                                        ) {
                                                                                // Team logo
                                                                                formData.selectedMatch?.homeTeam?.let { teamName ->
                                                                                        val team = uiState.availableTeams.find { it.name == teamName }
                                                                                        team?.localLogoRes?.let { logoRes ->
                                                                                                Image(
                                                                                                        painter = painterResource(id = logoRes),
                                                                                                        contentDescription = teamName,
                                                                                                        modifier = Modifier.size(24.dp)
                                                                                                )
                                                                                        }
                                                                                }
                                                                                Spacer(modifier = Modifier.width(8.dp))
                                                                                Text(
                                                                                        text = formData.selectedMatch?.homeTeam ?: "",
                                                                                        style = MaterialTheme.typography.titleMedium,
                                                                                        fontWeight = FontWeight.Medium,
                                                                                        color = MaterialTheme.colorScheme.onSurface
                                                                                )
                                                                        }
                                                                        
                                                                        // VS
                                                                        Text(
                                                                                text = " VS ",
                                                                                style = MaterialTheme.typography.titleMedium,
                                                                                fontWeight = FontWeight.Medium,
                                                                                color = MaterialTheme.colorScheme.primary,
                                                                                modifier = Modifier.padding(horizontal = 16.dp)
                                                                        )
                                                                        
                                                                        // Away Team with logo
                                                                        Row(
                                                                                verticalAlignment = Alignment.CenterVertically
                                                                        ) {
                                                                                Text(
                                                                                        text = formData.selectedMatch?.awayTeam ?: "",
                                                                                        style = MaterialTheme.typography.titleMedium,
                                                                                        fontWeight = FontWeight.Medium,
                                                                                        color = MaterialTheme.colorScheme.onSurface
                                                                                )
                                                                                Spacer(modifier = Modifier.width(8.dp))
                                                                                // Team logo
                                                                                formData.selectedMatch?.awayTeam?.let { teamName ->
                                                                                        val team = uiState.availableTeams.find { it.name == teamName }
                                                                                        team?.localLogoRes?.let { logoRes ->
                                                                                                Image(
                                                                                                        painter = painterResource(id = logoRes),
                                                                                                        contentDescription = teamName,
                                                                                                        modifier = Modifier.size(24.dp)
                                                                                                )
                                                                                        }
                                                                                }
                                                                        }
                                                                }
                                                                
                                                                Button(
                                                                        onClick = { eventViewModel.showCustomMatchDialog() },
                                                                        modifier = Modifier.fillMaxWidth(),
                                                                        colors = ButtonDefaults.buttonColors(
                                                                                containerColor = MaterialTheme.colorScheme.primary,
                                                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                                                        )
                                                                ) {
                                                                        Text("Edit Teams")
                                                                }
                                                        }
                                                } else {
                                                        // Match selection dropdown
                                                        var expanded by remember {
                                                                mutableStateOf(false)
                                                        }

                                                        ExposedDropdownMenuBox(
                                                                expanded = expanded,
                                                                onExpandedChange = {
                                                                        expanded = !expanded
                                                                }
                                                        ) {
                                                                OutlinedTextField(
                                                                        value =
                                                                                formData.selectedMatch
                                                                                        ?.let {
                                                                                                match
                                                                                                ->
                                                                                                "${match.homeTeam} vs ${match.awayTeam} - ${match.venue}"
                                                                                        }
                                                                                        ?: "Select a live match *",
                                                                        onValueChange = {},
                                                                        readOnly = true,
                                                                        label = { Text("Match *") },
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
                                                                                expanded = false
                                                                        }
                                                                ) {
                                                                        uiState.availableMatches
                                                                                .forEach { match ->
                                                                                        DropdownMenuItem(
                                                                                                text = {
                                                                                                        Column {
                                                                                                                Text(
                                                                                                                        text =
                                                                                                                                "${match.homeTeam} vs ${match.awayTeam}",
                                                                                                                        style =
                                                                                                                                MaterialTheme
                                                                                                                                        .typography
                                                                                                                                        .bodyMedium,
                                                                                                                        fontWeight =
                                                                                                                                FontWeight
                                                                                                                                        .SemiBold
                                                                                                                )
                                                                                                                Text(
                                                                                                                        text =
                                                                                                                                "${match.venue} • ${match.round}",
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
                                                                                                },
                                                                                                onClick = {
                                                                                                        eventViewModel
                                                                                                                .selectMatch(
                                                                                                                        match
                                                                                                                )
                                                                                                        expanded =
                                                                                                                false
                                                                                                }
                                                                                        )
                                                                                }
                                                                }
                                                        }
                                                }
                                        }

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
                                                                painter = painterResource(id = R.drawable.ic_location),
                                                                contentDescription = "Select location on map",
                                                                tint = MaterialTheme.colorScheme.onPrimary
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
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
                                                OutlinedTextField(
                                                        value = formData.capacity.toString(),
                                                        onValueChange = {
                                                                it.toIntOrNull()?.let { capacity ->
                                                                        eventViewModel
                                                                                .updateFormData(
                                                                                        formData.copy(
                                                                                                capacity =
                                                                                                        capacity
                                                                                        )
                                                                                )
                                                                }
                                                        },
                                                        label = { Text("Capacity *") },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        keyboardOptions =
                                                                KeyboardOptions(
                                                                        keyboardType =
                                                                                KeyboardType.Number
                                                                )
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
                                        var amenitiesExpanded by remember { mutableStateOf(false) } // Default to collapsed

                                        // Amenities
                                        EventFormSection(
                                                title = "Amenities",
                                                isExpanded = amenitiesExpanded,
                                                onHeaderClick = { amenitiesExpanded = !amenitiesExpanded}
                                        ){
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

                                        var accessibilityExpanded by remember { mutableStateOf(false) }

                                        // Accessibility
                                        EventFormSection(
                                                title = "Accessibility",
                                                isExpanded = accessibilityExpanded,
                                                onHeaderClick = { accessibilityExpanded = !accessibilityExpanded}
                                        ){
                                                CheckboxRow(
                                                        checked = formData.accessibility.isWheelchairAccessible,
                                                        onCheckedChange = { eventViewModel.updateFormData(formData.copy(accessibility = formData.accessibility.copy(isWheelchairAccessible = it)))},
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
                                }

                                // Footer buttons
                                HorizontalDivider()

                                Row(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                        OutlinedButton(
                                                onClick = onDismiss,
                                                modifier = Modifier.weight(1f)
                                        ) { Text("Cancel") }

                                        Button(
                                                onClick = onSave,
                                                modifier = Modifier.weight(1f),
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
                                                                modifier = Modifier.size(16.dp),
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
        }

        // Custom Match Dialog
        if (uiState.showCustomMatchDialog) {
                CustomMatchDialog(
                        availableTeams = uiState.availableTeams,
                        isLoadingTeams = uiState.isLoadingTeams,
                        onDismiss = { eventViewModel.hideCustomMatchDialog() },
                        onCreateMatch = { homeTeam, awayTeam ->
                                eventViewModel.createCustomMatch(homeTeam, awayTeam)
                        },
                        currentHomeTeam = formData.selectedMatch?.homeTeam,
                        currentAwayTeam = formData.selectedMatch?.awayTeam
                )
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
private fun EventFormSection(title: String, isExpanded: Boolean, onHeaderClick:() -> Unit, content: @Composable ColumnScope.() -> Unit) {
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
