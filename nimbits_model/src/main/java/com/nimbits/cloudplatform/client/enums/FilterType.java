/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.client.enums;

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
