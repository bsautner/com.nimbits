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

package com.nimbits.client.model;

import com.nimbits.client.model.email.EmailAddress;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 6/25/11
 * Time: 10:35 AM
 */
public class Const {
    public static final String CONST_SERVER_VERSION = "3.3.1.20";
    public static final long DEFAULT_SERIAL_VERSION = 12L;
    public static final String CACHE_KEY_PREFIX = CONST_SERVER_VERSION + DEFAULT_SERIAL_VERSION;
    public static final String PATH_NIMBITS_CORE_SERVERS_URL = "http://nimbits.com:8080/core/servers";
    public static final String PATH_NIMBITS_CORE_ENTITY_DESC_URL = "http://nimbits.com:8080/core/entity";
    public static final String REGEX_NAMESPACE = "[0-9A-Za-z._-]{0,100}";
    public static final String WORD_MULTI_PART = "multipart";
    public static final String ACTION_ALERT = "alert";
    public static final String ACTION_CREATE = "create";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_DOWNLOAD = "download";
    public static final String ACTION_IDLE = "idle";
    public static final String ACTION_ONOFF = "onoff";
    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_VALUE = "value";
    public static final String ANDROID_COL_CATEGORY = "CATEGORY";
    public static final String ANDROID_COL_DESCRIPTION = "DESCRIPTION";
    public static final String ANDROID_COL_DISPLAY_TYPE = "DISPLAYTYPE";
    public static final String ANDROID_COL_ID = "_id";
    public static final String ANDROID_COL_JSON = "JSON";
    public static final String ANDROID_COL_NAME = "NAME";
    public static final String ANDROID_COL_URL = "URL";
    public static final String ANDROID_COL_VALUE = "VALUE";
    public static final String ANDROID_DB_NAME = "nimbits126";
    public static final String ANDROID_DB_PATH = "/data/data/com.nimbits.android/databases/";
    public static final String ANDROID_TABLE_LEVEL_ONE_DISPLAY = "Level_One_Display";
    public static final String ANDROID_TABLE_LEVEL_TWO_DISPLAY = "Level_Two_Display";
    public static final String ANDROID_TABLE_SERVERS = "Servers";
    public static final String ANDROID_TABLE_SETTINGS = "Settings";
    public static final String CONST_AH = "ah";
    public static final String CONST_ENCODING = "UTF-8";
    //public static final String CONST_HIDDEN_CATEGORY = "Nimbits_Unsorted";
    public static final String CONST_SERVER_NAME = "Nimbits Server";
    // ------------------------------ FIELDS ------------------------------

    public static final String COPYRIGHT = "Copyright (c) 2010 Tonic Solutions LLC.  Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an \\\"AS IS\\\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.";
    public static final String DEFAULT_CHART_NAME = "Chart1";

    public static final String DEFAULT_EMAIL_SUBJECT = "Nimbits Messaging";
    public static final String DEFAULT_EMPTY_COL = "EMPTY";
    public static final String ERROR_BATCH_SERVICE_JDO = "Batch Service JDOException";
    public static final String FORMAT_DATE_TIME = "MM/dd/y HH:mm:ss";
    public static final String FROM_EMAIL = "support@nimbits.com";

