package com.example.digitalwellness;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreference {
    private SharedPreferences preferences;

    public MyPreference(Context context) {
        preferences = context.getSharedPreferences("Preference", Context.MODE_PRIVATE);
    }

    public long getStepCount(String key) {
        return this.preferences.getLong(key, 0);
    }

    public void setStep(String key, long value) {
        this.preferences.edit().putLong(key, value).apply();
    }

    public long getPreviousTotal() {
        return this.preferences.getLong("previous_total", 0);
    }

    public void setPreviousTotal(long value) {
        this.preferences.edit().putLong("previous_total", value).apply();
    }

    public boolean exist(String key) {
        return this.preferences.getLong(key, -1) != -1;
    }

    public int getPhysicalActivityValue() {
        return this.preferences.getInt("physical_activity", -1);
    }

    public void setPhysicalActivityValue(int value) {
        this.preferences.edit().putInt("physical_activity", value).apply();
    }
}
