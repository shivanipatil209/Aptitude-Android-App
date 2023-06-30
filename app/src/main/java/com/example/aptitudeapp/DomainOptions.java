package com.example.aptitudeapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;

public class DomainOptions extends AppCompatActivity {
    ImageButton button_quants;
    ImageButton button_logic;
    ImageButton button_puzzle;
    ImageButton button_verbal;
    Button pop_up;
    MediaPlayer mp;
    private View button_database;
    boolean doubleBackToExistPressedOnce;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domain_options);
         mp =MediaPlayer.create(getApplicationContext(),R.raw.click);

        button_quants=findViewById(R.id.button_quants);
        button_logic=findViewById(R.id.button_logic);
        button_puzzle=findViewById(R.id.button_puzzle);
        button_verbal=findViewById(R.id.button_verbal);

        pop_up = findViewById(R.id.popup);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        button_quants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent intent1=new Intent(DomainOptions.this,levels.class);
                String str = "quants";
                intent1.putExtra("Domain_name",str);
                startActivity(intent1);

            }
        });
        button_logic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent intent2=new Intent(DomainOptions.this,levels.class);
                String str = "logic";
                intent2.putExtra("Domain_name",str);
                startActivity(intent2);

            }
        });
        button_verbal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent intent3=new Intent(DomainOptions.this,levels.class);
                String str = "verbal";
                intent3.putExtra("Domain_name",str);
                startActivity(intent3);

            }
        });
        button_puzzle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent intent4=new Intent(DomainOptions.this,levels.class);
                String str = "puzzle";
                intent4.putExtra("Domain_name",str);
                startActivity(intent4);

            }
        });

        pop_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                PopupMenu popupMenu = new PopupMenu(DomainOptions.this, pop_up);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        // Toast message on menu item clicked

                        if(menuItem.getTitle().equals("Sign Out")){
                            mAuth.signOut();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();

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
        Intent intent = new Intent(DomainOptions.this, EmailPasswordActivity.class);
        startActivity(intent);
    }

}