    public static final String GSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss Z";
    public static final String IN_CONTENT = "inContent";
    public static final String MESSAGE_ADD_CATEGORY = "Add a new data point Category";
    public static final String MESSAGE_CLICK_TO_TREND = "Click to Trend";
    public static final String MESSAGE_CONFIGURE_POINT = "Configure Data Point";
    public static final String MESSAGE_DATA_POINT = "Data Point";
    public static final String MESSAGE_DIAGRAM_NOT_FOUND_EXCEPTION = "The requested diagram could not be located.";
    public static final String MESSAGE_DIAGRAM_PROTECTION_EXCEPTION = "The diagram you requested could not be accessed. You are either not the diagram owner, or the owner has set the pretection level" +
            " to a level you cannot access. Please contact the owner of the diagram.";
    public static final String MESSAGE_EMAIL_SUBJECT = "Message from nimbits.com";
    public static final String MESSAGE_HIGH_ALERT_ON = "High Alert ON";
    public static final String MESSAGE_LINKS_AFTER_ERROR = " <P><a href = \"Http://www.nimbits.com\" go to Nimbits.com </P>";
    public static final String MESSAGE_LOADING_POINTS = "Loading Data Points...";
    public static final String MESSAGE_LOW_ALERT_ON = "Low Alert On";
    public static final String MESSAGE_NEW_CATEGORY = "New Category";
    public static final String MESSAGE_NEW_CATEGORY_PROMPT = "Please enter the name of the new Category";
    public static final String MESSAGE_NEW_POINT = "New Data Point";
    public static final String MESSAGE_NEW_POINT_PROMPT = "Please enter the name of the new data point.";
    public static final String MESSAGE_NO_ACCOUNT = "No Google OwnerAccount";
    public static final String MESSAGE_NO_DATA = "";
    public static final String MESSAGE_POINT_DELETED = "Point Deleted. You may need to refresh your browser";
    public static final String MESSAGE_SELECT_POINT = "Select a Data Point";
    public static final String MESSAGE_SERVER_SECRET_ERROR = " ERROR Could not get the server secret";
    public static final String MESSAGE_TWITTER_ADDED = "Nice! Your nimbits account is now connected to twitter. " +
            "You can configure data points to send alerts and updates to your twitter feed " +
            "on the property menu";
    public static final String MESSAGE_UPLOAD_SVG = "Upload a process diagram in .svg format";


    public static final String N = "NimbitsV";
    public static final String NAMESPACE_DEFAULT = "default";
    public static final String PARAM_ACTION = "action";
    public static final String PARAM_ADMIN = "admin";
    public static final String PARAM_AUTO_SCALE = "autoscale";
    public static final String PARAM_BASE_URL = "baseURL";
    public static final String PARAM_BLOB_KEY = "blob-key";
    public static final String PARAM_CATEGORY = "category";
    public static final String PARAM_CHART_DATA = "chartData";
    public static final String PARAM_CLIENT = "client";
    public static final String PARAM_CODE = "code";
    public static final String PARAM_COOKIE = "cookie";
    public static final String PARAM_COUNT = "count";
    public static final String PARAM_DIAGRAM = "diagram";
    public static final String PARAM_BLOB = "blob";
    public static final String PARAM_EMAIL = "email";
    public static final String SETTING_ENABLE_CONNECTIONS = "enableConnections";
    public static final String PARAM_END_DATE = "ed";
    public static final String PARAM_EXP = "exp";
    public static final String PARAM_FACEBOOK = "FB";
    public static final String PARAM_FOLDER = "folder";
    public static final String PARAM_FORMAT = "format";
    public static final String PARAM_FROM_ADDRESS = "fromAddress";
    public static final String PARAM_GOOGLE_COM = "com.google";
    public static final String PARAM_HOST = "host";
    public static final String PARAM_ICON = "icon";
    public static final String PARAM_ID = "id";
    public static final String PARAM_IDLE = "idle";
    public static final String PARAM_INCLUDE_DIAGRAMS = "includediagrams";
    public static final String PARAM_INCLUDE_SUBSCRIPTIONS = "includesubscriptions";
    public static final String PARAM_INCLUDE_POINTS = "includepoints";
    public static final String PARAM_IN_CONTENT = "inContent";
    public static final String PARAM_IS_LOGGED_IN = "isLoggedIn";
    public static final String PARAM_JSON = "json";
    public static final String PARAM_JSON_POINT = "pointJson";
    public static final String PARAM_JSON_USER = "pointUser";
    public static final String PARAM_JSON_VALUE = "valueJson";
    public static final String PARAM_KEY = "key";
    public static final String PARAM_LAT = "lat";
    public static final String PARAM_LNG = "lng";
    public static final String PARAM_LOGGED_IN = "loggedin";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_NOTE = "note";
    public static final String PARAM_OAUTH = "oauth_token";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_PATH = "path";
    public static final String PARAM_POINT = "point";
    public static final String PARAM_POINTS = "points";
    public static final String PARAM_POINT_COUNT = "pointcount";
    public static final String PARAM_POINT_ID = "pointID";
    public static final String PARAM_PROPERTY = "property";
    public static final String PARAM_RELOAD = "reload";

