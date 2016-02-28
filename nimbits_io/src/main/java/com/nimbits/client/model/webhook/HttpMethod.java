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

public enum HttpMethod implements Serializable {
    POST(1), GET(2), DELETE(3), PUT(4);

    private static final Map<Integer, HttpMethod> lookup = new HashMap<Integer, HttpMethod>(4);

    static {
        for (HttpMethod s : EnumSet.allOf(HttpMethod.class))
            lookup.put(s.code, s);
    }

    private final int code;

    HttpMethod(int i) {
        this.code = i;

    }

    public int getCode() {
        return code;
    }

    public static HttpMethod lookup(int i) {
        return lookup.get(i);
    }
}
