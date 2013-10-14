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

package com.nimbits.cloudplatform.server.transactions.cache;

import com.nimbits.cloudplatform.client.enums.MemCacheKey;
import com.nimbits.cloudplatform.client.enums.SettingType;

/**
 * Created by benjamin on 10/9/13.
 */
public class BaseCache {

    public static final String GENERATED_KEY = "GENERATED_KEY";
    private final static String LEGAL_CHARS = "[0-9A-Za-z._-]{0,100}";
    private final static String SAFE_REPLACEMENT = "_";
    private final static int HOLD_TIME = 10000;
    protected String generateKey(final String key) {
        if (key.startsWith(GENERATED_KEY)) {
            return key; //duplicate call;
        }
        else {
           return GENERATED_KEY +  SettingType.serverVersion.getDefaultValue() + MemCacheKey.setting + getSafeNamespaceKey(key);
        }

    }


    private static String getSafeNamespaceKey(final String key) {

        final StringBuilder sb = new StringBuilder(key.length());
        for (char c : key.toCharArray()) {
            if (String.valueOf(c).matches(LEGAL_CHARS)) {
                sb.append(c);
            }
            else {
                sb.append(SAFE_REPLACEMENT);
            }
        }
        return sb.toString();
    }
}
