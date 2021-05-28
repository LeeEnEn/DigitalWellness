package com.example.digitalwellness;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import java.util.Calendar;

public class MyAlarms {
    private final AlarmManager alarmManager;
    private final Context context;

    public MyAlarms(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void startAlarm() {
        // Set the alarm to start at approximately 1:00 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 1);

        Intent alarmIntent = new Intent(this.context, MyBroadCastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, alarmIntent, 0);
//        this.alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, pendingIntent);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, pendingIntent);
    }
}
