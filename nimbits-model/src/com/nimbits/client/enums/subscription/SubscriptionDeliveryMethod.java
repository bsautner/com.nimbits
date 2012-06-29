/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.enums.subscription;

import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 6/24/11
 * Time: 1:00 PM
 */
public enum SubscriptionDeliveryMethod {
    none(-1),
    email(0),
    facebook(1),
    twitter(2),
    instantMessage(3),
    stream(4);



    private static final Map<Integer, SubscriptionDeliveryMethod> lookup = new HashMap<Integer, SubscriptionDeliveryMethod>(6);

    static {
        for (SubscriptionDeliveryMethod s : EnumSet.allOf(SubscriptionDeliveryMethod.class))
            lookup.put(s.code, s);
    }

    private final int code;

    private SubscriptionDeliveryMethod(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static SubscriptionDeliveryMethod get(int code) {
        return lookup.get(code);
    }

}
