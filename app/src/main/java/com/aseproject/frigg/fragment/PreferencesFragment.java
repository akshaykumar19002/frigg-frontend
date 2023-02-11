package com.aseproject.frigg.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aseproject.frigg.R;
import com.aseproject.frigg.activity.NavActivity;
import com.aseproject.frigg.activity.PreferencesActivity;
import com.aseproject.frigg.common.AppSessionManager;
import com.aseproject.frigg.model.UserDetails;
import com.aseproject.frigg.service.PreferencesService;
import com.aseproject.frigg.service.SessionFacade;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreferencesFragment extends Fragment implements PreferencesService.PreferencesServicePostListener, PreferencesService.PreferencesServiceGetListener {

    Context context;
    SessionFacade sessionFacade;
    private static final String TAG = "PreferencesFragment";
    private static final String GET_PREFERENCES_PURPOSE = "GET_PREFERENCES";
    private static final String SET_PREFERENCES_PURPOSE = "SET_PREFERENCES";

    private LinearLayout saveButton;
    private TextInputLayout noOfNotificationsET;

    private int userId;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sessionFacade = new SessionFacade();
        userId = AppSessionManager.getInstance().getUser_id();
        sessionFacade.getPreferences(context, GET_PREFERENCES_PURPOSE, this, userId);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preferences, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((PreferencesActivity) context).setTitle("Preferences");

        saveButton = view.findViewById(R.id.save_btn);
        noOfNotificationsET = view.findViewById(R.id.edit_no_of_notifications);

        saveButton.setOnClickListener((v) -> {
            int noOfNotifications = Integer.parseInt(noOfNotificationsET.getEditText().getText().toString());
            sessionFacade.setPreferences(context, SET_PREFERENCES_PURPOSE, this, userId, noOfNotifications);
        });
    }

    @Override
    public void notifyFetchSuccess(UserDetails userDetails, String purpose) {
        noOfNotificationsET.getEditText().setText(userDetails.getNo_of_notifications().toString());
    }

    @Override
    public void notifyFetchError(String error, String purpose) {
        Log.d(TAG, "Failed to fetch no of notifications");
    }

    @Override
    public void notifyPostSuccess(String response, String purpose) {
        Toast.makeText(context, "Preferences updated successfully", Toast.LENGTH_LONG).show();
        MoreFragment fragment = new MoreFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment).commit();
    }

    @Override
    public void notifyPostError(String error, String purpose) {
        Log.e(TAG, "Failed to update no of notifications: " + error);
    }
}