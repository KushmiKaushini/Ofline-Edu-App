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

public class loginActivity extends AppCompatActivity {

    EditText userId, Password;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userId = findViewById(R.id.editTextText3Username);
        Password = findViewById(R.id.editTextTextPassword2Password);
        login = findViewById(R.id.button2Login2);
        Database db = new Database(getApplicationContext(),"oflineeduapp",null,1);

    login.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String username = userId.getText().toString().trim();
            String password = Password.getText().toString().trim();

            if (userId.length()==0 || Password.length()==0){
                Toast.makeText(getApplicationContext(), "Ivalid Usernamme or Password", Toast.LENGTH_SHORT).show();

            }else{
                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                }

            Intent intent = new Intent(loginActivity.this, content1Activity.class);
            startActivity(intent);

            }
    });
    }
}