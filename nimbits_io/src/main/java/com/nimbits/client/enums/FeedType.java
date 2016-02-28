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
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/20/12
 * Time: 4:06 PM
 */
public enum FeedType {
    error(0, "Errors"),
    status(1, "User Status"),
    system(2, "System Messages"),
    info(3, "Info"),
    data(4, "Subscription Data"),
    all(5, "Everything"),;


    private static final Map<Integer, FeedType> lookup = new HashMap<Integer, FeedType>(5);

    static {
        for (FeedType s : EnumSet.allOf(FeedType.class))
            lookup.put(s.code, s);
    }

    private final int code;
    private final String text;

    private FeedType(int code, String text) {
        this.code = code;
        this.text = text;


    }

    public int getCode() {
        return code;
    }

    public static FeedType get(int code) {
        return lookup.get(code);
    }

    public String getText() {
        return text;
    }
}
