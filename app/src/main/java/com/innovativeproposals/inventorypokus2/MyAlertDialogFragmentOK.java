package com.innovativeproposals.inventorypokus2;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Lubos on 01.03.18.
 */

public class MyAlertDialogFragmentOK extends DialogFragment {

    public static final String ARG_TITLE = "AlertDialog.Title";
    public static final String ARG_MESSAGE = "AlertDialog.Message";

    public static void showAlert(String title, String message, Fragment targetFragment) {
        DialogFragment dialog = new MyAlertDialogFragmentOK();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        dialog.setArguments(args);
        dialog.setTargetFragment(targetFragment, 0);
        dialog.show(targetFragment.getFragmentManager(), "tag");
    }

    public MyAlertDialogFragmentOK() {}

 //   @NonNull
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args.getString(ARG_TITLE, "");
        String message = args.getString(ARG_MESSAGE, "");

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
                    }
                })
                .create();
    }
}
