package com.nimbits.android.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import com.nimbits.android.ToastHelper;
import com.nimbits.android.main.async.AddUpdateEntityTask;
import com.nimbits.android.settings.async.PointSettingsTask;
import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.cloudplatform.R;
import com.nimbits.cloudplatform.client.enums.FilterType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.enums.point.PointType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.point.PointModel;
import com.nimbits.cloudplatform.client.model.point.PointModelFactory;
import com.nimbits.android.MainActivity;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.List;


/**
 * Author: Benjamin Sautner
 * Date: 1/12/13
 * Time: 11:19 AM
 */
public class PointSettingsActivity extends Activity {
    private Point point;
    private EditText unit;
    private RadioGroup pointTypes;
    private Entity entity;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress);
        Intent intent = getIntent();
        Entity entity = (Entity) intent.getSerializableExtra(Parameters.entity.getText());


        if (!StringUtils.isEmpty(entity.getKey())) {

            PointSettingsTask.getInstance(new PointSettingsTask.PointSettingsTaskListener() {
                @Override
                public void onSuccess(List<Point> response) {
                    if (!response.isEmpty()) {
                        point = response.get(0);
                        setContentView(R.layout.point_settings);
                        initView();
                        TextView name = (TextView) findViewById(R.id.entity_name);
                        TextView currentValue = (TextView) findViewById((R.id.value));
                        ImageView expand = (ImageView) findViewById(R.id.image_expand);
                        ImageView entityImage = (ImageView) findViewById(R.id.entity_image);
                        currentValue.setVisibility(View.GONE);
                        expand.setVisibility(View.GONE);

                        name.setText(point.getName().getValue());


                        switch (point.getPointType()) {
                            case basic:
                                pointTypes.check(R.id.basic_point_type);
                                break;
                            case cumulative:
                                pointTypes.check(R.id.cumulative_point_type);
                                break;
                            case timespan:
                                pointTypes.check(R.id.timespan_point_type);
                                break;
                        }


                        unit.setInputType(InputType.TYPE_CLASS_TEXT);
                        unit.setText(point.getUnit());


                    }
                }

            }).execute(entity);


        } else {

                setContentView(R.layout.point_settings);
                initView();
                point = PointModelFactory.createPointModel(entity, 0.0, 90, "", 0.0, false, false, false, 0, false, FilterType.none, 0.0, false, PointType.basic, 0, false, 0.0);
                pointTypes.check(R.id.basic_point_type);

        }


        //


    }


    private void initView() {
        pointTypes = (RadioGroup) findViewById(R.id.point_type);
        unit = (EditText) findViewById(R.id.unit);
        initSaveButton();
    }

    @SuppressWarnings("unchecked")
    private void initSaveButton() {
        ImageButton save = (ImageButton) findViewById(R.id.save_button);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RadioButton selected = (RadioButton) findViewById(pointTypes.getCheckedRadioButtonId());
                point.setUnit(unit.getText().toString());


                PointType type = PointType.valueOf(String.valueOf(selected.getText()));
                point.setPointType(type);


                AddUpdateEntityTask.getInstance(new AddUpdateEntityTask.AddUpdateEntityTaskListener() {

                    @Override
                    public void onSuccess(List response) {
                        if (response.isEmpty()) {
                            ToastHelper.show(getApplicationContext(), "something went wrong...");
                        } else {
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            intent.putExtra(Parameters.entity.getText(), (Serializable) response.get(0));
                            Nimbits.tree.add((Entity) response.get(0));
                            intent.putExtra(Parameters.refresh.getText(), true);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFail(Exception ex) {
                        ToastHelper.show(getApplicationContext(), ex.getMessage());
                    }
                }).execute(point, PointModel.class, StringUtils.isEmpty(point.getKey()));
                setContentView(R.layout.progress);


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