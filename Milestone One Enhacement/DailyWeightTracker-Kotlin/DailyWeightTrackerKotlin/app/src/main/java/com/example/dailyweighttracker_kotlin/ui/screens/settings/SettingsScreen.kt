package com.example.dailyweighttracker_kotlin.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailyweighttracker_kotlin.AppViewModelProvider
import com.example.dailyweighttracker_kotlin.data.WeightUnit

@OptIn(ExperimentalMaterial3Api::class)
// Settings screen
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onLogout: () -> Unit
) {
    // Collect the UI state from the ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Display the settings screen content
    SettingsScreenContent(
        modifier = modifier,
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onLogout = onLogout
    )
}

@OptIn(ExperimentalMaterial3Api::class)

// Settings screen content
@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    uiState: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
    onLogout: () -> Unit
) {
    // Display the change username dialog
    if (uiState.showChangeUsernameDialog) {
        ChangeUsernameDialog(uiState = uiState, onEvent = onEvent)
    }

    // Display the change password dialog
    if (uiState.showChangePasswordDialog) {
        ChangePasswordDialog(uiState = uiState, onEvent = onEvent)
    }

    // Main settings screen content
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            Text("Account", style = MaterialTheme.typography.headlineSmall)

            Text("Username: ${uiState.username}")

            // Buttons to change username and password
            Button(
                onClick = { onEvent(SettingsEvent.OnChangeUsername) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Change Username")
            }

            Button(
                onClick = { onEvent(SettingsEvent.OnChangePassword) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Change Password")
            }

            Text("Units", style = MaterialTheme.typography.headlineSmall)

            // Weight unit selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Weight Unit")
                SingleChoiceSegmentedButtonRow {
                    WeightUnit.entries.forEachIndexed { index, unit ->
                        SegmentedButton(
                            selected = uiState.weightUnit == unit,
                            onClick = { onEvent(SettingsEvent.OnWeightUnitChanged(unit)) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = WeightUnit.entries.size)
                        ) {
                            Text(unit.label)
                        }
                    }
                }
            }

            Text("Notifications", style = MaterialTheme.typography.headlineSmall)

            // Goal notification switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Goal Reached Notification")
                Switch(
                    checked = uiState.goalNotificationEnabled,
                    onCheckedChange = { onEvent(SettingsEvent.OnGoalNotificationToggled(it)) }
                )
            }
        }

        // Logout button
        OutlinedButton(
            onClick = {
                onEvent(SettingsEvent.OnLogout)
                onLogout()
            },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}

