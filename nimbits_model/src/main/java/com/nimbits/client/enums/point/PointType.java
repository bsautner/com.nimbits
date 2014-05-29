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

package com.nimbits.client.enums.point;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


public enum PointType implements Serializable {
    basic(0),
    backend(3),
    cumulative(4),
    timespan(5),
    flag(6);


    private static final Map<Integer, PointType> lookup = new HashMap<Integer, PointType>(PointType.values().length);

    static {
        for (PointType s : EnumSet.allOf(PointType.class))
            lookup.put(s.code, s);
    }

    private final int code;


    private PointType(final int code ) {
        this.code = code;

    }


    public int getCode() {
        return code;
    }

    public static PointType get(int code) {
        return lookup.get(code);
    }

}
