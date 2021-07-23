package com.example.digitalwellness;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class StepTracker extends AppCompatActivity {


    private int step;
    private TextView stepView;
    private ProgressBar stepProgress;
    private MyPreference myPreference;

    private static int nSteps;
    private static int sensorSteps;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_steptracker);

        TransitionBuilder transitionBuilder = new TransitionBuilder(this, R.id.step_layout);
        transitionBuilder.applyTransition();

        super.onCreate(savedInstanceState);

        // Display back button.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Initialize variables.
        FirebaseHelper firebase = new FirebaseHelper();
        long stepCountDatabase = firebase.getStepCount();
        // Set views.
        stepProgress = (ProgressBar) findViewById(R.id.step_progress);
        stepProgress.setMax(10000);

        stepView =  (TextView) findViewById(R.id.step_view);

        myPreference = new MyPreference(this, "Steps");
        String key = firebase.getCurrentDate() + firebase.getUid();
        int stepCountPref = myPreference.getCurrentStepCount(key);

        step = Math.max((int) stepCountDatabase, stepCountPref);
        stepProgress.setProgress(step);
        stepView.setText(String.valueOf(nSteps));

        // Plot chart.
        MyCharts myCharts = new MyCharts(this);
        myCharts.showStepGraph(firebase.getSevenDaySteps(), firebase.getAxis());

        Button sevenDay = (Button) findViewById(R.id.step_seven_days);
        Button allTime = (Button) findViewById(R.id.step_all_time);

        sevenDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show 7 day chart
                myCharts.showStepGraph(firebase.getSevenDaySteps(), firebase.getAxis());
            }
        });

        allTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show all time chart
                myCharts.showStepGraph(firebase.getAllTimeSteps(), firebase.getAllTimeAxis());

            }
        });
        // Start tracker.
        startTracker();
    }

    // Enables real time updates.
    private void startTracker() {
        SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        int prevTotal = myPreference.getPreviousTotalStepCount();

        SensorEventListener eventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                sensorSteps = (int) event.values[0];
                nSteps = sensorSteps + step - prevTotal;
                stepProgress.setProgress(nSteps);
                stepView.setText(String.valueOf(nSteps));
                // Goal reached, update streak.
                if (nSteps >= 100) {
                    FirebaseHelper firebase = new FirebaseHelper();
                    if (!firebase.isUpdated()) {
                        firebase.setIsUpdated(true);
                    }
                }
                System.out.println(sensorSteps + " ss");
                System.out.println(nSteps + " ns");
                System.out.println(prevTotal + " prev");
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        manager.registerListener(eventListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    // Enables back button to be usable. Brings user back one page.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            (new Handler()).postDelayed(this::finish, 500);

        }
        return super.onOptionsItemSelected(item);
    }

    public int getSteps() {
        return nSteps;
    }

    public int getPreviousTotalSteps() {
        return sensorSteps;
    }

    public void resetNStep() {
        nSteps = 0;
    }
}
