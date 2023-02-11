package com.aseproject.frigg.fragment;

import android.content.Context;
import android.content.Intent;
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
import com.aseproject.frigg.activity.AuthActivity;
import com.aseproject.frigg.activity.NavActivity;
import com.aseproject.frigg.model.UserDetails;
import com.aseproject.frigg.service.AuthService;
import com.aseproject.frigg.service.SessionFacade;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class RegisterFragment extends Fragment implements AuthService.AuthServicePostListener {

    private static final String TAG = "RegisterFragment";
    TextInputLayout emailET, passwordET, nameET;
    MaterialButton login, register;
    SessionFacade sessionFacade;
    Context context;
    private TextInputLayout registration_code;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
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
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailET = view.findViewById(R.id.edit_email);
        passwordET = view.findViewById(R.id.edit_password);
        nameET = view.findViewById(R.id.edit_name);
        registration_code = view.findViewById(R.id.registration_code);
        login = view.findViewById(R.id.button_login);
        register = view.findViewById(R.id.button_register);
        handleButtonActions();
    }

    private void handleButtonActions() {
        login.setOnClickListener(view -> {
            LoginFragment loginFragment = new LoginFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();
            fragmentTransaction.replace(R.id.container, loginFragment).commit();
        });

        register.setOnClickListener(view -> {
            String name = Objects.requireNonNull(nameET.getEditText()).getText().toString();
            String email = Objects.requireNonNull(emailET.getEditText()).getText().toString();
            String password = Objects.requireNonNull(passwordET.getEditText()).getText().toString();
            String invite_code = Objects.requireNonNull(registration_code.getEditText()).getText().toString();
            UserDetails userDetails = new UserDetails(email, password, name, invite_code);
            sessionFacade.loginAndRegister(context, "REGISTER", this, userDetails);
        });
    }

    @Override
    public void notifyPostSuccess(String response, String purpose) {
        Log.d(TAG, response);
        Intent myIntent = new Intent(context, AuthActivity.class);
        context.startActivity(myIntent);
    }

    @Override
    public void notifyPostError(String error, String purpose) {
        Log.d(TAG, error);
    }
}