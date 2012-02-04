/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.android;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;

import java.io.UnsupportedEncodingException;

public class CustomDialog extends Dialog {
    public interface ReadyListener {
        public void ready(String name) throws UnsupportedEncodingException, NimbitsException;
    }


    private final String prompt;

    String getEntry() {
        return entry;
    }

    void setEntry(String entry) {
        this.entry = entry;
    }

    private String entry;

    private final ReadyListener readyListener;
    private EditText etName;

    public CustomDialog(Context context, String prompt,
                        ReadyListener readyListener) {
        super(context);
        // this.name = name;
        this.prompt = prompt;
        this.readyListener = readyListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catagory_dialog);
        setTitle(prompt);
        Button buttonOK = (Button) findViewById(R.id.Button01);
        buttonOK.setOnClickListener(new OKListener());
        etName = (EditText) findViewById(R.id.EditText01);
    }

    private class OKListener implements android.view.View.OnClickListener {
        public void onClick(View v) {
            setEntry(String.valueOf(etName.getText()));

            try {
                try {
                    readyListener.ready(getEntry());
                } catch (NimbitsException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            } catch (UnsupportedEncodingException e) {
                Log.e(Const.N, e.getMessage());
            }
            CustomDialog.this.dismiss();
        }
    }

}