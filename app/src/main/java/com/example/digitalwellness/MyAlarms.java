package com.example.digitalwellness;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import java.util.Calendar;

public class MyAlarms {
    private static AlarmManager alarmManagerForService;
    private static PendingIntent intentForService;
    private final Context context;
    private final FirebaseHelper firebase;

    public MyAlarms(Context context) {
        this.context = context;
        this.firebase = new FirebaseHelper();
    }

    /**
     * An alarm that updates the previous day's step to database.
     */
    public void startUpdateAlarm() {
        // Set the alarm to start at approximately 1:00 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND,0);

        AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(this.context, MyBroadCastReceiver.class);
        alarmIntent.putExtra("code", 1);
        alarmIntent.putExtra("uid", firebase.getUid());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, alarmIntent, 0);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 60000, pendingIntent);
        System.out.println("Update alarm started");
    }

    /**
     * An alarm that wakes the phone to update current day's step to shared preference.
     */
    public void startServiceAlarm() {
        // Set the alarm to start at exactly 11.55
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 55);
        calendar.set(Calendar.SECOND, 0);

        alarmManagerForService = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(this.context, MyBroadCastReceiver.class);
        alarmIntent.putExtra("code", 2);
        alarmIntent.putExtra("uid", firebase.getUid());

        intentForService = PendingIntent.getBroadcast(this.context, 1, alarmIntent, 0);

        alarmManagerForService.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, intentForService);
        System.out.println("Service alarm started");
//        alarmManagerForService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 60000, intentForService);
    }

    /**
     * Cancel alarm if user decides not to allow step tracker to run in the background.
     */
    public void cancelServiceAlarm() {
        System.out.println("alarm service cancelled");
        alarmManagerForService.cancel(intentForService);
    }
}
