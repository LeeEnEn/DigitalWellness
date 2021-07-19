package com.example.digitalwellness;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

public class MyPermissions {
    private final int ACTIVITY_RECOGNITION_CODE = 10;
    private final int LOCATION_PERMISSION_CODE = 11;
    private final String ACTIVITY_RECOGNITION = Manifest.permission.ACTIVITY_RECOGNITION;
    private final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String ALLOW = "Allow";
    private final String DENY = "Deny";
    private final String STEP_RATIONALE = "Physical activity Permission is required for Step Tracker to work.";
    private final String LOCATION_RATIONALE = "Location Permission is required for Distance Tracker to work.";
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
        this.myPreference = new MyPreference(context, "permissions");
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
    public String getRationale(int code) {
        return code == 10 ? STEP_RATIONALE : LOCATION_RATIONALE;
    }

    /**
     * Checks shared preferences value. Asking the user for the first time if value is -1.
     * Second time if value is 0, when user denies permission twice, bring user to app's settings
     * to allow permission.
     */
    public void requestPermission(int code) {
        // Step tracker code.
        int count = 0;
        System.out.println("request code " + code);
        if (code == 1001) {
            count = myPreference.getValue(10);
        } else if (code == 1002) { // Distance tracker code.
            count = myPreference.getValue(11);
        }

        if (count == -1) {
            runPermission(code);
        } else if (count == 0){
            displayAnotherDialog(code);
        } else {
            displayChangeSettingsDialog();
        }
    }

    /**
     * Checks if permission is granted. Otherwise ask user for permission
     */
    private void runPermission(int code) {
        if (code == 1001) {
            ActivityCompat.requestPermissions(this.activity,
                    new String[] {this.ACTIVITY_RECOGNITION},
                    this.ACTIVITY_RECOGNITION_CODE);
        } else if (code == 1002) {
            ActivityCompat.requestPermissions(this.activity,
                    new String[] {this.LOCATION_PERMISSION},
                    this.LOCATION_PERMISSION_CODE);
        }
    }

    /**
     * Shows reasoning for needing said permission. In this dialog, user can deny permission as many
     * times as they want. However, upon clicking allow then deny, user will have to go to app's
     * settings to manually change permission.
     */
    private void displayAnotherDialog(int code) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Permission needed: ");
        String RATIONALE = "";
        System.out.println(code + " code??");
        if (code == 1001) {
            RATIONALE = STEP_RATIONALE;
        } else if (code == 1002) {
            RATIONALE = LOCATION_RATIONALE;
        }

        alertDialogBuilder.setMessage(RATIONALE);
        alertDialogBuilder.setPositiveButton(ALLOW, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                runPermission(code);
            }
        });
        alertDialogBuilder.setNegativeButton(DENY, new DialogInterface.OnClickListener() {
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

        alertDialogBuilder.setTitle("Permission needed:");
        alertDialogBuilder.setMessage("Go to settings and change permission?");
        alertDialogBuilder.setPositiveButton(ALLOW, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);
            }
        });
        alertDialogBuilder.setNegativeButton(DENY, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Action denied!", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }

    /**
     * Shows user an option to allow step tracker to run as a service.
     */
    public void displayServiceDialog() {
        Intent intent = new Intent(context, StepTracker.class);
        MyPreference myPreference = new MyPreference(context, "permissions");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Permission needed:");
        alertDialogBuilder.setMessage("Allow Step Tracker to run in the background?");
        alertDialogBuilder.setPositiveButton(ALLOW, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Start service
                myPreference.setService(true);
                context.startService(new Intent(context, StepTrackerService.class));
                context.startActivity(intent);
            }
        });
        alertDialogBuilder.setNegativeButton(DENY, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                myPreference.setService(false);
                context.startActivity(intent);
            }
        });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }

    /**
     * Compares the requestCode with ACTIVITY_RECOGNITION_CODE.
     *
     * @param requestCode The value to be compared with.
     * @return A boolean.
     */
    public boolean checkLocationPermissionCode(int requestCode) {
        return this.LOCATION_PERMISSION_CODE == requestCode;
    }

    /**
     * String containing the said permission.
     *
     * @return String containing the said permission.
     */
    public String getLocationPermission() {
        return this.LOCATION_PERMISSION;
    }
}