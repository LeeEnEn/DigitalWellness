package com.example.digitalwellness;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class ClockBroadcastService extends Service {

    CountDownTimer countDownTimer = null;
    String TAG = "ClockBroadcastService";
    public static final String COUNTDOWN_BR = "com.example.digitalwellness";
    Intent intent = new Intent(COUNTDOWN_BR);

    @Override
    public void onCreate() {
        super.onCreate();
        countDownTimer = new CountDownTimer(30000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished);
                intent.putExtra("countdown", millisUntilFinished);
                sendBroadcast(intent);
            }

            @Override
            public void onFinish() {

                Log.i(TAG, "Countdown finished");
            }
        };

        countDownTimer.start();
    }

    @Override
    public void onDestroy() {
        countDownTimer.cancel();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
