package com.aseproject.frigg.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aseproject.frigg.R;
import com.aseproject.frigg.activity.FoodDetailActivity;
import com.aseproject.frigg.activity.ProfileActivity;
import com.aseproject.frigg.common.AppSessionManager;
import com.aseproject.frigg.model.ChangePassword;
import com.aseproject.frigg.service.FamilyMembersService;
import com.aseproject.frigg.service.SessionFacade;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

public class ProfileFragment extends Fragment implements FamilyMembersService.FamilyMemberPostListener {
    private TextView name;
    private TextView email;
    private Context context;
    private TextInputLayout old_password;
    private TextInputLayout new_password;
    private TextInputLayout confirm_password;
    private LinearLayout btn_change_password;
    private SessionFacade sessionFacade;
    private static final String CHANGE_PASSWORD = "CHANGE_PASSWORD";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        ((ProfileActivity) context).setTitle("Profile");
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        btn_change_password = view.findViewById(R.id.btn_change_password);
        old_password = view.findViewById(R.id.old_password);
        new_password = view.findViewById(R.id.new_password);
        confirm_password = view.findViewById(R.id.confirm_password);

        prepareView();
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        btn_change_password.setOnClickListener(view -> {
            String oldPassword = old_password.getEditText().getText().toString();
            String newPassword = new_password.getEditText().getText().toString();
            String confirmPassword = confirm_password.getEditText().getText().toString();
            if(oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty() ||
                    oldPassword.equals(newPassword) || !newPassword.equals(confirmPassword)) {
                Toast.makeText(context, "Please enter accurate values!", Toast.LENGTH_LONG).show();
            } else {
                ((ProfileActivity) context).showActivityIndicator(context.getString(R.string.saving_data));
                ChangePassword changePassword = new ChangePassword();
                changePassword.setConfirm_password(confirmPassword);
                changePassword.setNew_password(newPassword);
                changePassword.setOld_password(oldPassword);
                changePassword.setId(AppSessionManager.getInstance().getUser_id());
                sessionFacade.changePassword(context, new Gson().toJson(changePassword), this, CHANGE_PASSWORD);
            }
        });
    }

    private void prepareView() {
        name.setText("Name: "+AppSessionManager.getInstance().getName());
        email.setText("Email: "+AppSessionManager.getInstance().getEmail());
    }

    @Override
    public void notifyPostSuccess(String response, String purpose) {
        ((ProfileActivity) context).hideActivityIndicator();
        Toast.makeText(context, "Password changed successfully", Toast.LENGTH_LONG).show();
        ((ProfileActivity) context).onBackPressed();
    }

    @Override
    public void notifyPostError(String error, String purpose) {
        ((ProfileActivity) context).hideActivityIndicator();
    }
}