package com.example.dailyweighttracker_kotlin.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
// Username change dialog
@Composable
fun ChangeUsernameDialog(uiState: SettingsState, onEvent: (SettingsEvent) -> Unit) {
    AlertDialog(
        // Dismiss when clicking outside the dialog
        onDismissRequest = { onEvent(SettingsEvent.OnDismissDialog) },
        title = { Text("Change Username") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // New username field
                OutlinedTextField(
                    value = uiState.newUsername,
                    onValueChange = { onEvent(SettingsEvent.OnUsernameChanged(it)) },
                    label = { Text("New Username") }
                )
                OutlinedTextField(
                    // Current password field
                    value = uiState.currentPassword,
                    onValueChange = { onEvent(SettingsEvent.OnCurrentPasswordChanged(it)) },
                    label = { Text("Current Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        // Confirm button
        confirmButton = {
            Button(onClick = { onEvent(SettingsEvent.OnConfirmUsernameChange) }) {
                Text("Save")
            }
        },
        // Dismiss button
        dismissButton = {
            Button(onClick = { onEvent(SettingsEvent.OnDismissDialog) }) {
                Text("Cancel")
            }
        }
    )
}
