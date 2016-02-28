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

package com.nimbits.client.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 7/12/11
 * Time: 5:39 PM
 */
public enum DisplayType {
    Category(1),
    Point(2),
    HighAlarm(3),
    LowAlarm(4),
    IdleAlarm(5),
    Diagram(6);

    private static final Map<Integer, DisplayType> lookup = new HashMap<Integer, DisplayType>(6);

    static {
        for (DisplayType s : EnumSet.allOf(DisplayType.class))
            lookup.put(s.code, s);
    }

    private final int code;

    private DisplayType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static DisplayType get(int code) {
        return lookup.get(code);
    }

}
