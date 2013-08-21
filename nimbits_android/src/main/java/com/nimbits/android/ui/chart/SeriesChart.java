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

import android.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.value.Value;
import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author: Benjamin Sautner
 * Date: 12/30/12
 * Time: 9:57 AM
 */
public class SeriesChart extends AbstractChart {
    private static final long HOUR = 3600 * 1000;

    private static final long DAY = HOUR * 24;

    // private static final int HOURS = 24;

    /**
     * Returns the chart name.
     *
     * @return the chart name
     */
    public String getName() {
        return "Sensor data";
    }

    /**
     * Returns the chart description.
     *
     * @return the chart description
     */
    public String getDesc() {
        return "The temperature, as read from an outside and an inside sensors";
    }

    /**
     * Executes the chart demo.
     *
     * @param context        the context
     * @param entity
     * @param valuesResponse @return the built intent
     */
    public View execute(Context context, Entity entity, List<Value> valuesResponse) throws Exception {
        String[] titles = new String[]{entity.getName().getValue()};

        List<Date[]> dateCollection = new ArrayList<Date[]>();
        List<double[]> valueCollection = new ArrayList<double[]>();
        for (int i = 0; i < titles.length; i++) {
            Date[] dates = new Date[valuesResponse.size()];
            double[] values = new double[valuesResponse.size()];

            for (int j = 0; j < valuesResponse.size(); j++) {
                dates[j] = valuesResponse.get(j).getTimestamp();
                values[j] = valuesResponse.get(j).getDoubleValue();
            }
            dateCollection.add(dates);
            valueCollection.add(values);
        }


        int[] colors = new int[]{Color.GREEN};
        PointStyle[] styles = new PointStyle[]{PointStyle.CIRCLE};
        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);

        renderer.setBackgroundColor(R.color.transparent);
        renderer.setMarginsColor(R.color.transparent);
        int length = renderer.getSeriesRendererCount();
        for (int i = 0; i < length; i++) {
            ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
        }

        long start, end;

        end = valuesResponse.get(0).getTimestamp().getTime();
        start = valuesResponse.get(valuesResponse.size() - 1).getTimestamp().getTime();


        renderer.setXLabels(10);
        renderer.setYLabels(10);
        renderer.setShowGrid(true);
        renderer.setXLabelsAlign(Paint.Align.CENTER);
        renderer.setYLabelsAlign(Paint.Align.RIGHT);
        setChartSettings(renderer, "", "", "", start, end, -5, 30, Color.LTGRAY, Color.LTGRAY);
        return ChartFactory.getTimeChartView(context, buildDateDataset(titles, dateCollection, valueCollection),
                renderer, "h:mm:ss a");

    }

}
