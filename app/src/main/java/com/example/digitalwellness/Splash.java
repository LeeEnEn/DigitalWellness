package com.example.digitalwellness;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseHelper firebase = new FirebaseHelper();
        MyPreference myPreference = new MyPreference(this, "permissions");

        // User is logged in.
        if (firebase.isLoggedIn()) {
            // Start screen time services.
            if (myPreference.getScreenService()) {
                startService(new Intent(Splash.this, ScreenTimeService.class));
            }

            // Start work manager to periodically launch updates to firebase.
            firebase.startUpdates(this);
            // Fetch data, for graph creation.
            firebase.getData(this, new Intent(this, MainActivity.class));
        } else {
            // User is not logged in, send to login page.
            Intent intent = new Intent(this, StartMenu.class);
            startActivity(intent);
        }
    }
}