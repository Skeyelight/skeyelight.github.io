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

// Password change dialog
@Composable
fun ChangePasswordDialog(uiState: SettingsState, onEvent: (SettingsEvent) -> Unit) {
    AlertDialog(
        // Dismiss the dialog when the user clicks outside the dialog or on the back button
        onDismissRequest = { onEvent(SettingsEvent.OnDismissDialog) },
        title = { Text("Change Password") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Current password input
                OutlinedTextField(
                    value = uiState.currentPassword,
                    onValueChange = { onEvent(SettingsEvent.OnCurrentPasswordChanged(it)) },
                    label = { Text("Current Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                // New password input
                OutlinedTextField(
                    value = uiState.newPassword,
                    onValueChange = { onEvent(SettingsEvent.OnPasswordChanged(it)) },
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                // Confirm new password input
                OutlinedTextField(
                    value = uiState.confirmPassword,
                    onValueChange = { onEvent(SettingsEvent.OnConfirmPasswordChanged(it)) },
                    label = { Text("Confirm New Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        // Save button
        confirmButton = {
            Button(onClick = { onEvent(SettingsEvent.OnConfirmPasswordChange) }) {
                Text("Save")
            }
        },
        // Cancel button
        dismissButton = {
            Button(onClick = { onEvent(SettingsEvent.OnDismissDialog) }) {
                Text("Cancel")
            }
        }
    )
}
