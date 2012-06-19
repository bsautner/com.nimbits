package com.nimbits.client.enums;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/28/12
 * Time: 2:18 PM
 */
public enum Parameters {

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
    facebook(ParamConstants.PARAM_FACEBOOK),
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
    twitter(ParamConstants.PARAM_TWITTER),
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
    location(ParamConstants.PARAM_LOCATION);

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

        public static final String PARAM_ACTION = "action";
        public static final String PARAM_AUTO_SCALE = "autoscale";
        public static final String PARAM_BLOB_KEY = "blob-key";
        public static final String PARAM_CATEGORY = "category";
        public static final String PARAM_CLIENT = "client";
        public static final String PARAM_CODE = "code";
        public static final String PARAM_COUNT = "count";
        public static final String PARAM_DIAGRAM = "diagram";
        public static final String PARAM_BLOB = "blob";
        public static final String PARAM_EMAIL = "email";
        public static final String PARAM_END_DATE = "ed";
        public static final String PARAM_EXP = "exp";
        public static final String PARAM_FACEBOOK = "FB";
        public static final String PARAM_FORMAT = "format";
        public static final String PARAM_FROM_ADDRESS = "fromAddress";
        public static final String PARAM_GOOGLE_COM = "com.google";
        public static final String PARAM_HOST = "host";
        public static final String PARAM_ID = "id";
        public static final String PARAM_IDLE = "idle";
        public static final String PARAM_IN_CONTENT = "inContent";
        public static final String PARAM_IS_LOGGED_IN = "isLoggedIn";
        public static final String PARAM_JSON = "json";
        public static final String PARAM_JSON_POINT = "pointJson";
        public static final String PARAM_JSON_USER = "pointUser";
        public static final String PARAM_JSON_VALUE = "valueJson";
        public static final String PARAM_KEY = "key";
        public static final String PARAM_LAT = "lat";
        public static final String PARAM_LNG = "lng";
        public static final String PARAM_NAME = "name";
        public static final String PARAM_NOTE = "note";
        public static final String PARAM_OAUTH = "oauth_token";
        public static final String PARAM_PASSWORD = "password";
        public static final String PARAM_PATH = "path";
        public static final String PARAM_POINT = "point";
        public static final String PARAM_POINTS = "points";
        public static final String PARAM_SECRET = "secret";
        public static final String PARAM_SEGMENT = "seg";
        public static final String PARAM_START_DATE = "sd";
        public static final String PARAM_TIMESTAMP = "timestamp";
        public static final String PARAM_TOKEN = "rToken";
        public static final String PARAM_TWITTER = "TW";
        public static final String PARAM_CONTENT_TYPE = "Content-Type";
        public static final String PARAM_HTML = "html";
        public static final String PARAM_NO = "no";
        public static final String PARAM_YES = "no";
        public static final String PARAM_URL = "url";
        public static final String PARAM_UUID = "uuid";
        public static final String PARAM_VALUE = "value";
        public static final String PARAM_VERBOSE = "verbose";
        public static final String PARAM_GENKEY = "genkey";
        public static final String PARAM_OUT = "out";
        public static final String PARAM_I = "i";
        public static final String PARAM_DATA = "data";
        public static final String PARAM_USER = "user";
        public static final String PARAM_RESULT = "Result";
        public static final String PARAM_INPUT = "input";
        public static final String PARAM_LOOP = "loop";
        public static final String PARAM_SEARCH = "search";
        public static final String PARAM_OFFSET = "offset";
        public static final String PARAM_ENTITY = "entity";
        public static final String PARAM_ENTITY_TYPE = "type";
        public static final String PARAM_CHANNEL = "channel";
        public static final String PARAM_DESCRIPTION = "description";
        public static final String PARAM_FILE_NAME = "fn";
        public static final String PARAM_DIRTY = "dirty";
        public static final String PARAM_ACSID = "ACSID";
        public static final String PARAM_MY_FILE = "myFile";
        public static final String PARAM_FILE_ID = "diagramId";
        public static final String PARAM_UPLOAD_TYPE_HIDDEN_FIELD = "uploadTypeHiddenField";
        public static final String PARAM_EMAIL_HIDDEN_FIELD = "emailHiddenField";
        public static final String PARAM_LISTEN = "listen";
        public static final String PARAM_APP_ID = "appid";
        public static final String ACTION_RECORD = "record";
        public static final String PARAM_SERVER = "server";
        public static final String PARAM_PROTECTION = "protection";
        public static final String PARAM_PARENT = "parent";
        public static final String PARAM_INSTANCE = "instance";
        public static final String PARAM_LOCATION = "location";

        private ParamConstants() {
        }
    }


}
