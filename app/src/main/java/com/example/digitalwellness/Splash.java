package com.example.digitalwellness;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseHelper firebase = new FirebaseHelper();
        MyPreference myPreference = new MyPreference(this, "permissions");
        MyPreference friendsPreference = new MyPreference(this, "friends");

        // User is logged in.
        if (firebase.isLoggedIn()) {
            // Start screen time services.
            if (myPreference.getScreenService()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(Splash.this, ScreenTimeService.class));
                } else {
                    startService(new Intent(Splash.this, ScreenTimeService.class));
                }
            }

            /*Set<String> set = new HashSet<>();
            try {
                set.addAll(firebase.getAllUsersId());
                myPreference.updateFriends(set);
                Log.i("Friends", "Storing user data as friends successful");
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.i("Friends", "Storing user data as friends NOT successful");

            }*/

            MyAlarms myAlarms = new MyAlarms(this);
            myAlarms.startDailyUpdates();
            // Fetch data, for graph creation.
            firebase.getData(this, new Intent(this, MainActivity.class));
        } else {
            // User is not logged in, send to login page.
            Intent intent = new Intent(this, StartMenu.class);
            startActivity(intent);
        }
    }
}