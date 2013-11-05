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




import java.util.*;

public enum Action {
    admin(ActionConstants.ACTION_ADMIN),
    calculation(ActionConstants.ACTION_CALC),
    record(ActionConstants.ACTION_RECORD),
    read(ActionConstants.ACTION_READ),
    recordValue(ActionConstants.ACTION_RECORD_VALUE),
    currentValue(ActionConstants.CURRENT_VALUE),
    create(ActionConstants.ACTION_CREATE),
    createmissing(ActionConstants.ACTION_CREATE_MISSING),
    delete(ActionConstants.ACTION_DELETE),
    update(ActionConstants.ACTION_UPDATE),
    readJsonValue(ActionConstants.ACTION_RECORD_JSON),
    readNote(ActionConstants.ACTION_READ_NOTE),
    readValue(ActionConstants.ACTION_READ_VALUE),
    readGps(ActionConstants.ACTION_READ_GPS),
    readJson(ActionConstants.ACTION_READ_JSON),
    subscribe(ActionConstants.ACTION_SUBSCRIBE),
    android(ActionConstants.ACTION_ANDROID),
    none(ActionConstants.ACTION_NONE),
    report(ActionConstants.ACTION_REPORT),
    diagram(ActionConstants.ACTION_DIAGRAM),
    start(ActionConstants.ACTION_START),
    user(ActionConstants.ACTION_USER),
    category(ActionConstants.ACTION_CATEGORY),
    point(ActionConstants.ACTION_POINT),
    refresh(ActionConstants.ACTION_REFRESH),
    expand(ActionConstants.ACTION_EXPAND),
    logout(ActionConstants.ACTION_LOGOUT),
    xmpp(ActionConstants.ACTION_XMPP),
    save(ActionConstants.ACTION_SAVE),
    alert(ActionConstants.ACTION_ALERT),
    idle(ActionConstants.ACTION_IDLE),
    value(ActionConstants.ACTION_VALUE),
    onOff(ActionConstants.ACTION_ONOFF),
    download(ActionConstants.ACTION_DOWNLOAD),
    validateExists(ActionConstants.VALIDATE_EXISTS),
    list(ActionConstants.LIST),
    listen(ActionConstants.LISTEN),
    notify(ActionConstants.NOTIFY);
    private static final Map<String, Action> lookup = new HashMap<String, Action>(Action.values().length);

    static {
        for (Action s : EnumSet.allOf(Action.class))
            lookup.put(s.code, s);
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

    private static class ActionConstants {
        protected static final String ACTION_ADMIN = "admin";
        protected static final String ACTION_CALC = "calc";
        protected static final String ACTION_ALERT = "alert";
        protected static final String ACTION_CREATE = "create";
        protected static final String ACTION_CREATE_MISSING = "createmissing";
        protected static final String ACTION_DELETE = "delete";
        protected static final String ACTION_DOWNLOAD = "download";
        protected static final String ACTION_IDLE = "idle";
        protected static final String ACTION_ONOFF = "onoff";
        protected static final String ACTION_UPDATE = "update";
        protected static final String ACTION_VALUE = "value";
        protected static final String ACTION_RECORD = "record";
        protected static final String ACTION_READ = "read";
        protected static final String ACTION_RECORD_VALUE = "recordvalue";
        protected static final String ACTION_RECORD_JSON = "recordjson";
        protected static final String ACTION_READ_NOTE = "readnote";
        protected static final String ACTION_READ_VALUE = "readvalue";
        protected static final String ACTION_READ_GPS = "readgps";
        protected static final String ACTION_READ_JSON = "readjson";
        protected static final String ACTION_SUBSCRIBE = "subscribe";
        protected static final String ACTION_NONE = "none";
        protected static final String ACTION_ANDROID = "com.nimbits.mobile";
        protected static final String ACTION_REPORT = "report";
        protected static final String ACTION_DIAGRAM = "diagram";
        protected static final String ACTION_START = "start";
        protected static final String ACTION_USER = "user";
        protected static final String ACTION_CATEGORY = "category";
        protected static final String ACTION_POINT = "point";
        protected static final String ACTION_REFRESH = "refresh" ;
        protected static final String ACTION_EXPAND = "expand";
        protected static final String ACTION_LOGOUT = "logout";
        protected static final String ACTION_XMPP = "xmpp";
        protected static final String ACTION_ADD_CHART = "addchart";
        protected static final String ACTION_SAVE = "save";
        protected static final String CURRENT_VALUE = "currentvalue";
        public static final String VALIDATE_EXISTS = "exists";
        public static final String LIST = "list";
        public static final String LISTEN = "listen";
        public static final String NOTIFY = "notify";
        private ActionConstants() {
        }
    }

}
