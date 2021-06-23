package com.example.digitalwellness;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

public class Settings extends PreferenceActivity {

    boolean toggle;
    boolean screenBool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs);

        MyAlarms myAlarms = new MyAlarms(this);
        MyPreference myPreference = new MyPreference(this, "permissions");

        toggle = myPreference.getService();
        screenBool = myPreference.getScreenService();
        Log.e("Checked", String.valueOf(screenBool));
        CheckBoxPreference trackerBox = (CheckBoxPreference) getPreferenceManager().findPreference("trackerCheckBox");
        CheckBoxPreference screenBox = (CheckBoxPreference) getPreferenceManager().findPreference("screenCheckBox");

        if (screenBool )
        trackerBox.setChecked(toggle);
        screenBox.setChecked(screenBool);

        trackerBox.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                toggle = !toggle;
                if (toggle) {
                    myAlarms.startServiceAlarm();
                } else {
                    myAlarms.cancelServiceAlarm();
                }
                trackerBox.setChecked(toggle);
                myPreference.setService(toggle);
                return toggle;
            }
        });

        screenBox.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                screenBool = !screenBool;
                if (screenBool) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(new Intent(Settings.this, ScreenTimeService.class));
                    } else {
                        startService(new Intent(Settings.this, ScreenTimeService.class));
                    }
                } else {
                    stopService(new Intent(Settings.this, ScreenTimeService.class));
                }

                myPreference.setService(toggle);
                return toggle;
            }
        });


    }
}
