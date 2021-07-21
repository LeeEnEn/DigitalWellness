package com.example.digitalwellness;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;


public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseHelper firebase = new FirebaseHelper();
        MyPreference myPreference = new MyPreference(this, "permissions");
        MyPreference friendsPreference = new MyPreference(this, "friends");

        boolean networkAvailable = CheckNetwork.isInternetAvailable(this);

        if (networkAvailable) {
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
                firebase.getData();
                firebase.createStreakData(Splash.this);
                Intent intent = new Intent(Splash.this, MainActivity.class);
                firebase.createDelay(2500, Splash.this, intent);
            } else {
                toStartMenu();
            }
        } else {
            toStartMenu();
        }
    }

    private void toStartMenu() {
        // User is not logged in, send to login page.
        Intent intent = new Intent(this, StartMenu.class);
        startActivity(intent);
    }
}