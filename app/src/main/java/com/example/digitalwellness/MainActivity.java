package com.example.digitalwellness;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private ActionBarDrawerToggle mToggle;
    private ArrayList<MyModel> modeArrayList;
    private ActionBar actionBar;
    private CardView stepTracker;
    private DrawerLayout mDrawerLayout;
    private FirebaseHelper firebaseHelper;
    private ImageView profileButton, userPic;
    private MyAlarms myAlarms;
    private MyAdapter myAdapter;
    private MyPermissions myPermissions;
    private MyPreference myPreference;
    private NavigationView navView;
    private String url;
    private TextView userdisplay;
    private ViewPager viewPager;


    private int keyCount = 0;

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

        stepTracker = (CardView) findViewById(R.id.stepsTracker);
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

        checkNumberFriendRequest();

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
                checkStepPermission();
            }
        });

        distanceTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDistancePermission();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.stepDrawer) {
                    checkStepPermission();
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
                } else if (id == R.id.distance_tracker) {
                    checkDistancePermission();
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void loadStreak() {
        ImageView[] array = new ImageView[7];
        array[6] = findViewById(R.id.sunday_circle);
        array[0] = findViewById(R.id.monday_circle);
        array[1] = findViewById(R.id.tuesday_circle);
        array[2] = findViewById(R.id.wednesday_circle);
        array[3] = findViewById(R.id.thursday_circle);
        array[4] = findViewById(R.id.friday_circle);
        array[5] = findViewById(R.id.saturday_circle);

        for (int i = 0; i < 7; i++) {
            if (firebaseHelper.getStreakCircles()[i]) {
                array[i].setImageResource(R.drawable.filled_circle);
            }
        }
        TextView textView = (TextView) findViewById(R.id.streak_value);
        textView.setText(String.valueOf(firebaseHelper.getStreakCount()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MyPreference myPreference = new MyPreference(MainActivity.this, "permissions");
        if (myPermissions.checkActivityRecognitionCode(requestCode)) {
            if (grantResults[0] == 0) {
                myPermissions.displayServiceDialog();
            } else {
                int count = myPreference.getValue(requestCode);
                myPreference.setValue(requestCode, ++count);
                System.out.println();
                Toast.makeText(getApplicationContext(), myPermissions.getRationale(requestCode), Toast.LENGTH_LONG).show();
            }
        } else if (myPermissions.checkLocationPermissionCode(requestCode)) {
            if (grantResults[0] == 0) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            } else {
                int count = myPreference.getValue(requestCode);
                myPreference.setValue(requestCode, ++count);
                Toast.makeText(getApplicationContext(), myPermissions.getRationale(requestCode), Toast.LENGTH_LONG).show();
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
        } else if (id == R.id.friends) {
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
        //Toast.makeText(MainActivity.this, String.valueOf(currentScreenTime), Toast.LENGTH_SHORT).show();
        int hours = (int) (currentScreenTime / 3600);
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
        viewPager.setPadding(50, 0, 50, 0);

    }

    public void getProfilePicture() {
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

    private void checkStepPermission() {
        int permission = ContextCompat.checkSelfPermission(MainActivity.this, myPermissions.getActivityRecognition());

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                MainActivity.this, stepTracker, stepTracker.getTransitionName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && permission != PackageManager.PERMISSION_GRANTED) {
            myPermissions.requestPermission(1001);
        } else {
            Intent intent = new Intent(MainActivity.this, StepTracker.class);
            startActivity(intent, options.toBundle());
        }
    }

    private void checkDistancePermission() {
        int permission = ContextCompat.checkSelfPermission(MainActivity.this, myPermissions.getLocationPermission());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && permission != PackageManager.PERMISSION_GRANTED) {
            myPermissions.requestPermission(1002);
        } else {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        }
    }


    private int checkNumberFriendRequest() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersdRef = rootRef.child("Requests").child(firebaseHelper.getUid());
        final int[] count = {0};

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        count[0] = count[0] + 1;
                    }
                    userdisplay.setText("You have " + count[0] + " new friend requests");
                    setFriendAlert(count[0]);

                    userdisplay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(MainActivity.this, FriendList.class));
                        }
                    });

                } else {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        usersdRef.addListenerForSingleValueEvent(eventListener);
        return count[0];
    }

    private void setFriendAlert(int numofFriends) {
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(MainActivity.this);
        builder.setMessage("You have " + numofFriends + " friend requests");
        builder.setTitle("Friend Request Received");
        builder.setPositiveButton(
                "View Requests", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {
                        startActivity(new Intent(MainActivity.this, FriendList.class));
                    }
                });

        builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,
                                int which)
            {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();

            // Show the Alert Dialog box
        alertDialog.show();
    }

}