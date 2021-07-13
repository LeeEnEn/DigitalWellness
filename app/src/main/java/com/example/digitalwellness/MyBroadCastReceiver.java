package com.example.digitalwellness;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
            System.out.println("hohohoho");
            MyPreference servicePref = new MyPreference(context, "Service");
            String date = intent.getStringExtra("date");
            int steps;
            int previousTotalSteps;

            if (servicePref.getService()) {
                StepTrackerService stepService = new StepTrackerService();
                steps = stepService.getSteps();
                previousTotalSteps = stepService.getPreviousTotalSteps();
                // Restart service.
                Intent serviceIntent = new Intent(context, StepTrackerService.class);
                context.stopService(serviceIntent);
                context.startService(serviceIntent);
            } else {
                StepTracker stepTracker = new StepTracker();
                steps = stepTracker.getSteps();
                previousTotalSteps = stepTracker.getPreviousTotalSteps();
            }
            update(date, steps, previousTotalSteps);
            // Restart alarm.
            MyAlarms myAlarms = new MyAlarms(context);
            myAlarms.startDailyUpdates();
        }
    }

    private void update(String date, int step, int prevTotalSteps) {
        FirebaseHelper firebase = new FirebaseHelper();
        firebase.updateSteps(date, step);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ASDASD");
        reference.setValue("ADASDAS");
        MyPreference stepPref = new MyPreference(context, "Steps");
        stepPref.setPreviousTotalStepCount(prevTotalSteps);

        MyPreference streakPref = new MyPreference(context, "Streak");
        streakPref.setStreak("isUpdated", false);

        firebase.createDailyData(date);
        System.out.println("hdhasdasda");
    }
}