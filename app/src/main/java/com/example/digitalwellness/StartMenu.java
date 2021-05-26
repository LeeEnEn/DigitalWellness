package com.example.digitalwellness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class StartMenu extends AppCompatActivity {

    /**
     * For Facebook Implementation
     */
    private static final String EMAIL = "EMAIL";

    /**
     * Callback Manager for Facebook
     */
    private CallbackManager callbackManager;

    /**
     * Firebase Authentication
     */
    //private FirebaseHelper firebaseHelper;

    /**
     * Firebase Helper
     */
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        getSupportActionBar().hide();

        /**
         * CallbackManager for Facebook Login
         */
        callbackManager = CallbackManager.Factory.create();

        firebaseHelper = new FirebaseHelper();
        //mAuth = FirebaseAuth.getInstance();

        /**
         * Note: Facebook Button currently not in use. Currently using Facebook's implementation
         */
        final Button regButton = findViewById(R.id.mainRegister);
        final Button loginButton = findViewById(R.id.mainLogin);
        final Button facebookButton = findViewById(R.id.facebookreg);
        final Button googleButton = findViewById(R.id.googlereg);
        final LoginButton facebookLoginButton = (LoginButton) findViewById(R.id.facebook_login);
        facebookLoginButton.setReadPermissions("email", "public_profile");

        /**
         * Button listener for users who are already registered
         */
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
            }
        });

        /**
         * Button listener for users signing up with EMAIL.
         */
        regButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Registration.class);
                startActivity(i);
            }
        });


        /**
         * Facebook Button Initialisation
         */
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FACEBOOK", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                firebaseHelper.registerEmailAndPassword(firebaseHelper.getUser().getEmail(), "12345", StartMenu.this);
            }

            @Override
            public void onCancel() {
                Log.d("FACEBOOK", "facebook:onCancel:");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("FACEBOOK", "facebook:onError", exception);
            }
        });


        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookLoginButton.performClick();
            }
        });

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseHelper.getUser();
        if (firebaseHelper.isLoggedIn()) {
            Toast.makeText(getApplicationContext(), "User is logged in", Toast.LENGTH_SHORT).show();
        }
        //updateUI(currentUser);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // Disables user to go back to splash screen.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Handling Facebook Access
     *
     * @param token
     */
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("FACEBOOK", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseHelper.getFirebaseAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FACEBOOK", "signInWithCredential:success");
                            FirebaseUser user = firebaseHelper.getUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FACEBOOK", "signInWithCredential:failure", task.getException());
                            //updateUI(null);
                        }
                    }
                });
    }
}