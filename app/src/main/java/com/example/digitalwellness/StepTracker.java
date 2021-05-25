package com.example.digitalwellness;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class StepTracker extends AppCompatActivity {
    private SensorManager sensorManager;
    private Sensor sensor;

    private double MagnitudePrevious = 0;
    private int stepCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steptracker);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        TextView view = (TextView) findViewById(R.id.number_of_steps);
        view.setText("Steps: " + 0);

        SensorEventListener detector = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                System.out.println(Arrays.toString(event.values));
                if (event.values != null) {
                    float a = event.values[0];
                    System.out.println(a);
                    view.setText("Steps: " + (int) a);
                }
            }
            
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                System.out.println("accuracy changed: " + accuracy);
            }
        };
        sensorManager.registerListener(detector, sensor, SensorManager.SENSOR_DELAY_NORMAL);


    }
}
