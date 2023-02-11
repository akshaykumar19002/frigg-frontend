package com.aseproject.frigg.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.aseproject.frigg.R;
import com.aseproject.frigg.fragment.DishRecipeFragment;
import com.aseproject.frigg.fragment.FoodDetailFragment;
import com.aseproject.frigg.model.FoodItem;

import java.io.Serializable;
import java.util.List;

public class DishRecipeActivity extends FriggActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_recipe);

        DishRecipeFragment dishRecipeFragment = new DishRecipeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, dishRecipeFragment).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}