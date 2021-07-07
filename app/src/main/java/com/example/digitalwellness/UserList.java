package com.example.digitalwellness;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserList extends AppCompatActivity {

    private ListView listView;
    private FirebaseHelper firebaseHelper;
    private List<String> userList = new ArrayList<>();
    private MyPreference myPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);


        firebaseHelper = new FirebaseHelper();
        myPreference = new MyPreference(this, "friends");
        listView = findViewById(R.id.userList);

        List<String> arrayList = new ArrayList<String>(myPreference.getFriends());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(UserList.this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);



    }
}