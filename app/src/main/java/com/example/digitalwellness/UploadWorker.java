package com.example.digitalwellness;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UploadWorker extends Worker {

    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Do work here
        myWork();
        return Result.success();
    }

    private void myWork() {
        FirebaseHelper firebase = new FirebaseHelper();
        MyPreference streakPreference = new MyPreference(getApplicationContext(), "Streak");
        MyPreference preference = new MyPreference(getApplicationContext(), "Steps");

        String ytd = firebase.getPreviousDate();
        String uid = firebase.getUid();
        String key = ytd + uid;
        // Upload data to firebase
        firebase.updateSteps(ytd, preference.getCurrentStepCount(key));
        firebase.updateScreen(ytd, preference.getScreenTime(ytd));
        // Restart service
        Intent intent = new Intent(getApplicationContext(), StepTrackerService.class);
        getApplicationContext().stopService(intent);
        getApplicationContext().startService(intent);
        // Reset streak
        boolean today = streakPreference.getStreak("Today");
        streakPreference.setStreak("Yesterday", today);
        streakPreference.setStreak("Today", false);
        // Create basic data
        firebase.createBasicData(getApplicationContext());
        // Reload data
        firebase.getData();
    }
}
