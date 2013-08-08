package com.nimbits.cloudplatform.client.android;

/**
 * Created by benjamin on 8/5/13.
 */
public class AndroidControlFactory {

    public static final int SLOW_TIMER = 5000;
    public static final int SMALL_CHART_VALUES = 10;

    public static AndroidControl getInstance(int timer, int chart) {
        return new AndroidControlImpl(timer, chart);
    }

    public static AndroidControl getConservativeInstance() {
        return new AndroidControlImpl(SLOW_TIMER, SMALL_CHART_VALUES);
    }

}
