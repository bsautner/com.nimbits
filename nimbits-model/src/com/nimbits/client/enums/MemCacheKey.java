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
    activePoints(0, Const.CONST_SERVER_VERSION + Const.KEY_ACTIVE_POINTS),
    valueCache(1, Const.CONST_SERVER_VERSION + Const.KEY_VALUE),
    allSettings(2, Const.CONST_SERVER_VERSION + Const.KEY_ALL_SETTINGS),
    setting(3, Const.CONST_SERVER_VERSION + Const.KEY_SETTING),
    userNamespace(4, Const.CONST_SERVER_VERSION + Const.KEY_USER_NAMESPACE),
    defaultNamespace(5, Const.CONST_SERVER_VERSION + Const.KEY_DEFAULT_NAMESPACE),
    entityMap(6, Const.CONST_SERVER_VERSION + Const.KEY_ENTITY_MAP),
    userPointNamespace(7, Const.CONST_SERVER_VERSION + Const.KEY_USER_POINT_NAMESPACE);

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



}
