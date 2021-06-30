package com.example.digitalwellness;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TimeBar;

import java.sql.Time;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private final Context context;
    private final String[] titles;
    private final Uri[] data;
    private final ExoPlayer[] players;

    public CustomAdapter(Uri[] data, String[] titles, ExoPlayer[] players, Context context) {
        this.context = context;
        this.data = data;
        this.titles = titles;
        this.players = players;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SimpleExoPlayer exoPlayer = new SimpleExoPlayer.Builder(context).build();
        // Set each video in an array.
        players[position] = exoPlayer;
        // Get title from array.
        holder.textView.setText(titles[position]);
        // Get uri from array.
        MediaItem mediaItem = MediaItem.fromUri(data[position]);
        exoPlayer.setMediaItem(mediaItem);
        // Do not auto play.
        exoPlayer.setPlayWhenReady(false);
        // Start from beginning.
        exoPlayer.seekTo(0, 0);
        exoPlayer.prepare();
        holder.playerView.setPlayer(exoPlayer);
        // Set fast forward time to 10 secs.
        holder.playerView.setFastForwardIncrementMs(10000);
        // Set rewind time to 10 secs.
        holder.playerView.setRewindIncrementMs(10000);
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        int position = holder.getAdapterPosition();

    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final PlayerView playerView;
        private final TextView textView;
        private final TimeBar timeBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.playerView = (PlayerView) itemView.findViewById(R.id.video);
            this.textView = (TextView) itemView.findViewById(R.id.video_title);
            this.timeBar = (TimeBar) itemView.findViewById(R.id.timebar);
        }
    }
}
