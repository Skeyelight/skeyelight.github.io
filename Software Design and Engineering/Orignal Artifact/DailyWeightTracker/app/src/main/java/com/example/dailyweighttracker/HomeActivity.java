package com.example.dailyweighttracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;


/**
 * This activity displays the main screen of the app,  user's most recent weight, and goal weight.
 * Also displays options to add a new weight, view weight history, and manage account settings.
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button setGoalButton;
    private TextView recentWeightTextView;
    private TextView recentDateTextView;
    private TextView goalWeightTextView;

    // Database and preferences
    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;

    // User-specific data
    private String unit;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize database helper and shared preferences
        dbHelper = new DatabaseHelper(this);
        prefs = getSharedPreferences("WeightTrackerPrefs", Context.MODE_PRIVATE);

        
        // UI elements
        Button addWeightButton = findViewById(R.id.button_add_weight);
        Button viewHistoryButton = findViewById(R.id.button_view_history);
        Button smsNotificationsButton = findViewById(R.id.button_sms_notifications);
        setGoalButton = findViewById(R.id.button_set_goal);
        Button accountButton = findViewById(R.id.button_account);
        recentWeightTextView = findViewById(R.id.text_recent_weight);
        recentDateTextView = findViewById(R.id.text_recent_date);
        goalWeightTextView = findViewById(R.id.text_goal_weight);

        // Set up click listeners for buttons
        addWeightButton.setOnClickListener(this);
        viewHistoryButton.setOnClickListener(this);
        smsNotificationsButton.setOnClickListener(this);
        setGoalButton.setOnClickListener(this);
        accountButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload user data and update the UI when the activity is resumed
        username = dbHelper.getCurrentUsername(prefs);
        unit = prefs.getString("unit_" + username, "lbs");
        updateUI();
        checkGoalWeight();
    }

    /**
     * Checks if a goal weight has been set and prompts the user to set one if not.
     */
    private void checkGoalWeight() {
        if (prefs.getFloat("goal_weight_" + username, 0) == 0) {
            showSetGoalDialog();
        }
    }

    /**
     * Shows a dialog to add a new weight entry.
     */
    private void showAddWeightDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_weight, null);
        builder.setView(dialogView);

        final EditText weightInput = dialogView.findViewById(R.id.inputWeight);
        Button saveButton = dialogView.findViewById(R.id.btnSave);
        Button cancelButton = dialogView.findViewById(R.id.btnCancel);

        final AlertDialog dialog = builder.create();

        saveButton.setOnClickListener(v -> {
            String weightStr = weightInput.getText().toString();
            if (!weightStr.isEmpty()) {
                try {
                    float weight = Float.parseFloat(weightStr);
                    dbHelper.addWeight(HomeActivity.this, weight, prefs);
                    updateUI(); // Refresh the UI with the new weight
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter a valid weight.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter a weight.", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Shows a dialog to set or update the user's goal weight.
     */
    private void showSetGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_set_goal, null);
        builder.setView(dialogView);

        final EditText goalWeightInput = dialogView.findViewById(R.id.input_goal_weight);
        final RadioGroup unitsGroup = dialogView.findViewById(R.id.units_radio_group);
        final RadioButton lbsButton = dialogView.findViewById(R.id.lbs_radio_button);
        final RadioButton kgButton = dialogView.findViewById(R.id.kg_radio_button);

        // Set the current unit preference
        String currentUnit = prefs.getString("unit_" + username, "lbs");
        if (currentUnit.equals("kg")) {
            kgButton.setChecked(true);
        } else {
            lbsButton.setChecked(true);
        }

        builder.setPositiveButton("OK", (dialog, which) -> {
            String goalWeightStr = goalWeightInput.getText().toString();
            if (!goalWeightStr.isEmpty()) {
                float goalWeight = Float.parseFloat(goalWeightStr);
                int selectedUnitId = unitsGroup.getCheckedRadioButtonId();
                String selectedUnit = "lbs";
                if (selectedUnitId == R.id.kg_radio_button) {
                    selectedUnit = "kg";
                }
                // Save the goal weight and unit preference
                prefs.edit().putFloat("goal_weight_" + username, goalWeight).apply();
                prefs.edit().putString("unit_" + username, selectedUnit).apply();
                unit = selectedUnit;
                updateUI(); // Refresh the UI with the new goal
            } else {
                Toast.makeText(this, "Please enter a valid weight.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Updates the UI with the most recent weight and goal weight information.
     */
    private void updateUI() {
        // Update recent weight
        Cursor cursor = dbHelper.getMostRecentWeight(prefs);
        if (cursor != null && cursor.moveToFirst()) {
            double weight = cursor.getDouble(cursor.getColumnIndexOrThrow("weight"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            DecimalFormat df = new DecimalFormat("#.0");
            recentWeightTextView.setText(String.format("%s %s", df.format(weight), unit));
            recentDateTextView.setText(date);
            cursor.close();
        } else {
            recentWeightTextView.setText("N/A");
            recentDateTextView.setText("");
        }

        // Update goal weight
        float goalWeight = prefs.getFloat("goal_weight_" + username, 0);
        if (goalWeight > 0) {
            DecimalFormat df = new DecimalFormat("#.0");
            goalWeightTextView.setText(String.format("%s %s", df.format(goalWeight), unit));
            setGoalButton.setText(R.string.change_goal);
        } else {
            goalWeightTextView.setText("N/A");
            setGoalButton.setText(R.string.set_goal);
        }
    }

    /**
     * Handles clicks for all buttons in the activity.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_add_weight) {
            showAddWeightDialog();
        } else if (v.getId() == R.id.button_view_history) {
            // Go to the weight history screen
            Intent intent = new Intent(HomeActivity.this, WeightHistoryActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.button_sms_notifications) {
            // Go to the SMS notifications screen
            Intent intent = new Intent(HomeActivity.this, SmsNotificationsActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.button_set_goal) {
            showSetGoalDialog();
        } else if (v.getId() == R.id.button_account) {
            // Restrict account management to registered users
            Intent intent;
            if (DatabaseHelper.GUEST_USERNAME.equals(username)) {
                // Redirect guest users to the login screen
                intent = new Intent(HomeActivity.this, LoginActivity.class);
            } else {
                // Go to the account management screen
                intent = new Intent(HomeActivity.this, AccountActivity.class);
            }
            startActivity(intent);
        }
    }
}
