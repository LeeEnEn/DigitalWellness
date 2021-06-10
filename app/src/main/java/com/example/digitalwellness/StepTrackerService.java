package com.example.digitalwellness;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class StepTrackerService extends Service {
    private static long stepCount;
    private static String key;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        System.out.println("Service started");
        FirebaseHelper firebase = new FirebaseHelper();
        key = firebase.getCurrentDate() + firebase.getUid();
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        SensorEventListener eventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                stepCount = (long) event.values[0];
                getSharedPreferences("Steps", MODE_PRIVATE).edit().putLong(key, stepCount).apply();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(eventListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        getSharedPreferences("Steps", MODE_PRIVATE).edit().putLong(key, stepCount).apply();
        System.out.println("Service destroyed");
    }

    public long getStepCount() {
        return stepCount;
    }
}
