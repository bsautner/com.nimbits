package com.nimbits.android.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import com.nimbits.cloudplatform.R;
import com.nimbits.android.MainActivity;

/**
 * Author: Benjamin Sautner
 * Date: 12/29/12
 * Time: 3:34 PM
 */
public class SettingsActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        final SharedPreferences settings = getApplicationContext().getSharedPreferences(getString(R.string.app_name), 0);
        final SharedPreferences.Editor editor = settings.edit();


        final RadioButton button = (RadioButton) findViewById(R.id.use_gps_switch);


        button.setChecked(settings.getBoolean(Settings.gps.getCode(), false));


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setChecked(!settings.getBoolean(Settings.gps.getCode(), false));
                editor.remove(Settings.gps.getCode());
                editor.putBoolean(Settings.gps.getCode(), button.isChecked());
                editor.commit();


            }
        });


        final EditText baseUrl = (EditText) findViewById(R.id.base_url);
        baseUrl.setInputType(InputType.TYPE_TEXT_VARIATION_URI);

        baseUrl.setText(settings.getString(getString(R.string.base_url_setting), getString(R.string.base_url)));
        baseUrl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    String base_url = baseUrl.getText().toString();
                    editor.putString(getString(R.string.base_url_setting), base_url);
                }
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {


        finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

}