package com.example.digitalwellness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadCastReceiver extends BroadcastReceiver {
    private FirebaseHelper firebase;
    private MyPreference myPreference;
    private Context context;
    private String previousDate;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        firebase = new FirebaseHelper();
        myPreference  = new MyPreference(context, "Steps");
        previousDate = firebase.getPreviousDate();

        if (intent.getAction() != null) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                MyAlarms myAlarms = new MyAlarms(context);
                myAlarms.startDailyUpdates();
                myAlarms.startUpdateToFirebase();
                myPreference.setPreviousTotalStepCount(0);
            }
        }

        switch (intent.getIntExtra("code", 0)) {
            case 1:
                updateToFirebase(intent.getStringExtra("uid"));
                break;
            case 2:
                dailyUpdates(intent.getStringExtra("uid"));
                break;
        }
    }

    private void updateToFirebase(String uid) {
        previousDate = firebase.getPreviousDate();
        String key = previousDate + uid;
        firebase.updateSteps(previousDate, myPreference.getCurrentStepCount(key));
        firebase.updateScreen(previousDate, myPreference.getScreenTime(previousDate));

        System.out.println("normal updates done");
    }

    private void dailyUpdates(String uid) {
        previousDate = firebase.getPreviousDate();
        String key =  previousDate + uid;
        long value = myPreference.getCurrentStepCount(key);
        // Update steps to database.
        firebase.updateSteps(previousDate, value);
        // Create basic data for the next day.
        firebase.createBasicData(context);
        myPreference.setPreviousTotalStepCount(value);
        // Reset streak
        MyPreference streak = new MyPreference(context, "Streak");
        boolean today = streak.getStreak("Today");
        streak.setStreak("Yesterday", today);
        streak.setStreak("Today", false);
        // Restart service.
        context.stopService(new Intent(context, StepTrackerService.class));
        context.startService(new Intent(context, StepTrackerService.class));

        System.out.println("alarm service done");
    }
}