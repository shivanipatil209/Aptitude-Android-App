package com.example.aptitudeapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class splashscreen extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        /*
        DBHelper dbHelper = new DBHelper();
        dbHelper.createData();
         */
        Thread t=new Thread(){
            @Override
            public void run() {
                try{
                    sleep(2000);
                    // Initialize Firebase Auth
                    mAuth = FirebaseAuth.getInstance();
                    // Display One-Tap Sign In if user isn't logged in
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    if(currentUser!=null){
                        System.out.println("User: "+currentUser);
                        Intent intent2 = new Intent(getApplicationContext(),DomainOptions.class);
                        startActivity(intent2);
                    }
                    else{
                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                    }

                    finish();
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }

            }
        };
        t.start();
    }
}
