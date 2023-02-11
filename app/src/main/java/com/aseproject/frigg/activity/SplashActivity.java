package com.aseproject.frigg.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.aseproject.frigg.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        Handler mHandler = new Handler();
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                mHandler.post(() -> {
                    Intent intent = new Intent(this, AuthActivity.class);
                    startActivity(intent);
                });
            } catch (Exception e) {
                // TODO: handle exception
            }
        }).start();
    }
}