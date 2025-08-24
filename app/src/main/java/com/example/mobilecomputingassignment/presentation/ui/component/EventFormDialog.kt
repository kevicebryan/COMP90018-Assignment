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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
                  text = if (isEditing) "Edit Event" else "Create Event",
                  style = MaterialTheme.typography.headlineSmall,
                  fontWeight = FontWeight.Bold
          )

          IconButton(onClick = onDismiss) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
          }
        }

        Divider()

        // Form content
        Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          // Basic Information
          EventFormSection(title = "Basic Information") {
            OutlinedTextField(
                    value = formData.title,
                    onValueChange = { eventViewModel.updateFormData(formData.copy(title = it)) },
                    label = { Text("Event Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
            )

            OutlinedTextField(
                    value = formData.description,
                    onValueChange = {
                      eventViewModel.updateFormData(formData.copy(description = it))
                    },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
            )
          }

          // Date and Time
          EventFormSection(title = "Date & Time") {
            // For now, using text fields - we'll add date/time pickers later
            OutlinedTextField(
                    value =
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    .format(formData.date),
                    onValueChange = { /* TODO: Implement date picker */},
                    label = { Text("Event Date *") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
            )

            OutlinedTextField(
                    value =
                            SimpleDateFormat("HH:mm", Locale.getDefault())
                                    .format(formData.checkInTime),
                    onValueChange = { /* TODO: Implement time picker */},
                    label = { Text("Check-in Time *") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
            )
          }

          // Location
          EventFormSection(title = "Location") {
            OutlinedTextField(
                    value = formData.locationName,
                    onValueChange = {
                      eventViewModel.updateFormData(formData.copy(locationName = it))
                    },
                    label = { Text("Location Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
            )

            OutlinedTextField(
                    value = formData.locationAddress,
                    onValueChange = {
                      eventViewModel.updateFormData(formData.copy(locationAddress = it))
                    },
                    label = { Text("Address *") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
            )

            // TODO: Add map picker for lat/lng
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedTextField(
                      value = formData.latitude.toString(),
                      onValueChange = {
                        it.toDoubleOrNull()?.let { lat ->
                          eventViewModel.updateFormData(formData.copy(latitude = lat))
                        }
                      },
                      label = { Text("Latitude") },
                      modifier = Modifier.weight(1f),
                      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
              )

              OutlinedTextField(
                      value = formData.longitude.toString(),
                      onValueChange = {
                        it.toDoubleOrNull()?.let { lng ->
                          eventViewModel.updateFormData(formData.copy(longitude = lng))
                        }
                      },
                      label = { Text("Longitude") },
                      modifier = Modifier.weight(1f),
                      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
              )
            }
          }

          // Capacity and Contact
          EventFormSection(title = "Details") {
            OutlinedTextField(
                    value = formData.capacity.toString(),
                    onValueChange = {
                      it.toIntOrNull()?.let { capacity ->
                        eventViewModel.updateFormData(formData.copy(capacity = capacity))
                      }
                    },
                    label = { Text("Capacity *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                    value = formData.contactNumber,
                    onValueChange = {
                      eventViewModel.updateFormData(formData.copy(contactNumber = it))
                    },
                    label = { Text("Contact Number (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
          }

          // Amenities
          EventFormSection(title = "Amenities") {
            CheckboxRow(
                    checked = formData.amenities.isIndoor,
                    onCheckedChange = {
                      eventViewModel.updateFormData(
                              formData.copy(amenities = formData.amenities.copy(isIndoor = it))
                      )
                    },
                    text = "Indoor"
            )

            CheckboxRow(
                    checked = formData.amenities.isOutdoor,
                    onCheckedChange = {
                      eventViewModel.updateFormData(
                              formData.copy(amenities = formData.amenities.copy(isOutdoor = it))
                      )
                    },
                    text = "Outdoor"
            )

            CheckboxRow(
                    checked = formData.amenities.isChildFriendly,
                    onCheckedChange = {
                      eventViewModel.updateFormData(
                              formData.copy(
                                      amenities = formData.amenities.copy(isChildFriendly = it)
                              )
                      )
                    },
                    text = "Child Friendly"
            )

            CheckboxRow(
                    checked = formData.amenities.isPetFriendly,
                    onCheckedChange = {
                      eventViewModel.updateFormData(
                              formData.copy(amenities = formData.amenities.copy(isPetFriendly = it))
                      )
                    },
                    text = "Pet Friendly"
            )
          }

          // Accessibility
          EventFormSection(title = "Accessibility") {
            CheckboxRow(
                    checked = formData.accessibility.isWheelchairAccessible,
                    onCheckedChange = {
                      eventViewModel.updateFormData(
                              formData.copy(
                                      accessibility =
                                              formData.accessibility.copy(
                                                      isWheelchairAccessible = it
                                              )
                              )
                      )
                    },
                    text = "Wheelchair Accessible"
            )

            CheckboxRow(
                    checked = formData.accessibility.hasAccessibleToilets,
                    onCheckedChange = {
                      eventViewModel.updateFormData(
                              formData.copy(
                                      accessibility =
                                              formData.accessibility.copy(hasAccessibleToilets = it)
                              )
                      )
                    },
                    text = "Accessible Toilets"
            )
          }
        }

        // Footer buttons
        Divider()

        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }

          Button(
                  onClick = onSave,
                  modifier = Modifier.weight(1f),
                  enabled =
                          !uiState.isLoading &&
                                  formData.title.isNotBlank() &&
                                  formData.locationName.isNotBlank() &&
                                  formData.locationAddress.isNotBlank()
          ) {
            if (uiState.isLoading) {
              CircularProgressIndicator(
                      modifier = Modifier.size(16.dp),
                      color = MaterialTheme.colorScheme.onPrimary
              )
            } else {
              Text(if (isEditing) "Update" else "Create")
            }
          }
        }
      }
    }
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

    Column(verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
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
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
    )

    Spacer(modifier = Modifier.width(8.dp))

    Text(text = text, style = MaterialTheme.typography.bodyMedium)
  }
}
