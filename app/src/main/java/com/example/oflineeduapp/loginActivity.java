package com.example.oflineeduapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class loginActivity extends AppCompatActivity {

    private EditText edtUsernameEmail, edtPassword;
    private Button btnLogin;
    private CheckBox cbRememberMe;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        edtUsernameEmail = findViewById(R.id.edtUsernameEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        cbRememberMe = findViewById(R.id.cbRememberMe);

        // Initialize database
        db = new Database(this);

        // Login button click listener
        btnLogin.setOnClickListener(v -> attemptLogin());

        // You might want to add click listeners for these as well
        findViewById(R.id.txtForgotPassword).setOnClickListener(v -> {
            // Handle forgot password
        });

        findViewById(R.id.txtSignUp).setOnClickListener(v -> {
            // Handle sign up
        });
    }

    private void attemptLogin() {
        String input = edtUsernameEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (input.isEmpty()) {
            edtUsernameEmail.setError("Please enter username or email");
            edtUsernameEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Please enter password");
            edtPassword.requestFocus();
            return;
        }

        // Show loading state
        btnLogin.setEnabled(false);

        // Perform login in background thread
        new Thread(() -> {
            User user = db.login(input, password);

            runOnUiThread(() -> {
                btnLogin.setEnabled(true);

                if (user != null) {
                    handleSuccessfulLogin(user);
                } else {
                    handleFailedLogin();
                }
            });
        }).start();
    }

    private void handleSuccessfulLogin(User user) {
        // Save login state if Remember Me is checked
        if (cbRememberMe.isChecked()) {
            // You should implement secure storage for credentials
            // For example using SharedPreferences or EncryptedSharedPreferences
        }

        Toast.makeText(this, "Welcome, " + user.getUsername() + "!", Toast.LENGTH_SHORT).show();

        Intent intent;
        if (user.getRole().equalsIgnoreCase("Student")) {
            intent = new Intent(this, studentConActivity.class);
        } else {
            intent = new Intent(this, teacherConActivity.class);
        }

        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    private void handleFailedLogin() {
        Toast.makeText(this, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show();
        edtPassword.setText("");
        edtPassword.requestFocus();
    }

    @Override
    protected void onDestroy() {
        if (db != null) {
            db.close();
        }
        super.onDestroy();
    }
}