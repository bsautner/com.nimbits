/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.client.enums.point;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/29/11
 * Time: 1:36 PM
 */
public enum PointType implements Serializable {
    basic(0),
    backend(3),
    cumulative(4),
    timespan(5);


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
