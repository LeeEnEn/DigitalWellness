package com.example.digitalwellness;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FriendProfile extends AppCompatActivity {


    private String uid, name, url;
    private FirebaseHelper firebaseHelper;
    private TextView profileScreen, profileSteps, numofFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile2);

        profileScreen = findViewById(R.id.profilescreen);
        profileSteps = findViewById(R.id.profilesteps);
        firebaseHelper = new FirebaseHelper();
        numofFriends = (TextView) findViewById(R.id.friendNumber);

        name = getIntent().getExtras().getString("name");
        url = getIntent().getExtras().getString("url");
        uid = getIntent().getExtras().getString("uid");

        setUpDisplay();
        setFriendsNumber(uid);
        setDataDisplay(uid);

    }

    private void setUpDisplay() {
        ImageView profileSettings = (ImageView) findViewById(R.id.profilesettings);
        profileSettings.setVisibility(View.INVISIBLE);
        ImageView changeImage = findViewById(R.id.changePicture);
        changeImage.setVisibility(View.INVISIBLE);
        TextView header = (TextView) findViewById(R.id.titleTv);
        header.setText("Your friend's summary");
        TextView profileName = (TextView) findViewById(R.id.profileName);
        profileName.setText(name);
        ImageView profileImage = findViewById(R.id.profile_image);

        Picasso.get().load(url).into(profileImage);
    }

    private void setFriendsNumber(String uid) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersdRef = rootRef.child("UsersDB").child(uid).child("friend");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0 ;
                if (dataSnapshot.exists()) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        count = count + 1;
                    }

                    numofFriends.setText(String.valueOf(count));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        usersdRef.addListenerForSingleValueEvent(eventListener);
    }

    private void setDataDisplay(String uid) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersdRef = rootRef.child("Users")
                .child(uid)
                .child("Data");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int screen = 0;
                int steps = 0;

                if (dataSnapshot.exists()) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {

                        
                    }
                    profileScreen.setText(String.valueOf(screen));
                    profileSteps.setText(String.valueOf(steps));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        usersdRef.addListenerForSingleValueEvent(eventListener);

    }

}
