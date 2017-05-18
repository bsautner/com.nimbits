/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.client.enums.point;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


/**
 * Point types can be set in the point's property menu on the nimbits console.
 * <ul>
 * <li>basic - stores values as they come in</li>
 * <li>backend - system point, do not use</li>
 * <li>cumulative - adds the new value to the last recorded value</li>
 * <li>timespan - deprecated, do not use<li>
 * <li>flag - has two possible values, 1 or 0 for on and off.  Any value not equal to 0 will result in a 1</li>
 * <li>high - only records a value if higher than the previous one</li>
 * <li>low - only records a value if lower than the previous one</li>
 * </ul>
 */

public enum PointType implements Serializable {
    basic(0),
    backend(3),
    cumulative(4),
    timespan(5),
    flag(6),
    high(7),
    low(8);


    private static final Map<Integer, PointType> lookup = new HashMap<Integer, PointType>(PointType.values().length);

    static {
        for (PointType s : EnumSet.allOf(PointType.class))
            lookup.put(s.code, s);
    }

    private final int code;


    PointType(final int code) {
        this.code = code;

    }


    public int getCode() {
        return code;
    }

    public static PointType get(int code) {
        return lookup.get(code);
    }

}
