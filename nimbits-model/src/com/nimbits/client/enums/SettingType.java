/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.enums;

import com.nimbits.client.constants.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/26/12
 * Time: 12:04 PM
 */
public enum SettingType {

    lastChecked(SettingConstants.SETTING_LAST_CHECKED, new Date().toString(), true, true),
    serverVersion(SettingConstants.SETTING_VERSION, SettingConstants.CONST_SERVER_VERSION, true, true),
    serverIsDiscoverable(SettingConstants.SETTING_SERVER_IS_DISCOVERABLE,Const.TRUE, false, true),
    billingEnabled(SettingConstants.SETTING_BILLING_ENABLED, Const.TRUE, true,true),
    admin(SettingConstants.SETTING_ADMIN, Const.TEST_ACCOUNT, false, true),
    connectionsEnabled(SettingConstants.SETTING_ENABLE_CONNECTIONS, Const.TRUE, false, true),
    facebookAPIKey(SettingConstants.SETTING_FACEBOOK_API_KEY, Const.EMPTY, false, true),
    facebookSecret(SettingConstants.SETTING_FACEBOOK_SECRET, Const.EMPTY, false, true),
    wolframKey(SettingConstants.SETTING_WOLFRAM, Const.CONST_UNKNOWN, false, true),
    twitterClientId(SettingConstants.SETTING_TWITTER_CLIENT_ID, Const.EMPTY, false, true),
    twitterSecret(SettingConstants.SETTING_TWITTER_SECRET, Const.EMPTY, false, true),
    source(SettingConstants.SETTING_SOURCE,Const.EMPTY,false, false),
    testAccount(SettingConstants.SETTING_TEST_ACCOUNT,Const.EMPTY,false, false),
    testPassword(SettingConstants.SETTING_TEST_PASSWORD,Const.EMPTY,false, false),
    testURL(SettingConstants.SETTING_TEST_URL,Const.EMPTY,false, false),
    facebookClientId(SettingConstants.SETTING_FACEBOOK_CLIENT_ID, Const.EMPTY, false, true),
    localDevAccount(SettingConstants.SETTING_LOCAL_DEV_ACCOUNT, Const.EMPTY, false, false),
    localDevPath(SettingConstants.SETTING_LOCAL_DEV_PATH, Const.EMPTY, false, false),
    localDevKey(SettingConstants.SETTING_LOCAL_DEV_KEY, Const.EMPTY, false, false),
    domainUser(SettingConstants.SETTING_IS_DOMAIN,Const.FALSE ,false ,false );


    private static final Map<String, SettingType> lookup = new HashMap<String, SettingType>(21);


    static {
        for (final SettingType s : EnumSet.allOf(SettingType.class))
            lookup.put(s.name, s);
    }


    private final String name;
    private final String defaultValue;
    private final boolean update;
    private final boolean create;
    private SettingType(final String name, final String defaultValue, final boolean update, final boolean create) {
        this.name = name;
        this.update = update;
        this.defaultValue = defaultValue;
        this.create = create;
    }


    public static SettingType get(final String name) {
        return lookup.get(name);
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public boolean isUpdate() {
        return update;
    }

    public String getName() {
        return name;
    }

    public boolean isCreate() {
        return create;
    }

    @Override
    public String toString() {
        return name;
    }
    private static class SettingConstants {
        static final String CONST_SERVER_VERSION = "3.3.1";
        static final String SETTING_LOCAL_DEV_ACCOUNT = "SETTING_LOCAL_DEV_ACCOUNT";
        static final String SETTING_LOCAL_DEV_KEY = "SETTING_LOCAL_DEV_KEY";
        static final String SETTING_LOCAL_DEV_PATH = "SETTING_LOCAL_DEV_PATH";



        static final String SETTING_ENABLE_CONNECTIONS = "enableConnections";
        static final String SETTING_ADMIN = "admin";
        static final String SETTING_LAST_CHECKED = "lastChecked";
        static final String SETTING_FACEBOOK_CLIENT_ID = "facebookClientId";
        static final String SETTING_FACEBOOK_SECRET = "facebookSecret";
        static final String SETTING_FACEBOOK_REDIRECT_URL = "facebookRedirectUrl";
        static final String SETTING_SERVER_IS_DISCOVERABLE = "serverIsDiscoverable";
        static final String SETTING_BILLING_ENABLED = "billingEnabled";
        static final String SETTING_WOLFRAM = "wolframAlphaKey";
        static final String SETTING_TWITTER_CLIENT_ID = "twitterClientId";
        static final String SETTING_TWITTER_SECRET = "twitterSecret";
        static final String SETTING_VERSION = "version";
        static final String SETTING_FACEBOOK_API_KEY = "facebookApiKey";
        static final String SETTING_SOURCE = "source";
        static final String SETTING_TEST_ACCOUNT = "testAccount";
        static final String SETTING_TEST_PASSWORD = "testPassword";
        static final String SETTING_TEST_URL = "testURL";
        static final String SETTING_IS_DOMAIN = "IS_DOMAIN";

        private SettingConstants() {
        }
    }
}
