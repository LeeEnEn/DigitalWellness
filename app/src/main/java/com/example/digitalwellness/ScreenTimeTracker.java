package com.example.digitalwellness;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ScreenTimeTracker extends AppCompatActivity {

    private MyPreference myPreference;
    private FirebaseHelper firebaseHelper;
    private int NUM_OF_SECONDS_PER_DAY = 10800 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_time_tracker);


        firebaseHelper = new FirebaseHelper();
        myPreference = new MyPreference(this, firebaseHelper.getUid());


        long progress = (myPreference.getScreenTime(firebaseHelper.getCurrentDate()) / NUM_OF_SECONDS_PER_DAY) * 100;
        Toast.makeText(ScreenTimeTracker.this, String.valueOf(myPreference.getScreenTime(firebaseHelper.getCurrentDate())), Toast.LENGTH_SHORT).show();
        Toast.makeText(ScreenTimeTracker.this, String.valueOf(progress), Toast.LENGTH_SHORT).show();

        ProgressBar bar = (ProgressBar) findViewById(R.id.progressBarToday);
        TextView text = (TextView) findViewById(R.id.displayStatus);
        bar.setProgress((int)progress);
        text.setText(String.valueOf(progress) + "%");
    }
}