package com.example.digitalwellness;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public class Video extends AppCompatActivity {

    private int counterUri = 0;
    private int counterTitle = 0;
    private Context context;
    private static Uri[] data;
    private static String[] titles;
    private static ExoPlayer[] players;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_video);

        TransitionBuilder transitionBuilder = new TransitionBuilder(this, R.id.videoLayout);
        transitionBuilder.applyTransition();

        super.onCreate(savedInstanceState);
        this.context = this;

        // Display back button.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Display progress bar
        FrameLayout progressLayout = (FrameLayout) findViewById(R.id.progress_overlay);
        progressLayout.setVisibility(View.VISIBLE);

        // Get storage reference.
        StorageReference reference = FirebaseStorage.getInstance().getReference();
        // Get all items in bucket.
        if (data == null || titles == null || players == null) {
            reference.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
                @Override
                public void onComplete(@NonNull Task<ListResult> task) {
                    if (task.isSuccessful()) {
                        // Initialize data size.
                        int size = task.getResult().getItems().size();
                        data = new Uri[size];
                        titles = new String[size];
                        players = new ExoPlayer[size];
                        System.out.println("data fetched");
                        // For each reference, get download url
                        for (StorageReference ref: task.getResult().getItems()) {
                            titles[counterTitle] = ref.getName();
                            ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        data[counterUri] = task.getResult();
                                        counterUri++;
                                    }
                                    // Once getting of data is completed, set up the view.
                                    if (counterUri == size) {
                                        showLayout(progressLayout);
                                    }
                                }
                            });
                            counterTitle++;
                        }
                    }
                }
            });
        } else {
            showLayout(progressLayout);
        }
    }

    private void showLayout(FrameLayout progressLayout) {
        progressLayout.setVisibility(View.GONE);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        CustomAdapter customAdapter = new CustomAdapter(data, titles, players, context);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(customAdapter);
    }

    // Enables back button to be usable. Brings user back one page.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Release all exoplayer in the page.
        if (players != null) {
            for (int i = 0; i < players.length; i++) {
                players[i].release();
            }
        }
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Pause all exoplayer in the page.
        if (players != null) {
            for (int i = 0; i < players.length; i++) {
                players[i].pause();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
