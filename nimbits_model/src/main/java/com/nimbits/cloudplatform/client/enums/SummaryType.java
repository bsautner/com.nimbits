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

package com.nimbits.cloudplatform.client.enums;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 9:54 AM
 */
public enum SummaryType {
    average(0, "Average"),
    standardDeviation(1, "Standard Deviation"),
    skewness(2, "Skewness"),
    sum(3, "Sum"),
    variance(4, "Variance"),
    max(6, "Max"),
    min(7, "Min"),
    delta(8, "Delta")


    ;

    private static final Map<Integer, SummaryType> lookup = new HashMap<Integer, SummaryType>(8);

    static {
        for (SummaryType s : EnumSet.allOf(SummaryType.class))
            lookup.put(s.code, s);
    }

    private final int code;
    private final String text;

    private SummaryType(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public static SummaryType get(int code) {
        return lookup.get(code);
    }

    public String getText() {
        return text;
    }
}
