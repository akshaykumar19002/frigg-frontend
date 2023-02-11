package com.aseproject.frigg.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.aseproject.frigg.R;
import com.aseproject.frigg.fragment.FamilyMemberFragment;

public class FamilyMemberActivity extends FriggActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FamilyMemberFragment familyMemberFragment = new FamilyMemberFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, familyMemberFragment).commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}