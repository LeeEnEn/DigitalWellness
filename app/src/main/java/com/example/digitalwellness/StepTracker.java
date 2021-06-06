package com.example.digitalwellness;

import android.content.Context;
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
    private FirebaseHelper firebase;
    private MyPreference myPreference;
    private SensorEventListener eventListener;
    private SensorManager sensorManager;
    private Sensor sensor;
    private String key;
    private TextView textView;

    private long databaseValue;
    private long currentSensorValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steptracker);

        FrameLayout progressLayout = (FrameLayout) findViewById(R.id.progress_overlay);
        progressLayout.setVisibility(View.VISIBLE);
        textView = (TextView) findViewById(R.id.number_of_steps);

        firebase = new FirebaseHelper();
        myPreference = new MyPreference(this, "Steps");
        DatabaseReference ref = firebase.getStepsRef();

        key = firebase.getCurrentDate() + firebase.getUid();


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().getValue() != null) {
                    databaseValue = (long) task.getResult().getValue();
                    startTracker();
                } else {
                    ref.setValue(0);
                }
                progressLayout.setVisibility(View.GONE);
                textView.setText(String.valueOf(databaseValue));
            }
        });
    }

    private void startTracker() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

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
        sensorManager.unregisterListener(eventListener);
        firebase.updateSteps(myPreference.getCurrentStepCount(key));
        myPreference.setPreviousTotalStepCount(currentSensorValue);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(eventListener);
        firebase.updateSteps(myPreference.getCurrentStepCount(key));
        myPreference.setPreviousTotalStepCount(currentSensorValue);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
