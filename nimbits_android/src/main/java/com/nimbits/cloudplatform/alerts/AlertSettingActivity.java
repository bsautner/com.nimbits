package com.nimbits.cloudplatform.alerts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import com.nimbits.cloudplatform.R;
import com.nimbits.cloudplatform.ToastHelper;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.point.PointModel;
import com.nimbits.cloudplatform.main.MainActivity;
import com.nimbits.cloudplatform.main.async.AddUpdateEntityTask;
import com.nimbits.cloudplatform.settings.async.PointSettingsTask;

import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 1/15/13
 * Time: 12:41 PM
 */
public class AlertSettingActivity extends Activity {
    private Point point;
    private Entity entity;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress);
        Intent intent = getIntent();
        entity = (Entity) intent.getSerializableExtra(Parameters.entity.getText());

        PointSettingsTask.getInstance(new PointSettingsTask.PointSettingsTaskListener() {
            @Override
            public void onSuccess(List<Point> response) {
                setContentView(R.layout.alert_settings);

                displayPoint(response);


            }

        }).execute(entity);


    }

    private void displayPoint(List<Point> response) {
        final ImageButton saveButton = (ImageButton) findViewById(R.id.save_button);

        final TextView high = (TextView) findViewById(R.id.alert_high_value);
        final CheckBox highOn = (CheckBox) findViewById(R.id.alert_high_enabled);
        high.setInputType(InputType.TYPE_CLASS_NUMBER);

        final TextView low = (TextView) findViewById(R.id.alert_low_value);
        final CheckBox lowOn = (CheckBox) findViewById(R.id.alert_low_enabled);
        low.setInputType(InputType.TYPE_CLASS_NUMBER);

        final TextView idle = (TextView) findViewById(R.id.alert_idle_value);
        final CheckBox idleOn = (CheckBox) findViewById(R.id.alert_idle_enabled);
        idle.setInputType(InputType.TYPE_CLASS_NUMBER);

        final TextView delta = (TextView) findViewById(R.id.alert_delta_value);
        final CheckBox deltaOn = (CheckBox) findViewById(R.id.alert_delta_enabled);
        final TextView deltaSeconds = (TextView) findViewById(R.id.alert_delta_seconds);
        delta.setInputType(InputType.TYPE_CLASS_NUMBER);
        deltaSeconds.setInputType(InputType.TYPE_CLASS_NUMBER);


        if (!response.isEmpty()) {
            point = response.get(0);


            high.setText(String.valueOf(point.getHighAlarm()));
            highOn.setChecked(point.isHighAlarmOn());


            low.setText(String.valueOf(point.getLowAlarm()));
            lowOn.setChecked(point.isLowAlarmOn());

            idle.setText(String.valueOf(point.getIdleSeconds()));
            idleOn.setChecked(point.isIdleAlarmOn());


            delta.setText(String.valueOf(point.getDeltaAlarm()));
            deltaSeconds.setText(String.valueOf(point.getDeltaSeconds()));
            deltaOn.setChecked(point.isDeltaAlarmOn());
        }


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                point.setHighAlarmOn(highOn.isChecked());
                point.setHighAlarm(Double.valueOf(high.getText().toString()));

                point.setLowAlarmOn(lowOn.isChecked());
                point.setLowAlarm(Double.valueOf(low.getText().toString()));

                point.setIdleAlarmOn(idleOn.isChecked());
                point.setIdleSeconds(Integer.valueOf(idle.getText().toString()));

                point.setDeltaAlarmOn(deltaOn.isChecked());
                point.setDeltaAlarm(Double.valueOf(delta.getText().toString()));
                point.setDeltaSeconds(Integer.valueOf(deltaSeconds.getText().toString()));
                setContentView(R.layout.progress);

                AddUpdateEntityTask.getInstance(new AddUpdateEntityTask.AddUpdateEntityTaskListener() {

                    @Override
                    public void onSuccess(List response) {
                        ToastHelper.show(getApplicationContext(), "Point updated");
                        setContentView(R.layout.alert_settings);
                        displayPoint(response);
                    }

                    @Override
                    public void onFail(Exception ex) {
                        ToastHelper.show(getApplicationContext(), ex.getMessage());
                        finish();
                    }
                }).execute(point, PointModel.class, false);

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra(Parameters.entity.getText(), entity);
        intent.putExtra(Parameters.refresh.getText(), true);
        startActivity(intent);
        finish();
    }

}