package com.aseproject.frigg.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aseproject.frigg.R;
import com.aseproject.frigg.activity.AuthActivity;
import com.aseproject.frigg.activity.FamilyMemberActivity;
import com.aseproject.frigg.activity.NavActivity;
import com.aseproject.frigg.activity.PreferencesActivity;
import com.aseproject.frigg.activity.ProfileActivity;


public class MoreFragment extends Fragment {
    private TextView tvSetPreference;
    private TextView tvAddFamilyMembers;
    private TextView tvProfile;
    private TextView tvSignout;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((NavActivity) context).setTitle("More");
        tvSetPreference = view.findViewById(R.id.tvSetPreference);
        tvAddFamilyMembers = view.findViewById(R.id.tvAddFamilyMembers);
        tvProfile = view.findViewById(R.id.tvProfile);
        tvSignout = view.findViewById(R.id.signout);

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        tvSetPreference.setOnClickListener(view -> {
            Intent intent = new Intent(context, PreferencesActivity.class);
            startActivity(intent);
        });

        tvProfile.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            startActivity(intent);
        });

        tvSignout.setOnClickListener(view -> {
            SharedPreferences preferences = context.getSharedPreferences(getString(R.string.fridge_id), Context.MODE_PRIVATE);
            preferences.edit().clear().commit();
            Intent intent = new Intent(context, AuthActivity.class);
            startActivity(intent);
            ((NavActivity) context).finish();
        });

        tvAddFamilyMembers.setOnClickListener(view -> {
            Intent intent = new Intent(context, FamilyMemberActivity.class);
            startActivity(intent);

        });
    }
}