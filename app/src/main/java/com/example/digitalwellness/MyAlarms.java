package com.example.digitalwellness;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import java.util.Calendar;
import java.util.Date;

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
    public void startUpdateToFirebase() {
        // Set the alarm to start at approximately 1:00 a.m.
        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 28);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(this.context, MyBroadCastReceiver.class);
        alarmIntent.putExtra("code", 1);
        alarmIntent.putExtra("uid", firebase.getUid());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, alarmIntent, 0);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 60000, pendingIntent);
        System.out.println("Update to firebase alarm started");
    }

    /**
     * An alarm that wakes the phone to update current day's step to shared preference.
     */
    public void startDailyUpdates() {
        // Set the alarm to start at exactly 12.00
        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 28);
        calendar.set(Calendar.SECOND, 0);

        alarmManagerForService = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(this.context, MyBroadCastReceiver.class);
        alarmIntent.putExtra("code", 2);
        alarmIntent.putExtra("uid", firebase.getUid());

        intentForService = PendingIntent.getBroadcast(this.context, 1, alarmIntent, 0);

        alarmManagerForService.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, intentForService);
        System.out.println("update to shared pref alarm started");
//        alarmManagerForService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 60000, intentForService);
    }

    public void startAlarmAtTime(int hour, int minute) {

        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        alarmManagerForService = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(this.context, MyBroadCastReceiver.class);
        intentForService = PendingIntent.getBroadcast(this.context, 1, alarmIntent, 0);
        alarmManagerForService.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intentForService);
    }
}
