package com.example.oflineeduapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Database extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "oflineeduapp.db";
    private static final int DATABASE_VERSION = 2;

    // Table and column names (changed to public)
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_CREATED_AT = "created_at";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE NOT NULL,"
                + COLUMN_EMAIL + " TEXT UNIQUE NOT NULL,"
                + COLUMN_PHONE + " TEXT,"
                + COLUMN_PASSWORD + " TEXT NOT NULL,"
                + COLUMN_ROLE + " TEXT NOT NULL,"
                + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP");
        }
    }

    // Hash password using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Error hashing password: " + e.getMessage());
            return null;
        }
    }

    // Signup method with transaction and error handling
    public boolean signup(String username, String email, String phone, String password, String role) {
        if (checkUserExists(username)) {
            Log.w(TAG, "Signup failed: Username already exists");
            return false;
        }
        if (checkEmailExists(email)) {
            Log.w(TAG, "Signup failed: Email already exists");
            return false;
        }

        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            Log.e(TAG, "Password hashing failed");
            return false;
        }

        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, username);
            values.put(COLUMN_EMAIL, email);
            values.put(COLUMN_PHONE, phone);
            values.put(COLUMN_PASSWORD, hashedPassword);
            values.put(COLUMN_ROLE, role);

            long result = db.insertOrThrow(TABLE_USERS, null, values);
            db.setTransactionSuccessful();
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error during signup: " + e.getMessage());
            return false;
        } finally {
            if (db != null) {
                if (db.inTransaction()) {
                    db.endTransaction();
                }
                db.close();
            }
        }
    }

    // Login method with hashed password comparison
    public User login(String input, String password) {
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            return null;
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            String[] columns = {COLUMN_ID, COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_PHONE, COLUMN_ROLE};
            String selection = "(username = ? OR email = ?) AND password = ?";
            String[] selectionArgs = {input, input, hashedPassword};

            cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                return new User(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                        "", // Don't return password
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE))
                );
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error during login: " + e.getMessage());
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    // Get user details
    public User getUserDetails(String input) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            String[] columns = {COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_PHONE, COLUMN_ROLE};
            String selection = "username = ? OR email = ?";
            String[] selectionArgs = {input, input};

            cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                return new User(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                        "", // Don't return password
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE))
                );
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting user details: " + e.getMessage());
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    // Update user profile
    public boolean updateUserProfile(String oldUsername, User updatedUser) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, updatedUser.getUsername());
            values.put(COLUMN_EMAIL, updatedUser.getEmail());
            values.put(COLUMN_PHONE, updatedUser.getPhone());

            // Only update password if it's not empty
            if (!updatedUser.getPassword().isEmpty()) {
                String hashedPassword = hashPassword(updatedUser.getPassword());
                if (hashedPassword == null) {
                    return false;
                }
                values.put(COLUMN_PASSWORD, hashedPassword);
            }

            int rowsAffected = db.update(TABLE_USERS, values,
                    COLUMN_USERNAME + " = ?",
                    new String[]{oldUsername});

            db.setTransactionSuccessful();
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating profile: " + e.getMessage());
            return false;
        } finally {
            if (db != null) {
                if (db.inTransaction()) {
                    db.endTransaction();
                }
                db.close();
            }
        }
    }

    // Check if username exists
    public boolean checkUserExists(String username) {
        return checkFieldExists(COLUMN_USERNAME, username);
    }

    // Check if email exists
    public boolean checkEmailExists(String email) {
        return checkFieldExists(COLUMN_EMAIL, email);
    }

    private boolean checkFieldExists(String column, String value) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            String query = "SELECT 1 FROM " + TABLE_USERS + " WHERE " + column + " = ? LIMIT 1";
            cursor = db.rawQuery(query, new String[]{value});
            return cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking " + column + ": " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    // Delete user
    public boolean deleteUser(String username) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();
            int rowsAffected = db.delete(TABLE_USERS,
                    COLUMN_USERNAME + " = ?",
                    new String[]{username});
            db.setTransactionSuccessful();
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting user: " + e.getMessage());
            return false;
        } finally {
            if (db != null) {
                if (db.inTransaction()) {
                    db.endTransaction();
                }
                db.close();
            }
        }
    }
}