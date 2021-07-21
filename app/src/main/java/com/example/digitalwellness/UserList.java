package com.example.digitalwellness;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserList extends AppCompatActivity implements recyclerAdapter.OnNoteListener, AdapterView.OnItemSelectedListener{

    private ArrayList<User> usersList;
    private ArrayList<String> friendList;
    private ArrayList<User> friendAL;
    private ArrayList<User> nonfriendAL;
    private RecyclerView recyclerView;
    private FirebaseHelper firebaseHelper;
    private Spinner spinner;
    private ImageView friendRequestButton;
    String[] options = {"All", "Friends", "Not Friends"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(!CheckNetwork.isInternetAvailable(UserList.this)) //returns true if internet available
        {
            buildAlert("No Internet Connection", "This feature requires internet connection. Please check your Wi-Fi settings before continuing");
        }

        super.onCreate(savedInstanceState);

        TransitionBuilder transitionBuilder = new TransitionBuilder(this, R.id.userlistlayout);
        transitionBuilder.applyTransition();

        setContentView(R.layout.activity_user_list);
        friendList = new ArrayList<>();
        usersList = new ArrayList<>();
        friendAL = new ArrayList<>();
        nonfriendAL = new ArrayList<>();
        recyclerView = findViewById(R.id.userList);
        firebaseHelper = new FirebaseHelper();
        spinner = (Spinner) findViewById(R.id.userspinner);
        getFriendStatus();
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item, options);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        friendRequestButton = (ImageView) findViewById(R.id.friendrequest);
        spinner.setAdapter(aa);
        setFriendButton();

        friendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserList.this, "No request available", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setAdapter(ArrayList<User> e) {
        recyclerAdapter adapter = new recyclerAdapter(e, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setUserInfo(String name, String email, String url, String uid, boolean friend) {
        if (!email.equals(firebaseHelper.getUser().getEmail())) {
            usersList.add(new User(name, email, url, uid, friend));
        }
    }


    public void getAllUsersDetails()  {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersdRef = rootRef.child("UsersDB");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Set<String> usernames = new HashSet<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.child("name").getValue().toString();
                    String email = ds.child("email").getValue().toString();
                    String url = ds.child("picture").getValue().toString();
                    String uid = ds.getKey();
                    setUserInfo(name, email, url, uid, friendList.contains(uid));
                    Log.d("User ID", name + " " + email);
                }
                setAdapter(usersList);
                getFriendsAL();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        usersdRef.addListenerForSingleValueEvent(eventListener);

    }

    public ArrayList<String> getFriendStatus() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersdRef = rootRef.child("UsersDB").child(firebaseHelper.getUid()).child("friend");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String friends = ds.getKey();
                    friendList.add(ds.getKey());
                    Log.d("Friends", friends);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        usersdRef.addListenerForSingleValueEvent(eventListener);
        getAllUsersDetails();
        return friendList;
    }

    public void getFriendsAL() {
        for (User user: usersList) {
            if (user.isFriend()) {
                friendAL.add(user);
            } else {
                nonfriendAL.add(user);
            }
        }

    }

    public void buildAlert(String title, String message) {
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(UserList.this);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton(
                "close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {
                        onBackPressed();
                    }
                });
        AlertDialog alertDialog = builder.create();

        // Show the Alert Dialog box
        alertDialog.show();
    }


    @Override
    public void onNoteCLick(int position) {
        //Toast.makeText(this, usersList.get(position).getUid(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position == 0) {
            setAdapter(usersList);
        } else if (position == 1) {
            setAdapter(friendAL);
        } else if (position == 2) {
            setAdapter(nonfriendAL);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void setFriendButton() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersdRef = rootRef.child("Requests").child(firebaseHelper.getUid());
        final int[] count = {0};

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    friendRequestButton.setImageResource(R.drawable.friendrequest);

                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        count[0] = count[0] + 1;
                    }

                    friendRequestButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(UserList.this, FriendList.class));
                        }
                    });

                } else {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        usersdRef.addListenerForSingleValueEvent(eventListener);
    }
}