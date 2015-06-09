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

package com.nimbits.client.constants;


public class Const {

    public static final String VERSION = "3.9.3";

    public static final String SOCKET_RELAY = "130.211.147.107:8080";

    public static final String WEBSITE = "http://www.nimbits.com";
    public static final String LOGGED_IN_EMAIL = "LOGGED_IN_EMAIL";
    public static final String CONTENT_TYPE_JSON = "application/json";

    public static final int DEFAULT_TIMER_UPDATE_SPEED = 5000;
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String FORMAT_DATE_TIME = "MM/dd/y HH:mm:ss";
    public static final String GSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss Z";
    public static final String WARNING_UNCHECKED = "unchecked";

    public static final int DEFAULT_HTTP_TIMEOUT = 15000;

    public static final String CONTENT_TYPE_HTML = "text/html";
    public static final String CONTENT_TYPE_PLAIN = "text/plain";

    public static final double CONST_IGNORED_NUMBER_VALUE = -9999999.9999999;

    public static final int CONST_MAX_NAME_LENGTH = 250;


    public static final String CONST_ANON_EMAIL = "uncle_nobody@nimbits.com";


    private Const() {
        throw new AssertionError();
    }
}
