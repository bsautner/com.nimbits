package com.nimbits.android.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputType;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.nimbits.android.ToastHelper;
import com.nimbits.android.chart.Chart;
import com.nimbits.android.main.async.LoadEntityTask;
import com.nimbits.android.main.async.LoadValueTask;
import com.nimbits.android.main.async.PostValueTask;
import com.nimbits.android.MainActivity;
import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.cloudplatform.R;
import com.nimbits.android.chart.SeriesChart;
import com.nimbits.cloudplatform.client.enums.Action;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.enums.point.PointType;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.point.PointModel;
import com.nimbits.cloudplatform.client.model.simple.SimpleValue;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueModel;
import com.nimbits.android.main.async.SeriesTask;
import com.nimbits.android.settings.async.LocalSettingsTask;
import org.apache.commons.lang3.Range;

import java.util.Date;
import java.util.List;

/**
 * @Author: benjamin
 */
public class PointActivity extends Activity {
    public static final int VIBRATE_DURATION = 50;
    private final Chart seriesChart = new SeriesChart();
    private ImageButton saveButton;
    private ImageButton addButton;
    private ImageButton subtractButton;
    private Vibrator myVib;
    ProgressBar progressBar;
    private enum SaveType {
        add, subtract, set
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point);
        TextView name = (TextView) findViewById(R.id.entity_name);
        name.setText(Nimbits.currentEntity.getName().getValue());
        ImageView expand = (ImageView) findViewById(R.id.image_expand);
        expand.setVisibility(View.GONE);
        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        processSinglePointView();
    }
    private void getSeries(final Point point) {
        SeriesTask.getInstance(new SeriesTask.SeriesTaskListener() {
            @Override
            public void onSuccess(List<Value> response) {
                View chart;
                try {
                    if (response.isEmpty()) {

                    } else {
                        chart = seriesChart.execute(getApplicationContext(), point, response);
                        chart.setLongClickable(true);
                        FrameLayout chartFrame = (FrameLayout) findViewById(R.id.chart_frame);
                        chartFrame.removeAllViews();
                        chartFrame.addView(chart);
                    }
                } catch (Exception e) {
                    Log.e(this.getClass().getSimpleName(), e.getMessage());


                }

            }

        }).execute(point, Range.between(0, 10));
    }



    private void processSinglePointView() {


        final EditText enterData = (EditText)  findViewById(R.id.enter_data);
       //  final TextView note = (TextView) findViewById(R.id.note);
        saveButton = (ImageButton) findViewById(R.id.save_button);
        addButton = (ImageButton) findViewById(R.id.add_button);
        subtractButton = (ImageButton) findViewById(R.id.subtract_button);

        saveButton.setVisibility(View.GONE);
        addButton.setVisibility(View.GONE);
        subtractButton.setVisibility(View.GONE);
        final TextView valueView = (TextView) findViewById(R.id.value);
        final ImageView status = (ImageView) findViewById(R.id.entity_image);
        final TextView timestamp = (TextView) findViewById(R.id.timestamp);

        LoadEntityTask task =  LoadEntityTask.getInstance(new LoadEntityTask.LoadPointTaskListener<Point>() {
            @Override
            public void onSuccess(List<Point> response)  {
                if (!response.isEmpty()) {
                    Point point = response.get(0);

                    initEnterDataField(point);
                    initSaveButton(point, enterData, valueView, timestamp,  status, addButton, SaveType.add);
                    initSaveButton(point, enterData, valueView, timestamp,  status, subtractButton, SaveType.subtract);
                    initSaveButton(point, enterData, valueView, timestamp,  status, saveButton, SaveType.set);

                    LoadValueTask.getInstance(new LoadValueTask.LoadValueTaskListener() {
                        @Override
                        public void onSuccess(Value response) {
                            PointViewHelper.setViews(response, valueView, timestamp, status, SimpleValue.getEmptyInstance());
                        }
                    }).execute(point);
                    getSeries(point);

                }
            }


        });
        task.execute(Nimbits.currentEntity, PointModel.class);


    }

    //Single Point View Controls
    private void initEnterDataField(Point point) {

        final EditText enterData = (EditText) findViewById(R.id.enter_data);

        progressBar.setVisibility(View.GONE);

        switch (point.getPointType()) {

            case basic:
                enterData.setInputType(InputType.TYPE_CLASS_TEXT);
                saveButton.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.GONE);
                subtractButton.setVisibility(View.GONE);
                break;
            case cumulative:

                enterData.setInputType(InputType.TYPE_CLASS_NUMBER);
                LocalSettingsTask.getInstance(new LocalSettingsTask.LocalSettingsTaskListener() {
                    @Override
                    public void onRead(final SimpleValue<String> response) {


                        runOnUiThread(new Runnable() {
                            public void run() {
                                String text = response.getValue();
                                enterData.setText(text);


                            }
                        });
                    }
                }).execute(getApplicationContext(), Action.read, Parameters.preferedValue, enterData);
                saveButton.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.GONE);
                subtractButton.setVisibility(View.GONE);


                break;
            case timespan:
                enterData.setInputType(InputType.TYPE_CLASS_DATETIME);
                saveButton.setImageResource(android.R.drawable.ic_media_play);
                saveButton.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.GONE);
                subtractButton.setVisibility(View.GONE);
                break;

            case backend:
                enterData.setEnabled(false);
                saveButton.setVisibility(View.GONE);
                addButton.setVisibility(View.GONE);
                subtractButton.setVisibility(View.GONE);
                break;
        }
    }

    private void initSaveButton(final Point point,
                                final EditText enterData,
                                final TextView currentValue,
                                final TextView timestamp,

                                final ImageView status,
                                final ImageButton button,
                                final SaveType saveType) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = enterData.getText().toString();
                Value value = ValueModel.getInstance(SimpleValue.getInstance(data), new Date());
                myVib.vibrate(VIBRATE_DURATION);
                button.setVisibility(View.GONE);
                button.performHapticFeedback(HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                progressBar.setVisibility(View.VISIBLE);
                switch (saveType) {

                    case add:
                        value = ValueModel.getInstance(value, value.getDoubleValue());
                        break;
                    case subtract:
                        value = ValueModel.getInstance(value, value.getDoubleValue() * -1);
                        break;
                    case set:
                        value = ValueModel.getInstance(value, value.getDoubleValue());
                        break;
                }


                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(enterData.getWindowToken(), 0);

                PostValueTask.getInstance(new PostValueTask.PostValueTaskListener() {
                    @Override
                    public void onSuccess(List<Value> response) throws Exception {
                        ToastHelper.show(getApplicationContext(), getString(R.string.record_success));
                        if (point.getPointType().equals(PointType.cumulative)) {
                            // saveButton.setImageResource(android.R.drawable.ic_input_add);
                            LocalSettingsTask.getInstance().execute(getApplicationContext(), Action.create, Parameters.preferedValue, SimpleValue.getInstance(enterData.getText()));
                            currentValue.setVisibility(View.VISIBLE);
                            timestamp.setVisibility(View.VISIBLE);
                        } else {
                            enterData.setText("");
                        }
                        if (!response.isEmpty()) {
                            PointViewHelper.setViews(response.get(0), currentValue, timestamp, status, SimpleValue.getEmptyInstance());
                            // LoadValueTask.getInstance(context, currentValue, timestamp, status, SimpleValue.getInstance(point.getUnit())).setViews();
                        }
                        progressBar.setVisibility(View.GONE);
                        button.setVisibility(View.VISIBLE);
                        getSeries(point);
                    }

                    @Override
                    public void onFail(Exception ex) {
                        ToastHelper.show(getApplicationContext(), ex.getMessage());
                    }
                }).execute(Nimbits.currentEntity, value);


            }
        });


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Nimbits.currentEntity = Nimbits.getParentEntity();
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra(Parameters.refresh.getText(), false);
        startActivity(intent);
        finish();


    }
}