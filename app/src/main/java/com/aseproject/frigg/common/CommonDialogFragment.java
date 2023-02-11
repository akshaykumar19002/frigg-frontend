package com.aseproject.frigg.common;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.aseproject.frigg.R;

public class CommonDialogFragment extends DialogFragment {
    public static String TAG = "PurchaseConfirmationDialog";
    private DialogInterface dialogInterface;
    private String title = "";
    private String message = "";
    private String posBtn = "";
    private String negBtn = "";

    public CommonDialogFragment(DialogInterface context, String title, String message, String posBtn, String ngBtn) {
        this.dialogInterface = context;
        this.title = title;
        this.message = message;
        this.posBtn = posBtn;
        this.negBtn = ngBtn;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.common_dialog_fragment, null);
        TextView title = view.findViewById(R.id.tvTitle);
        TextView msg = view.findViewById(R.id.tvMessage);
        LinearLayout posBtn = view.findViewById(R.id.llPos);
        TextView posBtnTxt = posBtn.findViewById(R.id.btn_text);
        LinearLayout negBtn = view.findViewById(R.id.llNeg);
        TextView negBtnTxt = negBtn.findViewById(R.id.btn_text);


        title.setText(this.title);
        msg.setText(this.message);
        posBtnTxt.setText(this.posBtn);
        negBtnTxt.setText(this.negBtn);

        if (this.posBtn.isEmpty()) {
            posBtn.setVisibility(View.GONE);
        }
        if (this.negBtn.isEmpty()) {
            negBtn.setVisibility(View.GONE);
        }

        posBtn.setOnClickListener(view1 -> {
            dismiss();
            dialogInterface.onSelectedPosBtn();
        });

        negBtn.setOnClickListener(view12 -> {
            dismiss();
            dialogInterface.onSelectedNegBtn();
        });

        builder.setView(view);
        return builder.create();
    }

    public interface DialogInterface {
        void onSelectedPosBtn();

        void onSelectedNegBtn();
    }

}