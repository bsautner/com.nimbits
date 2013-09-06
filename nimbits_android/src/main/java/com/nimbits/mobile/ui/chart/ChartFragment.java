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

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.nimbits.mobile.R;
import com.nimbits.mobile.ToastHelper;
import com.nimbits.mobile.content.ContentProvider;
import com.nimbits.mobile.main.async.SeriesTask;
import com.nimbits.cloudplatform.client.model.value.Value;
import org.apache.commons.lang3.Range;

import java.util.Date;
import java.util.List;


@SuppressWarnings("unused")
public class ChartFragment extends Fragment implements SeriesTask.SeriesTaskListener {
    private final String TAG = "ChartFragment";
    private Chart seriesChart;


    private View view;
    private Range<Date> range;
    private SeriesTask.SeriesTaskListener listener;
    FrameLayout chartFrame;

    public ChartFragment() {
    }

    public ChartFragment(SeriesTask.SeriesTaskListener listener, Range<Date> range) {
        this.listener = listener;
        this.range = range;
    }

    public ChartFragment(SeriesTask.SeriesTaskListener listener) {
        this.listener = listener;
        this.range = null;
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.chart_fragment_layout, container, false);
            if (view != null) {
                chartFrame = (FrameLayout) view.findViewById(R.id.chart_frame);
                ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
                TextView title = (TextView) view.findViewById(R.id.textView);
                if (ContentProvider.currentEntity != null) {
                    if (title != null) {
                        title.setText(ContentProvider.currentEntity.getName().getValue());
                    }


                    Log.v(TAG, "view created ");
                    seriesChart = new SeriesChart();
                } else {
                    ToastHelper.show(getActivity(), "No Entity Selected");
                }
            }

        }
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        SeriesTask.getInstance(this, range).execute();
        Log.v(TAG, "onResume" + (seriesChart == null));
    }


    @Override
    public void onSuccess(List<Value> response) {
        View chart;
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        if (!response.isEmpty()) {
            try {
                chart = seriesChart.execute(getActivity(), ContentProvider.currentEntity, response);

                chart.setLongClickable(true);

                chartFrame.removeAllViews();
                chartFrame.addView(chart);
                listener.onSuccess(response);
            } catch (Exception e) {
                ToastHelper.show(getActivity(), e.getMessage());
            }
        }

    }
}
