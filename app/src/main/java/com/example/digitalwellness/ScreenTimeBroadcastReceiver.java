package com.example.digitalwellness;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.TimeUnit;

public class ScreenTimeBroadcastReceiver extends BroadcastReceiver {

    private String KEY_SCREEN = "Screen";
    private long startTimer;
    private long endTimer;
    private long screenOnTimeSingle;
    private long screenOnTime;
    private final long TIME_ERROR = 10;
    private NotificationCompat.Builder builder;
    private FirebaseHelper firebaseHelper;
    private MyPreference myPreference;


    public void onReceive(Context context, Intent intent) {
        //Log.i("SCREEN TIME RECEIVER", "ScreenTimeService onReceive");
        /*builder = new NotificationCompat.Builder(context, "CHANNEL_ID")
                .setSmallIcon(R.drawable.digitalwellnesslogo)
                .setContentTitle("Test")
                .setContentText("This function is working")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);*/

        firebaseHelper = new FirebaseHelper();
        myPreference = new MyPreference(context, firebaseHelper.getUid());

        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.i("SCREEN TIME RECEIVER", "ScreenTimeService ON");
            startTimer = System.currentTimeMillis();
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.i("SCREEN TIME RECEIVER", "ScreenTimeService OFF");
            endTimer = System.currentTimeMillis();
            screenOnTimeSingle = endTimer - startTimer;
            //screenOnTime = screenOnTime + screenOnTimeSingle;
            Log.i("SCREEN TIME RECEIVER", "Time spent: " +
                    String.format("%d sec", TimeUnit.MILLISECONDS.toSeconds(screenOnTimeSingle)));

            long screenOnTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(screenOnTimeSingle);

            if (screenOnTimeSeconds < 86400) {
                myPreference.updateScreenTime(firebaseHelper.getCurrentDate(), screenOnTimeSeconds);
            }

            Log.i("SCREEN TIME RECEIVER", String.valueOf(myPreference.getScreenTime(firebaseHelper.getCurrentDate())));
        }
    }
}