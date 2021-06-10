package com.example.digitalwellness;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreference {
    private final String KEY_SCREEN = "Screen";
    private final SharedPreferences preferences;

    /**
     * Public constructor.
     *
     * @param context Current context.
     * @param filename Name of file.
     */
    public MyPreference(Context context, String filename) {
        this.preferences = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
    }

    /**
     * Get current step count.
     *
     * @param key Consists of date + Uid.
     * @return Step count.
     */
    public long getCurrentStepCount(String key) {
        return this.preferences.getLong(key, 0);
    }

    /**
     * Set current step count.
     *
     * @param key Consists of date + Uid.
     * @param value The value to be updated.
     */
    public void setCurrentStepCount(String key, long value) {
        this.preferences.edit().putLong(key, value).apply();
    }

    /**
     * Get previous step count for calculation purposes.
     *
     * @return Step count.
     */
    public long getPreviousTotalStepCount() {
        return this.preferences.getLong("previous_step_count", 0);
    }

    /**
     * Set previous step count for calculation purposes.
     *
     * @param value The value to be updated.
     */
    public void setPreviousTotalStepCount(long value) {
        this.preferences.edit().putLong("previous_step_count", value).apply();
    }

    /**
     * Get current number of times user denys the dialog.
     *
     * @return A value corresponding to the number of times.
     */
    public int getPhysicalActivityValue() {
        return this.preferences.getInt("physical_activity", -1);
    }

    /**
     * Set the number of times user denys the dialog.
     *
     * @param value The value to be updated.
     */
    public void setPhysicalActivityValue(int value) {
        this.preferences.edit().putInt("physical_activity", value).apply();
    }

    /**
     *
     *
     * @param date
     * @return
     */
    public long getScreenTime(String date) {
        return this.preferences.getLong(date + KEY_SCREEN, 0);
    }

    /**
     *
     *
     * @param date
     * @param value
     */
    public void updateScreenTime(String date, long value) {
        long existing = getScreenTime(date);
        long newvalue = existing + value;
        this.preferences.edit().putLong(date + KEY_SCREEN, newvalue).apply();
    }

    /**
     *
     *
     * @param value
     */
    public void setScreenLimit(int value) {
        this.preferences.edit().putInt("screenLimit", value).apply();
    }

    /**
     *
     * @return
     */
    public int getScreenLimit() {
        return this.preferences.getInt("screenLimit", 1);
    }

    /**
     * Returns true if user accepts step tracking to be run in the background. False otherwise.
     *
     * @return A boolean.
     */
    public boolean getService() {
        return this.preferences.getBoolean("service", false);
    }

    /**
     * Set to true if user allows step tracker to work in the background. False otherwise.
     *
     * @param bool The value to be updated.
     */
    public void setService(boolean bool) {
        this.preferences.edit().putBoolean("service", bool).apply();
    }
}
