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