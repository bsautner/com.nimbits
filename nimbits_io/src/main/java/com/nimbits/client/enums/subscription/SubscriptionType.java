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

package com.nimbits.client.enums.subscription;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


public enum SubscriptionType {

    none(-1, "None"),
    anyAlert(1, "Any Alert"),
    high(2, "High Alert"),
    low(3, "Low Alert"),
    idle(4, "Idle Alert"),
    newValue(5, "New Values"),
    deltaAlert(7, "Delta Alert"),
    increase(8, "Value Increases"),
    decrease(9, "Value Decreases");

    private static final Map<Integer, SubscriptionType> lookup = new HashMap<Integer, SubscriptionType>(7);

    static {
        for (SubscriptionType s : EnumSet.allOf(SubscriptionType.class))
            lookup.put(s.code, s);
    }

    private final int code;
    private final String text;

    private SubscriptionType(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public static SubscriptionType get(int code) {
        return lookup.get(code);
    }
}
