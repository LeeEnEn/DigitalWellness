package com.example.digitalwellness;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ScreenTimeTracker extends AppCompatActivity {

    private MyPreference myPreference;
    private FirebaseHelper firebaseHelper;
    private int NUM_OF_SECONDS_PER_DAY = 86400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_time_tracker);


        myPreference = new MyPreference(this, "");
        firebaseHelper = new FirebaseHelper();

        int progress = (int) myPreference.getScreenTime(firebaseHelper.getCurrentDate()) / NUM_OF_SECONDS_PER_DAY;
        ProgressBar bar = (ProgressBar) findViewById(R.id.progressBarToday);
        TextView text = (TextView) findViewById(R.id.displayStatus);
        bar.setProgress(progress);
        text.setText(String.valueOf(progress) + "%");
    }
}