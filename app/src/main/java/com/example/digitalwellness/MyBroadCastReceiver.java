package com.example.digitalwellness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadCastReceiver extends BroadcastReceiver {

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        if (intent.getAction() != null) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                // Set previous total to zero.
                MyPreference steps  = new MyPreference(context, "Steps");
                steps.setPreviousTotalStepCount(0);
                // Start alarm.
                MyAlarms alarms = new MyAlarms(context);
                alarms.startDailyUpdates();
            }
        }

        int code = intent.getIntExtra("code", 0);

        if (code == 7) {
            MyPreference servicePref = new MyPreference(context, "Service");

            int steps;
            int previousTotalSteps;

            StepTracker stepTracker = new StepTracker();

            if (servicePref.getService()) {
                StepTrackerService stepService = new StepTrackerService();
                steps = stepService.getSteps();
                previousTotalSteps = stepService.getPreviousTotalSteps();
                // Restart service.
                Intent serviceIntent = new Intent(context, StepTrackerService.class);
                context.stopService(serviceIntent);
                context.startService(serviceIntent);
                stepService.resetNSteps();
            } else {
                steps = stepTracker.getSteps();
                previousTotalSteps = stepTracker.getPreviousTotalSteps();
            }

            stepTracker.resetNStep();
            update(steps, previousTotalSteps);
            // Restart alarm.
            MyAlarms myAlarms = new MyAlarms(context);
            myAlarms.startDailyUpdates();
        }
    }

    private void update(int step, int prevTotalSteps) {
        FirebaseHelper firebase = new FirebaseHelper();
        String prevDate = firebase.getPreviousDate();

        MyPreference screenPref = new MyPreference(context, firebase.getUid());
        long screen = screenPref.getScreenTime(prevDate);

        firebase.updateSteps(prevDate, step);
        firebase.updateScreen(prevDate, screen);
        firebase.setNotUpdated();

        MyPreference stepPref = new MyPreference(context, "Steps");
        stepPref.setPreviousTotalStepCount(prevTotalSteps);
        stepPref.setCurrentStepCount(prevDate + firebase.getUid(), step);
        stepPref.setNSteps(0);

        firebase.checkForMissedDays();
        firebase.createDailyData();
    }
}