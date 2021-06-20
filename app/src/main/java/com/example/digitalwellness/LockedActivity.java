package com.example.digitalwellness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class LockedActivity extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;
    Date currentTime;
    TextView countdownTimer;
    private int toHours, toMinutes;
    private int hoursLeft, minutesLeft;

    final int MILLIS_PER_HOUR = 3600000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locked);
        countdownTimer = (TextView) findViewById(R.id.lockdowntimer);
        currentTime = Calendar.getInstance().getTime();
        //Toast.makeText(this, String.valueOf(currentTime.getHours()), Toast.LENGTH_SHORT).show();

        Intent intent = getIntent();
        toHours = intent.getIntExtra("hours", 0);
        toMinutes = intent.getIntExtra("minutes", 0);

        setClock(toHours - currentTime.getHours(), toMinutes - currentTime.getMinutes());
    }

    public void setClock(long hours, long minutes) {
        long total = hours * MILLIS_PER_HOUR + minutes * 60000;
        new CountDownTimer(total, 1000) {
            DecimalFormat form = new DecimalFormat("00");

            public void onTick(long millisUntilFinished) {
                //Toast.makeText(LockedActivity.this, String.valueOf(millisUntilFinished), Toast.LENGTH_SHORT).show();
                if (millisUntilFinished < MILLIS_PER_HOUR) {
                    countdownTimer.setText("00:" + form.format(millisUntilFinished/60000));
                } else {
                    long numofHours = total / MILLIS_PER_HOUR;
                    countdownTimer.setText(form.format(numofHours) + ":" + form.format((total - numofHours)/60000));
                }
            }

            public void onFinish() {
                countdownTimer.setText("FOCUS MODE COMPLETED");
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

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}