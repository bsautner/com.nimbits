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


public enum ServerSetting {


    version(SettingConstants.SETTING_VERSION, SettingConstants.CONST_SERVER_VERSION, true, true, true, false, false),
    uuid(SettingConstants.SETTING_UUID, SettingConstants.SETTING_UUID_DEFAULT, true, true, true, false, false),
    storeDirectory(SettingConstants.SETTING_STORE_DIRECTORY, SettingConstants.STORE_DIRECTORY_DEFAULT, false, true, false, false, false),
    admin(SettingConstants.SETTING_ADMIN, SettingConstants.SETTING_ADMIN_DEFAULT, false, true, false, false, false),
    apiKey(SettingConstants.API_KEY, SettingConstants.API_KEY_DEFAULT, false, true, false, false, false),
    smtp(SettingConstants.SETTING_SMTP, SettingConstants.SETTING_SMTP_DEFAULT, false, true, false, false, false),
    smtpPassword(SettingConstants.SETTING_SMTP_PASSWORD, SettingConstants.SETTING_SMTP_PASSWORD_DEFAULT, false, true, false, true, false),
    stats(SettingConstants.SETTING_STATS, SettingConstants.SETTING_STATS_DEFAULT, false, true, false, false, true)

    ;


    private static final Map<String, ServerSetting> lookup = new HashMap<String, ServerSetting>();


    static {
        for (final ServerSetting s : EnumSet.allOf(ServerSetting.class))
            lookup.put(s.name, s);
    }


    private final String name;
    private final String defaultValue;
    private final boolean update;
    private final boolean create;
    private final boolean readOnly;
    private final boolean encrypt;
    private final boolean isFlag;

    private ServerSetting(final String name,
                          final String defaultValue,
                          final boolean update,
                          final boolean create,
                          final boolean readonly,
                          final boolean encrypt,
                          final boolean isFlag

    ) {
        this.name = name;
        this.update = update;
        this.defaultValue = defaultValue;
        this.create = create;
        this.readOnly = readonly;
        this.encrypt = encrypt;
        this.isFlag = isFlag;
    }

    public boolean isFlag() {
        return isFlag;
    }

    public static ServerSetting get(final String name) {
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


    public boolean isEncrypted() {
        return encrypt;
    }

    @Override
    public String toString() {
        return name;
    }

    private static class SettingConstants {
        static final String CONST_SERVER_VERSION = "3.7.1";
        static final String STORE_DIRECTORY_DEFAULT = "/tmp";
        static final String SETTING_ADMIN = "admin";
        static final String SETTING_SMTP = "smtp";
        static final String SETTING_SMTP_PASSWORD = "smtp password";
        static final String SETTING_ADMIN_DEFAULT = "support@nimbits.com";
        static final String SETTING_VERSION = "version";
        static final String API_KEY = "API_KEY";
        static final String SETTING_STORE_DIRECTORY = "blob dir";
        static final String API_KEY_DEFAULT = "API_KEY_DEFAULT";
        static final String SETTING_SMTP_DEFAULT = "smtp.gmail.com";
        static final String SETTING_SMTP_PASSWORD_DEFAULT = "";
        static final String SETTING_STATS = "upload stats to nimbits.com";
        static final String SETTING_STATS_DEFAULT = Boolean.TRUE.toString();
        static final String SETTING_UUID= "uuid";
        static final String SETTING_UUID_DEFAULT = "";
        private SettingConstants() {
        }
    }
}
