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

/**
 * Created by benjamin on 8/5/13.
 */
public class AndroidControlFactory {

    public static final int SLOW_TIMER = 5000;
    public static final int SMALL_CHART_VALUES = 10;
    public static final double CRAZY_VERSION = 1000.0;
    public static AndroidControl getInstance(int timer, int chart, double minVersion) {
        return new AndroidControlImpl(timer, chart, minVersion);
    }

    public static AndroidControl getConservativeInstance() {
        return new AndroidControlImpl(SLOW_TIMER, SMALL_CHART_VALUES, CRAZY_VERSION);
    }

}
