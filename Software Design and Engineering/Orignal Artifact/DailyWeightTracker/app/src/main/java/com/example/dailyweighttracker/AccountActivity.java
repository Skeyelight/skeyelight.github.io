package com.example.dailyweighttracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

/**
 * Allows users to manage their username,password, and preferred units.
 * Users can also delete all their weight entries or log out.
 */
public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    // UI elements
    private EditText usernameInput;
    private Button changeUsernameButton;
    private RadioButton lbsRadioButton, kgRadioButton;
    private TextInputLayout usernameInputLayout;

    // Database and preferences
    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Initialize database helper and shared preferences
        dbHelper = new DatabaseHelper(this);
        prefs = getSharedPreferences("WeightTrackerPrefs", Context.MODE_PRIVATE);
        username = dbHelper.getCurrentUsername(prefs);

        if (DatabaseHelper.GUEST_USERNAME.equals(username)) {
            Toast.makeText(this, "This feature is not available for guest users.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get references to UI elements
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        usernameInput = findViewById(R.id.username_input);
        changeUsernameButton = findViewById(R.id.button_change_username);
        Button changePasswordButton = findViewById(R.id.button_change_password);
        Button deleteAllWeightsButton = findViewById(R.id.button_delete_weights);
        Button logoutButton = findViewById(R.id.button_logout);
        RadioGroup unitsRadioGroup = findViewById(R.id.units_radio_group);
        lbsRadioButton = findViewById(R.id.lbs_radio_button);
        kgRadioButton = findViewById(R.id.kg_radio_button);
        usernameInputLayout = findViewById(R.id.username_input_layout);

        // Toolbar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        // Set up click listeners for all buttons
        changeUsernameButton.setOnClickListener(this);
        changePasswordButton.setOnClickListener(this);
        deleteAllWeightsButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

        // Load the user's preferred unit
        loadUnitPreference();

        // Listen for changes in the selected unit
        unitsRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.lbs_radio_button) {
                prefs.edit().putString("unit_" + username, "lbs").apply();
            } else if (checkedId == R.id.kg_radio_button) {
                prefs.edit().putString("unit_" + username, "kg").apply();
            }
        });
    }

    /**
     * Loads the user's preferred unit (lbs or kg)
     */
    private void loadUnitPreference() {
        String unit = prefs.getString("unit_" + username, "lbs");
        if (unit.equals("kg")) {
            kgRadioButton.setChecked(true);
        } else {
            lbsRadioButton.setChecked(true);
        }
    }

    /**
     * Handles clicks for all buttons in the activity.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_change_username) {
            // Toggle the visibility of the username input field
            if (usernameInputLayout.getVisibility() == View.GONE) {
                usernameInputLayout.setVisibility(View.VISIBLE);
                usernameInput.setText(username);
                changeUsernameButton.setText(R.string.save_username);
            } else {
                changeUsername();
            }
        } else if (v.getId() == R.id.button_change_password) {
            showChangePasswordDialog();
        } else if (v.getId() == R.id.button_delete_weights) {
            showDeleteWeightsConfirmationDialog();
        } else if (v.getId() == R.id.button_logout) {
            logout();
        }
    }

    /**
     * Updates the user's username in the database and shared preferences.
     */
    private void changeUsername() {
        String newUsername = usernameInput.getText().toString();
        if (newUsername.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.updateUsername(username, newUsername)) {
            // Update username in SharedPreferences as well
            prefs.edit().putString("username", newUsername).apply();
            username = newUsername;
            Toast.makeText(this, "Username updated successfully.", Toast.LENGTH_SHORT).show();
            // Hide the input field and reset the button text
            usernameInputLayout.setVisibility(View.GONE);
            changeUsernameButton.setText(R.string.change_username);
        } else {
            Toast.makeText(this, "Failed to update username. The username might already be taken.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a dialog for the user to change their password.
     */
    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        final EditText currentPasswordInput = dialogView.findViewById(R.id.current_password_input);
        final EditText newPasswordInput = dialogView.findViewById(R.id.new_password_input);
        Button saveButton = dialogView.findViewById(R.id.btn_save_password_change);
        Button cancelButton = dialogView.findViewById(R.id.btn_cancel_password_change);

        final AlertDialog dialog = builder.create();

        saveButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordInput.getText().toString();
            String newPassword = newPasswordInput.getText().toString();

            // Verify the current password before updating to the new one
            if (dbHelper.checkUser(username, currentPassword)) {
                if (dbHelper.updatePassword(username, newPassword)) {
                    Toast.makeText(AccountActivity.this, "Password updated successfully.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(AccountActivity.this, "Failed to update password.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AccountActivity.this, "Incorrect current password.", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Shows a confirmation dialog before deleting all the user's weight entries.
     */
    private void showDeleteWeightsConfirmationDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete All Weights?")
                .setMessage("Are you sure you want to delete all of your saved weight entries? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteAllWeightsForUser(username);
                    Toast.makeText(AccountActivity.this, "All weights deleted.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Logs the user out by clearing their username from shared preferences and returning to the main screen.
     */
    private void logout() {
        prefs.edit().remove("username").apply();
        Intent intent = new Intent(AccountActivity.this, MainActivity.class);
        // Clear the activity stack so the user can't navigate back to the account screen
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        return true;
    }
}
