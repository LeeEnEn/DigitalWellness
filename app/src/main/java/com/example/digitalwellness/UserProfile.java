package com.example.digitalwellness;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserProfile extends AppCompatActivity {

    private ImageView infoButton, settingButton;
    private ImageView profileImage;
    private FirebaseHelper firebaseHelper;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile2);

        infoButton = findViewById(R.id.infoButton);
        settingButton = findViewById(R.id.profilesettings);
        profileImage = findViewById(R.id.profile_image);

        firebaseHelper = new FirebaseHelper();

        getProfilePicture();

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String title = "How Leadership Is Calculated";
                String message = "The leadership board is calculated based according to all the users in this application. The leadership board ranks" +
                        "users with the highest number of steps taken in each day";
                buildAlert(title, message);
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(UserProfile.this, Settings.class);
                startActivity(intent);
            }
        });




    }


    public void buildAlert(String title, String message) {
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(UserProfile.this);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton(
                "close", new DialogInterface.OnClickListener() {
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

    public void getProfilePicture()  {
        ArrayList<String> nameList = new ArrayList<>();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersdRef = rootRef
                .child("Users")
                .child(firebaseHelper.getUser().getUid())
                .child("picture");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                url = dataSnapshot.getValue().toString();
                Log.d("User ID", "profile:" + url);
                Picasso.get().load(url).into(profileImage);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        usersdRef.addListenerForSingleValueEvent(eventListener);
    }
}