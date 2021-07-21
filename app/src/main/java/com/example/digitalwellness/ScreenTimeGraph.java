package com.example.digitalwellness;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

public class ScreenTimeGraph extends AppCompatActivity {

    private FirebaseHelper firebaseHelper;
    private MyCharts myCharts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_time_graph);

        FrameLayout progressLayout = (FrameLayout) findViewById(R.id.progress_overlay);
        progressLayout.setVisibility(View.VISIBLE);

        firebaseHelper = new FirebaseHelper();
        myCharts = new MyCharts(this);

//        myCharts.showScreenGraph(firebaseHelper.getScreen(), firebaseHelper.getAxis());
        progressLayout.setVisibility(View.GONE);

    }
}
