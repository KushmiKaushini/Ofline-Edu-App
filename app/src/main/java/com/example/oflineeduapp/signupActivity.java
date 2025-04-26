package com.example.oflineeduapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class signupActivity extends AppCompatActivity {

    private EditText edtUsername, edtEmail, edtPhone, edtPassword, edtConfirmPassword;
    private Spinner spinnerRole;
    private Button btnSignup;
    private Database db;
    private String selectedRole = ""; // To store the selected role

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        initializeViews();

        // Initialize database
        db = new Database(this);

        // Set up the role spinner
        setupRoleSpinner();

        // Set up text watchers for real-time validation
        setupTextWatchers();

        // Set up signup button click listener
        btnSignup.setOnClickListener(v -> attemptSignup());
    }

    private void initializeViews() {
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnSignup = findViewById(R.id.btnSignup);
    }

    private void setupRoleSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerRole.setAdapter(adapter);

        // Set spinner item selection listener
        spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRole = "";
            }
        });
    }

    private void setupTextWatchers() {
        // Username validation (alphanumeric, 4-20 chars)
        edtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().matches("^[a-zA-Z0-9]{4,20}$")) {
                    edtUsername.setError("4-20 alphanumeric characters");
                } else {
                    edtUsername.setError(null);
                }
            }
        });

        // Email validation
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (!Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    edtEmail.setError("Invalid email format");
                } else {
                    edtEmail.setError(null);
                }
            }
        });

        // Phone validation (basic international format)
        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().matches("^[+0-9\\s-]{8,20}$")) {
                    edtPhone.setError("Invalid phone format");
                } else {
                    edtPhone.setError(null);
                }
            }
        });

        // Password strength validation
        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 6) {
                    edtPassword.setError("At least 6 characters");
                } else {
                    edtPassword.setError(null);
                }
            }
        });

        // Password confirmation match
        edtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(edtPassword.getText().toString())) {
                    edtConfirmPassword.setError("Passwords don't match");
                } else {
                    edtConfirmPassword.setError(null);
                }
            }
        });
    }

    private void attemptSignup() {
        // Get input values
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(username, email, phone, password, confirmPassword)) {
            return;
        }

        // Show loading state
        btnSignup.setEnabled(false);

        // Perform signup in background thread
        new Thread(() -> {
            boolean registrationSuccess = db.signup(
                    username,
                    email,
                    phone,
                    password,
                    selectedRole
            );

            runOnUiThread(() -> {
                btnSignup.setEnabled(true);

                if (registrationSuccess) {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    navigateToLogin();
                } else {
                    Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private boolean validateInputs(String username, String email, String phone,
                                   String password, String confirmPassword) {
        boolean isValid = true;

        if (username.isEmpty() || !username.matches("^[a-zA-Z0-9]{4,20}$")) {
            edtUsername.setError("Invalid username");
            isValid = false;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Invalid email");
            isValid = false;
        }

        if (!phone.isEmpty() && !phone.matches("^[+0-9\\s-]{8,20}$")) {
            edtPhone.setError("Invalid phone");
            isValid = false;
        }

        if (password.isEmpty() || password.length() < 6) {
            edtPassword.setError("Password too short");
            isValid = false;
        }

        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Passwords don't match");
            isValid = false;
        }

        if (selectedRole.isEmpty()) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, loginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (db != null) {
            db.close();
        }
        super.onDestroy();
    }
}