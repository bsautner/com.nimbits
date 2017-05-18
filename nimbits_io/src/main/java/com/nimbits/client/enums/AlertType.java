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


public enum AlertType {
    LowAlert(0),
    HighAlert(1),
    IdleAlert(2),
    OK(3);


    private static final Map<Integer, AlertType> lookup = new HashMap<Integer, AlertType>(AlertType.values().length);

    static {
        for (AlertType s : EnumSet.allOf(AlertType.class))
            lookup.put(s.code, s);
    }

    private final int code;

    private AlertType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static AlertType get(int code) {

        return lookup.get(code);
    }

}
