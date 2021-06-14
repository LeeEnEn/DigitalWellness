package com.example.digitalwellness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadCastReceiver extends BroadcastReceiver {
    private FirebaseHelper firebase;
    private MyPreference myPreference;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        firebase = new FirebaseHelper();
        myPreference  = new MyPreference(context, "Steps");

        if (intent.getAction() != null) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                MyAlarms myAlarms = new MyAlarms(context);
                myAlarms.startUpdateAlarm();
                myPreference.setPreviousTotalStepCount(0);
            }
        }

        switch (intent.getIntExtra("code", 0)) {
            case 1:
                dailyUpdates(intent.getStringExtra("uid"));
                break;
            case 2:
                serviceUpdates(intent.getStringExtra("uid"));
                break;
        }
    }

    private void dailyUpdates(String uid) {
        String previousDate = firebase.getPreviousDate();
        String key = previousDate + uid;
        firebase.updateSteps(previousDate, myPreference.getCurrentStepCount(key));
        firebase.updateScreen(previousDate, myPreference.getScreenTime(previousDate));

        System.out.println("normal updates done");
    }

    private void serviceUpdates(String uid) {
        String currentDate = firebase.getCurrentDate();
        String key = currentDate + uid;
        long value = myPreference.getCurrentStepCount(key);
        firebase.updateSteps(currentDate, value);
        myPreference.setPreviousTotalStepCount(value);

        System.out.println("alarm service done");
    }
}