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
    private static long val;
    private static String key;
    private MyPreference myPreference = null;
    private long databaseVal = 0L;
    private SensorManager sensorManager;
    private Sensor sensor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        System.out.println("Service started");
        FirebaseHelper firebase = new FirebaseHelper();
        String currentDate = firebase.getCurrentDate();
        key = currentDate + firebase.getUid();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        DatabaseReference ref = firebase.getStepsRef(currentDate);
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    databaseVal = (long) task.getResult().getValue();
                    startTracker();
                }
            }
        });


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        getSharedPreferences("Steps", MODE_PRIVATE).edit().putLong(key, val).apply();
        System.out.println("Service destroyed");
    }

    private void startTracker() {
        long previousCount = getSharedPreferences("Steps", MODE_PRIVATE).getLong(key, 0);
        SensorEventListener eventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                long stepCount = (long) event.values[0];
                val = stepCount + databaseVal - previousCount;
                getSharedPreferences("Steps", MODE_PRIVATE).edit().putLong(key, val).apply();

                if (myPreference == null && val >= 100) {
                    getSharedPreferences("Streak", MODE_PRIVATE)
                            .edit()
                            .putBoolean(String.valueOf(Calendar.DAY_OF_WEEK), true)
                            .apply();
                    getSharedPreferences("Streak", MODE_PRIVATE)
                            .edit()
                            .putBoolean("Today", true)
                            .apply();
                    myPreference = new MyPreference(StepTrackerService.this, "Streak");
                    System.out.println("in here");
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(eventListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
}
