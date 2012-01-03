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


import com.nimbits.client.model.*;

import java.util.*;

public enum Action {
    record(Const.ACTION_RECORD),
    read(Const.ACTION_READ),
    recordValue(Const.ACTION_RECORD_VALUE),
    currentValue(Const.CURRENT_VALUE),
    create(Const.ACTION_CREATE),
    delete(Const.ACTION_DELETE),
    update(Const.ACTION_UPDATE),
    readJsonValue(Const.ACTION_RECORD_JSON),
    readNote(Const.ACTION_READ_NOTE),
    readValue(Const.ACTION_READ_VALUE),
    readGps(Const.ACTION_READ_GPS),
    readJson(Const.ACTION_READ_JSON);

    private static final Map<String, Action> lookup = new HashMap<String, Action>();

    static {
        for (Action s : EnumSet.allOf(Action.class))
            lookup.put(s.getCode(), s);
    }

    private final String code;

    private Action(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Action get(String code) {
        return lookup.get(code);
    }
}
