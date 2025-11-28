package com.example.dailyweighttracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.util.Objects;

/**
 * This activity displays a grid of the user's past weight entries. Allows users to add new entries,
 * edit existing entries, and delete entries.
 */
public class WeightHistoryActivity extends AppCompatActivity {

    // Database, adapter, and preferences
    private DatabaseHelper dbHelper;
    private WeightCursorAdapter adapter;
    private SharedPreferences prefs;

    // UI elements
    private FloatingActionButton fab;
    private CardView popupOverlay, editPopupOverlay;
    private EditText inputWeight, editInputWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datagrid);

        // Initialize database helper and shared preferences
        dbHelper = new DatabaseHelper(this);
        prefs = getSharedPreferences("WeightTrackerPrefs", Context.MODE_PRIVATE);
        String username = dbHelper.getCurrentUsername(prefs);

        // Determine the user's preferred unit
        String unit = prefs.getString("unit_" + username, "lbs");

        // Get references to UI elements
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        GridView gridView = findViewById(R.id.weight_grid);
        fab = findViewById(R.id.fab_add_weight);
        popupOverlay = findViewById(R.id.popupOverlay);
        editPopupOverlay = findViewById(R.id.editPopupOverlay);
        inputWeight = findViewById(R.id.inputWeight);
        editInputWeight = findViewById(R.id.editInputWeight);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);
        Button btnEditSave = findViewById(R.id.btnEditSave);
        Button btnEditCancel = findViewById(R.id.btnEditCancel);

        // Set up the toolbar with a back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        // Set up the grid view with a custom adapter
        Cursor cursor = dbHelper.getAllWeights(prefs);
        adapter = new WeightCursorAdapter(this, cursor, unit, prefs, (id, currentWeight) -> {
            // Show the edit popup when a weight entry is clicked
            editPopupOverlay.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
            DecimalFormat df = new DecimalFormat("#.0");
            editInputWeight.setText(df.format(currentWeight));
            // Save the edited weight entry
            btnEditSave.setOnClickListener(v -> {
                String weightStr = editInputWeight.getText().toString();
                if (!weightStr.isEmpty()) {
                    dbHelper.updateWeight(id, Float.parseFloat(weightStr));
                    updateWeightList(); // Refresh the list
                    editPopupOverlay.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                    editInputWeight.setText("");
                } else {
                    Toast.makeText(this, "Please enter a weight", Toast.LENGTH_SHORT).show();
                }
            });
            // Cancel editing the weight entry
            btnEditCancel.setOnClickListener(v -> {
                editPopupOverlay.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                editInputWeight.setText("");
            });
        }, dbHelper);
        gridView.setAdapter(adapter);

        // Show the add weight popup when the FAB is clicked
        fab.setOnClickListener(v -> {
            popupOverlay.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
        });

        // Save the new weight entry
        btnSave.setOnClickListener(v -> {
            String weight = inputWeight.getText().toString();
            if (!weight.isEmpty()) {
                dbHelper.addWeight(this, Float.parseFloat(weight), prefs);
                updateWeightList(); // Refresh the list
                popupOverlay.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                inputWeight.setText("");
            } else {
                Toast.makeText(this, "Please enter a weight.", Toast.LENGTH_SHORT).show();
            }
        });

        // Cancel adding a new weight entry
        btnCancel.setOnClickListener(v -> {
            popupOverlay.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
            inputWeight.setText("");
        });
    }

    /**
     * Updates the weight list by creating a new cursor and swapping it into the adapter.
     */
    private void updateWeightList() {
        Cursor newCursor = dbHelper.getAllWeights(prefs);
        adapter.swapCursor(newCursor);
    }

    @Override
    // Handle back button press
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        return true;
    }
}
