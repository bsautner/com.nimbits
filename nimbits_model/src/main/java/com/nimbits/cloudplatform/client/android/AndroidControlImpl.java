package com.nimbits.cloudplatform.client.android;

import java.io.Serializable;

/**
 * Created by benjamin on 8/5/13.
 */
public class AndroidControlImpl implements AndroidControl, Serializable{


    private int timer;
    private int chartValues;

    public AndroidControlImpl() {

    }


    public AndroidControlImpl(int timer, int chartValues) {
        this.timer = timer;
        this.chartValues = chartValues;
    }


    @Override
    public int getTimer() {
        return timer;
    }
    @Override
    public int getChartValues() {
        return chartValues;
    }
}
