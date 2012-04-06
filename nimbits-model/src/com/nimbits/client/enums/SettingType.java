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
    billingEnabled(SettingConstants.SETTING_BILLING_ENABLED, Const.FALSE, false, true),
    admin(SettingConstants.SETTING_ADMIN, Const.TEST_ACCOUNT, false, true),
    connectionsEnabled(SettingConstants.SETTING_ENABLE_CONNECTIONS, Const.TRUE, false, true),
    facebookRedirectURL(SettingConstants.SETTING_FACEBOOK_REDIRECT_URL, Path.PATH_FACEBOOK_REDIRECT, false, true),
    facebookAPIKey(SettingConstants.SETTING_FACEBOOK_API_KEY, Const.EMPTY, false, true),
    facebookSecret(SettingConstants.SETTING_FACEBOOK_SECRET, Const.EMPTY, false, true),
    wolframKey(SettingConstants.SETTING_WOLFRAM, Const.CONST_UNKNOWN, false, true),
    twitterClientId(SettingConstants.SETTING_TWITTER_CLIENT_ID, Const.EMPTY, false, true),
    twitterSecret(SettingConstants.SETTING_TWITTER_SECRET, Const.EMPTY, false, true),
    source(SettingConstants.SETTING_SOURCE,Const.EMPTY,false, false),
    testAccount(SettingConstants.SETTING_TEST_ACCOUNT,Const.EMPTY,false, false),
    testPassword(SettingConstants.SETTING_TEST_PASSWORD,Const.EMPTY,false, false),
    testURL(SettingConstants.SETTING_TEST_URL,Const.EMPTY,false, false),
    quotaEnabled(SettingConstants.SETTING_ENABLE_QUOTA, Const.FALSE, false, true),
    facebookClientId(SettingConstants.SETTING_FACEBOOK_CLIENT_ID, Const.EMPTY, false, true),
    localDevAccount(SettingConstants.SETTING_LOCAL_DEV_ACCOUNT, Const.EMPTY, false, false),
    localDevPath(SettingConstants.SETTING_LOCAL_DEV_PATH, Const.EMPTY, false, false),
    localDevKey(SettingConstants.SETTING_LOCAL_DEV_KEY, Const.EMPTY, false, false),
    ;


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
        private static final String CONST_SERVER_VERSION = "3.2.0.2";
        private static final String SETTING_LOCAL_DEV_ACCOUNT = "SETTING_LOCAL_DEV_ACCOUNT";
        private static final String SETTING_LOCAL_DEV_KEY = "SETTING_LOCAL_DEV_KEY";
        private static final String SETTING_LOCAL_DEV_PATH = "SETTING_LOCAL_DEV_PATH";



        private static final String SETTING_ENABLE_CONNECTIONS = "enableConnections";
        private static final String SETTING_ADMIN = "admin";
        private static final String SETTING_LAST_CHECKED = "lastChecked";
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
        private static final String SETTING_SOURCE = "source";
        private static final String SETTING_TEST_ACCOUNT = "testAccount";
        private static final String SETTING_TEST_PASSWORD = "testPassword";
        private static final String SETTING_TEST_URL = "testURL";
        private static final String SETTING_ENABLE_QUOTA = "SETTING_ENABLE_QUOTA";
    }
}
