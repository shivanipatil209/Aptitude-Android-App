package com.example.aptitudeapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GoogleSignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SignInClient signInClient;
    Button startQuizButton,signOutButton;
    TextView status;
    MediaPlayer mp;
    boolean doubleBackToExistPressedOnce;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);
        mp=MediaPlayer.create(getApplicationContext(),R.raw.click);

        startQuizButton = (Button) findViewById(R.id.startQuiz);
        signOutButton = (Button) findViewById(R.id.signOutButton);

        status = (TextView) findViewById(R.id.status);



        startQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoogleSignInActivity.this, DomainOptions.class);
                startActivity(intent);
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }

    private void signOut() {
        // Firebase sign out

        mAuth.signOut();
        signInClient = Identity.getSignInClient(GoogleSignInActivity.this);
        // Google sign out
        signInClient.signOut().addOnCompleteListener(GoogleSignInActivity.this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(GoogleSignInActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
    }
    private void updateUI(FirebaseUser user) {

        if (user != null) {
            status.setText(getString(R.string.google_status_fmt, user.getEmail()));
            signOutButton.setVisibility(View.VISIBLE);
        } else {
            status.setText(R.string.signed_out);
            signOutButton.setVisibility(View.GONE);
        }
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
