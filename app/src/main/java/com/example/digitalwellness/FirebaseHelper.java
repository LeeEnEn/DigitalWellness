package com.example.digitalwellness;

import android.content.Context;
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
    private Date date = Calendar.getInstance().getTime();

    public FirebaseHelper() {
        this.auth = FirebaseAuth.getInstance();
    }

    public void setRef(String child) {
        ref = FirebaseDatabase.getInstance().getReference(child);
    }

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

    public void login(String username, String password, Context context) {
        auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Send to main page
                            Toast.makeText(context, "Login Successful!", Toast.LENGTH_LONG).show();
                            ref.push().child(date.toString()).setValue(username + " has logged in.");
                        } else {
                            Toast.makeText(context, "Username and password does not match!", Toast.LENGTH_LONG).show();
                            ref.push().child(date.toString()).setValue(username + " entered a wrong password ");
                        }
                    }
                });
    }
}
