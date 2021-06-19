package com.example.digitalwellness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class FocusMode extends AppCompatActivity {

    TextView textView;
    String TAG = "Countdown";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_mode);

        textView = (TextView) findViewById(R.id.countdownclock);
        startService(new Intent(this, ClockBroadcastService.class));
        Log.i(TAG, "Countdown Started");
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(ClockBroadcastService.COUNTDOWN_BR));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {

        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, ClockBroadcastService.class));
        super.onDestroy();
    }

    private void updateGUI (Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown", 3000);
            Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);

            textView.setText(Long.toString(millisUntilFinished/1000));
        }

    }
}