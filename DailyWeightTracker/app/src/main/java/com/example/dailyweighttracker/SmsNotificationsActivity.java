package com.example.dailyweighttracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

/**
 *  Allows users to manage SMS notification settings.
 */
public class SmsNotificationsActivity extends AppCompatActivity implements View.OnClickListener {

    // UI elements
    private Button permissionActionButton;
    private EditText phoneNumberInput;
    private Button sendTestSmsButton;
    private TextView permissionStatus;
    private TextInputLayout phoneNumberLayout;

    // SharedPreferences for storing the phone number
    private SharedPreferences prefs;

    // Request code for SMS permission
    private static final int SMS_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "SmsNotificationsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        // Get references to UI elements
        permissionActionButton = findViewById(R.id.permission_action_button);
        phoneNumberInput = findViewById(R.id.phone_number_input);
        sendTestSmsButton = findViewById(R.id.button_send_test_sms);
        permissionStatus = findViewById(R.id.permission_status);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        phoneNumberLayout = findViewById(R.id.phone_number_layout);
        prefs = getSharedPreferences("WeightTrackerPrefs", Context.MODE_PRIVATE);

        // Set up the toolbar with a back button
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        // Set up click listeners for the buttons
        permissionActionButton.setOnClickListener(this);
        sendTestSmsButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update the permission status and load the saved phone number
        updatePermissionStatus();
        phoneNumberInput.setText(prefs.getString("phone_number", ""));
    }

    /**
     * Handles clicks for all buttons in the activity.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.permission_action_button) {
            // If SMS permission is not granted, request it. Otherwise, open the app settings.
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
            } else {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        } else if (v.getId() == R.id.button_send_test_sms) {
            String phoneNumber = phoneNumberInput.getText().toString();
            if (!phoneNumber.isEmpty()) {
                // Save the phone number and send a test SMS
                prefs.edit().putString("phone_number", phoneNumber).apply();
                try {
                    SmsManager smsManager = getSystemService(SmsManager.class);
                    smsManager.sendTextMessage(phoneNumber, null, "This is a test SMS from Daily Weight Tracker.", null, null);
                    Toast.makeText(this, "Test SMS sent.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to send SMS. Please check permissions and phone number.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to send test SMS", e);
                }
            } else {
                Toast.makeText(this, "Please enter a phone number.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Handles the result of the SMS permission request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            // Update the UI based on the permission result
            updatePermissionStatus();
        }
    }

    /**
     * Updates the UI to reflect the current SMS permission status.
     */
    private void updatePermissionStatus() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            // If permission is granted, show the UI for sending a test SMS
            permissionStatus.setText(R.string.permission_granted);
            permissionStatus.setTextColor(ContextCompat.getColor(this, R.color.primary_dark));
            permissionActionButton.setText(R.string.revoke);
            showTestSmsUI(true);
        } else {
            // If permission is not granted, show the UI for requesting permission
            permissionStatus.setText(R.string.permission_required);
            permissionStatus.setTextColor(ContextCompat.getColor(this, R.color.error_color));
            permissionActionButton.setText(R.string.allow);
            showTestSmsUI(false);
        }
    }

    /**
     * Shows or hides the UI for sending a test SMS.
     */
    private void showTestSmsUI(boolean show) {
        if (show) {
            phoneNumberLayout.setVisibility(View.VISIBLE);
            sendTestSmsButton.setVisibility(View.VISIBLE);
        } else {
            phoneNumberLayout.setVisibility(View.GONE);
            sendTestSmsButton.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        return true;
    }
}
