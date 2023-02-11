package com.aseproject.frigg.activity;

import android.os.Bundle;

import com.aseproject.frigg.R;
import com.aseproject.frigg.fragment.FridgeToGroceryFragment;

public class FridgeToGroceryActivity extends FriggActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fridge_to_grocery);


        FridgeToGroceryFragment fragment = new FridgeToGroceryFragment();
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