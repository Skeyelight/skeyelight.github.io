package com.example.dailyweighttracker_kotlin.ui.screens.set_goal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailyweighttracker_kotlin.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetGoalDialog(
    onDismiss: () -> Unit,
    viewModel: SetGoalViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // Get UI from the ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Dialog got entering a new goal weight
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Goal Weight") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.goalWeight,
                    onValueChange = { viewModel.onGoalWeightChange(it) },
                    label = { Text("Goal (${uiState.selectedUnit.label})") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        // Save goal button
        confirmButton = {
            Button(
                onClick = {
                    viewModel.saveGoalWeight()
                    onDismiss()
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Set Goal")
            }
        }
    )
}
