package com.example.digitalwellness;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmBroadcastManager extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        //MediaPlayer mediaPlayer=MediaPlayer.create(context,Settings.System.DEFAULT_RINGTONE_URI);
        //mediaPlayer.start();
        Toast.makeText(context,"Focus Mode will now turn off",Toast.LENGTH_SHORT).show();
        mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);


    }
}
