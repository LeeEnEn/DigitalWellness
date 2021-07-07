package com.example.digitalwellness;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class VideoPlayer extends AppCompatActivity {

    private ExoPlayer exoPlayer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        // Get media item from intent.
        MediaItem mediaItem = MediaItem.fromUri(getIntent().getStringExtra("uri"));
        // Get title from intent.
        String title = getIntent().getStringExtra("title");
        // Display back button.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(title);
        // Initialize player view.
        PlayerView playerView = (PlayerView) findViewById(R.id.video);


        // Set full screen landscape.
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) playerView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;

        // Initialize exoplayer.
        exoPlayer = new SimpleExoPlayer.Builder(getApplicationContext()).build();
        exoPlayer.setMediaItem(mediaItem);
        // Set auto play.
        exoPlayer.setPlayWhenReady(true);
        // Start from beginning.
        exoPlayer.seekTo(0, 0);
        exoPlayer.prepare();

        playerView.setPlayer(exoPlayer);
        // Set fast forward time to 10 secs.
        playerView.setFastForwardIncrementMs(10000);
        // Set rewind time to 10 secs.
        playerView.setRewindIncrementMs(10000);
    }

    // Enables back button to be usable. Brings user back one page.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            exoPlayer.release();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exoPlayer.release();
    }

    @Override
    public void onPause() {
        super.onPause();
        exoPlayer.pause();
    }
}
