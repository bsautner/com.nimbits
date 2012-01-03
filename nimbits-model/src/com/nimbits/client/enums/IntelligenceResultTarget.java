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
 * Date: 10/29/11
 * Time: 12:34 PM
 */
public enum IntelligenceResultTarget {
    data(1), value(2);

    private static final Map<Integer, IntelligenceResultTarget> lookup = new HashMap<Integer, IntelligenceResultTarget>();

    static {
        for (IntelligenceResultTarget s : EnumSet.allOf(IntelligenceResultTarget.class))
            lookup.put(s.getCode(), s);
    }

    private final int code;

    private IntelligenceResultTarget(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static IntelligenceResultTarget get(int code) {
        return lookup.get(code);
    }


}
