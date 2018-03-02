package com.innovativeproposals.inventorypokus2;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

// tu je priklad
// https://guides.codepath.com/android/using-dialogfragment

/**
 * Created by Lubos on 01.03.18.
 */

public class MyAlertDialogFragmentOK extends DialogFragment implements View.OnClickListener{

    public static final String ARG_TITLE = "AlertDialog.Title";
    public static final String ARG_MESSAGE = "AlertDialog.Message";
   // private EditText mEditText;
    private TextView tvMessage;
    private Button btOK;



    public MyAlertDialogFragmentOK() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below

    }

    public static MyAlertDialogFragmentOK newInstance(String title) {
        MyAlertDialogFragmentOK frag = new MyAlertDialogFragmentOK();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        tvMessage = (TextView) view.findViewById(R.id.lbl_message);
        btOK = (Button)view.findViewById(R.id.buttonOK);
        btOK.setOnClickListener(this);

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        tvMessage.setText(title);


        // Show soft keyboard automatically and request focus to field
        // mEditText.requestFocus();

        // vyvolanie klavesnice
        // getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }


    @Override
    public void onClick(View v) {
        dismiss();

    }
}
