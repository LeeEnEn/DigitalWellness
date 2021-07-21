package com.example.digitalwellness;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class StartMenu extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    /**
     * For Facebook Implementation
     */
    private static final String EMAIL = "EMAIL";

    /**
     * Callback Manager for Facebook
     */
    private CallbackManager callbackManager;

    /**
     * Firebase Helper
     */
    private FirebaseHelper firebaseHelper;

    /**
     * Google Sign In Options
     *
     */
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);
        getSupportActionBar().hide();

        /**
         * Initialise Firebase helper
         */
        firebaseHelper = new FirebaseHelper();

        /**
         * CallbackManager for Facebook Login
         */
        callbackManager = CallbackManager.Factory.create();

        /**
         * Implementation for Google Login
         */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        final Button regButton = findViewById(R.id.mainRegister);
        final Button loginButton = findViewById(R.id.mainLogin);
        final Button facebookButton = findViewById(R.id.facebookreg);
        final Button googleButton = findViewById(R.id.googlereg);
        final LoginButton facebookLoginButton = (LoginButton) findViewById(R.id.facebook_login);
        facebookLoginButton.setReadPermissions("email", "public_profile");
        final Button forgetPassword = findViewById(R.id.forget_password);

        /**
         * Button listener for users who are already registered
         */
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
            }
        });

        // Button listener for users signing up with EMAIL.
        regButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Registration.class);
                startActivity(i);
            }
        });

        // Facebook Button Initialisation
        facebookLoginButton.setReadPermissions("email", "public_profile");
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FACEBOOK", "facebook:onSuccess:" + loginResult);
                Profile profile = Profile.getCurrentProfile();
                handleFacebookAccessToken(loginResult.getAccessToken());
                loginResult.getAccessToken();
                //firebaseHelper.registerEmailAndPassword(firebaseHelper.getUser().getEmail(), "12345", StartMenu.this);
                //Toast.makeText(getApplicationContext(),"Welcome back, " + firebaseHelper.getUser().getDisplayName(),Toast. LENGTH_SHORT).show();
                MyAlarms myAlarms = new MyAlarms(StartMenu.this);
                myAlarms.startDailyUpdates();
                firebaseHelper.getData();
                firebaseHelper.createStreakData(StartMenu.this);
                Intent intent = new Intent(StartMenu.this, MainActivity.class);
                firebaseHelper.createDelay(1000, StartMenu.this, intent);
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
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resetPasswordIntent = new Intent(StartMenu.this, PasswordReset.class);
                startActivity(resetPasswordIntent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (firebaseHelper.isLoggedIn()) {
            Intent intent = new Intent(this, MainActivity.class);
            Toast.makeText(getApplicationContext(),"Welcome back, " + firebaseHelper.getUser().getDisplayName(),Toast. LENGTH_SHORT).show();
            this.startActivity(intent);
        }
        //updateUI(currentUser);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Google
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("GOOGLE", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("GOOGLE", "Google sign in failed", e);
            }

        }
        //FACEBOOK
        else {
            // Pass the activity result back to the facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
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

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseHelper.getFirebaseAuth().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("GOOGLE", "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            firebaseHelper.setDetails(user.getDisplayName(), user.getEmail(), user.getPhotoUrl(), user.getUid());
                            //Toast.makeText(getApplicationContext(),"Welcome back, " + user.getDisplayName(),Toast. LENGTH_SHORT).show();
                            MyAlarms myAlarms = new MyAlarms(StartMenu.this);
                            myAlarms.startDailyUpdates();
                            firebaseHelper.updateProfile(user.getDisplayName(), "Account created!", StartMenu.this);
                            firebaseHelper.createDailyData();
                            firebaseHelper.createStreakData(StartMenu.this);
                            Intent intent = new Intent(StartMenu.this, MainActivity.class);
                            startActivity(intent);
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("GOOGLE", "signInWithCredential:failure", task.getException());
                            //updateUI(null);
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}