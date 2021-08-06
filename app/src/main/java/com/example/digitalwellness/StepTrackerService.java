package com.example.digitalwellness;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;

public class StepTrackerService extends Service {

    private static int nStep;
    private static int sensorSteps;

    private int step;
    private FirebaseHelper firebase;
    private MyPreference myPreference;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        System.out.println("Service started");

        firebase = new FirebaseHelper();
        myPreference = new MyPreference(getApplicationContext(), "Steps");
        step = Math.max((int) firebase.getStepCount(),
                myPreference.getCurrentStepCount(firebase.getCurrentDate() + firebase.getUid()));

        startTracker();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void startTracker() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        int prevTotal = myPreference.getPreviousTotalStepCount();

        SensorEventListener eventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                sensorSteps = (int) event.values[0];
                nStep = sensorSteps + step - prevTotal;

                if (nStep >= 100) {
                    FirebaseHelper firebase = new FirebaseHelper();
                    if (!firebase.isUpdated()) {
                        firebase.setIsUpdated(true);
                    }
                }
                myPreference.setNSteps(nStep);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(eventListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public int getSteps() {
        return nStep;
    }

    public int getPreviousTotalSteps() {
        return sensorSteps;
    }

    public void resetNSteps() {
        nStep = 0;
    }
}
