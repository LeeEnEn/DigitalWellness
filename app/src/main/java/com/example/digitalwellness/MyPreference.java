package com.example.digitalwellness;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreference {
    private final String KEY_SCREEN = "Screen";
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

    public long getScreenTime(String date) {
        return this.preferences.getLong(date + KEY_SCREEN, 0);
    }

    public void updateScreenTime(String date, long value) {
        long existing = getScreenTime(date);
        long newvalue = existing + value;
        this.preferences.edit().putLong(date + KEY_SCREEN, newvalue).apply();
    }

    public void setScreenLimit(int value) {
        this.preferences.edit().putInt("screenLimit", value).apply();
    }

    public int getScreenLimit() {
        return this.preferences.getInt("screenLimit", 0);
    }
}
