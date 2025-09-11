package com.example.mobilecomputingassignment.presentation.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                                                var showDatePicker by remember {
                                                        mutableStateOf(false)
                                                }
                                                var showTimePicker by remember {
                                                        mutableStateOf(false)
                                                }

                                                // Date picker
                                                OutlinedTextField(
                                                        value =
                                                                SimpleDateFormat(
                                                                                "yyyy-MM-dd",
                                                                                Locale.getDefault()
                                                                        )
                                                                        .format(formData.date),
                                                        onValueChange = {},
                                                        label = { Text("Event Date *") },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        readOnly = true,
                                                        leadingIcon = {
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
                                                        },
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
                                                        value =
                                                                SimpleDateFormat(
                                                                                "HH:mm",
                                                                                Locale.getDefault()
                                                                        )
                                                                        .format(
                                                                                formData.checkInTime
                                                                        ),
                                                        onValueChange = {},
                                                        label = { Text("Check-in Time *") },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        readOnly = true,
                                                        leadingIcon = {
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
                                                        },
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
                                                                                }
                                                                        ) { Text("OK") }
                                                                },
                                                                dismissButton = {
                                                                        TextButton(
                                                                                onClick = {
                                                                                        showDatePicker =
                                                                                                false
                                                                                }
                                                                        ) { Text("Cancel") }
                                                                }
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
                                                                        Text("Select Check-in Time")
                                                                },
                                                                text = {
                                                                        TimePicker(
                                                                                state =
                                                                                        timePickerState
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
                                                                                }
                                                                        ) { Text("OK") }
                                                                },
                                                                dismissButton = {
                                                                        TextButton(
                                                                                onClick = {
                                                                                        showTimePicker =
                                                                                                false
                                                                                }
                                                                        ) { Text("Cancel") }
                                                                }
                                                        )
                                                }
                                        }

                                        // Match Selection
                                        EventFormSection(title = "Match Showing") {
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
                                                } else if (uiState.availableMatches.isEmpty()) {
                                                        Column(
                                                                verticalArrangement = Arrangement.spacedBy(12.dp)
                                                        ) {
                                                                Text(
                                                                        text = "No matches available for selected date",
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
                                                                                        ?: "Select a match *",
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
                                                                painter =
                                                                        painterResource(
                                                                                id =
                                                                                        R.drawable
                                                                                                .ic_my_location
                                                                        ),
                                                                contentDescription =
                                                                        "Select location on map",
                                                                tint =
                                                                        MaterialTheme.colorScheme
                                                                                .onPrimary
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text("Select Location on Map")
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
                                                                Text("Contact Number (Optional)")
                                                        },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        keyboardOptions =
                                                                KeyboardOptions(
                                                                        keyboardType =
                                                                                KeyboardType.Phone
                                                                )
                                                )
                                        }

                                        // Amenities
                                        EventFormSection(title = "Amenities") {
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
                                                        text = "Has Parking"
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
                                                        text = "Has Food"
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
                                                        text = "Has Toilet"
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
                                                        text = "Has WiFi"
                                                )
                                        }

                                        // Accessibility
                                        EventFormSection(title = "Accessibility") {
                                                CheckboxRow(
                                                        checked =
                                                                formData.accessibility
                                                                        .isWheelchairAccessible,
                                                        onCheckedChange = {
                                                                eventViewModel.updateFormData(
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
                        }
                )
        }
}

@Composable
private fun EventFormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
        Column {
                Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp), content = content)
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
