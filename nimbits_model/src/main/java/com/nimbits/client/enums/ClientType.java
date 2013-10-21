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

/**
 * Created by bsautner
 * User: benjamin
 * Date: 7/15/11
 * Time: 4:49 PM
 */
public enum ClientType {

    android("com.nimbits.mobile"), arduino("arduino"), other("other");
    private static final Map<String, ClientType> lookup = new HashMap<String, ClientType>(3);

    static {
        for (final ClientType s : EnumSet.allOf(ClientType.class))
            lookup.put(s.code, s);
    }

    private final String code;

    private ClientType(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ClientType get(final String code) {
        return lookup.get(code);
    }

    }