    public static final String PARAM_SECRET = "secret";
    public static final String PARAM_SEGMENT = "seg";
    public static final String PARAM_SELF = "self";
    public static final String PARAM_START_DATE = "sd";
    public static final String PARAM_STATE = "state";
    public static final String PARAM_TIMESTAMP = "timestamp";
    public static final String PARAM_TOKEN = "rToken";
    public static final String PARAM_TOTAL = "total";
    public static final String PARAM_TWITTER = "TW";
    public static final String SETTING_TWITTER_CLIENT_ID = "twitterClientId";
    public static final String SETTING_TWITTER_SECRET = "twitterSecret";
    public static final String PARAM_TYPE = "type";
    public static final String PARAM_URL = "url";
    public static final String PARAM_UUID = "uuid";
    public static final String PARAM_VALUE = "value";
    public static final String PARAM_VERBOSE = "verbose";
    public static final String PATH_AUTHTEST_SERVICE = "/service/authtest";
    public static final String PATH_CATEGORY_SERVICE = "/service/category";

    public static final String PATH_CONNECTION_IMAGE = "/resources/images/network.jpg";
    public static final String PATH_CURRENT_VALUE = "/service/currentvalue";
    public static final String PATH_DELETE_DATA_TASK = "/task/DeleteRecordedValuesTask";
    public static final String PATH_BLOB_SERVICE = "/service/blob";
    public static final String PATH_GOOGLE_CLIENT_LOGIN = "https://www.google.com/accounts/ClientLogin";
    public static final String PATH_INCOMING_MAIL_QUEUE = "/task/incommingmail";
    public static final String PATH_NIMBITS_HOME = "http://www.nimbits.com";

    public static final String PATH_NIMBITS_PUBLIC_SERVER = "http://app.nimbits.com";
    public static final String PATH_OBJECT_PROTECTION_URL = "http://www.nimbits.com/app/diagram_protection.html";
    public static final String PATH_POINT_MAINT_TASK = "/task/pointmaint";
    public static final String PATH_CATEGORY_MAINT_TASK = "/task/categorymaint";
    public static final String PATH_UPGRADE_TASK = "/task/upgrade";
    public static final String PATH_MOVE_TASK = "/task/move";
    public static final String PATH_POINT_SERVICE = "/service/point";
    public static final String PATH_SERIES_SERVICE = "/service/series";
    public static final String PATH_TASK_RECORD_VALUE = "/task/recordvaluetask";
    public static final String PATH_TASK_PROCESS_BATCH = "/task/processbatchtask";
    public static final String PATH_TASK_UPDATE_POINT_STATS = "/task/updatepointstats";
    public static final String PATH_USER_SERVICE = "/admin/user";

    public static final String PATH_WA_URL = "./wa.html";
    public static final String Path_CHART_API = "/service/chartapi";
    public static final String QUEUE_DELETE_DATA = "deletedata";
    public static final String QUEUE_INCOMING_MAIL = "incommingmail";
    public static final String QUEUE_RECORD_VALUE = "recordvaluequeue";
    public static final String QUEUE_PROCESS_BATCH = "processbatchqueue";
    public static final String RESPONSE_CATEGORY_EXISTS = "Category Exists";
    public static final String QUEUE_UPDATE_POINT_STATS = "updatepointstats";
    //  public static final String RESPONSE_CATEGORY_NOT_FOUND = "Category does not exist";
    public static final String RESPONSE_ERROR_SAVING_VALUE = "Error Saving Value";
    public static final String RESPONSE_MISSING_POINT_PARAM = "missing point parameter";

