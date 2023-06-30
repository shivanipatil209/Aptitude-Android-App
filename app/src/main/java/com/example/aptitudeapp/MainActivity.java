package com.example.aptitudeapp;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView datetime;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;
    private FirebaseAuth mAuth;
    private SignInClient signInClient;
    MediaPlayer mysong;
    boolean doubleBackToExistPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        final MediaPlayer mp=MediaPlayer.create(getApplicationContext(),R.raw.click);
        mysong=MediaPlayer.create(getApplicationContext(),R.raw.study_music);
        mysong.setVolume(10,10);
        mysong.start();
        mysong.setLooping(true);

        datetime = (TextView) findViewById(R.id.datetime);
        Button email_signin_button = findViewById(R.id.emailSignInButton);
        Button google_signin_button = findViewById(R.id.googleSignButton);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("EEE, d MMM, yyyy        hh:mm aaa");
        date = dateFormat.format(new Date());
        datetime.setText(date);


        email_signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent intent1 = new Intent(MainActivity.this,EmailPasswordActivity.class );
                startActivity(intent1);
            }
        });

        google_signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });
        // Configure Google Sign In
        signInClient = Identity.getSignInClient(MainActivity.this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Display One-Tap Sign In if user isn't logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        System.out.println("User in main: "+currentUser);
        if (currentUser == null) {
            oneTapSignIn();
        }
    }
    public final ActivityResultLauncher<IntentSenderRequest> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    handleSignInResult(result.getData());
                }
            }
    );
    public void handleSignInResult(Intent data) {
        try {
            // Google Sign In was successful, authenticate with Firebase
            SignInCredential credential = signInClient.getSignInCredentialFromIntent(data);
            String idToken = credential.getGoogleIdToken();
            Log.d(TAG, "firebaseAuthWithGoogle:" + credential.getId());
            firebaseAuthWithGoogle(idToken);
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e);
        }
    }

    public void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(MainActivity.this, GoogleSignInActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void googleSignIn() {
        GetSignInIntentRequest signInRequest = GetSignInIntentRequest.builder()
                .setServerClientId(getString(R.string.default_web_client_id))
                .build();

        signInClient.getSignInIntent(signInRequest)
                .addOnSuccessListener(new OnSuccessListener<PendingIntent>() {
                    @Override
                    public void onSuccess(PendingIntent pendingIntent) {
                        launchSignIn(pendingIntent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Google Sign-in failed", e);
                    }
                });
    }

    public void oneTapSignIn() {
        // Configure One Tap UI
        BeginSignInRequest oneTapRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(getString(R.string.default_web_client_id))
                                .setFilterByAuthorizedAccounts(true)
                                .build()
                )
                .build();

        // Display the One Tap UI
        signInClient.beginSignIn(oneTapRequest)
                .addOnSuccessListener(new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult beginSignInResult) {
                        launchSignIn(beginSignInResult.getPendingIntent());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                    }
                });
    }

    public void launchSignIn(PendingIntent pendingIntent) {
        try {
            IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(pendingIntent)
                    .build();
            signInLauncher.launch(intentSenderRequest);
        } catch (Exception e) {
            Log.e(TAG, "Couldn't start Sign In: " + e.getLocalizedMessage());
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onBackPressed() {
        if(doubleBackToExistPressedOnce) {
            finishAffinity();
            mysong.stop();
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
