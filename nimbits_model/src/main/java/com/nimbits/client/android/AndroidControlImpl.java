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

package com.nimbits.client.android;

import java.io.Serializable;


public class AndroidControlImpl implements AndroidControl, Serializable {


    private int timer;
    private int chartValues;
    private double minVersion;

    public AndroidControlImpl() {

    }


    public AndroidControlImpl(int timer, int chartValues, double minVersion) {
        this.timer = timer;
        this.chartValues = chartValues;
        this.minVersion = minVersion;
    }


    @Override
    public int getTimer() {
        return timer;
    }

    @Override
    public int getChartValues() {
        return chartValues;
    }

    @Override
    public double getMinVersion() {
        return minVersion;
    }
}
