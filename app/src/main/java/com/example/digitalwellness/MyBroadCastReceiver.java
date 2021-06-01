package com.example.digitalwellness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        FirebaseHelper firebase = new FirebaseHelper();
        MyPreference myPreference = new MyPreference(context, firebase.getUid());

        if (intent.getAction() != null) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                MyAlarms myAlarms = new MyAlarms(context);
                myAlarms.startAlarm();
                myPreference.setPreviousTotalStepCount(0);
            }
        }

        String key = firebase.getPreviousDate() + firebase.getUid();
        firebase.updateSteps(myPreference.getCurrentStepCount(key));
    }
}
