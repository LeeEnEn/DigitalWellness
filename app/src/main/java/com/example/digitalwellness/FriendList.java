package com.example.digitalwellness;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendList extends AppCompatActivity implements FriendsAdapter.OnNoteListener {

    private ArrayList<Friend> friendList;
    private RecyclerView recyclerView;
    private FirebaseHelper firebaseHelper;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        firebaseHelper = new FirebaseHelper();
        friendList = new ArrayList<>();
        recyclerView = findViewById(R.id.userList);
        firebaseHelper = new FirebaseHelper();

        getAllUsersDetails();

        spinner = (Spinner) findViewById(R.id.userspinner);
        spinner.setVisibility(View.GONE);

    }

    private void setAdapter() {
        FriendsAdapter adapter = new FriendsAdapter(friendList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setUserInfo(String uid, String name, String url) {
        friendList.add(new Friend(uid, name, url));
    }

    public void getAllUsersDetails()  {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersdRef = rootRef.child("Requests").child(firebaseHelper.getUid());

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        String name = ds.child("name").getValue().toString();
                        String uid = ds.getKey();
                        String url = ds.child("picture").getValue().toString();
                        setUserInfo(uid, name, url);
                    }
                } else {
                    Toast.makeText(FriendList.this, "No requests as of now", Toast.LENGTH_SHORT).show();
                }
                setAdapter();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        usersdRef.addListenerForSingleValueEvent(eventListener);

    }

    @Override
    public void onNoteCLick(int position) {

    }
}
