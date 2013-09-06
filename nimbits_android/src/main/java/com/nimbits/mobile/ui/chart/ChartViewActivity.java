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

package com.nimbits.mobile.ui.chart;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import com.nimbits.mobile.R;
import com.nimbits.mobile.main.async.SeriesTask;
import com.nimbits.mobile.ui.time.DatePickerFragment;
import com.nimbits.cloudplatform.client.model.value.Value;
import org.apache.commons.lang3.Range;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChartViewActivity extends Activity implements SeriesTask.SeriesTaskListener {
    private static final String TAG = "ChartViewActivity";
    private Range<Date> range = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_view_activity);
        showChartFragment();
        TextView sd = (TextView) findViewById(R.id.sd);
        sd.setInputType(InputType.TYPE_NULL);

        TextView ed = (TextView) findViewById(R.id.ed);
        ed.setInputType(InputType.TYPE_NULL);


        ImageButton refresh = (ImageButton) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChartFragment();
            }
        });


    }

    private void showChartFragment() {
        Log.v(TAG, "showChartFragment");
        WebView webView = (WebView) findViewById(R.id.webView);
        if (webView != null) {
            webView.setVisibility(View.GONE);
        }

        FrameLayout frame = (FrameLayout) findViewById(R.id.chart_frame);
        frame.removeAllViews();
        ChartFragment chartFragment = new ChartFragment(this, range);
        chartFragment.setArguments(getIntent().getExtras());
        getFragmentManager().beginTransaction().replace(R.id.chart_frame, chartFragment).commit();

        //chartFragment.getSeries(entity);
    }

    public void showDatePickerDialog(final View view) {
        DialogFragment newFragment = new DatePickerFragment(new DatePickerFragment.DatePickerListener() {
            @Override
            public void onDateSet(int year, int month, int day) {
                EditText t = (EditText) view;
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                String s = dateFormat.format(calendar.getTime());
                t.setText(s);
                if (view.getId() == R.id.sd) {
                    range = Range.between(calendar.getTime(), range == null ? new Date() : range.getMaximum());
                } else if (view.getId() == R.id.ed) {
                    range = Range.between(range == null ? new Date() : range.getMinimum(), calendar.getTime());
                }

            }
        });
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onSuccess(List<Value> response) {
        Date sd, ed;
        if (!response.isEmpty()) {
            ed = response.get(0).getTimestamp();
            sd = response.get(response.size() - 1).getTimestamp();
            range = Range.between(sd, ed);
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());

            EditText sdText = (EditText) findViewById(R.id.sd);
            sdText.setText(dateFormat.format(sd));
            EditText edText = (EditText) findViewById(R.id.ed);
            edText.setText(dateFormat.format(ed));
        }


    }
}