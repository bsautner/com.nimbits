package com.nimbits.android.ui.chart;

import android.app.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.nimbits.android.content.ContentProvider;
import com.nimbits.android.main.async.SeriesTask;
import com.nimbits.android.ui.entitylist.EntityListAdapter;
import com.nimbits.android.ui.entitylist.EntityListener;
import com.nimbits.android.R;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.value.Value;
import org.apache.commons.lang3.Range;

import java.util.List;

/**
 * Created by benjamin on 7/25/13.
 */
public class ChartFragment extends Fragment {
    private final String TAG = "ChartFragment";
    private Chart seriesChart;
    private ListView list;
    private EntityListener listener;
    private View view;
    private EntityListAdapter adapter;
    FrameLayout chartFrame;
    public ChartFragment() {
    }

    public static final ChartFragment getInstance(Activity activity) {
        ChartFragment instance = new ChartFragment();
        instance.listener = (EntityListener) activity;
        return instance;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.chart_fragment_layout, container, false);
        chartFrame = (FrameLayout) view.findViewById(R.id.chart_frame);
        Log.v(TAG, "view created " + (adapter == null));
        seriesChart = new SeriesChart();

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        getSeries(ContentProvider.currentEntity);
        Log.v(TAG, "onResume" + (seriesChart == null));
    }

    private void getSeries(final Entity point) {
        SeriesTask.getInstance(new SeriesTask.SeriesTaskListener() {
            @Override
            public void onSuccess(List<Value> response) {
                View chart;
                try {
                    if (response.isEmpty()) {

                    } else {
                        chart = seriesChart.execute(getActivity(), point, response);
                        chart.setLongClickable(true);

                        chartFrame.removeAllViews();
                        chartFrame.addView(chart);
                    }
                } catch (Exception e) {
                    Log.e(this.getClass().getSimpleName(), e.getMessage());


                }

            }

        }).execute(point, Range.between(0, 10));
    }
}
