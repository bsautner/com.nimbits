package com.nimbits.client.enums;

import com.nimbits.client.model.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/26/12
 * Time: 12:04 PM
 */
public enum SettingType {


    lastChecked(SettingConstants.SETTING_LAST_CHECKED, new Date().toString(), true),
    serverVersion(SettingConstants.SETTING_VERSION, SettingConstants.CONST_SERVER_VERSION, true),
    serverIsDiscoverable(SettingConstants.SETTING_SERVER_IS_DISCOVERABLE,SettingConstants.TRUE, false),
    billingEnabled(SettingConstants.SETTING_BILLING_ENABLED, SettingConstants.FALSE, false),
    admin(SettingConstants.SETTING_ADMIN, Const.TEST_ACCOUNT, false),
    connectionsEnabled(SettingConstants.SETTING_ENABLE_CONNECTIONS, SettingConstants.TRUE, false),
    facebookClientId(SettingConstants.SETTING_FACEBOOK_CLIENT_ID, SettingConstants.EMPTY, false),
    facebookRedirectURL(SettingConstants.SETTING_FACEBOOK_REDIRECT_URL, Const.PATH_FACEBOOK_REDIRECT, false),
    facebookAPIKey(SettingConstants.SETTING_FACEBOOK_API_KEY, SettingConstants.EMPTY, false),
    facebookSecret(SettingConstants.SETTING_FACEBOOK_SECRET, SettingConstants.EMPTY, false),
    wolframKey(SettingConstants.SETTING_WOLFRAM, Const.CONST_UNKNOWN, false),
    twitterClientId(SettingConstants.SETTING_TWITTER_CLIENT_ID, SettingConstants.EMPTY, false),
    twitterSecret(SettingConstants.SETTING_TWITTER_SECRET, SettingConstants.EMPTY, false);


    private static final Map<String, SettingType> lookup = new HashMap<String, SettingType>();


    static {
        for (SettingType s : EnumSet.allOf(SettingType.class))
            lookup.put(s.getName(), s);
    }


    private final String name;
    private final String defaultValue;
    private final boolean update;

    private SettingType(final String name, final String defaultValue, final boolean update) {
        this.name = name;
        this.update = update;
        this.defaultValue = defaultValue;
    }


    public static SettingType get(String name) {
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

    @Override
    public String toString() {
        return name;
    }
    private class SettingConstants {
        static final String TRUE = "1";
        static final String FALSE = "0";
        static final String EMPTY = "";
        static final String CONST_SERVER_VERSION = "3.3.2.2";
        static final String SETTING_ENABLE_CONNECTIONS = "enableConnections";
        static final String SETTING_ADMIN = "admin";
        static final String SETTING_LAST_CHECKED = "lastChecked";

        private static final String SETTING_FACEBOOK_CLIENT_ID = "facebookClientId";
        private static final String SETTING_FACEBOOK_SECRET = "facebookSecret";
        private static final String SETTING_FACEBOOK_REDIRECT_URL = "facebookRedirectUrl";
        private static final String SETTING_SERVER_IS_DISCOVERABLE = "serverIsDiscoverable";
        private static final String SETTING_BILLING_ENABLED = "SETTING_BILLING_ENABLED";
        private static final String SETTING_WOLFRAM = "wolframAlphaKey";
        private static final String SETTING_TWITTER_CLIENT_ID = "twitterClientId";
        private static final String SETTING_TWITTER_SECRET = "twitterSecret";
        private static final String SETTING_VERSION = "version";
        private static final String SETTING_FACEBOOK_API_KEY = "facebookApiKey";
    }
}
