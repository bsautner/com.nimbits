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

package com.nimbits.cloudplatform.client.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/28/12
 * Time: 2:18 PM
 */
public enum Parameters {
    apikey(ParamConstants.PARAM_API_KEY),
    action(ParamConstants.PARAM_ACTION),
    autoscale(ParamConstants.PARAM_AUTO_SCALE),
    blobkey(ParamConstants.PARAM_BLOB_KEY),
    category(ParamConstants.PARAM_CATEGORY),
    client(ParamConstants.PARAM_CLIENT),
    code(ParamConstants.PARAM_CODE),
    count(ParamConstants.PARAM_COUNT),
    diagram(ParamConstants.PARAM_DIAGRAM),
    blob(ParamConstants.PARAM_BLOB),
    email(ParamConstants.PARAM_EMAIL),
    ed(ParamConstants.PARAM_END_DATE),
    exp(ParamConstants.PARAM_EXP),
    format(ParamConstants.PARAM_FORMAT),
    fromAddress(ParamConstants.PARAM_FROM_ADDRESS),
    comGoogle(ParamConstants.PARAM_GOOGLE_COM),
    host(ParamConstants.PARAM_HOST),
    id(ParamConstants.PARAM_ID),
    idle(ParamConstants.PARAM_IDLE),
    inContent(ParamConstants.PARAM_IN_CONTENT),
    isLoggedIn(ParamConstants.PARAM_IS_LOGGED_IN),
    json(ParamConstants.PARAM_JSON),
    pointJson(ParamConstants.PARAM_JSON_POINT),
    pointUser(ParamConstants.PARAM_JSON_USER),
    valueJson(ParamConstants.PARAM_JSON_VALUE),
    key(ParamConstants.PARAM_KEY),
    lat(ParamConstants.PARAM_LAT),
    lng(ParamConstants.PARAM_LNG),
    name(ParamConstants.PARAM_NAME),
    note(ParamConstants.PARAM_NOTE),
    oauth_token(ParamConstants.PARAM_OAUTH),
    password(ParamConstants.PARAM_PASSWORD),
    path(ParamConstants.PARAM_PATH),
    point(ParamConstants.PARAM_POINT),
    points(ParamConstants.PARAM_POINTS),
    secret(ParamConstants.PARAM_SECRET),
    seg(ParamConstants.PARAM_SEGMENT),
    sd(ParamConstants.PARAM_START_DATE),
    timestamp(ParamConstants.PARAM_TIMESTAMP),
    rToken(ParamConstants.PARAM_TOKEN),
    contentType(ParamConstants.PARAM_CONTENT_TYPE),
    html(ParamConstants.PARAM_HTML),
    no(ParamConstants.PARAM_NO),
    yes(ParamConstants.PARAM_YES),
    entityType(ParamConstants.PARAM_ENTITY_TYPE),
    url(ParamConstants.PARAM_URL),
    uuid(ParamConstants.PARAM_UUID),
    value(ParamConstants.PARAM_VALUE),
    verbose(ParamConstants.PARAM_VERBOSE),
    genkey(ParamConstants.PARAM_GENKEY),
    out(ParamConstants.PARAM_OUT),
    i(ParamConstants.PARAM_I),
    data(ParamConstants.PARAM_DATA),
    user(ParamConstants.PARAM_USER),
    result(ParamConstants.PARAM_RESULT),
    input(ParamConstants.PARAM_INPUT),
    loop(ParamConstants.PARAM_LOOP),
    search(ParamConstants.PARAM_SEARCH),
    offset(ParamConstants.PARAM_OFFSET),
    entity(ParamConstants.PARAM_ENTITY),
    type(ParamConstants.PARAM_ENTITY_TYPE),
    channel(ParamConstants.PARAM_CHANNEL),
    description(ParamConstants.PARAM_DESCRIPTION),
    fileName(ParamConstants.PARAM_FILE_NAME),
    dirty(ParamConstants.PARAM_DIRTY),
    acsid(ParamConstants.PARAM_ACSID),
    myFile(ParamConstants.PARAM_MY_FILE),
    fileId(ParamConstants.PARAM_FILE_ID),
    uploadTypeHiddenField(ParamConstants.PARAM_UPLOAD_TYPE_HIDDEN_FIELD),
    emailHiddenField(ParamConstants.PARAM_EMAIL_HIDDEN_FIELD),
    listen(ParamConstants.PARAM_LISTEN),
    appid(ParamConstants.PARAM_APP_ID),
    record(ParamConstants.ACTION_RECORD),
    server(ParamConstants.PARAM_SERVER),
    protection(ParamConstants.PARAM_PROTECTION),
    parent(ParamConstants.PARAM_PARENT),
    instance(ParamConstants.PARAM_INSTANCE),
    location(ParamConstants.PARAM_LOCATION),
    domain(ParamConstants.PARAM_DOMAIN),
    hd(ParamConstants.PARAM_DOMAIN),
    protocol(ParamConstants.PROTOCOL),
    keys(ParamConstants.PARAM_KEYS),
    lastUpdate(ParamConstants.LAST_UPDATED),
    refresh(ParamConstants.REFRESH), preferedValue(ParamConstants.PREFERRED_VALUE), owner(ParamConstants.OWNER),
    session(ParamConstants.SESSION);

