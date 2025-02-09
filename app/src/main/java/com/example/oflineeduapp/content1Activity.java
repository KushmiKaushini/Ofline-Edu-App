package com.example.oflineeduapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class content1Activity extends AppCompatActivity {

    EditText search;
    Button Sbtn, Cbtn;
    ImageView scnc, tech, arts, ol, profile;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_content1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        profile = findViewById(R.id.Profpic);
        search = findViewById(R.id.Search);
        Sbtn = findViewById(R.id.searchbutton);
        Cbtn = findViewById(R.id.ContinueButton);
        scnc = findViewById(R.id.Science);
        tech = findViewById(R.id.Techno);
        arts = findViewById(R.id.Arts);
        ol = findViewById(R.id.Ordinary);

        Sbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String Search = search.getText().toString().trim();

              if (search.length()== 1){
                  Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

                  Intent intent = new Intent(content1Activity.this, content2Activity.class);
                  startActivity(intent);

              }else{
                  Toast.makeText(getApplicationContext(),"Search what you want", Toast.LENGTH_SHORT).show();
              }
            }
        });

        Cbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(content1Activity.this, MathsActivity.class));
            }
        });

        scnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(content1Activity.this, scienceActivity.class));
            }
        });

        tech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(content1Activity.this, technoActivity.class));
            }
        });

    }
}