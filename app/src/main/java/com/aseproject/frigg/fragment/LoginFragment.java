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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aseproject.frigg.R;
import com.aseproject.frigg.activity.FriggActivity;
import com.aseproject.frigg.activity.NavActivity;
import com.aseproject.frigg.common.AppSessionManager;
import com.aseproject.frigg.model.UserDetails;
import com.aseproject.frigg.service.AuthService;
import com.aseproject.frigg.service.SessionFacade;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

public class LoginFragment extends Fragment implements AuthService.AuthServicePostListener {

    private static final String TAG = "LoginFragment";
    TextInputLayout email, password;
    MaterialButton login, register;
    SessionFacade sessionFacade;
    Context context;
    SharedPreferences prefs;
    int fridgeId = 0;
    private String inviteCode;
    private String userName;
    private String userEmail;
    private Integer userId;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sessionFacade = new SessionFacade();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        email = view.findViewById(R.id.edit_email);
        password = view.findViewById(R.id.edit_password);
        login = view.findViewById(R.id.button_login);
        register = view.findViewById(R.id.button_register);
        prefs = context.getSharedPreferences(getString(R.string.fridge_id), Context.MODE_PRIVATE);
        fridgeId = prefs.getInt(getString(R.string.fridge_id), -1);
        inviteCode = prefs.getString(getString(R.string.invite_code), "");
        userName = prefs.getString(getString(R.string.user_name), "");
        userEmail = prefs.getString(getString(R.string.user_email), "");
        userId = prefs.getInt(getString(R.string.user_id), -1);
        AppSessionManager.getInstance().setFridgeId(fridgeId);
        AppSessionManager.getInstance().setInviteCode(inviteCode);
        AppSessionManager.getInstance().setName(userName);
        AppSessionManager.getInstance().setEmail(userEmail);
        AppSessionManager.getInstance().setUser_id(userId);

        if (fridgeId != -1) {
            Intent myIntent = new Intent(context, NavActivity.class);
            context.startActivity(myIntent);
        }
        handleButtonActions();
    }

    private void handleButtonActions() {
        login.setOnClickListener(view -> {
            UserDetails userDetails = new UserDetails(email.getEditText().getText().toString(), password.getEditText().getText().toString());
            sessionFacade.loginAndRegister(context, "LOGIN", this, userDetails);
        });

        register.setOnClickListener(view -> {
            RegisterFragment registerFragment = new RegisterFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();
            fragmentTransaction.replace(R.id.container, registerFragment).commit();
        });
    }

    @Override
    public void notifyPostSuccess(String response, String purpose) {
        Log.d(TAG, response);
        UserDetails details = new Gson().fromJson(response, UserDetails.class);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(getString(R.string.fridge_id), details.getFridge_id());
        editor.putString(getString(R.string.invite_code), details.getInviteCode());
        editor.putString(getString(R.string.user_name), details.getFull_name());
        editor.putString(getString(R.string.user_email), details.getEmail());
        editor.putInt(getString(R.string.user_id), details.getUser_id());
        AppSessionManager.getInstance().setInviteCode(details.getInviteCode());
        AppSessionManager.getInstance().setFridgeId(details.getFridge_id());
        AppSessionManager.getInstance().setEmail(details.getEmail());
        AppSessionManager.getInstance().setName(details.getFull_name());
        AppSessionManager.getInstance().setUser_id(details.getUser_id());
        editor.apply();
        Intent myIntent = new Intent(context, NavActivity.class);
        context.startActivity(myIntent);
    }

    @Override
    public void notifyPostError(String error, String purpose) {
        if (error != null)
            Log.d(TAG, error);
        Toast.makeText(context, "Failed to login", Toast.LENGTH_LONG);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(getString(R.string.fridge_id), -1);
        editor.apply();
    }
}