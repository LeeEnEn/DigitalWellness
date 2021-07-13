package com.example.digitalwellness;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
// Class not needed, shifted to UploadWorker
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
     * An alarm that wakes the phone to update current day's step to shared preference.
     */
    public void startDailyUpdates() {
        // Set the alarm to start at exactly 12.00
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(this.context, MyBroadCastReceiver.class);
        alarmIntent.putExtra("code", 7);
        alarmIntent.putExtra("date", firebase.getCurrentDate());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 1, alarmIntent, 0);

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        System.out.println("update to shared pref alarm started");
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
