package com.example.digitalwellness;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class StepTracker extends AppCompatActivity {
    private FirebaseHelper firebase;
    private MyPreference myPreference;
    private TextView textView;
    private long value = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steptracker);

        FrameLayout progressLayout = (FrameLayout) findViewById(R.id.progress_overlay);
        progressLayout.setVisibility(View.VISIBLE);
        textView = (TextView) findViewById(R.id.number_of_steps);

        firebase = new FirebaseHelper();
        myPreference = new MyPreference(this);
        DatabaseReference ref = firebase.getSomeRef("Steps");

        MyAlarms myAlarms = new MyAlarms(this);
        myAlarms.startAlarm();

        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    value = (long) task.getResult().getValue();
                } else {
                    ref.setValue(value);
                }
                progressLayout.setVisibility(View.GONE);
                textView.setText(String.valueOf(value));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        SensorEventListener eventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values != null) {
                    value = (long) event.values[0] - myPreference.getPreviousTotal();
                    myPreference.setStep(firebase.getSimpleDate(), value);
                    textView.setText(String.valueOf(value));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        sensorManager.registerListener(eventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
