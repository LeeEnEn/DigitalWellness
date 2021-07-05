package com.example.digitalwellness;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class UserProfile extends AppCompatActivity {

    private ImageView infoButton, settingButton;
    private ImageView profileImage;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile2);

        infoButton = findViewById(R.id.infoButton);
        settingButton = findViewById(R.id.profilesettings);
        profileImage = findViewById(R.id.profile_image);

        firebaseHelper = new FirebaseHelper();

        Picasso.get().load(firebaseHelper.getUser().getPhotoUrl()).into(profileImage);

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
}