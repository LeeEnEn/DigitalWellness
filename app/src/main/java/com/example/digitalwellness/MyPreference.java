package com.example.digitalwellness;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreference {
    private final SharedPreferences preferences;

    public MyPreference(Context context, String filename) {
        this.preferences = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
    }

    public long getCurrentStepCount(String key) {
        return this.preferences.getLong(key, 0);
    }

    public void setCurrentStepCount(String key, long value) {
        this.preferences.edit().putLong(key, value).apply();
    }

    public long getPreviousTotalStepCount() {
        return this.preferences.getLong("previous_step_count", 0);
    }

    public void setPreviousTotalStepCount(long value) {
        this.preferences.edit().putLong("previous_step_count", value).apply();
    }

    public int getPhysicalActivityValue() {
        return this.preferences.getInt("physical_activity", -1);
    }

    public void setPhysicalActivityValue(int value) {
        this.preferences.edit().putInt("physical_activity", value).apply();
    }
}
