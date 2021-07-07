package com.example.digitalwellness;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class Video extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static VideoHelper all = null;
    private static VideoHelper basic = null;
    private static VideoHelper intermediate = null;
    private static VideoHelper advanced = null;
    private static VideoHelper myList = null;
    private static String[] options = {"All", "Basic", "Intermediate", "Advanced"};

    private Context context;
    private FrameLayout progressLayout;
    private CustomAdapter customAdapter;

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
        progressLayout = (FrameLayout) findViewById(R.id.progress_overlay);
        progressLayout.setVisibility(View.VISIBLE);

        // Create spinner (drop down selector)
        Spinner spinner = (Spinner) findViewById(R.id.video_spinner);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Acquire data once and store all the values.
        if (myList == null) {
            all = new VideoHelper();
            basic = new VideoHelper();
            intermediate = new VideoHelper();
            advanced = new VideoHelper();
            myList = new VideoHelper();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Video");
            reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if (snapshot != null) {
                            for (DataSnapshot data: snapshot.getChildren()) {
                                String tag = (String) data.child("Tag").getValue();
                                String title = data.getKey();
                                String thumbnail = (String) data.child("Thumbnail").getValue();
                                String videoUrl = (String) data.child("VideoUrl").getValue();
                                String link = (String) data.child("Link").getValue();

                                if (tag.equals("Advanced")) {
                                    advanced.addData(thumbnail, videoUrl, title, link);
                                } else if (tag.equals("Basic")) {
                                    basic.addData(thumbnail, videoUrl, title, link);
                                } else {
                                    intermediate.addData(thumbnail, videoUrl, title, link);
                                }
                                all.addData(thumbnail, videoUrl, title, link);
                            }
                            myList.changeData(all);
                            setLayout();
                        }
                    }
                }
            });
        } else {
            // Data already acquired, load list.
            setLayout();
        }
    }

    // Enables back button to be usable. Brings user back one page.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            (new Handler()).postDelayed(this::finish, 500);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setLayout() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        customAdapter = new CustomAdapter(myList, this);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(customAdapter);
        progressLayout.setVisibility(View.GONE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (customAdapter != null) {
            if (position == 0) {
                myList.changeData(all);
            } else if (position == 1) {
                myList.changeData(basic);
            } else if (position == 2) {
                myList.changeData(intermediate);
            } else {
                myList.changeData(advanced);
            }
            customAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    static class VideoHelper {

        private ArrayList<String> thumbnail;
        private ArrayList<String> link;
        private ArrayList<String> title;
        private ArrayList<String> videoUrl;

        public VideoHelper() {
            this.thumbnail = new ArrayList<String>();
            this.link = new ArrayList<String>();
            this.title = new ArrayList<String>();
            this.videoUrl = new ArrayList<String>();
        }

        public void addData(String thumbnail, String videoUrl, String title, String link) {
            this.thumbnail.add(thumbnail);
            this.link.add(link);
            this.title.add(title);
            this.videoUrl.add(videoUrl);
        }

        public ArrayList<String> getThumbnail() {
            return this.thumbnail;
        }

        public ArrayList<String> getLink() {
            return this.link;
        }

        public ArrayList<String> getTitle() {
            return this.title;
        }

        public ArrayList<String> getVideoUrl() {
            return this.videoUrl;
        }

        public void changeData(VideoHelper helper) {
            this.thumbnail.clear();
            this.videoUrl.clear();
            this.title.clear();
            this.link.clear();
            this.thumbnail.addAll(helper.getThumbnail());
            this.videoUrl.addAll(helper.getVideoUrl());
            this.title.addAll(helper.getTitle());
            this.link.addAll(helper.getLink());
        }
    }
}
