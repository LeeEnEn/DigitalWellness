package com.example.digitalwellness;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserProfile extends AppCompatActivity {

    private ImageView infoButton, settingButton;
    private ImageView profileImage, changeImage;
    private FirebaseHelper firebaseHelper;
    private String url;
    private TextView profileScreen;
    private Uri imageUri;

    private static final int IMAGE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile2);

        infoButton = findViewById(R.id.infoButton);
        settingButton = findViewById(R.id.profilesettings);
        profileImage = findViewById(R.id.profile_image);
        profileScreen = findViewById(R.id.profilescreen);
        firebaseHelper = new FirebaseHelper();
        changeImage = findViewById(R.id.changePicture);

        getProfilePicture();
        loadData();

        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                openImage();

            }
        });

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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(UserProfile.this, MainActivity.class));
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();
            uploadImage();
        }
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

    public void loadData() {

        MyPreference myPreference = new MyPreference(this, firebaseHelper.getUid());

        long currentScreenTime = myPreference.getScreenTime(firebaseHelper.getCurrentDate());
        int hours = (int) (currentScreenTime/3600);
        int minutes = (int) (currentScreenTime - (hours * 3600)) / 60;

        profileScreen.setText(hours + "h " + minutes + " mins");

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(UserProfile.this);
        pd.setMessage("Uploading");
        pd.show();

        if(imageUri != null) {
            StorageReference fileRef = FirebaseStorage
                    .getInstance()
                    .getReference()
                    .child("profile")
                    .child(firebaseHelper.getUid() + "." + getFileExtension(imageUri));

            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Log.i("upload", url);
                            firebaseHelper.setImage(url);
                            pd.dismiss();
                            Toast.makeText(UserProfile.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);
                        }
                    });
                }
            });
        }
    }
}