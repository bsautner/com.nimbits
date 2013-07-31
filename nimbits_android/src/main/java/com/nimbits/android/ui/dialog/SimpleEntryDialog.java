package com.nimbits.android.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.nimbits.android.ui.entitylist.EntityListener;
import com.nimbits.android.R;
import com.nimbits.cloudplatform.client.enums.Action;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.common.impl.CommonFactory;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityName;

/**
 * Created by benjamin on 7/24/13.
 */
public class SimpleEntryDialog extends DialogFragment {
    final private Entity entity;
    private EntityListener mListener;
    final private EntityType type;
    private final Action action;
    private final String  title;
    public SimpleEntryDialog(Entity entity, EntityType type, Action action, String title) {
        this.type = type;
        this.entity = entity;
        this.action = action;
        this.title = title;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //  Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (EntityListener) activity;

        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement EntityListener");
        }
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_entity_dialog, null);
        final TextView textView;
        if (view != null) {
            textView = (TextView) view.findViewById(R.id.entity_name);


            builder.setView(view).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (textView != null && textView.getEditableText() != null) {
                        String entry = textView.getEditableText().toString();


                        if (action.equals(Action.create)) {
                            EntityName name = CommonFactory.createName(entry, type);
                            mListener.onNewEntity(entity, type, name);
                        }
                        else if (action.equals(Action.recordValue)) {
                            mListener.onNewValue(entity, entry);
                        }
                    }

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });

        }

        return builder.create();
    }
}