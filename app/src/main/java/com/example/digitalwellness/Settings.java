package com.example.digitalwellness;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

public class Settings extends PreferenceActivity {

    boolean toggle;
    boolean screenBool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs);

        MyPreference myPreference = new MyPreference(this, "permissions");
        Intent intent = new Intent(this, StepTrackerService.class);
        FirebaseHelper firebase = new FirebaseHelper();

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
                    startService(intent);
                } else {
                    stopService(intent);
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


        Preference preference = (Preference) getPreferenceManager().findPreference("reset_password");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                firebase.resetPassword(firebase.getUser().getEmail(), Settings.this);
                return false;
            }
        });
    }
}
