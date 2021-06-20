package com.example.digitalwellness;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class FocusMode extends AppCompatActivity {

    TextView textView;
    String TAG = "Countdown";
    Button confirmAlarm;
    private TimePicker timePicker;
    FirebaseHelper firebaseHelper;

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_mode);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseHelper = new FirebaseHelper();

        textView = (TextView) findViewById(R.id.countdownclock);
        confirmAlarm = (Button) findViewById(R.id.fragment_createalarm_scheduleAlarm);
        //startService(new Intent(this, ClockBroadcastService.class));
        //Log.i(TAG, "Countdown Started");
        timePicker = (TimePicker) findViewById(R.id.fragment_createalarm_timePicker);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);



        confirmAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_alarm_event();
                if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivity(intent);
                } else {
                    mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                    Intent i = new Intent(FocusMode.this, LockedActivity.class);
                    i.putExtra("hours", timePicker.getHour());
                    i.putExtra("minutes", timePicker.getMinute());
                    startActivity(i);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public  void start_alarm_event(){
        Calendar calendar=Calendar.getInstance();
        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                timePicker.getHour(),
                timePicker.getMinute(),
                0
        );
        setAlarm(calendar.getTimeInMillis());
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.M)
    public void setAlarm(long timeInMillis){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmBroadcastManager.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        Toast.makeText(getApplicationContext(),"Focus Mode is enabled. You will not receive any notifications until " +
                String.valueOf(timePicker.getHour()) +
                        String.valueOf(timePicker.getMinute()),
                Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(FocusMode.this, Settings.class));
        } else if (id == R.id.options_logout) {
            firebaseHelper.logoutUser();
            startActivity(new Intent(FocusMode.this, StartMenu.class));
            finish();

        }
        return super.onOptionsItemSelected(item);
    }


}