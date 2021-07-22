package com.example.digitalwellness;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ScreenTimeService extends Service {

    private BroadcastReceiver screenTimeBroadcastReceiver;
    private String NOTIFICATION_CHANNEL_ID = "1234";
    private String channelName = "STS";
    private int NUM_OF_SECONDS_PER_DAY;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();


        FirebaseHelper firebaseHelper = new FirebaseHelper();
        MyPreference myPreference = new MyPreference(this, firebaseHelper.getUid());

        NUM_OF_SECONDS_PER_DAY = myPreference.getScreenLimit();
        NUM_OF_SECONDS_PER_DAY = NUM_OF_SECONDS_PER_DAY * 60 * 60;

        long currenttime = myPreference.getScreenTime(firebaseHelper.getCurrentDate());
        long progress = (currenttime * 100) / NUM_OF_SECONDS_PER_DAY;


        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "STS00";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Background Service",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }

        //Log.d("SERVICE", "Service Started");
        screenTimeBroadcastReceiver = new ScreenTimeBroadcastReceiver();
        IntentFilter lockFilter = new IntentFilter();
        lockFilter.addAction(Intent.ACTION_SCREEN_ON);
        lockFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenTimeBroadcastReceiver, lockFilter);


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Screen Limit", "Screen Limit", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        if (progress < 100) {
            buildNotification();
        } else {
            buildWarningNotification();
        }

    }

    @Override
    public void onDestroy() {
        unregisterReceiver(screenTimeBroadcastReceiver);
        Log.d("SERVICE", "Service Stopped");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void buildWarningNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Screen Limit");
        builder.setContentTitle("Screen Limit");
        builder.setContentText("You have exceeded your screen usage timing. We strongly encourage you to take a break.");
        builder.setSmallIcon(R.drawable.heart);
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(ScreenTimeService.this);
        managerCompat.notify(1, builder.build());
    }
    public void buildNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Screen Limit");
        builder.setContentTitle("Screen Limit");
        builder.setContentText("Your screen usage is within your limits :)\nDo take a break once in awhile");
        builder.setSmallIcon(R.drawable.heart);
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(ScreenTimeService.this);
        managerCompat.notify(1, builder.build());
    }
}