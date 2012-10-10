package com.nimbits.client.enums;



import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/14/12
 * Time: 4:02 PM
 */
public enum MemCacheKey {

    activePoints(0, SettingType.serverVersion.getDefaultValue() + KeyConstants.KEY_ACTIVE_POINTS),

    valueCache(1,SettingType.serverVersion.getDefaultValue()  + KeyConstants.KEY_VALUE),
    currentValueCache(1,SettingType.serverVersion.getDefaultValue()  + KeyConstants.KEY_CURRENT_VALUE),
    allSettings(2, SettingType.serverVersion.getDefaultValue()  + KeyConstants.KEY_ALL_SETTINGS),
    setting(3,SettingType.serverVersion.getDefaultValue()  + KeyConstants.KEY_SETTING),
    userNamespace(4, SettingType.serverVersion.getDefaultValue()  + KeyConstants.KEY_USER_NAMESPACE),
    defaultNamespace(5, SettingType.serverVersion.getDefaultValue() + KeyConstants.KEY_DEFAULT_NAMESPACE),
    entityMap(6, SettingType.serverVersion.getDefaultValue()  + KeyConstants.KEY_ENTITY_MAP),
    userPointNamespace(7, SettingType.serverVersion.getDefaultValue() + KeyConstants.KEY_USER_POINT_NAMESPACE),
    location(8, SettingType.serverVersion.getDefaultValue() + KeyConstants.KEY_LOCATION),
    quota(9, SettingType.serverVersion.getDefaultValue() + KeyConstants.KEY_QUOTA),
    preload(10, SettingType.serverVersion.getDefaultValue() + KeyConstants.PRE_LOAD),
    allUsers(11, SettingType.serverVersion.getDefaultValue() + KeyConstants.KEY_ALL_USERS),
    users(12, SettingType.serverVersion.getDefaultValue() + KeyConstants.KEY_USER),
    triggers(13,SettingType.serverVersion.getDefaultValue()  + KeyConstants.KEY_TRIGGERS),
    bufferedValueList(14,SettingType.serverVersion.getDefaultValue()  + KeyConstants.KEY_BUFFERED_VALUE_LIST),
    userEntityTree(15,SettingType.serverVersion.getDefaultValue()  + KeyConstants.KEY_USER_ENTITY_TREE),
    quotaNamespace(16, buildQuotaKey()),
    subscribedEntity(17, SettingType.serverVersion.getDefaultValue() + KeyConstants.KEY_SUBSCRIBED_ENTITY),
    entityNameCache(18, SettingType.serverVersion.getDefaultValue() + KeyConstants.KEY_ENTITY_NAME),
    twitter(19, SettingType.serverVersion.getDefaultValue() + KeyConstants.KEY_TWITTER),
    docService(20, SettingType.serverVersion.getDefaultValue() + KeyConstants.KEY_DOC_SERVICE),
    userTempCacheKey(21, SettingType.serverVersion.getDefaultValue() + KeyConstants.KEY_USER_TEMP_CACHE_KEY);

    private static String buildQuotaKey() {
        //use day of month
        return SettingType.serverVersion.getDefaultValue()  + KeyConstants.KEY_QUOTA_NAMESPACE;
    }

    private static final Map<Integer, MemCacheKey> lookup = new HashMap<Integer, MemCacheKey>(MemCacheKey.values().length);
    private final int code;
    private final String text;
    private final static String LEGAL_CHARS = "[0-9A-Za-z._-]{0,100}";
    private final static String SAFE_REPLACEMENT = "_";
    private final static int HOLD_TIME = 10000;

    static {
        for (MemCacheKey s : EnumSet.allOf(MemCacheKey.class))
            lookup.put(s.code, s);
    }

    public static String getKey(final MemCacheKey memCacheKey, final String uniqueIdentifier) {
        return memCacheKey.getText() + getSafeNamespaceKey(uniqueIdentifier);

    }

    public static int getHoldTime() {
        return HOLD_TIME;
    }

    private MemCacheKey(final int code, final String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public static MemCacheKey get(int code) {
        return lookup.get(code);
    }

    public String getText() {
        return text;
    }


    public static String getSafeNamespaceKey(final String key) {

        final StringBuilder sb = new StringBuilder(key.length());
        for (char c : key.toCharArray()) {
            if (String.valueOf(c).matches(LEGAL_CHARS)) {
                sb.append(c);
            }
            else {
                sb.append(SAFE_REPLACEMENT);
            }
        }
        return sb.toString();
    }

    private static class KeyConstants {
        static final String KEY_ALL_SETTINGS = "KEY_ALL_SETTINGS";
        static final String KEY_ALL_USERS = "KEY_ALL_USERS";
        static final String KEY_SETTING = "KEY_SETTING";
        static final String KEY_USER_NAMESPACE = "KEY_USER_NAMESPACE";
        static final String KEY_DEFAULT_NAMESPACE = "KEY_DEFAULT_NAMESPACE";
        static final String KEY_ENTITY_MAP = "KEY_ENTITY_MAP";
        static final String KEY_USER_POINT_NAMESPACE = "KEY_USER_POINT_NAMESPACE";
        static final String KEY_ACTIVE_POINTS = "KEY_ACTIVE_POINTS";
        static final String KEY_VALUE = "KEY_VALUE";
        static final String KEY_BUFFERED_VALUE_LIST = "KEY_BUFFERED_VALUE_LIST";
        static final String KEY_TRIGGERS = "KEY_TRIGGERS";
        static final String KEY_CURRENT_VALUE = "KEY_CURRENT_VALUE";
        static final String PRE_LOAD = "PRE_LOAD";
        static final String KEY_LOCATION = "KEY_GPS_LOCATION";
        static final String KEY_QUOTA = "KEY_QUOTA";
        static final String KEY_USER = "KEY_USER";
        static final String KEY_USER_ENTITY_TREE = "KEY_USER_ENTITY_TREE";
        static final String KEY_QUOTA_NAMESPACE = "KEY_QUOTA_NAMESPACE";
        public static final String KEY_SUBSCRIBED_ENTITY = "KEY_SUBSCRIBED_ENTITY";
        public static final String KEY_ENTITY_NAME = "KEY_ENTITY_NAME";
        public static final String KEY_TWITTER = "KEY_TWITTER";
        public static final String KEY_DOC_SERVICE = "KEY_DOC_SERVICE";
        public static final String KEY_USER_TEMP_CACHE_KEY = "KEY_USER_TEMP_CACHE_KEY";

        private KeyConstants() {
        }
    }

}
