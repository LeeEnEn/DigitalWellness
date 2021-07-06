package com.example.digitalwellness;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
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
        this.preferences.edit().putStringSet("friends", list).commit();
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

    public int getStreakCount() {
        boolean tdyStreak = this.preferences.getBoolean("Today", false);
        boolean ytdStreak = this.preferences.getBoolean("Yesterday", false);
        boolean isCounted = this.preferences.getBoolean("Counted", false);
        int count = 0;

        if (!isCounted && tdyStreak) {
            //i am so good at programming!
            if (ytdStreak) {
                count = this.preferences.getInt("streak_count", 0);
            }
            count++;
            this.preferences.edit().putInt("streak_count", count).apply();
        }
        return count;
    }

    public boolean getStreak(String day) {
        return this.preferences.getBoolean(day, false);
    }

    public void setStreak(String day, boolean val) {
        this.preferences.edit().putBoolean(day, val).apply();
    }

    public void setStreak() {
        this.preferences.edit().putInt("streak_count", 0).apply();
    }

    public boolean isDataCreated() {
        return this.preferences.getBoolean("isCreated", false);
    }

    public void dataCreated() {
        this.preferences.edit().putBoolean("isCreated", true).apply();
    }
}
