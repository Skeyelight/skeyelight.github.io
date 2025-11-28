package com.example.dailyweighttracker;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WeightTracker.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TAG = "DatabaseHelper";

    // Weights Table
    private static final String TABLE_WEIGHTS = "weights";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_USER = "user";

    // Users Table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    public static final String GUEST_USERNAME = "guest";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WEIGHTS_TABLE = "CREATE TABLE " + TABLE_WEIGHTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_WEIGHT + " TEXT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_USER + " TEXT" + ")";
        db.execSQL(CREATE_WEIGHTS_TABLE);

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public void addWeight(Context context, double weight, SharedPreferences prefs) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        String username = getCurrentUsername(prefs);
        values.put(COLUMN_USER, username);
        db.insert(TABLE_WEIGHTS, null, values);

        String goalWeightKey = "goal_weight_" + username;
        double goalWeight = prefs.getFloat(goalWeightKey, 0);

        String unitKey = "unit_" + username;
        String unit = prefs.getString(unitKey, "lbs");

        if (goalWeight > 0 && weight == goalWeight) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                String phoneNumber = prefs.getString("phone_number", null);
                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    try {
                        SmsManager smsManager = context.getSystemService(SmsManager.class);
                        smsManager.sendTextMessage(phoneNumber, null, "Congratulations! You've reached your goal weight of " + goalWeight + " " + unit + "!", null, null);
                        Toast.makeText(context, "Goal reached! SMS sent.", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, "Failed to send SMS.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to send SMS", e);
                    }
                } else {
                    // Only show this toast if the user is not a guest
                    if (!GUEST_USERNAME.equals(username)) {
                        Toast.makeText(context, "No phone number set for SMS notifications.", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                // Only show this toast if the user is not a guest
                if (!GUEST_USERNAME.equals(username)) {
                    Toast.makeText(context, "SMS permission not granted.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    // Delete weight by ID
    public void deleteWeight(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WEIGHTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }
    // Delete all weights for a user
    public void deleteAllWeightsForUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WEIGHTS, COLUMN_USER + " = ?", new String[]{username});
    }
    // Update weight by ID
    public void updateWeight(long id, float weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WEIGHT, weight);
        int rows = db.update(TABLE_WEIGHTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }
    // Get all weights for a user
    public Cursor getAllWeights(SharedPreferences prefs) {
        SQLiteDatabase db = this.getReadableDatabase();
        String username = getCurrentUsername(prefs);
        return db.rawQuery("SELECT " + COLUMN_ID + " as _id, " + COLUMN_WEIGHT + ", " + COLUMN_DATE + " FROM " + TABLE_WEIGHTS + " WHERE " + COLUMN_USER + " = ?", new String[]{username});
    }
    // Get most recent weight for a user
    public Cursor getMostRecentWeight(SharedPreferences prefs) {
        SQLiteDatabase db = this.getReadableDatabase();
        String username = getCurrentUsername(prefs);
        return db.rawQuery("SELECT * FROM " + TABLE_WEIGHTS + " WHERE " + COLUMN_USER + " = ?" + " ORDER BY " + COLUMN_DATE + " DESC LIMIT 1", new String[]{username});
    }
    // Add a new user
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }
    // Check if username & password combo exists
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USERNAME + " = ?" + " AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }
    // Check if username exists
    public boolean checkUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }
    // Updates username
    public boolean updateUsername(String oldUsername, String newUsername) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, newUsername);
        int rows = db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{oldUsername});
        return rows > 0;
    }
    // Updates Password
    public boolean updatePassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);
        int rows = db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});
        return rows > 0;
    }
    // Get current username
    public String getCurrentUsername(SharedPreferences prefs) {
        return prefs.getString("username", GUEST_USERNAME);
    }
}
