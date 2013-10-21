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

package com.nimbits.mobile.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import com.nimbits.mobile.R;
import com.nimbits.client.model.entity.Entity;

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

                SettingOption action = SettingOption.get(String.valueOf(list[i]));
                mListener.onSettingOptionSelected(action, entity);
                dismiss();
            }
        });


        return builder.create();
    }
}