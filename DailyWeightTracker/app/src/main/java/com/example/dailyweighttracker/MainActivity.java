package com.example.dailyweighttracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize SharedPreferences
        SharedPreferences prefs = getSharedPreferences("WeightTrackerPrefs", Context.MODE_PRIVATE);

        // Check if a user is already logged in
        if (prefs.contains("username")) {
            // If logged in, go to the Home screen
            goToHome();
        } else {
            // If not logged in, go to the Login screen
            goToLogin();
        }
    }

    /**
     * Navigates to the HomeActivity.
     */
    private void goToHome() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Navigates to the LoginActivity.
     */
    private void goToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
