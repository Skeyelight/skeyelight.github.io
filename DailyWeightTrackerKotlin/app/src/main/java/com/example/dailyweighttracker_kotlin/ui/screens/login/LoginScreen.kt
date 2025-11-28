package com.example.dailyweighttracker_kotlin.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailyweighttracker_kotlin.AppViewModelProvider

// Login Screen
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onLoginSuccess: () -> Unit
) {
    // Get the current UI state of the ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // When user logs in successfully, navigate to the home screen
    if (uiState.loginSuccess) {
        LaunchedEffect(Unit) {
            onLoginSuccess()
        }
    }

    // Show login screen UI
    LoginScreenContent(
        modifier = modifier,
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

// Login Screen Content
@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    uiState: LoginState,
    onEvent: (LoginEvent) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Screen Title
        Text(text = "Login", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // Username and Password Input Fields
        OutlinedTextField(
            value = uiState.username,
            onValueChange = { onEvent(LoginEvent.OnUsernameChanged(it)) },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { onEvent(LoginEvent.OnPasswordChanged(it)) },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Show error message if there is one
        if (!uiState.error.isNullOrEmpty()) {
            Text(text = uiState.error, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Show loading indicator or login button
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onEvent(LoginEvent.OnLoginClicked) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }
                Button(
                    onClick = { onEvent(LoginEvent.OnCreateUserClicked) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create User")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Continue as Guest button
            OutlinedButton(
                onClick = { onEvent(LoginEvent.OnContinueAsGuestClicked) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue as Guest")
            }
        }
    }
}


