/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.client.enums;


import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Deprecated //get rid of caching this way
public enum MemCacheKey {


    hotPoints(0, ServerSetting.version.getDefaultValue() + KeyConstants.KEY_HOT_POINTS),
    valueCache(1, ServerSetting.version.getDefaultValue() + KeyConstants.KEY_VALUE),
    allSettings(2, ServerSetting.version.getDefaultValue() + KeyConstants.KEY_ALL_SETTINGS),
    setting(3, ServerSetting.version.getDefaultValue() + KeyConstants.KEY_SETTING),
    userNamespace(4, ServerSetting.version.getDefaultValue() + KeyConstants.KEY_USER_NAMESPACE),
    defaultNamespace(5, ServerSetting.version.getDefaultValue() + KeyConstants.KEY_DEFAULT_NAMESPACE),
    entityMap(6, ServerSetting.version.getDefaultValue() + KeyConstants.KEY_ENTITY_MAP),
    userPointNamespace(7, ServerSetting.version.getDefaultValue() + KeyConstants.KEY_USER_POINT_NAMESPACE),
    preload(10, ServerSetting.version.getDefaultValue() + KeyConstants.PRE_LOAD),
    allUsers(11, ServerSetting.version.getDefaultValue() + KeyConstants.KEY_ALL_USERS),
    users(12, ServerSetting.version.getDefaultValue() + KeyConstants.KEY_USER),
    triggers(13, ServerSetting.version.getDefaultValue() + KeyConstants.KEY_TRIGGERS),
    userEntityTree(15, ServerSetting.version.getDefaultValue() + KeyConstants.KEY_USER_ENTITY_TREE),
    subscribedEntity(17, ServerSetting.version.getDefaultValue() + KeyConstants.KEY_SUBSCRIBED_ENTITY),
    entityNameCache(18, ServerSetting.version.getDefaultValue() + KeyConstants.KEY_ENTITY_NAME),
    docService(20, ServerSetting.version.getDefaultValue() + KeyConstants.KEY_DOC_SERVICE),
    userTempCacheKey(21, ServerSetting.version.getDefaultValue() + KeyConstants.KEY_USER_TEMP_CACHE_KEY),
    userReport(22, ServerSetting.version.getDefaultValue() + KeyConstants.USER_REPORT_STATUS_GRID_KEY);


    private static final Map<Integer, MemCacheKey> lookup = new HashMap<Integer, MemCacheKey>(MemCacheKey.values().length);
    private final int code;
    private final String text;

    static {
        for (MemCacheKey s : EnumSet.allOf(MemCacheKey.class))
            lookup.put(s.code, s);
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


    private static class KeyConstants {
        static final String KEY_ALL_SETTINGS = "KEY_ALL_SETTINGS";
        static final String KEY_ALL_USERS = "KEY_ALL_USERS";
        static final String KEY_SETTING = "KEY_SETTING";
        static final String KEY_USER_NAMESPACE = "KEY_USER_NAMESPACE";
        static final String KEY_DEFAULT_NAMESPACE = "KEY_DEFAULT_NAMESPACE";
        static final String KEY_ENTITY_MAP = "KEY_ENTITY_MAP";
        static final String KEY_USER_POINT_NAMESPACE = "KEY_USER_POINT_NAMESPACE";
        static final String KEY_ACTIVE_POINTS = "KEY_ACTIVE_POINTS";
        static final String KEY_HOT_POINTS = "KEY_HOT_POINTS";
        static final String KEY_VALUE = "KEY_VALUE";
        static final String KEY_TRIGGERS = "KEY_TRIGGERS";
        static final String PRE_LOAD = "PRE_LOAD";
        static final String KEY_USER = "KEY_USER";
        static final String KEY_USER_ENTITY_TREE = "KEY_USER_ENTITY_TREE";
        static final String KEY_SUBSCRIBED_ENTITY = "KEY_SUBSCRIBED_ENTITY";
        static final String KEY_ENTITY_NAME = "KEY_ENTITY_NAME";
        static final String KEY_DOC_SERVICE = "KEY_DOC_SERVICE";
        static final String KEY_USER_TEMP_CACHE_KEY = "KEY_USER_TEMP_CACHE_KEY";
        static final String USER_REPORT_STATUS_GRID_KEY = "USER_REPORT_STATUS_GRID_KEY";

        private KeyConstants() {
        }
    }

}
