package com.example.digitalwellness;

import android.app.ActivityOptions;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private ImageView profileButton, userPic;
    private MyAlarms myAlarms;
    private ActionBar actionBar;
    private String url;

    private ViewPager viewPager;

    private ArrayList<MyModel> modeArrayList;

    private MyAdapter myAdapter;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        setExitSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
        getWindow().setSharedElementsUseOverlay(false);

        TransitionBuilder transitionBuilder = new TransitionBuilder(this, R.layout.activity_main);
        transitionBuilder.applyTransition();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CardView stepTracker = (CardView) findViewById(R.id.stepsTracker);
        CardView screenTracker = (CardView) findViewById(R.id.screenTracker);
        CardView distanceTracker = (CardView) findViewById(R.id.distanceTracker);

        userdisplay = (TextView) findViewById(R.id.textView5);
        firebaseHelper = new FirebaseHelper();
        myPermissions = new MyPermissions(this, MainActivity.this);
        myPreference = new MyPreference(this, "permissions");
        userdisplay.setText("Welcome back, " + firebaseHelper.getUser().getDisplayName());
        viewPager = findViewById(R.id.viewPager);
        profileButton = findViewById(R.id.profileButton);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);


        /**
         * Setting Email and Name on Navigation View
         * Can put into a method in the future??
         */
        View headerView = navigationView.getHeaderView(0);
        TextView userName = (TextView) headerView.findViewById(R.id.user_name);
        TextView userEmail = (TextView) headerView.findViewById(R.id.user_email);
        userPic = (ImageView) headerView.findViewById(R.id.user_display);
        userName.setText(firebaseHelper.getUser().getDisplayName());
        userEmail.setText(firebaseHelper.getUser().getEmail());

        //firebaseHelper.setDetails();

        getProfilePicture();

        // Load streak here
        loadStreak();

        //load cards
        loadCards();

        // Video button here
        CardView videoButton = (CardView) findViewById(R.id.video_button);

        //Lockdown Button here
        CardView lockdownButton = (CardView) findViewById(R.id.lockdownbutton);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("MainNotification", "Screen Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


        /*swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                finish();
                overridePendingTransition( 0, 0);
                startActivity(getIntent());
                overridePendingTransition( 0, 0);
            }
        });*/



        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        MainActivity.this, profileButton, profileButton.getTransitionName());

                Intent intent = new Intent(MainActivity.this, UserProfile.class);
                startActivity(intent, options.toBundle());
            }
        });

        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        MainActivity.this, videoButton, videoButton.getTransitionName());

                Intent intent = new Intent(MainActivity.this, Video.class);
                startActivity(intent, options.toBundle());
            }
        });

        lockdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        MainActivity.this, lockdownButton, lockdownButton.getTransitionName());

                startActivity(new Intent(MainActivity.this, FocusMode.class), options.toBundle());
            }
        });


        screenTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        MainActivity.this, screenTracker, screenTracker.getTransitionName());

                Intent i = new Intent(MainActivity.this, ScreenTimeTracker.class);
                startActivity(i, options.toBundle());
            }
        });

        stepTracker.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                int permission = ContextCompat.checkSelfPermission(MainActivity.this, myPermissions.getActivityRecognition());

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        MainActivity.this, stepTracker, stepTracker.getTransitionName());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && permission != PackageManager.PERMISSION_GRANTED) {
                    myPermissions.requestPermission();
                } else {
                    Intent intent = new Intent(MainActivity.this, StepTracker.class);
                    startActivity(intent, options.toBundle());
                }
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
                } else if(id == R.id.logout) {
                    // Update steps when user logs out
                    StepTracker stepTracker = new StepTracker();
                    // Update to firebase.
                    firebaseHelper.updateSteps(firebaseHelper.getCurrentDate(), stepTracker.getSteps());
                    // Update local database.
                    String key = firebaseHelper.getCurrentDate() + firebaseHelper.getUid();
                    MyPreference myPreference = new MyPreference(MainActivity.this, "Steps");
                    myPreference.setCurrentStepCount(key, stepTracker.getSteps());
                    myPreference.setPreviousTotalStepCount(stepTracker.getPreviousTotalSteps());
                    // Logs user out.
                    firebaseHelper.logoutUser();
                    // Send user back to start menu page
                    Intent i = new Intent(MainActivity.this, StartMenu.class);
                    startActivity(i);
                } else if (id == R.id.settings) {
                    Intent i = new Intent(MainActivity.this, Settings.class);
                    startActivity(i);
                } else if (id == R.id.focusmodedrawer) {
                    startActivity(new Intent(MainActivity.this, FocusMode.class));
                } else if (id == R.id.profile_image) {
                    startActivity(new Intent(MainActivity.this, UserProfile.class));
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
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
            if (myPreference.getStreak(String.valueOf(i))) {
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
        } else if (id == R.id.options_notify) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "MainNotification");
            builder.setContentTitle("This is a notification");
            builder.setContentText("This is a message");
            builder.setSmallIcon(R.drawable.digitalwellnesslogo);
            builder.setAutoCancel(true);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
            managerCompat.notify(1, builder.build());
        } else if (id == R.id.profile) {
            startActivity(new Intent(MainActivity.this, UserList.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStreak();
    }


    private void loadCards() {

        myPreference = new MyPreference(this, firebaseHelper.getUid());

        long currentScreenTime = myPreference.getScreenTime(firebaseHelper.getCurrentDate());
        Toast.makeText(MainActivity.this, String.valueOf(currentScreenTime), Toast.LENGTH_SHORT).show();
        int hours = (int) (currentScreenTime/3600);
        int minutes = (int) (currentScreenTime - (hours * 3600)) / 60;

        modeArrayList = new ArrayList<>();

        modeArrayList.add(new MyModel("Screen Time",
                hours + "h " + minutes + "mins",
                "",
                "1/2",
                R.drawable.increase));

        modeArrayList.add(new MyModel("Steps Taken Today",
                String.valueOf(hours) + "h " + String.valueOf(minutes) + "mins",
                "5% increament",
                "2/2",
                R.drawable.decrease));


        //setup adapter
        myAdapter = new MyAdapter(this, modeArrayList);
        //set adapter to view pager
        viewPager.setAdapter(myAdapter);
        //set default padding from left right
        viewPager.setPadding(50,0,50,0);

    }

    private void refreshCards() {
        long currentScreenTime = myPreference.getScreenTime(firebaseHelper.getCurrentDate());
        Toast.makeText(MainActivity.this, String.valueOf(currentScreenTime), Toast.LENGTH_SHORT).show();
        int hours = (int) (currentScreenTime/3600);
        int minutes = (int) (currentScreenTime - (hours * 3600)) / 60;

        modeArrayList.set(0, new MyModel("Screen Time",
                hours + "h " + minutes + "mins",
                "",
                "1/2",
                R.drawable.increase));

        modeArrayList.set(1, new MyModel("Screen Time",
                hours + "h " + minutes + "mins",
                "",
                "1/2",
                R.drawable.increase));

    }

    public void getProfilePicture()  {
        ArrayList<String> nameList = new ArrayList<>();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersdRef = rootRef
                .child("UsersDB")
                .child(firebaseHelper.getUser().getUid())
                .child("picture");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                url = dataSnapshot.getValue().toString();
                Log.d("User ID", "profile:" + url);
                Picasso.get().load(url).into(userPic);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        usersdRef.addListenerForSingleValueEvent(eventListener);
    }


}