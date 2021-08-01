package com.example.digitalwellness;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class LockedActivity extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;
    Calendar currentTime;
    TextView countdownTimer;
    private int toHours, toMinutes, toSeconds, total;
    private int hoursLeft, minutesLeft;
    private ImageView backdoor;
    final int MILLIS_PER_HOUR = 3600000;
    private HomeKeyLocker mHomeKeyLocker;
    private Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locked);
        countdownTimer = (TextView) findViewById(R.id.lockdowntimer);
        currentTime = Calendar.getInstance();
        //Toast.makeText(this, String.valueOf(currentTime.getHours()), Toast.LENGTH_SHORT).show();
        backdoor = (ImageView) findViewById(R.id.backdoor);

        backdoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        long toTime = getIntent().getLongExtra("calendar", 0);
        setClock(toTime - currentTime.getTimeInMillis());

        exitButton = (Button) findViewById(R.id.exitButton);

        exitButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                if (doubleBackToExitPressedOnce) {
                    NotificationManager mNotificationManager = (NotificationManager) LockedActivity.this.getSystemService(NOTIFICATION_SERVICE);
                    Toast.makeText(LockedActivity.this,"Focus Mode will now turn off",Toast.LENGTH_SHORT).show();
                    mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                    finish();
                }

                doubleBackToExitPressedOnce = true;
                Toast.makeText(LockedActivity.this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 2000);


            }
        });

    }

    public void setClock(long duration) {

        new CountDownTimer(duration, 1000) {
            DecimalFormat form = new DecimalFormat("00");

            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onTick(long millisUntilFinished) {

                long hours = Math.floorDiv(millisUntilFinished, MILLIS_PER_HOUR);
                long minutes = Math.floorDiv(millisUntilFinished - (hours * MILLIS_PER_HOUR), 60000);
                long seconds = (millisUntilFinished - (hours * MILLIS_PER_HOUR) - (minutes * 60000)) / 1000;

                if (hours > 0) {
                    countdownTimer.setText(String.valueOf(hours) + ":" + form.format(minutes) + ":" + form.format(seconds));
                } else {
                    countdownTimer.setText(form.format(minutes) + ":" + form.format(seconds));
                }

            }

            public void onFinish() {
                countdownTimer.setText("FOCUS FOCUS COMPLETED");
                finish();
            }

        }.start();

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = false;
        Toast.makeText(this, "Focus Mode is enabled. Exiting is not allowed", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}