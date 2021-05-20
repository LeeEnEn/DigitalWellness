package com.example.digitalwellness;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private int count = 0;
    private final int ACTIVITY_RECOGNITION_CODE = 10;
    private final String ACTIVITY_RECOGNITION = Manifest.permission.ACTIVITY_RECOGNITION;
    private final String RATIONALE = "Physical activity Permission is required for Step Tracker to work.";
    private final String PERMISSION = "activity_permission";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button stepTracker = (Button) findViewById(R.id.step_tracker);
        sharedPreferences = getApplicationContext().getSharedPreferences(PERMISSION, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        stepTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permission = ContextCompat.checkSelfPermission(MainActivity.this, ACTIVITY_RECOGNITION);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    requestPermission();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ACTIVITY_RECOGNITION_CODE) {
            if (grantResults[0] == 0) {
                Intent intent = new Intent(MainActivity.this, StepTracker.class);
                startActivity(intent);
            } else {
                int count = sharedPreferences.getInt(PERMISSION, -1);
                editor.putInt(PERMISSION, ++count).apply();
                Toast.makeText(getApplicationContext(), RATIONALE, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void runPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[] {ACTIVITY_RECOGNITION},
                ACTIVITY_RECOGNITION_CODE);
    }

    private void requestPermission() {
        ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, RATIONALE);
        int count = sharedPreferences.getInt(PERMISSION, -1);

        if (count == -1) {
            runPermission();
        } else if (count == 0){
            displayAnotherDialog();
        } else {
            displayChangeSettingsDialog();
        }
    }

    private void displayAnotherDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
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
                Toast.makeText(getApplicationContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }

    private void displayChangeSettingsDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("Permission needed: ");
        alertDialogBuilder.setMessage("Go to settings and change permission?");
        alertDialogBuilder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Action denied!", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }

    // Disables user to go back to login screen
    // Double tap the back button is equivalent to pressing the home button.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (count == 1) {
                count = 0;
                // Settle graceful exit of application
                Intent intent = new Intent().setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            } else {
                count++;
                Toast.makeText(getApplicationContext(), "Tap again to exit application!", Toast.LENGTH_LONG).show();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
