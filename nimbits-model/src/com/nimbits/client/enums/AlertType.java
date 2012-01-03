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

package com.nimbits.client.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 6/24/11
 * Time: 1:00 PM
 */
public enum AlertType {
    LowAlert(0),
    HighAlert(1),
    IdleAlert(2),
    OK(3);


    private static final Map<Integer, AlertType> lookup = new HashMap<Integer, AlertType>();

    static {
        for (AlertType s : EnumSet.allOf(AlertType.class))
            lookup.put(s.getCode(), s);
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