    public static final String RESPONSE_PERMISSION_DENIED = "Permission Denied";
    public static final String RESPONSE_POINT_EXISTS = "Point Exists";
    public static final String RESPONSE_POINT_NOT_FOUND_UUID = "could not find point using uuid ";
    public static final String RESPONSE_PROTECTED_POINT = "Unable to process. You didn't provide an oauth token or secret, and the point you requested is not public";
    public static final String RESPONSE_UNKNOWN_USER = "Could not identify user";
    public static final String TARGET_TYPE_BLANK = "blank";
    public static final String TASK_POINT_MAINT = "pointmaint";

    public static final String TASK_CATEGORY_MAINT = "categorymaint";
    public static final String TASK_UPGRADE = "upgrade";
    public static final String TASK_MOVE = "move";
    public static final String TEXT_NEW_CATEGORY = "Create category";
    public static final String TRANSACTION_OPTIONAL = "transactions-optional";
    public static final String WARNING_UNCHECKED = "unchecked";
    public static final String WORD_ANDROID = "android";
    public static final String WORD_ANNOTATION = "Annotation";
    public static final String WORD_BLANK = "blank";
    public static final String WORD_CATEGORY = "Category";
    public static final String WORD_DATE = "Date";
    public static final String WORD_DOUBLE = "double";
    public static final String WORD_ERROR = "Error";
    public static final String WORD_FALSE = "false";
    public static final String WORD_NAME = "Name";
    public static final String WORD_NIMBITS = "Nimbits";
    public static final String WORD_SUCCESS = "Success";
    public static final String WORD_TIMESTAMP = "Timestamp";
    public static final String WORD_TRUE = "true";
    public static final String WORD_VALUE = "Value";
    public static final String WORD_YES = "Yes";
    public static final int DEFAULT_TIMER_UPDATE_SPEED = 5000;
    public static final String PATH_AH_LOGIN = "/_ah/login";
    public static final String PARAM_ACSID = "ACSID";
    public static final String PARAM_MY_FILE = "myFile";
    public static final String PARAM_FILE_ID = "diagramId";
    public static final String PARAM_UPLOAD_TYPE_HIDDEN_FIELD = "uploadTypeHiddenField";
    public static final String PARAM_EMAIL_HIDDEN_FIELD = "emailHiddenField";
    public static final String PARAM_GENKEY = "genkey";
    public static final String PARAM_OUT = "out";
    public static final String PARAM_I = "i";
    public static final String PARAM_DATA = "data";
    public static String PATH_BATCH_SERVICE = "/service/batch";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String SETTING_FACEBOOK_CLIENT_ID = "facebookClientId";
    public static final String SETTING_FACEBOOK_SECRET = "facebookSecret";
    public static final String SETTING_FACEBOOK_REDIRECT_URL = "facebookRedirectUrl";
    public static final String WORD_PROPERTIES = "Properties";
    public static final String HTML_HOME_LINK = "<A href = \"http://www.nimbits.com\">nimbits.com</a>";
    public static final String PATH_FACEBOOK_ME = "https://graph.facebook.com/me";

    public static final String PATH_LOGO_IMG = "http://app.nimbits.com/resources/images/logo.png";
    public static final String PATH_WOLFRAM_ALPHA = "http://api.wolframalpha.com/v2/query";

    public static final String PARAM_SERVER = "server";
    public static final String ERROR_POINT_NOT_FOUND = "Point not found";
    public static final String ERROR_USER_NOT_FOUND = "User not found";
    public static final String PARAM_USER = "user";
    public static final String ERROR_RETRY = "There was an error saving this value. The system will try again.";

