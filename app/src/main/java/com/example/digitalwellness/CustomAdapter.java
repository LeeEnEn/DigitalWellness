package com.example.digitalwellness;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private final Context context;
    private final String[] titles;
    private final Uri[] data;

    public CustomAdapter(Uri[] data, String[] titles, Context context) {
        this.context = context;
        this.data = data;
        this.titles = titles;
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
        holder.playerView.setPlayer(exoPlayer);
        holder.textView.setText(titles[position]);
        MediaItem mediaItem = MediaItem.fromUri(data[position]);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.seekTo(0, 0);
        exoPlayer.prepare();
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final PlayerView playerView;
        private final TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.playerView = (PlayerView) itemView.findViewById(R.id.video);
            this.textView = (TextView) itemView.findViewById(R.id.video_title);
        }
    }
}