    private static final Map<String, Parameters> lookup = new HashMap<String, Parameters>(100);

    static {
        for (Parameters s : EnumSet.allOf(Parameters.class))
            lookup.put(s.text, s);
    }

    private final String text;



    private Parameters(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static Parameters get(String text) {
        return lookup.get(text);
    }

    @Override
    public String toString() {
        return text;
    }

    private static class ParamConstants {
        private static final String PARAM_API_KEY = "apikey";
        private static final String PARAM_ACTION = "action";
        private static final String PARAM_AUTO_SCALE = "autoscale";
        private static final String PARAM_BLOB_KEY = "blob-key";
        private static final String PARAM_CATEGORY = "category";
        private static final String PARAM_CLIENT = "client";
        private static final String PARAM_CODE = "code";
        private static final String PARAM_COUNT = "count";
        private static final String PARAM_DIAGRAM = "diagram";
        private static final String PARAM_BLOB = "blob";
        private static final String PARAM_EMAIL = "email";
        private static final String PARAM_END_DATE = "ed";
        private static final String PARAM_EXP = "exp";
        private static final String PARAM_FORMAT = "format";
        private static final String PARAM_FROM_ADDRESS = "fromAddress";
        private static final String PARAM_GOOGLE_COM = "com.google";
        private static final String PARAM_HOST = "host";
        private static final String PARAM_ID = "id";
        private static final String PARAM_IDLE = "idle";
        private static final String PARAM_IN_CONTENT = "inContent";
        private static final String PARAM_IS_LOGGED_IN = "isLoggedIn";
        private static final String PARAM_JSON = "json";
        private static final String PARAM_JSON_POINT = "pointJson";
        private static final String PARAM_JSON_USER = "pointUser";
        private static final String PARAM_JSON_VALUE = "valueJson";
        private static final String PARAM_KEY = "key";
        private static final String PARAM_LAT = "lat";
        private static final String PARAM_LNG = "lng";
        private static final String PARAM_NAME = "name";
        private static final String PARAM_NOTE = "note";
        private static final String PARAM_OAUTH = "oauth_token";
        private static final String PARAM_PASSWORD = "password";
        private static final String PARAM_PATH = "path";
        private static final String PARAM_POINT = "point";
        private static final String PARAM_POINTS = "points";
        private static final String PARAM_SECRET = "secret";
        private static final String PARAM_SEGMENT = "seg";
        private static final String PARAM_START_DATE = "sd";
        private static final String PARAM_TIMESTAMP = "timestamp";
        private static final String PARAM_TOKEN = "rToken";
        private static final String PARAM_CONTENT_TYPE = "Content-Type";
        private static final String PARAM_HTML = "html";
        private static final String PARAM_NO = "no";
        private static final String PARAM_YES = "no";
        private static final String PARAM_URL = "url";
        private static final String PARAM_UUID = "uuid";
        private static final String PARAM_VALUE = "value";
        private static final String PARAM_VERBOSE = "verbose";
        private static final String PARAM_GENKEY = "genkey";
        private static final String PARAM_OUT = "out";
        private static final String PARAM_I = "i";
        private static final String PARAM_DATA = "data";
        private static final String PARAM_USER = "user";
        private static final String PARAM_RESULT = "Result";
        private static final String PARAM_INPUT = "input";
        private static final String PARAM_LOOP = "loop";
        private static final String PARAM_SEARCH = "search";
        private static final String PARAM_OFFSET = "offset";
        private static final String PARAM_ENTITY = "entity";
        private static final String PARAM_ENTITY_TYPE = "type";
        private static final String PARAM_CHANNEL = "channel";
        private static final String PARAM_DESCRIPTION = "description";
        private static final String PARAM_FILE_NAME = "fn";
        private static final String PARAM_DIRTY = "dirty";
        private static final String PARAM_ACSID = "ACSID";
        private static final String PARAM_MY_FILE = "myFile";
        private static final String PARAM_FILE_ID = "diagramId";
        private static final String PARAM_UPLOAD_TYPE_HIDDEN_FIELD = "uploadTypeHiddenField";
        private static final String PARAM_EMAIL_HIDDEN_FIELD = "emailHiddenField";
        private static final String PARAM_LISTEN = "listen";
        private static final String PARAM_APP_ID = "appid";
        private static final String ACTION_RECORD = "record";
        private static final String PARAM_SERVER = "server";
        private static final String PARAM_PROTECTION = "protection";
        private static final String PARAM_PARENT = "parent";
        private static final String PARAM_INSTANCE = "instance";
        private static final String PARAM_LOCATION = "Location";
        private static final String PARAM_DOMAIN = "hd";
        private static final String PROTOCOL = "protocol";
        private static final String PARAM_KEYS = "keys";
        private static final String LAST_UPDATED = "lastUpdated";
        public static final String REFRESH = "refresh";
        public static final String PREFERRED_VALUE = "PREFERRED_VALUE";
        private static final String OWNER = "owner";
        private static final String SESSION = "session";
        private ParamConstants() {
        }
    }


}
