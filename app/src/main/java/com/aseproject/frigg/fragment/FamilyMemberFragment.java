package com.aseproject.frigg.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aseproject.frigg.R;
import com.aseproject.frigg.activity.FamilyMemberActivity;
import com.aseproject.frigg.activity.ProfileActivity;
import com.aseproject.frigg.adapter.FamilyMembersAdapter;
import com.aseproject.frigg.common.AppSessionManager;
import com.aseproject.frigg.common.FriggRecyclerView;
import com.aseproject.frigg.model.ConnectFamily;
import com.aseproject.frigg.model.FamilyMember;
import com.aseproject.frigg.model.FridgeId;
import com.aseproject.frigg.service.FamilyMembersService;
import com.aseproject.frigg.service.SessionFacade;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

public class FamilyMemberFragment extends Fragment implements FamilyMembersService.FamilyMemberListener,
        FamilyMembersService.FamilyMemberPostListener {

    private Button llCode;
    private Context context;
    private String inviteCode;
    private static final String PURPOSE_FAMILY_MEMBERS = "PURPOSE_FAMILY_MEMBERS";
    private static final String PURPOSE_CONNECT_FAMILY = "PURPOSE_CONNECT_FAMILY";
    private SessionFacade sessionFacade;
    private FriggRecyclerView rvFamilyMembers;
    private Button add_friends;
    private TextInputLayout edit_code;
    SharedPreferences prefs;
    private TextView tvFamilyMembers;
    private TextView tvSteps;
    private LinearLayout llSteps;
    private TextView tvConnectSteps;
    private LinearLayout llConnectSteps;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_family_memeber, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionFacade = new SessionFacade();
        ((FamilyMemberActivity) context).setTitle("Family Members");

        prefs = context.getSharedPreferences(getString(R.string.fridge_id), Context.MODE_PRIVATE);
        llCode = view.findViewById(R.id.btnCode);
        rvFamilyMembers = view.findViewById(R.id.rvFamilyMembers);
        add_friends = view.findViewById(R.id.add_friends);
        edit_code = view.findViewById(R.id.edit_code);
        tvFamilyMembers = view.findViewById(R.id.tvFamilyMembers);
        tvSteps = view.findViewById(R.id.tvSteps);
        llSteps = view.findViewById(R.id.llSteps);
        tvConnectSteps = view.findViewById(R.id.tvConnectSteps);
        llConnectSteps = view.findViewById(R.id.llConnectSteps);

        llCode.setText(AppSessionManager.getInstance().getInviteCode());
        tvSteps.setPaintFlags(tvSteps.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvConnectSteps.setPaintFlags(tvSteps.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        tvSteps.setText(context.getString(R.string.steps_to_register));
        tvConnectSteps.setText(context.getString(R.string.steps_to_connect));
        setRecyclerView();
        downloadFamilyMembers();
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        tvSteps.setOnClickListener(view -> {
            if (llSteps.getVisibility() == View.VISIBLE)
                llSteps.setVisibility(View.GONE);
            else
                llSteps.setVisibility(View.VISIBLE);
        });

        tvConnectSteps.setOnClickListener(view -> {
            if (llConnectSteps.getVisibility() == View.VISIBLE)
                llConnectSteps.setVisibility(View.GONE);
            else
                llConnectSteps.setVisibility(View.VISIBLE);
        });

        add_friends.setOnClickListener(view -> {
            ((FamilyMemberActivity) context).showActivityIndicator(context.getString(R.string.saving_data));
            ConnectFamily connectFamily = new ConnectFamily();
            connectFamily.setInvite_code(edit_code.getEditText().getText().toString());
            connectFamily.setUser_id(AppSessionManager.getInstance().getUser_id());
            sessionFacade.connectToFamily(context, PURPOSE_CONNECT_FAMILY, this, new Gson().toJson(connectFamily));
        });
    }

    private void setRecyclerView() {
        rvFamilyMembers.setHasFixedSize(true);
        rvFamilyMembers.setItemAnimator(null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvFamilyMembers.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, layoutManager.getOrientation());
        rvFamilyMembers.addItemDecoration(dividerItemDecoration);
    }


    private void downloadFamilyMembers() {
        ((FamilyMemberActivity) context).showActivityIndicator(context.getString(R.string.fetching_data));
        sessionFacade.getConnectedFamilyMembers(context, this, PURPOSE_FAMILY_MEMBERS);
    }

    @Override
    public void notifyFetchSuccess(FamilyMember[] familyMembers, String purpose) {
        ((FamilyMemberActivity) context).hideActivityIndicator();
        if (familyMembers.length == 0) {
            tvFamilyMembers.setVisibility(View.GONE);
        }
        updateUi(familyMembers);
    }

    private void updateUi(FamilyMember[] familyMembers) {
        FamilyMembersAdapter adapter = new FamilyMembersAdapter(context, familyMembers);
        rvFamilyMembers.setAdapter(adapter);

        rvFamilyMembers.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void notifyFetchError(String error, String purpose) {
        ((FamilyMemberActivity) context).hideActivityIndicator();
    }

    @Override
    public void notifyPostSuccess(String response, String purpose) {
        ((FamilyMemberActivity) context).hideActivityIndicator();
        FridgeId fridgeId = new Gson().fromJson(response, FridgeId.class);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(getString(R.string.fridge_id), fridgeId.getFridge_id());
        editor.putString(getString(R.string.invite_code), edit_code.getEditText().getText().toString());
        AppSessionManager.getInstance().setInviteCode(edit_code.getEditText().getText().toString());
        AppSessionManager.getInstance().setFridgeId(fridgeId.getFridge_id());
        Toast.makeText(context, "Fridge changed successfully!", Toast.LENGTH_LONG).show();
        ((FamilyMemberActivity) context).onBackPressed();
    }

    @Override
    public void notifyPostError(String error, String purpose) {
        ((FamilyMemberActivity) context).hideActivityIndicator();
        Toast.makeText(context, "Sorry! unable to merge.", Toast.LENGTH_LONG).show();
    }
}