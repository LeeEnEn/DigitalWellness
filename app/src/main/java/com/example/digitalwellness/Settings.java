package com.example.digitalwellness;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

public class Settings extends PreferenceActivity {

    private boolean toggle;
    private FirebaseHelper firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs);

        MyPreference myPreference = new MyPreference(this, "permissions");
        Intent intent = new Intent(this, StepTrackerService.class);
        firebase = new FirebaseHelper();

        toggle = myPreference.getService();
        CheckBoxPreference trackerBox = (CheckBoxPreference) getPreferenceManager().findPreference("trackerCheckBox");
        trackerBox.setChecked(toggle);

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

    }
}
