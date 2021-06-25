package com.example.digitalwellness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ScreenTimeTracker extends AppCompatActivity {

    private MyPreference myPreference;
    private FirebaseHelper firebaseHelper;
    private int NUM_OF_SECONDS_PER_DAY ;
    private Button moreDetailsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_time_tracker);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseHelper = new FirebaseHelper();
        myPreference = new MyPreference(this, firebaseHelper.getUid());

        moreDetailsButton = (Button) findViewById(R.id.moreDetails);

        NUM_OF_SECONDS_PER_DAY = myPreference.getScreenLimit();
        NUM_OF_SECONDS_PER_DAY = NUM_OF_SECONDS_PER_DAY * 60 * 60;

        long currenttime = myPreference.getScreenTime(firebaseHelper.getCurrentDate());
        long progress = (currenttime * 100) / NUM_OF_SECONDS_PER_DAY;
        Toast.makeText(ScreenTimeTracker.this, String.valueOf(myPreference.getScreenLimit() + String.valueOf(progress)), Toast.LENGTH_SHORT).show();


        Log.i("Screen Tracker Update", String.valueOf(currenttime));
        ProgressBar bar = (ProgressBar) findViewById(R.id.progressBarToday);
        TextView text = (TextView) findViewById(R.id.displayStatus);
        bar.setProgress((int)progress);
        text.setText(String.valueOf(progress) + "%");


        moreDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ScreenTimeTracker.this, ScreenTimeGraph.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch(item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }

        if (id == R.id.setting) {
            startActivity(new Intent(ScreenTimeTracker.this, Settings.class));
        } else if (id == R.id.options_logout) {
            firebaseHelper.logoutUser();
            startActivity(new Intent(ScreenTimeTracker.this, StartMenu.class));

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }
}