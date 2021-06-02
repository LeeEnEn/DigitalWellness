package com.example.digitalwellness;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.NoCopySpan;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class ScreenTimeBroadcastReceiver extends BroadcastReceiver {

    private long startTimer;
    private long endTimer;
    private long screenOnTimeSingle;
    private long screenOnTime;
    private final long TIME_ERROR = 1000;
    private NotificationCompat.Builder builder;

    public void onReceive(Context context, Intent intent) {
        Log.i("SCREEN TIME RECEIVER", "ScreenTimeService onReceive");

        builder = new NotificationCompat.Builder(context, "CHANNEL_ID")
                .setSmallIcon(R.drawable.digitalwellnesslogo)
                .setContentTitle("Test")
                .setContentText("This function is working")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            Log.i("SCREEN TIME RECEIVER", "ScreenTimeService ON");
            startTimer = System.currentTimeMillis();
        }
        else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            Log.i("SCREEN TIME RECEIVER", "ScreenTimeService OFF");
            endTimer = System.currentTimeMillis();
            screenOnTimeSingle = endTimer - startTimer;
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, builder.build());


            if(screenOnTimeSingle < TIME_ERROR) {
                screenOnTime += screenOnTime;
            }

        }
    }
}