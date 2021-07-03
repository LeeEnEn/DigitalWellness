package com.example.digitalwellness;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;

public class TransitionBuilder extends Activity {

    private Activity activity;
    private int id;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    TransitionBuilder(Activity activity, int id) {
        this.activity = activity;
        this.id = id;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void applyTransition() {
        setUpTransitions();
        buildTransitions();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private MaterialContainerTransform buildTransitions() {
        MaterialContainerTransform materialTransform = new MaterialContainerTransform();
        materialTransform.addTarget(id);
        materialTransform.setDuration(300);
        return materialTransform;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setUpTransitions() {
        activity.setEnterSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
        activity.getWindow().setSharedElementEnterTransition(buildTransitions());
        activity.getWindow().setExitTransition(buildTransitions());
        activity.getWindow().setReenterTransition(buildTransitions());
    }

}
