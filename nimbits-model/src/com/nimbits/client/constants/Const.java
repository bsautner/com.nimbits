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

package com.nimbits.client.constants;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 6/25/11
 * Time: 10:35 AM
 */
public class Const {

    public static final String TRUE = "1";
    public static final String FALSE = "0";
    public static final String EMPTY = "";
    public static final int MAX_DAILY_QUOTA = 1440;

    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONST_SERVER_NAME = "Nimbits Server";
    public static final String FILE_TYPE_SVG = ".svg";
    public static final int DEFAULT_FEED_LENGTH = 250;
    public static final String TEXT_DATA_FEED = "Data Feed Channel";
    public static final String REGEX_NAMESPACE = "[0-9A-Za-z._-]{0,100}";
    public static final String CONST_AH = "ah";
    public static final String CONST_ENCODING = "UTF-8";
    public static final int DEFAULT_TIMER_UPDATE_SPEED = 10000;
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String FORMAT_DATE_TIME = "MM/dd/y HH:mm:ss";
    public static final String GSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss Z";
    public static final String WARNING_UNCHECKED = "unchecked";
    public static final String HTML_HOME_LINK = "<A href = \"http://www.nimbits.com\">nimbits.com</a>";
    public static final String CONST_UNKNOWN = "";
    public static final int DEFAULT_HTTP_TIMEOUT = 15000;
    public static final String DEFAULT_XMPP_SERVER = "gmail.com";
    public static final String DEFAULT_APPSPOT = "appspot.com";
    public static final String TEST_ACCOUNT = "test@example.com";
    public static final String DELIMITER_COMMA = ",";
    public static final String DELIMITER_BAR = "|";
    public static final String HTML_BOOTSTRAP = "<html><head>" +
            "<link rel=\"stylesheet\" href=\"http://twitter.github.com/bootstrap/1.4.0/bootstrap.min.css\">" +
            "</head><body>";
    public static final String CONTENT_TYPE_HTML = "text/html";
    public static final String CONTENT_TYPE_PLAIN = "text/plain";
    public static final String REGEX_SPECIAL_CHARS = "[!@#$%^&*()]";
    public static final int CONST_QUERY_CHUNK_SIZE = 100;
    public static final int CONST_MAX_CACHED_VALUE_SIZE = 200;
    public static final double CONST_IGNORED_NUMBER_VALUE = -9999999.9999999;
    public static final int DEFAULT_DATA_EXPIRE_DAYS = 90;
    public static final double DEFAULT_POINT_COMPRESSION = 0.1;
    public static final int CONST_MAX_NAME_LENGTH = 250;
    public static final int CONST_MAX_BATCH_COUNT = 100;


// --------------------------- CONSTRUCTORS ---------------------------

    private Const() {
        throw new AssertionError();
    }
}
