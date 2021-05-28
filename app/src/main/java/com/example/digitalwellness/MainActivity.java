package com.example.digitalwellness;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private int keyCount = 0;
    private MyPermissions myPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button stepTracker = (Button) findViewById(R.id.step_tracker);
        myPermissions = new MyPermissions(this, MainActivity.this);

        stepTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permission = ContextCompat.checkSelfPermission(MainActivity.this, myPermissions.getActivityRecognition());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && permission != PackageManager.PERMISSION_GRANTED) {
                    myPermissions.requestPermission();
                } else {
                    Intent intent = new Intent(MainActivity.this, StepTracker.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (myPermissions.checkActivityRecognitionCode(requestCode)) {
            if (grantResults[0] == 0) {
                Intent intent = new Intent(MainActivity.this, StepTracker.class);
                startActivity(intent);
            } else {
                int count = myPermissions.getSharedPreferenceValue();
                myPermissions.putValue(++count);
                Toast.makeText(getApplicationContext(), myPermissions.getRationale(), Toast.LENGTH_LONG).show();
            }
        }
    }

    // Disables user to go back to login screen
    // Double tap the back button is equivalent to pressing the home button.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (keyCount == 1) {
                keyCount = 0;
                // Settle graceful exit of application
                Intent intent = new Intent().setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            } else {
                keyCount++;
                Toast.makeText(getApplicationContext(), "Tap again to exit application!", Toast.LENGTH_LONG).show();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}