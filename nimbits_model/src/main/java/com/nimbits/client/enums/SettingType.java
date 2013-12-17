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

package com.nimbits.client.enums;


import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/26/12
 * Time: 12:04 PM
 */
public enum SettingType  {


    version(SettingConstants.SETTING_VERSION, SettingConstants.CONST_SERVER_VERSION, true, true, true),
    admin(SettingConstants.SETTING_ADMIN, SettingConstants.SETTING_ADMIN_DEFAULT, false, true, false),
    apiKey(SettingConstants.API_KEY, SettingConstants.API_KEY_DEFAULT, false, true, false);


    private static final Map<String, SettingType> lookup = new HashMap<String, SettingType>();


    static {
        for (final SettingType s : EnumSet.allOf(SettingType.class))
            lookup.put(s.name, s);
    }


    private final String name;
    private final String defaultValue;
    private final boolean update;
    private final boolean create;
    private final boolean readOnly;
    private SettingType(final String name,
                        final String defaultValue,
                        final boolean update,
                        final boolean create,
                        final boolean readonly) {
        this.name = name;
        this.update = update;
        this.defaultValue = defaultValue;
        this.create = create;
        this.readOnly = readonly;
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

    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public String toString() {
        return name;
    }

    private static class SettingConstants {
        static final String CONST_SERVER_VERSION = "3.5.4.12";
        static final String SETTING_ADMIN = "admin";
        static final String SETTING_ADMIN_DEFAULT = "support@nimbits.com";
        static final String SETTING_VERSION = "version";
        static final String API_KEY = "API_KEY";
        static final String API_KEY_DEFAULT = "API_KEY_DEFAULT";

        private SettingConstants() {
        }
    }
}