    public static final String PARAM_RESULT = "Result";
    public static final String PARAM_RESULT_TARGET = "resultTarget";
    public static final String PARAM_INPUT = "input";
    public static final String PARAM_ENABLED = "enabled";
    public static final String PARAM_D = "d";
    public static final String PARAM_POINT_FK = "pointFK";
    public static final String SETTING_WOLFRAM = "wolframAlphaKey";
    public static final String CONST_UNKNOWN = "";
    public static final String WORD_COOKIE = "Cookie";
    public static final int DEFAULT_HTTP_TIMEOUT = 15000;
    public static final String DEFAULT_XMPP_SERVER = "gmail.com";
    public static final String PARAM_LISTEN = "listen";
    public static final String PARAM_APP_ID = "appid";
    public static final String DEFAULT_APPSPOT = "appspot.com";
    public static final String WORD_EXIT = "exit";
    public static final String PARAM_DEBUG = "debug";
    public static final String CACHE_KEY_USER_PREFIX = "USER_CACHE";
    public static final String CACHE_KEY_CATEGORY_PREFIX = "CATEGORY_CACHE";
    public static final String PATH_LOCAL = "http://192.168.1.2:8081";
    public static final String TEST_ACCOUNT = "test@example.com";
    public static final String PATH_CHART_SERVICE = "/service/chartapi";
    public static final String PARAM_LOOP = "loop";
    public static final String SETTING_VERSION = "version";
    public static final String SETTING_FACEBOOK_API_KEY = "facebookApiKey";
    public static final String PATH_GOOGLE_CHART_API = "http://chart.apis.google.com/chart";
    public static final String DELIMITER_COMMA = ",";
    public static final String DELIMITER_BAR = "|";
    public static final String SETTING_SERVER_IS_DISCOVERABLE = "serverIsDiscoverable";
    public static final String PATH_FACEBOOK_REDIRECT = "http://apps.facebook.com/nimbits/";
    public static final String SETTING_ADMIN = "admin";
    public static final String SETTING_LAST_CHECKED = "lastChecked";
    public static final String HTML_BOOTSTRAP = "<html><head>" +
            "<link rel=\"stylesheet\" href=\"http://twitter.github.com/bootstrap/1.4.0/bootstrap.min.css\">" +
            "</head><body>";
    public static final String CONTENT_TYPE_HTML = "text/html";
    public static final String CONTENT_TYPE_PLAIN = "text/plain";
    public static final String PARAM_SEARCH = "search";
    public static final String REGEX_SPECIAL_CHARS = "[!@#$%^&*()]";
    public static final int CONST_QUERY_CHUNK_SIZE = 100;
    public static final int CONST_MAX_CACHED_VALUE_SIZE = 200;
    public static final String PARAM_TIME = "time";
    public static final String PARAM_OFFSET = "offset";
    public static final String ACTION_RECORD = "record";
    public static final String ACTION_READ = "read";
    public static final String ACTION_RECORD_VALUE = "recordvalue";
    public static final String CURRENT_VALUE = "currentvalue";
    public static final String ACTION_RECORD_JSON = "recordjson";
    public static final String ACTION_READ_NOTE = "readnote";
    public static final String ACTION_READ_VALUE = "readvalue";
    public static final String ACTION_READ_GPS = "readgps";
    public static final String ACTION_READ_JSON = "readjson";

    public static final String WORD_DATA = "Data";
    public static final String PARAM_ENTITY = "entity";
    public static final String PARAM_ENTITY_TYPE = "type";
    public static final String PARAM_DEFAULT_WINDOW_OPTIONS = "menubar=no," +
            "location=false," +
            "resizable=yes," +
            "scrollbars=yes," +
            "width=980px," +
            "height=800," +
            "status=no," +
            "dependent=true";
    public static final String PARAM_CHANNEL = "channel";
    public static final String CACHE_KEY_SYSTEM = "SYSTEM_CACHE";
    public static final String CACHE_KEY_POINT_PREFIX = "POINT_CACHE";
    public static final String ACTION_SUBSCRIBE = "subscribe";
    public static final String ACTION_NONE = "none";
    public static final String ACTION_ANDROID = "android";
    public static final String ACTION_FACEBOOK = "facebook";
    public static final String ACTION_TWITTER = "twitter";
    public static final String ACTION_TWITTER_FINISH_REG = "twitterFinishReg";
    public static final String ACTION_REPORT = "report";
    public static final String ACTION_DIAGRAM = "diagram";
    public static final String PATH_GOOGLE_URL_SHORTENER =  "https://www.googleapis.com/urlshortener/v1/url";;
    public static final String PARAM_DESCRIPTION = "description";
    public static final String PARAM_FILE_NAME = "fn";

