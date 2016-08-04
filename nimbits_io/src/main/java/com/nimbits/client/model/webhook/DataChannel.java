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

package com.nimbits.client.model.webhook;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum DataChannel implements Serializable {
    none(0), number(1), data(2), meta(3), timestamp(4), gps(5), object(6);

    private static final Map<Integer, DataChannel> lookup = new HashMap<Integer, DataChannel>(5);

    static {
        for (DataChannel s : EnumSet.allOf(DataChannel.class))
            lookup.put(s.code, s);
    }

    private final int code;

    DataChannel(int i) {
        this.code = i;

    }

    public int getCode() {
        return code;
    }

    public static DataChannel lookup(int i) {
        return lookup.get(i);
    }
}
