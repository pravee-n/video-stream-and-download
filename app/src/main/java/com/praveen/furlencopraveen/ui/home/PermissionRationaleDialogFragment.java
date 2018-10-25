package com.praveen.furlencopraveen.ui.home;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Window;

import com.karumi.dexter.PermissionToken;
import com.praveen.furlencopraveen.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PermissionRationaleDialogFragment extends DialogFragment {

    private PermissionToken mToken;

    public static PermissionRationaleDialogFragment newInstance() {
        PermissionRationaleDialogFragment fragment = new PermissionRationaleDialogFragment();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getContext(),
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getContext());
        }

        builder.setTitle("")
                .setMessage(getString(R.string.storage_permission_message))
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mToken.cancelPermissionRequest();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mToken.continuePermissionRequest();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override public void onDismiss(DialogInterface dialog) {
                        mToken.cancelPermissionRequest();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return alertDialog;
    }

    public void setPermissionToken(PermissionToken token) {
        this.mToken = token;
    }
}
