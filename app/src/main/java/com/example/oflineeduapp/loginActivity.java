package com.example.oflineeduapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class loginActivity extends AppCompatActivity {

    EditText edtUsernameEmail, edtPassword;
    Button btnLogin;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login); // Make sure this XML exists and has correct IDs

        // Apply system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        edtUsernameEmail = findViewById(R.id.edtUsernameEmail);  // Ensure these IDs exist in activity_login.xml
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Initialize database
        db = new Database(getApplicationContext(), "oflineeduapp", null, 1);

        // Button click listener using lambda expression
        btnLogin.setOnClickListener(v -> {
            String input = edtUsernameEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (input.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter username/email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            int result = db.login(input, password);
            if (result == 1) {
                String role = db.getUserRole(input);
                String name = db.getUserName(input);

                Toast.makeText(getApplicationContext(), "Login successful as " + role, Toast.LENGTH_SHORT).show();

                Intent intent = createIntentForRole(role, name);  // Extracted intent creation method
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Incorrect credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Extracted method for creating an intent based on user role
    private Intent createIntentForRole(String role, String name) {
        Intent intent;
        if (role.equalsIgnoreCase("Student")) {
            intent = new Intent(loginActivity.this, studentConActivity.class);
        } else {
            intent = new Intent(loginActivity.this, teacherConActivity.class);
        }
        intent.putExtra("name", name);
        return intent;
    }
}