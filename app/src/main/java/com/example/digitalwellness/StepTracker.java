package com.example.digitalwellness;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.transition.platform.MaterialContainerTransform;

import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.transition.platform.MaterialArcMotion;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Calendar;

public class StepTracker extends AppCompatActivity {

    private FirebaseHelper firebase;
    private MyPreference myPreference;
    private MyPreference preference = null;
    private SensorEventListener eventListener;
    private SensorManager sensorManager;
    private String key;
    private String currentDate;
    private TextView textView;
    private static long displayValue;
    private long databaseValue = 0L;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_steptracker);

        TransitionBuilder transitionBuilder = new TransitionBuilder(this, R.id.stepRefreshLayout);
        transitionBuilder.applyTransition();

        super.onCreate(savedInstanceState);

        // Display back button.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Show loading screen.
        FrameLayout progressLayout = (FrameLayout) findViewById(R.id.progress_overlay);
        progressLayout.setVisibility(View.VISIBLE);
        textView = (TextView) findViewById(R.id.number_of_steps);

        // Initialize classes.
        firebase = new FirebaseHelper();
        myPreference = new MyPreference(this, "Steps");

        currentDate = firebase.getCurrentDate();
        key = currentDate + firebase.getUid();
        ArrayList<BarEntry> entry = firebase.getSteps();

        // Load data
        MyCharts myCharts = new MyCharts(this);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.stepRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                firebase.getData();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myCharts.showStepGraph(firebase.getSteps(), firebase.getAxis());
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        DatabaseReference reference = firebase.getStepsRef(currentDate);
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getValue() == null) {
                        reference.setValue(0);
                        firebase.getScreenRef(currentDate).setValue(0);
                    } else {
                        databaseValue = (long) task.getResult().getValue();
                    }

                    long temp = myPreference.getCurrentStepCount(key);
                    if (databaseValue < temp) {
                        entry.get(6).setY(temp);
                    }
                    myCharts.showStepGraph(firebase.getSteps(), firebase.getAxis());
                    progressLayout.setVisibility(View.GONE);
                    startTracker();
                }
            }
        });
    }

    // Enables real time updates.
    private void startTracker() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        long previousStepCount = myPreference.getPreviousTotalStepCount();

        if (sensor != null) {
            eventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.values != null) {
                        long currentSensorValue = (long) event.values[0];
                        displayValue = currentSensorValue + databaseValue - previousStepCount;
                        myPreference.setCurrentStepCount(key, displayValue);
                        textView.setText(String.valueOf(displayValue));

                        if (preference == null && currentSensorValue >= 100) {
                            preference = new MyPreference(StepTracker.this, "Streak");
                            preference.setStreak(String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)), true);
                            preference.setStreak("Today", true);
                            preference.getStreakCount();
                            preference.setStreak("isCounted", true);
                        }

                        System.out.println(currentSensorValue);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public long getCurrentStepValue() {
        return displayValue;
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



}
