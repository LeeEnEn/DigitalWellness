package com.example.digitalwellness;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<String> title;
    private final ArrayList<String> link;
    private final ArrayList<String> videoUri;
    private final ArrayList<String> thumbnail;
    private static Video.VideoHelper videoHelper = null;

    public CustomAdapter(Video.VideoHelper helper, Context context) {
        this.context = context;
        this.thumbnail = helper.getThumbnail();
        this.title = helper.getTitle();
        this.link = helper.getLink();
        this.videoUri = helper.getVideoUri();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load(thumbnail.get(position)).into(holder.imageView);
        holder.textView.setText(title.get(position));
        // More details button.
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Courtesy of:");
                builder.setMessage(link.get(position));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        // Opens a new page with corresponding video.
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoPlayer.class);
                intent.putExtra("uri", videoUri.get(position));
                intent.putExtra("title", title.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {

    }

    @Override
    public int getItemCount() {
        return title.size();
    }

    public void filter(String text, TextView view, Video.VideoHelper helper) {
        if (videoHelper == null) {
            videoHelper = helper;
        }
        if (text.isEmpty()) {
            resetData(videoHelper);
        } else {
            text = text.toLowerCase();
            clearData();
            for (int i = 0; i < videoHelper.getTitle().size(); i++) {
                if (videoHelper.getTitle().get(i).toLowerCase().contains(text)) {
                    addData(i);
                }
            }
            if (title.size() == 0) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }
        notifyDataSetChanged();
    }

    private void addData(int i) {
        this.title.add(videoHelper.getTitle().get(i));
        this.thumbnail.add(videoHelper.getThumbnail().get(i));
        this.link.add(videoHelper.getLink().get(i));
        this.videoUri.add(videoHelper.getVideoUri().get(i));
    }

    private void clearData() {
        this.title.clear();
        this.thumbnail.clear();
        this.link.clear();
        this.videoUri.clear();
    }

    private void resetData(Video.VideoHelper helper) {
        clearData();
        this.thumbnail.addAll(helper.getThumbnail());
        this.videoUri.addAll(helper.getVideoUri());
        this.title.addAll(helper.getTitle());
        this.link.addAll(helper.getLink());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;
        private final ImageView imageView;
        private final Button button;
        private final RelativeLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.video_title);
            this.imageView = (ImageView) itemView.findViewById(R.id.video_image);
            this.button = (Button) itemView.findViewById(R.id.video_details);
            this.layout = (RelativeLayout) itemView.findViewById(R.id.recycler_list_item_layout);
        }
    }
}
