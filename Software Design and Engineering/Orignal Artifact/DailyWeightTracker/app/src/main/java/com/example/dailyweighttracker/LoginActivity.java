package com.example.dailyweighttracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This activity handles user login and registration.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize database helper and shared preferences
        dbHelper = new DatabaseHelper(this);
        prefs = getSharedPreferences("WeightTrackerPrefs", Context.MODE_PRIVATE);

        // Get references to UI elements
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonSignUp = findViewById(R.id.buttonSignUp);
        Button buttonGuest = findViewById(R.id.buttonGuest);

        // Set up click listener for the login button
        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Validate input
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if user credentials are valid
            if (dbHelper.checkUser(username, password)) {
                // Save username to shared preferences
                prefs.edit().putString("username", username).apply();
                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                // Go to the home screen
                goToHome();
            } else {
                Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up click listener for the sign-up button
        buttonSignUp.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Validate input
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the user already exists
            if (dbHelper.checkUserExists(username)) {
                Toast.makeText(LoginActivity.this, "User already exists. Try logging in.", Toast.LENGTH_SHORT).show();
            } else {
                // Add the new user to the database
                boolean inserted = dbHelper.addUser(username, password);
                if (inserted) {
                    // Save username to shared preferences
                    prefs.edit().putString("username", username).apply();
                    Toast.makeText(LoginActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                    // Go to the home screen
                    goToHome();
                } else {
                    Toast.makeText(LoginActivity.this, "Error creating account", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set up click listener for the guest button
        buttonGuest.setOnClickListener(v -> goToHome());
    }

    /**
     * Navigates to the HomeActivity.
     */
    private void goToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
