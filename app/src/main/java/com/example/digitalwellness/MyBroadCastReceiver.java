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
                case1(intent.getStringExtra("uid"));
                break;
            case 2:
                case2(intent.getStringExtra("uid"));
                context.startService(new Intent(context, StepTrackerService.class));
                break;
        }
    }

    private void case1(String uid) {
        String previousDate = firebase.getPreviousDate();
        String key = previousDate + uid;
        firebase.updateSteps(previousDate, myPreference.getCurrentStepCount(key));
        firebase.updateScreen(previousDate, myPreference.getScreenTime(firebase.getPreviousDate()));
        System.out.println("case1: \n" + key);
        System.out.println(myPreference.getCurrentStepCount(key));
        System.out.println("normal updates done");
    }

    private void case2(String uid) {
        String currentDate = firebase.getCurrentDate();
        String key = currentDate + uid;
        StepTrackerService service = new StepTrackerService();
        myPreference.setCurrentStepCount(key, service.getStepCount());
        System.out.println("case2: \n" + key);
        System.out.println(service.getStepCount());
        firebase.updateSteps(currentDate, service.getStepCount());
        System.out.println("alarm service done");
    }
}