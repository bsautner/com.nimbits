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

package com.nimbits.client.enums.subscription;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


public enum SubscriptionNotifyMethod {
    none(-1, "None"),
    email(0, "Email"),
    instantMessage(3, "Instant Message (XMPP)"),
    cloud(5, "Android Notification (GCM)"),
    socket(6, "Connected Sockets"),
    webhook(7, "Web Hook"),
    proximity(8, "Proximity Points");



    private static final Map<Integer, SubscriptionNotifyMethod> lookup = new HashMap<Integer, SubscriptionNotifyMethod>(5);

    static {
        for (SubscriptionNotifyMethod s : EnumSet.allOf(SubscriptionNotifyMethod.class))
            lookup.put(s.code, s);
    }

    private final int code;
    private final String text;

    SubscriptionNotifyMethod(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public int getCode() {
        return code;
    }

    public static SubscriptionNotifyMethod get(int code) {
        SubscriptionNotifyMethod method = lookup.get(code);
        return method == null ? SubscriptionNotifyMethod.none : method;
    }

}
