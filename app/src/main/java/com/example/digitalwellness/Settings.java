package com.example.digitalwellness;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

public class Settings extends PreferenceActivity {

    boolean toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs);

        MyAlarms myAlarms = new MyAlarms(this);
        MyPreference myPreference = new MyPreference(this, "permissions");

        toggle = myPreference.getService();
        CheckBoxPreference trackerBox = (CheckBoxPreference) getPreferenceManager().findPreference("trackerCheckBox");
        trackerBox.setChecked(toggle);

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
    }
}
