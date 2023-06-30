package com.example.aptitudeapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailPasswordActivity extends AppCompatActivity {

    Button login_button,register_button,verifyEmailButton,skipButton,signOutButton;
    Group emailPasswordButtons,signedInButtons;
    String EmailHolder,PasswordHolder;
    EditText email, password;
    TextView status;
    Group emailPasswordFields;
    boolean doubleBackToExistPressedOnce;
    private FirebaseAuth mAuth;
    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        mp=MediaPlayer.create(getApplicationContext(),R.raw.click);

        login_button = (Button) findViewById(R.id.login);
        register_button = (Button) findViewById(R.id.emailCreateAccountButton);
        verifyEmailButton = (Button) findViewById(R.id.verifyEmailButton);
        skipButton = (Button) findViewById(R.id.skipButton);
        signOutButton = (Button) findViewById(R.id.signOutButton);
        email = (EditText) findViewById(R.id.editEmailAddress);
        password = (EditText) findViewById(R.id.editPassword);
        status = (TextView) findViewById(R.id.status);
        emailPasswordButtons = (Group) findViewById(R.id.emailPasswordButtons);
        signedInButtons = (Group) findViewById(R.id.signedInButtons);

        emailPasswordFields = (Group) findViewById(R.id.emailPasswordFields);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                EmailHolder = email.getText().toString();
                PasswordHolder = password.getText().toString();
                signIn(EmailHolder, PasswordHolder);
            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                EmailHolder = email.getText().toString();
                PasswordHolder = password.getText().toString();
                createAccount(EmailHolder, PasswordHolder);
            }
        });

        verifyEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailVerification();
            }
        });
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmailPasswordActivity.this, DomainOptions.class);
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

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }
    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }


    private boolean validateForm() {
        boolean valid = true;

        EmailHolder = email.getText().toString();
        PasswordHolder = password.getText().toString();
        if (TextUtils.isEmpty(EmailHolder)) {
            email.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }


        if (TextUtils.isEmpty(PasswordHolder)) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        if (!task.isSuccessful()) {
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void sendEmailVerification() {
        // Disable button
       verifyEmailButton.setEnabled(false);

        // Send verification email
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Re-enable button
                        verifyEmailButton.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(EmailPasswordActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EmailPasswordActivity.this, DomainOptions.class);
                            startActivity(intent);
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(EmailPasswordActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void updateUI(FirebaseUser user) {

        if (user != null) {

            status.setVisibility(View.VISIBLE);
            status.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            emailPasswordButtons.setVisibility(View.GONE);
            emailPasswordFields.setVisibility(View.GONE);
            signedInButtons.setVisibility(View.VISIBLE);

            if (user.isEmailVerified()) {
                verifyEmailButton.setVisibility(View.GONE);
                skipButton.setText("Start Quiz");
            } else {
                verifyEmailButton.setVisibility(View.VISIBLE);
            }
        } else {
            status.setVisibility(View.VISIBLE);
            status.setText(R.string.signed_out);
            emailPasswordButtons.setVisibility(View.VISIBLE);
            emailPasswordFields.setVisibility(View.VISIBLE);
            signedInButtons.setVisibility(View.GONE);
        }
    }
    private void reload() {
        mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateUI(mAuth.getCurrentUser());
                    Toast.makeText(EmailPasswordActivity.this,
                            "Reload successful!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "reload", task.getException());
                    Toast.makeText(EmailPasswordActivity.this,
                            "Failed to reload user.",
                            Toast.LENGTH_SHORT).show();
                }
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
        Intent intent = new Intent(EmailPasswordActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
