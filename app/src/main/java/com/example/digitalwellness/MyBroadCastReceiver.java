package com.example.digitalwellness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                MyAlarms myAlarms = new MyAlarms(context);
                myAlarms.startAlarm();
            }
        }
        FirebaseHelper firebase = new FirebaseHelper();

        MyPreference myPreference = new MyPreference(context);
        long previousStep = myPreference.getPreviousTotal() + myPreference.getStepCount(firebase.getPreviousDate());
        myPreference.setPreviousTotal(previousStep);

        firebase.updateStepsToDB(context);
    }
}
