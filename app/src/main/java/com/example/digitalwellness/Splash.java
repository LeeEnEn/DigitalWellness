package com.example.digitalwellness;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;


public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseHelper firebase = new FirebaseHelper();

        if (firebase.isLoggedIn()) {
            firebase.getData(this, new Intent(this, MainActivity.class));
        } else {
            Intent intent = new Intent(this, StartMenu.class);
            startActivity(intent);
        }
    }
}