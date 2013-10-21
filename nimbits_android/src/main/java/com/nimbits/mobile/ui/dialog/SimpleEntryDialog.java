/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.mobile.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.nimbits.mobile.R;
import com.nimbits.mobile.ui.entitylist.EntityListener;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;

/**
 * Created by benjamin on 7/24/13.
 */
public class SimpleEntryDialog extends DialogFragment {
    final private Entity entity;
    private EntityListener mListener;
    final private EntityType type;
    private final Action action;
    private final String title;

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
        View view = inflater.inflate(R.layout.simple_text_entry_dialog, null);
        final TextView textView;
        if (view != null) {
            textView = (TextView) view.findViewById(R.id.entity_name);


            builder.setView(view).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (textView != null && textView.getEditableText() != null) {
                        String entry = textView.getEditableText().toString();

                        switch (action) {
                            case create:
                                EntityName name = CommonFactory.createName(entry, type);
                                mListener.onNewEntity(entity, type, name);
                                break;
                            case recordValue:
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