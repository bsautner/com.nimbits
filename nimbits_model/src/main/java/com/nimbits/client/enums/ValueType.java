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


public enum ValueType {

    rawData(0), statisticShard(1), counter(2), calculated(3), mobile(4), manualEntry(5), emailBatch(6), xmpp(7), nonexistent(8);

    private static final Map<Integer, ValueType> lookup = new HashMap<Integer, ValueType>(9);

    static {
        for (ValueType s : EnumSet.allOf(ValueType.class))
            lookup.put(s.code, s);
    }

    private final int code;

    private ValueType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ValueType get(int code) {
        return lookup.get(code);
    }

}
