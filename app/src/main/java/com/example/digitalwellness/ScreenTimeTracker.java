package com.example.digitalwellness;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.transition.platform.MaterialArcMotion;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;

import java.util.Set;

public class ScreenTimeTracker extends AppCompatActivity {

    private MyPreference myPreference;
    private FirebaseHelper firebaseHelper;
    private int NUM_OF_SECONDS_PER_DAY ;
    private Button moreDetailsButton;
    private TextView duration, warning;
    private ImageView settings, info;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_screen_time_tracker);

        TransitionBuilder transitionBuilder = new TransitionBuilder(this, R.id.screenLayout);
        transitionBuilder.applyTransition();

        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseHelper = new FirebaseHelper();
        myPreference = new MyPreference(this, firebaseHelper.getUid());

        //moreDetailsButton = (Button) findViewById(R.id.moreDetails);

        NUM_OF_SECONDS_PER_DAY = myPreference.getScreenLimit();
        NUM_OF_SECONDS_PER_DAY = NUM_OF_SECONDS_PER_DAY * 60 * 60;

        long currenttime = myPreference.getScreenTime(firebaseHelper.getCurrentDate());
        long progress = (currenttime * 100) / NUM_OF_SECONDS_PER_DAY;
        //Toast.makeText(ScreenTimeTracker.this, String.valueOf(myPreference.getScreenLimit() + String.valueOf(progress)), Toast.LENGTH_SHORT).show();
        settings = (ImageView) findViewById(R.id.screensetting);
        info = (ImageView) findViewById(R.id.screeninfo);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ScreenTimeTracker.this, Settings.class);
                startActivity(i);
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder
                        = new AlertDialog
                        .Builder(ScreenTimeTracker.this);
                builder.setMessage("This value is calculated by this application tracking your screen on time. " +
                        "The contents on your screen will never be recorded by this application." +
                        "Your screen time target can also be configured in the settings in the application. You may also turn off this service in the settings.");
                builder.setTitle("How Does This Number Appear?");
                builder.setPositiveButton(
                        "close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which)
                            {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();

                // Show the Alert Dialog box
                alertDialog.show();
            }
        });



        Log.i("Screen Tracker Update", String.valueOf(currenttime));
        ProgressBar bar = (ProgressBar) findViewById(R.id.progressBarToday);
        TextView text = (TextView) findViewById(R.id.displayStatus);
        warning = (TextView) findViewById(R.id.warning);

        if (progress < 100) {
            warning.setVisibility(View.GONE);
            text.setText(String.valueOf(progress) + "%");
        } else {
            text.setVisibility(View.GONE);
            warning.setText("You have exceeded your target by " + String.valueOf(progress - 100) + "%. \nWe strongly advice you to take a break from your phone.");
        }

        duration = (TextView) findViewById(R.id.durationtext);
        long currentScreenTime = myPreference.getScreenTime(firebaseHelper.getCurrentDate());
        int hours = (int) (currentScreenTime / 3600);
        int minutes = (int) (currentScreenTime - (hours * 3600)) / 60;
        duration.setText(hours + "h " + minutes + " minutes");
        bar.setProgress((int) progress);
        /*moreDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ScreenTimeTracker.this, ScreenTimeGraph.class);
                startActivity(i);
            }
        });*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                (new Handler()).postDelayed(this::finish, 500);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}