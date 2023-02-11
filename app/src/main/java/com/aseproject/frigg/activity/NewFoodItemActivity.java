package com.aseproject.frigg.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.aseproject.frigg.R;
import com.aseproject.frigg.fragment.NewFoodItemFragment;

public class NewFoodItemActivity extends FriggActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_food_item);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NewFoodItemFragment foodFragment = new NewFoodItemFragment(getIntent().getStringExtra("LIST_TYPE"));
        getSupportFragmentManager().beginTransaction().replace(R.id.container, foodFragment).commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}