package com.nimbits.cloudplatform.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import com.nimbits.cloudplatform.R;
import com.nimbits.cloudplatform.client.model.entity.Entity;

/**
 * Author: Benjamin Sautner
 * Date: 1/8/13
 * Time: 12:49 PM
 */
public class SettingOptionsDialog extends DialogFragment {
    final Entity entity;
    OptionSelectedListener mListener;

    public SettingOptionsDialog(Entity currentEntity) {

        entity = currentEntity;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (OptionSelectedListener) activity;

        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SettingOptionsDialog");
        }
    }

    public interface OptionSelectedListener {
        public void onSettingOptionSelected(SettingOption option, Entity entity);

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_setting_option);

        final CharSequence[] list = SettingOption.toAndroidOptionArray(entity.getEntityType());

        builder.setSingleChoiceItems(list, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                SettingOption action =  SettingOption.get(String.valueOf(list[i]));
                mListener.onSettingOptionSelected(action, entity);
                dismiss();
            }
        });



        return builder.create();
    }
}