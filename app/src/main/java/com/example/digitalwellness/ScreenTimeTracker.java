package com.example.digitalwellness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ScreenTimeTracker extends AppCompatActivity {

    private MyPreference myPreference;
    private FirebaseHelper firebaseHelper;
    private int NUM_OF_SECONDS_PER_DAY ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_time_tracker);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseHelper = new FirebaseHelper();
        myPreference = new MyPreference(this, firebaseHelper.getUid());

        NUM_OF_SECONDS_PER_DAY = myPreference.getScreenLimit();
        NUM_OF_SECONDS_PER_DAY = NUM_OF_SECONDS_PER_DAY * 60 * 60;

        long currenttime = myPreference.getScreenTime(firebaseHelper.getCurrentDate());
        long progress = (currenttime * 100) / NUM_OF_SECONDS_PER_DAY;
        Toast.makeText(ScreenTimeTracker.this, String.valueOf(myPreference.getScreenLimit()), Toast.LENGTH_SHORT).show();

        ProgressBar bar = (ProgressBar) findViewById(R.id.progressBarToday);
        TextView text = (TextView) findViewById(R.id.displayStatus);
        bar.setProgress((int)progress);
        text.setText(String.valueOf(progress) + "%");
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