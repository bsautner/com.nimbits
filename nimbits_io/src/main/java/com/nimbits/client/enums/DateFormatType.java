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
 * Date: 11/20/11
 * Time: 10:05 AM
 */
public enum DateFormatType {

    unixEpoch("unix"), json("json");
    private static final Map<String, DateFormatType> lookup = new HashMap<String, DateFormatType>(2);

    static {
        for (DateFormatType s : EnumSet.allOf(DateFormatType.class))
            lookup.put(s.code, s);
    }

    private final String code;

    private DateFormatType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static DateFormatType get(String code) {
        return lookup.get(code);
    }

}
