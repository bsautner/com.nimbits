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

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public enum ProtectionLevel implements Serializable {
    onlyMe(0, "Only Me"),
    onlyConnection(1, "Connections"),
    everyone(2, "Everyone");


    private static final Map<Integer, ProtectionLevel> lookup = new HashMap<Integer, ProtectionLevel>(3);

    static {
        for (ProtectionLevel s : EnumSet.allOf(ProtectionLevel.class))
            lookup.put(s.code, s);
    }

    private final int code;
    private final String text;

    ProtectionLevel(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public static ProtectionLevel get(int code) {
        return lookup.get(code);
    }

    public String getText() {
        return text;
    }


}
