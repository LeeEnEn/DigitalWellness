package com.example.digitalwellness;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

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

        myCharts.showScreenGraph(firebaseHelper.getScreen(), firebaseHelper.getAxis());
        progressLayout.setVisibility(View.GONE);

    }
}
