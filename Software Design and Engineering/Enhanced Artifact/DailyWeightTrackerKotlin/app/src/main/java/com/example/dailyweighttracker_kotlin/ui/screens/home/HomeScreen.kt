package com.example.dailyweighttracker_kotlin.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailyweighttracker_kotlin.AppViewModelProvider
import com.example.dailyweighttracker_kotlin.R
import com.example.dailyweighttracker_kotlin.ui.screens.add_weight.AddWeightDialog
import com.example.dailyweighttracker_kotlin.ui.screens.set_goal.SetGoalDialog
import java.time.format.DateTimeFormatter

// Home Screen
@Composable
fun HomeScreen(
    onViewHistoryClick: () -> Unit,
    onAccountClick: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    // Collect UI state from the ViewModel
    val homeUiState by viewModel.homeUiState.collectAsState()

    // Format goal weight
    val goalWeightText = homeUiState.goalWeight?.let {
        stringResource(
            R.string.goal_weight_display,
            it,
            homeUiState.weightUnit.name.lowercase()
        )
    } ?: stringResource(R.string.not_set)

    // Render the UI
    HomeScreenContent(
        homeUiState = homeUiState,
        goalWeightText = goalWeightText,
        onViewHistoryClick = onViewHistoryClick,
        onAccountClick = onAccountClick,
        onAddWeightClick = viewModel::onAddWeightClick,
        onSetGoalClick = viewModel::onSetGoalClick,
        onAddWeightDialogDismiss = viewModel::onAddWeightDialogDismiss,
        onSetGoalDialogDismiss = viewModel::onSetGoalDialogDismiss
    )
}

// Home Screen content
@Composable
fun HomeScreenContent(
    homeUiState: HomeUiState,
    goalWeightText: String,
    onViewHistoryClick: () -> Unit,
    onAccountClick: () -> Unit,
    onAddWeightClick: () -> Unit,
    onSetGoalClick: () -> Unit,
    onAddWeightDialogDismiss: () -> Unit,
    onSetGoalDialogDismiss: () -> Unit
) {

    // Add weight dialog
    if (homeUiState.showAddWeightDialog) {
        AddWeightDialog(onDismiss = onAddWeightDialogDismiss)
    }

    // Set goal dialog
    if (homeUiState.showSetGoalDialog) {
        SetGoalDialog(onDismiss = onSetGoalDialogDismiss)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Most recent weight card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("most_recent_weight_card"),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                Text(
                    text = stringResource(R.string.most_recent_weight),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = homeUiState.mostRecentWeight?.weight?.let {
                        "%.1f ${homeUiState.weightUnit.name.lowercase()}".format(it)
                    } ?: "N/A",
                    style = MaterialTheme.typography.displaySmall
                )

                Text(
                    text = homeUiState.mostRecentWeight?.date?.format(
                        DateTimeFormatter.ofPattern(
                            "MM/dd/yyyy"
                        )
                    ) ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Goal weight card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("goal_weight_card"),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = stringResource(R.string.goal_weight),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = goalWeightText,
                    style = MaterialTheme.typography.displaySmall
                )


                Button(
                    onClick = onSetGoalClick,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = stringResource(R.string.set_goal))
                }
            }
        }

        // Buttons
        Column(
            modifier = Modifier
                .padding(top = 8.dp)
                .testTag("action_buttons"),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Add new weight button
            Button(
                onClick = onAddWeightClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_weight_button"),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = stringResource(R.string.add_new_weight))
            }

            // View weight history button
            OutlinedButton(
                onClick = onViewHistoryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("view_history_button"),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = stringResource(R.string.view_weight_history))
            }

            // Settings button
            OutlinedButton(
                onClick = onAccountClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_button"),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = stringResource(R.string.settings))
            }
        }
    }
}