package com.example.digitalwellness;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

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
    private MyAlarms myAlarms;

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
                        Toast.makeText(MainActivity.this, "Button Clicked, Service Initiated", Toast.LENGTH_SHORT).show();
                    } else {
                        startService(new Intent(MainActivity.this, ScreenTimeService.class));
                        Toast.makeText(MainActivity.this, "Button Clicked, Service Initiated", Toast.LENGTH_SHORT).show();
                    }
                } else if(id == R.id.logout) {
                    firebaseHelper.logoutUser();
                    Intent i = new Intent(MainActivity.this, StartMenu.class);
                    startActivity(i);
                } else if (id == R.id.settings) {
                    Intent i = new Intent(MainActivity.this, Settings.class);
                    startActivity(i);
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