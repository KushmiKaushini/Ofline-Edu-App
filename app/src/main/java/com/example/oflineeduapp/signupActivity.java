package com.example.oflineeduapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class signupActivity extends AppCompatActivity {

    EditText adEmail, adUsername, adPassword;
    Button signup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    adEmail = findViewById(R.id.editTextTextR_Email);
    adUsername = findViewById(R.id.editTextText2R_Username);
    adPassword = findViewById(R.id.editTextTextPasswordR_Password);
    signup = findViewById(R.id.buttonSignUp2);
    Database db = new Database(getApplicationContext(),"oflineeduapp",null,1);

    signup.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email = adEmail.getText().toString().trim();
            String username = adUsername.getText().toString().trim();
            String password = adPassword.getText().toString().trim();

            if (email.length() == 0 || username.length() == 0 || password.length() == 0) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();

            } else {
                if (db.signup(email,username,password)){
                    Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(signupActivity.this, loginActivity.class);
                    startActivity(intent);
                }
            }
        }
        });
    }
}