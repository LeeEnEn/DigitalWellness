package com.example.digitalwellness;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class ScreenTimeService extends Service {

    private BroadcastReceiver screenTimeBroadcastReceiver;
    private String NOTIFICATION_CHANNEL_ID = "1234";
    private String channelName = "STS";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "STS00";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Notification Channel Title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }

        Log.d("SERVICE", "Service Started");
        screenTimeBroadcastReceiver = new ScreenTimeBroadcastReceiver();
        IntentFilter lockFilter = new IntentFilter();
        lockFilter.addAction(Intent.ACTION_SCREEN_ON);
        lockFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenTimeBroadcastReceiver, lockFilter);
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
}
