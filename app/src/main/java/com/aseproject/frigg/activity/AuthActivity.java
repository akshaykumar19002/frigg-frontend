package com.aseproject.frigg.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.aseproject.frigg.R;
import com.aseproject.frigg.fragment.FoodFragment;
import com.aseproject.frigg.fragment.LoginFragment;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        LoginFragment loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, loginFragment).commit();
    }
}