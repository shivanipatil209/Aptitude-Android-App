package com.example.aptitudeapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class levels extends AppCompatActivity {

    boolean doubleBackToExistPressedOnce;
    MediaPlayer mp;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        mp = MediaPlayer.create(getApplicationContext(), R.raw.click);
        Button button_easy = findViewById(R.id.easy);
        Button button_medium = findViewById(R.id.medium);
        Button button_difficult = findViewById(R.id.difficult);

        Button button_gotodomains = findViewById(R.id.back1);
        final Intent intent = getIntent();
        final String str = intent.getStringExtra("Domain_name");

        Toast.makeText(this, "WELCOME TO QUIZ TEST", Toast.LENGTH_SHORT).show();

        button_easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent intent1 = new Intent(levels.this, instructionpage.class);
                String level_type = "normal";
                intent1.putExtra("Domain_name", str);
                intent1.putExtra("level_type", level_type);
                startActivity(intent1);

            }
        });



        button_medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent intent2 = new Intent(levels.this, instructionpage.class);
                String level_type = "difficult";
                intent2.putExtra("Domain_name", str);
                intent2.putExtra("level_type", level_type);
                startActivity(intent2);

            }
        });

        button_gotodomains.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                Intent intent4 = new Intent(levels.this, DomainOptions.class);
                startActivity(intent4);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(doubleBackToExistPressedOnce) {
            finishAffinity();
            mp.stop();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExistPressedOnce = true;
        Toast.makeText(this,"Please click BACK again to exist" , Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExistPressedOnce = false;
            }
        },2000);
    }
}
