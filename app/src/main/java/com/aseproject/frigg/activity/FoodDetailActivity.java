package com.aseproject.frigg.activity;

import android.os.Bundle;

import com.aseproject.frigg.R;
import com.aseproject.frigg.fragment.FoodDetailFragment;

public class FoodDetailActivity extends FriggActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FoodDetailFragment foodFragment = new FoodDetailFragment(getIntent().getStringExtra("TYPE"));
        getSupportFragmentManager().beginTransaction().replace(R.id.container, foodFragment).commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}