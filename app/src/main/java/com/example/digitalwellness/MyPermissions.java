package com.example.digitalwellness;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

public class MyPermissions {
    private final int ACTIVITY_RECOGNITION_CODE = 10;
    private final String ACTIVITY_RECOGNITION = Manifest.permission.ACTIVITY_RECOGNITION;
    private final String RATIONALE = "Physical activity Permission is required for Step Tracker to work.";
    private final MyPreference myPreference;
    private final Context context;
    private final Activity activity;

    /**
     * Public constructor for getting physical activities permission.
     *
     * @param context current context.
     * @param activity current activity.
     */
    public MyPermissions(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.myPreference = new MyPreference(context);
    }

    /**
     * Compares the requestCode with ACTIVITY_RECOGNITION_CODE.
     *
     * @param requestCode The value to be compared with.
     * @return A boolean.
     */
    public boolean checkActivityRecognitionCode(int requestCode) {
        return this.ACTIVITY_RECOGNITION_CODE == requestCode;
    }

    /**
     * String containing the said permission.
     *
     * @return String containing the said permission.
     */
    public String getActivityRecognition() {
        return this.ACTIVITY_RECOGNITION;
    }

    /**
     * String containing rationale to allow this permission.
     *
     * @return String containing rationale to allow this permission.
     */
    public String getRationale() {
        return this.RATIONALE;
    }

    /**
     * Checks shared preferences value. Asking the user for the first time if value is -1.
     * Second time if value is 0, when user denies permission twice, bring user to app's settings
     * to allow permission.
     */
    public void requestPermission() {
        int count = myPreference.getPhysicalActivityValue();

        if (count == -1) {
            runPermission();
        } else if (count == 0){
            displayAnotherDialog();
        } else {
            displayChangeSettingsDialog();
        }
    }

    /**
     * Checks if permission is granted. Otherwise ask user for permission
     */
    private void runPermission() {
        ActivityCompat.requestPermissions(this.activity,
                new String[] {this.ACTIVITY_RECOGNITION},
                this.ACTIVITY_RECOGNITION_CODE);
    }

    /**
     * Shows reasoning for needing said permission. In this dialog, user can deny permission as many
     * times as they want. However, upon clicking allow and then deny, user will have to go to app's
     * settings to manually change permission.
     */
    private void displayAnotherDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Permission needed: ");
        alertDialogBuilder.setMessage(RATIONALE);
        alertDialogBuilder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                runPermission();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }

    /**
     * Shows user an option to go to app's settings to change permission. Brings user to page upon
     * allowing. Otherwise do nothing.
     */
    private void displayChangeSettingsDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Permission needed: ");
        alertDialogBuilder.setMessage("Go to settings and change permission?");
        alertDialogBuilder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Action denied!", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }
}