package com.example.digitalwellness;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public class Video extends AppCompatActivity {

    private int counterUri = 0;
    private int counterTitle = 0;
    private Context context;
    private Uri[] data;
    private String[] titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
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
        reference.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if (task.isSuccessful()) {
                    // Initialize data size.
                    int size = task.getResult().getItems().size();
                    data = new Uri[size];
                    titles = new String[size];
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
                                    progressLayout.setVisibility(View.GONE);
                                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                                    CustomAdapter customAdapter = new CustomAdapter(data, titles, context);
                                    recyclerView.setHasFixedSize(true);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    recyclerView.setAdapter(customAdapter);
                                }
                            }
                        });
                        counterTitle++;
                    }
                }
            }
        });
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

//    private void initializePlayer() {
//        playerView = (PlayerView) findViewById(R.id.video);
//        exoPlayer = new SimpleExoPlayer.Builder(this).build();
//        playerView.setPlayer(exoPlayer);
//        MediaItem mediaItem = MediaItem.fromUri("https://firebasestorage.googleapis.com/v0/b/digitalwellness-72800.appspot.com/o/Video-7_Sit-down_exercises_(English).mp4?alt=media&token=02b2392d-9ba6-4387-a5d5-39dffa7dc915");
//        exoPlayer.setMediaItem(mediaItem);
//        exoPlayer.setPlayWhenReady(playWhenReady);
//        exoPlayer.seekTo(currentWindow, playbackPosition);
//        exoPlayer.prepare();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if (Util.SDK_INT >= 24) {
//            initializePlayer();
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        hideSystemUi();
//        if ((Util.SDK_INT < 24 || exoPlayer == null)) {
//            initializePlayer();
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (Util.SDK_INT < 24) {
//            releasePlayer();
//        }
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (Util.SDK_INT >= 24) {
//            releasePlayer();
//        }
//    }
//
//    @SuppressLint("InlinedApi")
//    private void hideSystemUi() {
//        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//    }
//
//    private void releasePlayer() {
//        if (exoPlayer != null) {
//            playWhenReady = exoPlayer.getPlayWhenReady();
//            playbackPosition = exoPlayer.getCurrentPosition();
//            currentWindow = exoPlayer.getCurrentWindowIndex();
//            exoPlayer.release();
//            exoPlayer = null;
//        }
//    }
}
