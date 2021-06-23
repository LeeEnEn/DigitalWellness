package com.example.digitalwellness;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseHelper firebase = new FirebaseHelper();
        MyPreference myPreference = new MyPreference(this, "permissions");

        if (firebase.isLoggedIn()) {

            if (myPreference.getScreenService()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(Splash.this, ScreenTimeService.class));
                } else {
                    startService(new Intent(Splash.this, ScreenTimeService.class));
                }
            }
            firebase.getData(this, new Intent(this, MainActivity.class));
        } else {
            Intent intent = new Intent(this, StartMenu.class);
            startActivity(intent);
        }
    }
}