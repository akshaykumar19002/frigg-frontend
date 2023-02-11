package com.aseproject.frigg.activity;

import android.os.Bundle;

import com.aseproject.frigg.R;
import com.aseproject.frigg.fragment.FoodFragment;
import com.aseproject.frigg.fragment.HomeFragment;
import com.aseproject.frigg.fragment.MoreFragment;
import com.aseproject.frigg.fragment.RecommendFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.atomic.AtomicReference;

public class NavActivity extends FriggActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        bottomNavigationView = findViewById(R.id.bottomNavBar);

        handleBottomNavigation();
        bottomNavigationView.setSelectedItemId(R.id.menu_item_0);
        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras() != null){
                FoodFragment foodFragment = new FoodFragment(getString(R.string.fridge_title));
                getSupportFragmentManager().beginTransaction().replace(R.id.container, foodFragment).commit();
            }
        }
    }

    private void handleBottomNavigation() {

        bottomNavigationView.setOnNavigationItemSelectedListener
                (item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_item_1:
                            FoodFragment foodFragment = new FoodFragment(getString(R.string.fridge_title));
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, foodFragment).commit();
                            break;
                        case R.id.menu_item_2:
                            foodFragment = new FoodFragment(getString(R.string.grocery_title));
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, foodFragment).commit();
                            break;
                        case R.id.menu_item_3:
                            RecommendFragment recommendFragment = new RecommendFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, recommendFragment).commit();
                            break;
                        case R.id.menu_item_4:
                            MoreFragment moreFragment = new MoreFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, moreFragment).commit();
                            break;
                        default:
                            HomeFragment fragment = new HomeFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                            break;
                    }
                    return true;
                });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}