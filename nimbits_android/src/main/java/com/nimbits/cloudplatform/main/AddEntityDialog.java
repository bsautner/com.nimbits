package com.nimbits.cloudplatform.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import com.nimbits.cloudplatform.R;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.entity.Entity;

/**
 * Author: Benjamin Sautner
 * Date: 1/8/13
 * Time: 12:49 PM
 */
public class AddEntityDialog extends DialogFragment {
    final Entity parent;
    EntitySelectedDialogListener mListener;

    public AddEntityDialog(Entity currentEntity) {

        parent = currentEntity;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

      //  Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
           mListener = (EntitySelectedDialogListener) activity;

        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement EntitySelectedDialogListener");
        }
    }

    public interface EntitySelectedDialogListener {
        public void onEntityTypeSelected(EntityType type, Entity parent);

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_entity_type);

        final CharSequence[] list = EntityType.toAndroidOptionArray();

        builder.setSingleChoiceItems(list, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                EntityType type = EntityType.valueOf(String.valueOf(list[i]));
                mListener.onEntityTypeSelected(type, parent);

                dismiss();
            }
        });


        return builder.create();
    }
}