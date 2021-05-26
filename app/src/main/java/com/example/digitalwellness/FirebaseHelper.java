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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
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
    public void setRef(String child) {
        ref = FirebaseDatabase.getInstance().getReference(child);
    }

    /**
     * Register user through valid email and password.
     *
     * @param username Username of user.
     * @param password Password of user.
     * @param context Current context.
     */
    public void registerEmailAndPassword(String username, String password, Context context) {
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
            createAccount(username, password, context);
        }
    }

    /**
     * Create user in Firebase.
     *
     * @param email User's email.
     * @param password User's password.
     * @param context Current context.
     */
    private void createAccount(String email, String password, Context context) {
        // [START create_user_with_email]
        this.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            ref.push().child(date.toString()).setValue("Account created under: " + email);
                            FirebaseUser user = auth.getCurrentUser();
                            Toast.makeText(context, "User created",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Failure", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
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
                            ref.push().child(date.toString()).setValue(username + " has logged in.");
                            Intent intent = new Intent(context, MainActivity.class);
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Username and password does not match!", Toast.LENGTH_LONG).show();
                            ref.push().child(date.toString()).setValue(username + " entered a wrong password ");
                        }
                    }
                });
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
}
