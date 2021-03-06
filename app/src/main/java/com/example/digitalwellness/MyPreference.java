package com.example.digitalwellness;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

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

    public void updateFriends(Set<String> list) {
        this.preferences.edit().putStringSet("friends", list).apply();
    }

    public Set<String> getFriends() {
        return this.preferences.getStringSet("friends", null);
    }

    /**
     * Get current step count.
     *
     * @param key Consists of date + Uid.
     * @return Step count.
     */
    public int getCurrentStepCount(String key) {
        return this.preferences.getInt(key, 0);
    }

    /**
     * Set current step count.
     *
     * @param key Consists of date + Uid.
     * @param value The value to be updated.
     */
    public void setCurrentStepCount(String key, int value) {
        this.preferences.edit().putInt(key, value).apply();
    }

    /**
     * Get previous step count for calculation purposes.
     *
     * @return Step count.
     */
    public int getPreviousTotalStepCount() {
        return this.preferences.getInt("previous_step_count", 0);
    }

    /**
     * Set previous step count for calculation purposes.
     *
     * @param value The value to be updated.
     */
    public void setPreviousTotalStepCount(int value) {
        this.preferences.edit().putInt("previous_step_count", value).apply();
    }

    /**
     * Get current number of times user denys the dialog.
     *
     * @return A value corresponding to the number of times.
     */
    public int getValue(int code) {
        if (code == 10) {
            return this.preferences.getInt("physical_activity", -1);
        } else if (code == 11) {
            return this.preferences.getInt("location_permission", -1);
        }
        return -1;
    }

    /**
     * Set the number of times user denys the dialog.
     *
     * @param value The value to be updated.
     */
    public void setValue(int code, int value) {
        if (code == 10) {
            this.preferences.edit().putInt("physical_activity", value).apply();
        } else if (code == 11) {
            this.preferences.edit().putInt("location_permission", value).apply();
        }
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
     * Set to true if user allows screen tracker to work in the background. False otherwise.
     *
     * @param bool The value to be updated.
     */
    public void setService(boolean bool) {
        this.preferences.edit().putBoolean("service", bool).apply();
    }

    public boolean getScreenService() {
        return this.preferences.getBoolean("service", false);
    }

    /**
     * Set to true if user allows screen tracker to work in the background. False otherwise.
     *
     * @param bool The value to be updated.
     */
    public void setScreenService(boolean bool) {
        this.preferences.edit().putBoolean("service", bool).apply();
    }

    public void setStreak(String day, boolean val) {
        this.preferences.edit().putBoolean(day, val).apply();
    }

    public void setNSteps(int val) {
        this.preferences.edit().putInt("nSteps", val).apply();
    }

    public int getNSteps() {
        return this.preferences.getInt("nSteps", 0);
    }
}
