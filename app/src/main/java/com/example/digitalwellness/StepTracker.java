package com.example.digitalwellness;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class StepTracker extends AppCompatActivity {
    private Boolean shouldServiceBeOn = false;
    private FirebaseHelper firebase;
    private MyPreference myPreference;
    private SensorEventListener eventListener;
    private SensorManager sensorManager;
    private String key;
    private String currentDate;
    private StepTrackerService service;
    private TextView textView;
    private long databaseValue = 0L;
    private long currentSensorValue;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steptracker);

        // Display back button.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Stop service.
        this.intent = new Intent(StepTracker.this, StepTrackerService.class);
        stopService(intent);

        // Show loading screen.
        FrameLayout progressLayout = (FrameLayout) findViewById(R.id.progress_overlay);
        progressLayout.setVisibility(View.VISIBLE);
        textView = (TextView) findViewById(R.id.number_of_steps);

        // Initialize classes.
        firebase = new FirebaseHelper();
        myPreference = new MyPreference(this, "Steps");

        currentDate = firebase.getCurrentDate();
        DatabaseReference ref = firebase.getStepsRef(currentDate);
        key = currentDate + firebase.getUid();

        // Update step count if user enables service.
        shouldServiceBeOn = new MyPreference(this, "permissions").getService();
        service = new StepTrackerService();
        if (shouldServiceBeOn) {
            myPreference.setCurrentStepCount(key, service.getStepCount());
        }

        // Load data
        MyCharts myCharts = new MyCharts(this);
        myCharts.showStepGraph(firebase.getSteps());

        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult().getValue() != null) {
                    databaseValue = (long) task.getResult().getValue();
                } else {
                    ref.setValue(0);
                }
                progressLayout.setVisibility(View.GONE);
                startTracker();
            }
        });
    }

    // Replaces service. Enables real time updates.
    private void startTracker() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (sensor != null) {
            eventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.values != null) {
                        currentSensorValue = (long) event.values[0];
                        long displayValue = currentSensorValue + databaseValue - myPreference.getPreviousTotalStepCount();
                        myPreference.setCurrentStepCount(key, displayValue);
                        textView.setText(String.valueOf(displayValue));
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
            sensorManager.registerListener(eventListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            Toast.makeText(this, "No motion sensor detected!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (shouldServiceBeOn) {
            startService(this.intent);
        } else {
//            sensorManager.unregisterListener(eventListener);
        }
        firebase.updateSteps(currentDate, myPreference.getCurrentStepCount(key));
        myPreference.setPreviousTotalStepCount(currentSensorValue);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (shouldServiceBeOn) {
            startService(this.intent);
        } else {
//            sensorManager.unregisterListener(eventListener);
        }
        firebase.updateSteps(currentDate, myPreference.getCurrentStepCount(key));
        myPreference.setPreviousTotalStepCount(currentSensorValue);
    }

    // Enables back button to be usable. Brings user back one page.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
