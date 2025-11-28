package com.example.dailyweighttracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DecimalFormat;

/**
 * Adapter for the weight grid view.
 */
public class WeightCursorAdapter extends CursorAdapter {

    private final String unit;
    private final SharedPreferences prefs;
    private final OnEditClickListener listener;
    private final DatabaseHelper dbHelper;

    public interface OnEditClickListener {
        void onEditClick(long id, double currentWeight);
    }
    // Constructor
    public WeightCursorAdapter(Context context, Cursor cursor, String unit, SharedPreferences prefs, OnEditClickListener listener, DatabaseHelper dbHelper) {
        super(context, cursor, 0);
        this.unit = unit;
        this.prefs = prefs;
        this.listener = listener;
        this.dbHelper = dbHelper;
    }

    @Override
    // Inflate the layout for each grid item
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.grid_item_weight, parent, false);
    }

    @Override
    // Bind the data to the views
    public void bindView(View view, Context context, Cursor cursor) {
        TextView weightTextView = view.findViewById(R.id.weight_text);
        TextView dateTextView = view.findViewById(R.id.date_text);
        ImageButton editButton = view.findViewById(R.id.edit_button);
        ImageButton deleteButton = view.findViewById(R.id.delete_button);
        // Get the data from the cursor
        long id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
        double weight = cursor.getDouble(cursor.getColumnIndexOrThrow("weight"));
        String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

        DecimalFormat df = new DecimalFormat("#.0");
        weightTextView.setText(String.format("%s %s", df.format(weight), unit));
        dateTextView.setText(date);
        // Set the click listeners for the buttons
        editButton.setOnClickListener(v -> listener.onEditClick(id, weight));

        deleteButton.setOnClickListener(v -> new MaterialAlertDialogBuilder(context)
                .setTitle("Delete Weight")
                .setMessage("Are you sure you want to delete this weight entry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteWeight(id);
                    swapCursor(dbHelper.getAllWeights(prefs));
                    Toast.makeText(context, "Weight deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show());
    }
}
