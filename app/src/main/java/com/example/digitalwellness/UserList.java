package com.example.digitalwellness;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class UserList extends AppCompatActivity implements recyclerAdapter.OnNoteListener{

    private ArrayList<User> usersList;
    private ArrayList<String> friendList;
    private RecyclerView recyclerView;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(!CheckNetwork.isInternetAvailable(UserList.this)) //returns true if internet available
        {
            buildAlert("No Internet Connection", "This feature requires internet connection. Please check your Wi-Fi settings before continuing");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        friendList = new ArrayList<>();
        usersList = new ArrayList<>();
        recyclerView = findViewById(R.id.userList);
        firebaseHelper = new FirebaseHelper();
        getFriendStatus();

    }

    private void setAdapter() {
        recyclerAdapter adapter = new recyclerAdapter(usersList, this);
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
                setAdapter();

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
        Toast.makeText(this, usersList.get(position).getUid(), Toast.LENGTH_SHORT).show();
    }
}