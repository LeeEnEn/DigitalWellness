package com.example.digitalwellness;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class Video extends AppCompatActivity {

    private final String[] data = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        data[0] = "https://firebasestorage.googleapis.com/v0/b/digitalwellness-72800.appspot.com/o/7%20Easy%20Exercises%20(Full%20Version%20in%20English).mp4?alt=media&token=5d2f0600-9f9c-4bb6-b678-0ebdcdc6ec0a";
        data[1] = "https://firebasestorage.googleapis.com/v0/b/digitalwellness-72800.appspot.com/o/7%20Easy%20Exercises%20(Full%20Version%20in%20English).mp4?alt=media&token=5d2f0600-9f9c-4bb6-b678-0ebdcdc6ec0a";

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        CustomAdapter customAdapter = new CustomAdapter(data, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(customAdapter);
    }
}
