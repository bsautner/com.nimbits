package com.nimbits.client.enums;

import com.nimbits.client.model.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/14/12
 * Time: 4:02 PM
 */
public enum MemCacheKey {

    activePoints(0, SettingType.serverVersion.getDefaultValue() + KeyConstants.KEY_ACTIVE_POINTS),
    valueCache(1,SettingType.serverVersion.getDefaultValue()  + KeyConstants.KEY_VALUE),
    allSettings(2, SettingType.serverVersion.getDefaultValue()  + KeyConstants.KEY_ALL_SETTINGS),
    setting(3,SettingType.serverVersion.getDefaultValue()  + KeyConstants.KEY_SETTING),
    userNamespace(4, SettingType.serverVersion.getDefaultValue()  + KeyConstants.KEY_USER_NAMESPACE),
    defaultNamespace(5, SettingType.serverVersion.getDefaultValue() + KeyConstants.KEY_DEFAULT_NAMESPACE),
    entityMap(6, SettingType.serverVersion.getDefaultValue()  + KeyConstants.KEY_ENTITY_MAP),
    userPointNamespace(7, SettingType.serverVersion.getDefaultValue() + KeyConstants.KEY_USER_POINT_NAMESPACE);

    private static final Map<Integer, MemCacheKey> lookup = new HashMap<Integer, MemCacheKey>();

    static {
        for (MemCacheKey s : EnumSet.allOf(MemCacheKey.class))
            lookup.put(s.getCode(), s);
    }

    private final int code;
    private final String text;


    private MemCacheKey(int code, String text) {
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

     private class KeyConstants {
        static final String KEY_ALL_SETTINGS = "KEY_ALL_SETTINGS";
        static final String KEY_SETTING = "KEY_SETTING";
        static final String KEY_USER_NAMESPACE = "KEY_USER_NAMESPACE";
        static final String KEY_DEFAULT_NAMESPACE = "KEY_DEFAULT_NAMESPACE";
        static final String KEY_ENTITY_MAP = "KEY_ENTITY_MAP";
        static final String KEY_USER_POINT_NAMESPACE = "KEY_USER_POINT_NAMESPACE";
        static final String KEY_ACTIVE_POINTS = "KEY_ACTIVE_POINTS";
        static final String KEY_VALUE = "KEY_VALUE";
    }

}
