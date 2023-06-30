package com.example.aptitudeapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class instructionpage extends AppCompatActivity {

    boolean doubleBackToExistPressedOnce;
    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mp=MediaPlayer.create(getApplicationContext(),R.raw.click);
        setContentView(R.layout.activity_instructionpage);


        Button buttonStartQuiz = findViewById(R.id.start);
        Button buttonGoBack = findViewById(R.id.back1);
        Intent intent = getIntent();

        final String Domain_name = intent.getStringExtra("Domain_name");
        final String level_type = intent.getStringExtra("level_type");

        buttonStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(instructionpage.this,questionspage.class);
                intent.putExtra("Domain_name",Domain_name);
                intent.putExtra("level_type",level_type);
                startActivity(intent);

            }
        });

        buttonGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                Intent intent1 = new Intent(instructionpage.this, levels.class);
                intent1.putExtra("Domain_name",Domain_name);
                startActivity(intent1);
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