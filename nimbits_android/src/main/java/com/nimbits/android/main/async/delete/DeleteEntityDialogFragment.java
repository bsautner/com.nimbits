package com.nimbits.android.main.async.delete;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import com.nimbits.android.R;

/**
 * Empathy Lab
 * User: benjamin
 * Date: 1/7/13
 * Time: 2:10 PM
 */
public class DeleteEntityDialogFragment extends DialogFragment {
    final Context context;

    public DeleteEntityDialogFragment(Context context) {
        this.context = context;
    }

    PositiveButtonClickHandler handler;

    public interface PositiveButtonClickHandler {
        void onPositiveDeleteEntityButtonClicked();

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        handler = (PositiveButtonClickHandler) getActivity();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirm_delete)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        handler.onPositiveDeleteEntityButtonClicked();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
