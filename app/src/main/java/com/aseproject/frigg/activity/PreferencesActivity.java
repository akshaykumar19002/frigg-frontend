package com.aseproject.frigg.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.aseproject.frigg.R;
import com.aseproject.frigg.fragment.DishRecipeFragment;
import com.aseproject.frigg.fragment.PreferencesFragment;

public class PreferencesActivity extends FriggActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        PreferencesFragment fragment = new PreferencesFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}