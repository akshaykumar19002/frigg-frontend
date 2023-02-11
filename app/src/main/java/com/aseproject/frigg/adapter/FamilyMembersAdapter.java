package com.aseproject.frigg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aseproject.frigg.R;
import com.aseproject.frigg.fragment.FamilyMemberFragment;
import com.aseproject.frigg.model.FamilyMember;
import com.aseproject.frigg.model.FoodItem;


public class FamilyMembersAdapter extends RecyclerView.Adapter<FamilyMembersAdapter.FamilyMembersHolder> {


    private final Context context;
    private final FamilyMember[] familyMembers;

    public FamilyMembersAdapter(Context context, FamilyMember[] familyMembers) {
        this.context = context;
        this.familyMembers = familyMembers;
    }

    @NonNull
    @Override
    public FamilyMembersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new FamilyMembersAdapter.FamilyMembersHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull FamilyMembersHolder holder, int position) {
        holder.bind(familyMembers[position]);
    }

    @Override
    public int getItemCount() {
        return familyMembers.length;
    }

    public class FamilyMembersHolder extends RecyclerView.ViewHolder {

        private final TextView tvMemberName;
        private final TextView tvMemberEmail;

        private FamilyMembersHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.family_member, parent, false));
            tvMemberName = itemView.findViewById(R.id.tvMemberName);
            tvMemberEmail = itemView.findViewById(R.id.tvMemberEmail);
        }

        public void bind(FamilyMember familyMember) {
            tvMemberName.setText(familyMember.getFull_name());
            tvMemberEmail.setText(familyMember.getEmail());
        }
    }

}
