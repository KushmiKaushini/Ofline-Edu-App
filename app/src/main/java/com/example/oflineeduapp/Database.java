package com.example.oflineeduapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String createUserTable = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE, " +
                "email TEXT UNIQUE, " +
                "phone TEXT, " +
                "password TEXT, " +
                "role TEXT)";
        db.execSQL(createUserTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop old table and recreate
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    // Signup method
    public boolean signup(String username, String email, String phone, String password, String role) {
        if (checkUserExists(username) || checkEmailExists(email)) {
            return false; // Duplicate user
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("email", email);
        cv.put("phone", phone); // Save phone number
        cv.put("password", password);
        cv.put("role", role);
        long result = db.insert("users", null, cv);
        db.close();
        return result != -1;
    }

    // Add a user (used for saveOrUpdateProfile)
    public boolean addUser(User user) {
        if (checkUserExists(user.username) || checkEmailExists(user.email)) {
            return false; // Duplicate user
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", user.username);
        cv.put("email", user.email);
        cv.put("phone", user.phone);
        cv.put("password", user.password);
        cv.put("role", user.role);
        long result = db.insert("users", null, cv);
        db.close();
        return result != -1;
    }

    // Update user details
    public boolean updateUserDetails(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", user.username);
        cv.put("email", user.email);
        cv.put("phone", user.phone);
        cv.put("password", user.password);
        cv.put("role", user.role);

        int result = db.update("users", cv, "username = ?", new String[]{user.username});
        db.close();
        return result > 0;
    }

    // Check if username exists
    public boolean checkUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Check if email exists
    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Login with email or username
    public int login(String input, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE (username = ? OR email = ?) AND password = ?",
                new String[]{input, input, password});
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0 ? 1 : 0;
    }

    // Get user role by username or email
    public String getUserRole(String input) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT role FROM users WHERE username = ? OR email = ?", new String[]{input, input});
        String role = null;
        if (cursor.moveToFirst()) {
            role = cursor.getString(cursor.getColumnIndexOrThrow("role"));
        }
        cursor.close();
        db.close();
        return role;
    }

    // Get user name by username or email
    public String getUserName(String input) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT username FROM users WHERE username = ? OR email = ?", new String[]{input, input});
        String name = null;
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow("username"));
        }
        cursor.close();
        db.close();
        return name;
    }

    // Get user details (username, email, phone, password, role) by username or email
    public User getUserDetails(String input) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? OR email = ?", new String[]{input, input});

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getString(cursor.getColumnIndexOrThrow("username")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                    cursor.getString(cursor.getColumnIndexOrThrow("password")),
                    cursor.getString(cursor.getColumnIndexOrThrow("role"))
            );
            cursor.close();
            db.close();
            return user;
        } else {
            cursor.close();
            db.close();
            return null; // Return null if no user found
        }
    }

    // Update user profile (name, email, password)
    public boolean updateUserProfile(String oldUsername, String newUsername, String newEmail, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", newUsername);
        cv.put("email", newEmail);
        cv.put("password", newPassword);

        int result = db.update("users", cv, "username = ?", new String[]{oldUsername});
        db.close();
        return result > 0;
    }

    // Delete user from database
    public boolean deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("users", "username = ?", new String[]{username});
        db.close();
        return result > 0;
    }

    // Optional: Clear the database (for dev/debug)
    public void clearUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM users");
        db.close();
    }
}