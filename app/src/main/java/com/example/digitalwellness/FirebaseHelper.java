package com.example.digitalwellness;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirebaseHelper {
    private final FirebaseAuth auth;
    private DatabaseReference ref;
    private final Date date = Calendar.getInstance().getTime();

    /**
     * Public constructor.
     */
    public FirebaseHelper() {
        this.auth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getFirebaseAuth() {
        return this.auth;
    }

    /**
     * Set database reference.
     *
     * @param child The child to update value.
     */
    public DatabaseReference setRef(String child) {
        this.ref = FirebaseDatabase.getInstance().getReference(child);
        return this.ref;
    }

    /**
     * Set database reference to child.
     *
     * @param child Set database reference to child.
     * @return Database reference to said child.
     */
    public DatabaseReference child(String child) {
        return this.ref.child(child);
    }

    /**
     * Returns user id.
     *
     * @return User id.
     */
    public String getUid() {
        return getUser().getUid();
    }

    /**
     * Returns current date in dd-MM-yyyy format.
     * @return current date.
     */
    public String getDate() {
        return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(this.date);
    }

    /**
     * Method called to get user data
     * @return Firebase User, containing users email etc
     */
    public FirebaseUser getUser() {
        return auth.getCurrentUser();
    }

    /**
     * Method called to check status of user logged in
     * @return boolean to check if user is logged in
     */
    public boolean isLoggedIn() {
        return this.getUser() != null ;
    }

    /**
     * Register user through valid email and password.
     *
     * @param username Username of user.
     * @param password Password of user.
     * @param context Current context.
     */
    public void registerEmailAndPassword(String username, String password, String name, Context context) {
        String emailRegSample = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegSample);
        Matcher matcher = pattern.matcher(username);

        if (!matcher.find()) {
            Toast.makeText(context, "Please enter a valid email!",
                    Toast.LENGTH_LONG).show();
        } else if (password.isEmpty() || password.length() < 8) {
            Toast.makeText(context, "Password should contain at least 8 characters!",
                    Toast.LENGTH_LONG).show();
        } else {
            createAccount(username, password, name, context);
        }
    }

    /**
     * Create user in Firebase.
     *
     * @param email User's email.
     * @param password User's password.
     * @param context Current context.
     */
    private void createAccount(String email, String password, String name, Context context) {
        // [START create_user_with_email]
        this.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // ref.push().child(date.toString()).setValue("Account created under: " + email);
                            updateProfile(name, "Account created!", context);
                            Intent intent = new Intent(context, MainActivity.class);
                            context.startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w("Failure", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(context, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /**
     * Sign in using email and password.
     *
     * @param username Username of user.
     * @param password Password of user.
     * @param context Current context.
     */
    public void login(String username, String password, Context context) {
        auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Login Successful!", Toast.LENGTH_LONG).show();
                            // ref.push().child(date.toString()).setValue(username + " has logged in.");
                            Intent intent = new Intent(context, MainActivity.class);
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Username and password does not match!", Toast.LENGTH_LONG).show();
                            // ref.push().child(date.toString()).setValue(username + " entered a wrong password ");
                        }
                    }
                });
    }

    /**
     * Update user's profile.
     *
     * @param name Name to be updated.
     * @param toastText Text to be displayed.
     * @param context Current context.
     */
    public void updateProfile(String name, String toastText, Context context) {
        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        getUser().updateProfile(profile)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Updating profile failed! Please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
