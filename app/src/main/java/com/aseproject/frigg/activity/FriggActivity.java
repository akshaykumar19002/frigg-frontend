package com.aseproject.frigg.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.aseproject.frigg.R;
import com.aseproject.frigg.fragment.LoadingDialogFragment;

public class FriggActivity extends AppCompatActivity {

    protected LoadingDialogFragment activityIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frigg);
    }

    public void showActivityIndicator(final String message) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        // Make us non-modal, so that others can receive touch events.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        // ...but notify us that it happened.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        Handler mainHandler = new Handler(this.getMainLooper());
        mainHandler.post(() -> {
            FragmentManager manager = getSupportFragmentManager();
            activityIndicator = LoadingDialogFragment.newInstance(message);
            activityIndicator.show(manager, "");
        });
    }

    public void hideActivityIndicator() {

        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Handler mainHandler = new Handler(this.getMainLooper());
        if (activityIndicator != null) {
            activityIndicator.dismissAllowingStateLoss();
        }
        mainHandler.post(() -> {
            if (activityIndicator != null) {
                activityIndicator.dismissAllowingStateLoss();
            }
        });
    }
}