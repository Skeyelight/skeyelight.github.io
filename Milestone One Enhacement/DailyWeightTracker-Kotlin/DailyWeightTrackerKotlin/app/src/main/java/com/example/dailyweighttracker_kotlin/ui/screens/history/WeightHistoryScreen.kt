package com.example.dailyweighttracker_kotlin.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailyweighttracker_kotlin.AppViewModelProvider
import java.time.format.DateTimeFormatter


// Screen showing the weight history
@Composable
fun WeightHistoryScreen(
    viewModel: WeightHistoryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    // Delete Dialog
    if (uiState.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(WeightHistoryEvent.OnDismissDelete) },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this weight entry?") },
            confirmButton = {
                Button(onClick = { viewModel.onEvent(WeightHistoryEvent.OnConfirmDelete) }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { viewModel.onEvent(WeightHistoryEvent.OnDismissDelete) }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Dialog
    if (uiState.weightToEdit != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(WeightHistoryEvent.OnDismissEdit) },
            title = { Text("Edit Weight") },
            text = {
                TextField(
                    value = uiState.editDialogNewWeight,
                    onValueChange = { newWeightString ->
                        viewModel.onEvent(WeightHistoryEvent.OnEditDialogNewWeightChanged(newWeightString))
                    },
                    label = { Text("New Weight") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                Button(onClick = { viewModel.onEvent(WeightHistoryEvent.OnConfirmEdit) }) {
                    Text("Save")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(uiState.weights) { weight ->
            WeightHistoryItem(
                weight = weight,
                weightUnit = uiState.weightUnit.label,
                onEvent = viewModel::onEvent
            )
        }
    }
}

// Card displaying a weight entry
@Composable
fun WeightHistoryItem(
    weight: WeightEntry,
    weightUnit: String,
    onEvent: (WeightHistoryEvent) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Weight
            Text(
                text = "${weight.weight} $weightUnit",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            // Date
            Text(
                text = weight.date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            // Edit + Delete buttons
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = {
                    onEvent(WeightHistoryEvent.OnEditWeight(weight))
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = {
                    onEvent(WeightHistoryEvent.OnDeleteWeight(weight))
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}



