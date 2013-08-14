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

package com.nimbits.android.ui.chart;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.nimbits.android.R;
import com.nimbits.android.content.ContentProvider;
import com.nimbits.android.main.async.SeriesTask;
import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.value.Value;
import org.apache.commons.lang3.Range;

import java.util.List;

/**
 * Created by benjamin on 7/25/13.
 */
public class ChartFragment extends Fragment {
    private final String TAG = "ChartFragment";
    private Chart seriesChart;


    private View view;

    FrameLayout chartFrame;
    public ChartFragment() {
    }



    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.chart_fragment_layout, container, false);
            chartFrame = (FrameLayout) view.findViewById(R.id.chart_frame);
            TextView title = (TextView) view.findViewById(R.id.textView);

            if (title != null) {
                title.setText(ContentProvider.currentEntity.getName().getValue());
            }


            Log.v(TAG, "view created ");
            seriesChart = new SeriesChart();
        }
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        getSeries(ContentProvider.currentEntity, Nimbits.getControl().getChartValues());
        Log.v(TAG, "onResume" + (seriesChart == null));
    }

    private void getSeries(final Entity point, final int count) {
        SeriesTask.getInstance(new SeriesTask.SeriesTaskListener() {
            @Override
            public void onSuccess(List<Value> response) {
                View chart;
                try {
                    if (!response.isEmpty()) {
                        chart = seriesChart.execute(getActivity(), point, response);
                        chart.setLongClickable(true);

                        chartFrame.removeAllViews();
                        chartFrame.addView(chart);
                    }
                } catch (Exception e) {
                    Log.e(this.getClass().getSimpleName(), e.getMessage());


                }

            }

        }).execute(point, Range.between(0, count));
    }
}