    public static String getConnectionInviteEmail(final EmailAddress email) {
        return "<P STYLE=\"margin-bottom: 0in\"> " + email.getValue() +
                " wants to connect with you on <a href = \"http://www.nimbits.com\"> Nimbits! </A></BR></P><BR> \n" +
                "<P><a href = \"http://www.nimbits.com\">Nimbits</A> is a data logging service that you can use to record time series\n" +
                "data, such as sensor readings, GPS Data, stock prices or anything else into Data Points on the cloud.</P>\n" +
                "<BR><P STYLE=\"margin-bottom: 0in\">\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">Nimbits uses Google Accounts for\n" +
                "authentication. If you have a gmail account, you can sign into\n" +
                "Nimbits immediately  using that account. You can also register any\n" +
                "email address with google accounts and then sign in to Nimbits.  It\n" +
                "only takes a few seconds to register:\n" +
                "<A HREF=\"https://www.google.com/accounts/NewAccount\">https://www.google.com/accounts/NewAccount</A></P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">\n" +
                "</P>\n" +
                "<BR><P STYLE=\"margin-bottom: 0in\"><A HREF=\"http://app.nimbits.com/\">Sign\n" +
                "into Nimbits</A> to approve this connection request.  <A HREF=\"http://www.nimbits.com/\">Go to \n" +
                "nimbits.com</A> to learn more.</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">\n" +
                "</P>\n" +
                "<BR><BR><P STYLE=\"margin-bottom: 0in\"><STRONG>More about Nimbits Services</STRONG></P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">Nimbits is a collection of software " +
                "designed for recording and working with time series data - such as " +
                "readings from a temperature probe, a stock price, or anything else " +
                "that changes over time - even textual and GPS data.  Nimbits allows " +
                "you to create online Data Points that provide a data channel into the " +
                "cloud.\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">\n" +
                "</P>\n" +
                "<BR><P STYLE=\"margin-bottom: 0in\">Nimbits Server, a data historian, is " +
                "available at <A HREF=\"http://www.nimbits.com/\">app.nimbits.com</A> " +
                "and provides a collection of web services, APIs and an interactive " +
                "portal enabling you to record data on a global cloud computing " +
                "infrastructure. You can also download and install your own instance " +
                "of a Nimbits Server, write your own software using Nimbits as a " +
                "powerful back end, or use our many free and open source downloads. " +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">\n" +
                "</P>\n" +
                "<BR><P STYLE=\"margin-bottom: 0in\">Built on cloud computing architecture,\n" +
                "and optimized to run on Google App Engine, you can run a Nimbits\n" +
                "Server with remarkable uptime and out of the box disaster recover\n" +
                "with zero upfront cost and a generous free quota. Then, only pay for\n" +
                "computing services you use with near limitless and instant\n" +
                "scalability when you need it. A typical 10 point Nimbits System costs\n" +
                "only pennies a week to run, and nothing at all when it's not in use.\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">\n" +
                "</P>\n" +
                "<P STYLE=\"margin-bottom: 0in\">As your data flows into a Nimbits Data\n" +
                "Point, values can be compressed, alarms can be triggered,\n" +
                "calculations can be performed and data can even be relayed to\n" +
                "facebook, Twitter or other connected systems.  You can chat with your\n" +
                "data over IM from anywhere, see and share your changing data values\n" +
                "in spreadsheets, diagrams and even on your phone with our free\n" +
                "android app. \n" +
                "</P>";
    }

// --------------------------- CONSTRUCTORS ---------------------------

    private Const() {
        throw new AssertionError();
    }
}
