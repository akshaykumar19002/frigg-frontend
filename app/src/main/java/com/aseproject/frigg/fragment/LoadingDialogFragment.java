package com.aseproject.frigg.fragment;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.aseproject.frigg.R;

public class LoadingDialogFragment extends DialogFragment {

    private static final String TAG = LoadingDialogFragment.class.getSimpleName();
    private String mMessage;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static LoadingDialogFragment newInstance(String message) {

        LoadingDialogFragment fragment = new LoadingDialogFragment();
        fragment.mMessage = message;
        return fragment;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final ViewGroup nullParent = null;
        View v = inflater.inflate(R.layout.fragment_loading_dialog, nullParent);
        setCancelable(false);

        builder.setView(v);

        TextView mTextView = v.findViewById(R.id.activityTextView);
        mTextView.setText(this.mMessage);

        return builder.create();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Log.e(TAG, "error" + e);
        }
    }

}
