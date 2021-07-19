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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.BarChart;
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

        System.out.println(stepCountDatabase + " db steps");
        System.out.println(step + " final step");
        System.out.println(stepCountPref + " local");

        // Plot chart.
        MyCharts myCharts = new MyCharts(this);
        firebase.getSteps().get(6).setY(step);
        myCharts.showStepGraph(firebase.getSteps(), firebase.getAxis());

        Button sevenDay = (Button) findViewById(R.id.step_seven_days);
        Button allTime = (Button) findViewById(R.id.step_all_time);

        sevenDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show 7 day chart
            }
        });

        allTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show all time chart
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
                if (nSteps >= 10) {
                    MyPreference streakPref = new MyPreference(StepTracker.this, "Streak");
                    streakPref.updateStreak();
                }
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
}
