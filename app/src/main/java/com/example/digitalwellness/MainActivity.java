package com.example.digitalwellness;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navView;
    private int keyCount = 0;
    private MyPermissions myPermissions;
    private MyPreference myPreference;
    private FirebaseHelper firebaseHelper;
    private TextView userdisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        CardView stepTracker = (CardView) findViewById(R.id.stepsTracker);
        CardView screenTracker = (CardView) findViewById(R.id.screenTracker);
        userdisplay = (TextView) findViewById(R.id.userdisplayname);
        firebaseHelper = new FirebaseHelper();
        myPermissions = new MyPermissions(this, MainActivity.this);
        myPreference = new MyPreference(this, "permissions");
        userdisplay.setText(firebaseHelper.getUser().getDisplayName());

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);

        /**
         * Setting Email and Name on Navigation View
         * Can put into a method in the future??
         */
        View headerView = navigationView.getHeaderView(0);
        TextView userName = (TextView) headerView.findViewById(R.id.user_name);
        TextView userEmail = (TextView) headerView.findViewById(R.id.user_email);
        ImageView userPic = (ImageView) headerView.findViewById(R.id.user_display);
        userName.setText(firebaseHelper.getUser().getDisplayName());
        userEmail.setText(firebaseHelper.getUser().getEmail());
        Picasso.get().load(firebaseHelper.getUser().getPhotoUrl()).into(userPic);

        // Load streak here
        loadStreak();

        // Video button here
        CardView videoButton = (CardView) findViewById(R.id.video_button);

        //Lockdown Button here
        CardView lockdownButton = (CardView) findViewById(R.id.lockdownbutton);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Video.class);
                startActivity(intent);
            }
        });

        lockdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FocusMode.class));
            }
        });


        screenTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ScreenTimeTracker.class);
                startActivity(i);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.stepDrawer) {
                    Intent i = new Intent(MainActivity.this, StepTracker.class);
                    startActivity(i);
                } else if(id == R.id.screenDrawer) {
                    Intent i = new Intent(MainActivity.this, ScreenTimeTracker.class);
                    startActivity(i);
                } else if (id == R.id.test) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(new Intent(MainActivity.this, ScreenTimeService.class));
                    } else {
                        startService(new Intent(MainActivity.this, ScreenTimeService.class));
                    }
                    Toast.makeText(MainActivity.this, "Button Clicked, Service Initiated", Toast.LENGTH_SHORT).show();
                } else if(id == R.id.logout) {
                    // Update steps when user logs out
                    String currentDate = firebaseHelper.getCurrentDate();
                    long value = new StepTracker().getCurrentStepValue();
                    MyPreference stepPref = new MyPreference(MainActivity.this, "Steps");
                    stepPref.setPreviousTotalStepCount(value);
                    firebaseHelper.updateSteps(currentDate, value);
                    firebaseHelper.logoutUser();
                    // Send user back to start menu page
                    Intent i = new Intent(MainActivity.this, StartMenu.class);
                    startActivity(i);
                } else if (id == R.id.settings) {
                    Intent i = new Intent(MainActivity.this, Settings.class);
                    startActivity(i);
                } else if (id == R.id.focusmodedrawer) {
                    startActivity(new Intent(MainActivity.this, FocusMode.class));
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

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

    private void loadStreak() {
        ImageView[] array = new ImageView[7];
        array[0] = findViewById(R.id.sunday_circle);
        array[1] = findViewById(R.id.monday_circle);
        array[2] = findViewById(R.id.tuesday_circle);
        array[3] = findViewById(R.id.wednesday_circle);
        array[4] = findViewById(R.id.thursday_circle);
        array[5] = findViewById(R.id.friday_circle);
        array[6] = findViewById(R.id.saturday_circle);

        MyPreference myPreference = new MyPreference(this, "Streak");
        for (int i = 1; i < 8; i++) {
            if (myPreference.getMilestone(String.valueOf(i))) {
                array[i-1].setImageResource(R.drawable.filled_circle);
            }
        }
        TextView textView = (TextView) findViewById(R.id.streak_value);
        textView.setText(String.valueOf(myPreference.getStreakCount()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (myPermissions.checkActivityRecognitionCode(requestCode)) {
            if (grantResults[0] == 0) {
                myPermissions.displayServiceDialog();
            } else {
                int count = myPreference.getPhysicalActivityValue();
                myPreference.setPhysicalActivityValue(++count);
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
        int id = item.getItemId();
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (id == R.id.setting) {
            Intent i = new Intent(MainActivity.this, Settings.class);
            startActivity(i);
        } else if (id == R.id.options_logout) {
            firebaseHelper.logoutUser();
            Intent i = new Intent(MainActivity.this, StartMenu.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

}