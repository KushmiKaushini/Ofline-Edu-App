package com.example.oflineeduapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class signupActivity extends AppCompatActivity {

    // Declare the UI elements
    EditText edtUsername, edtEmail, edtPhone, edtPassword;
    Button btnSignup;
    Spinner spinnerRole; // Spinner for role selection
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UI elements
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone); // Added phone field
        edtPassword = findViewById(R.id.edtPassword);
        spinnerRole = findViewById(R.id.spinner);  // Spinner for role selection
        btnSignup = findViewById(R.id.btnSignup);

        // Initialize the database helper class
        db = new Database(getApplicationContext(), "oflineeduapp", null, 1);

        // Handle the signup button click
        btnSignup.setOnClickListener(v -> {
            // Get the input values from UI elements
            String username = edtUsername.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim(); // Get phone number
            String password = edtPassword.getText().toString().trim();
            String role = spinnerRole.getSelectedItem().toString().trim();  // Get the selected role from the spinner

            // Check if any field is empty
            if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || role.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Try to register the user
            boolean registered = db.signup(username, email, phone, password, role);
            if (registered) {
                // If registration is successful, show success message and redirect to login
                Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(signupActivity.this, loginActivity.class);
                startActivity(intent);
                finish();  // Close the signup activity
            } else {
                // If registration failed (e.g., username or email already exists), show an error message
                Toast.makeText(getApplicationContext(), "Username or email already exists", Toast.LENGTH_SHORT).show();
            }
        });
    }
}