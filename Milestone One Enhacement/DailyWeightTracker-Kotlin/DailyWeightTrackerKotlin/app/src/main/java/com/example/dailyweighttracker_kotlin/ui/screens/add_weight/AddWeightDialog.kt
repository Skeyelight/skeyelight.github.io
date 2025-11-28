package com.example.dailyweighttracker_kotlin.ui.screens.add_weight

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailyweighttracker_kotlin.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)

// Dialogue for adding new weight
@Composable
fun AddWeightDialog(
    onDismiss: () -> Unit,
    viewModel: AddWeightViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    // Get the current Context
    val context = LocalContext.current

    // UI state from the viewModel
    val uiState by viewModel.uiState.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Weight") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Weight input
                OutlinedTextField(
                    value = uiState.weight,
                    onValueChange = { viewModel.onWeightChange(it) },
                    label = { Text("Weight (${uiState.selectedUnit.label})") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {

            // Save weight and close dialog
            Button(
                onClick = {
                    viewModel.saveWeight(context)
                    onDismiss()
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Save Weight")
            }
        }
    )
}
