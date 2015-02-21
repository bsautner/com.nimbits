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

package com.nimbits.client.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 3/30/12
 * Time: 9:02 AM
 */
public enum FilterType {

    fixedHysteresis(0, "Fixed Hysteresis"),
    percentageHysteresis(1, "Percentage Hysteresis"),
    ceiling(2, "Ceiling"),
    floor(3, "Floor"),
    none(4, "None");

    private static final Map<Integer, FilterType> lookup = new HashMap<Integer, FilterType>(4);

    static {
        for (FilterType s : EnumSet.allOf(FilterType.class))
            lookup.put(s.code, s);
    }

    private final int code;
    private final String text;

    private FilterType(int code, String text) {
        this.code = code;
        this.text = text;


    }

    public int getCode() {
        return code;
    }

    public static FilterType get(int code) {
        return lookup.get(code);
    }

    public String getText() {
        return text;
    }


}
