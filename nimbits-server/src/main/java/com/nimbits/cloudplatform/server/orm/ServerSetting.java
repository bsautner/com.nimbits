/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.server.orm;

import com.nimbits.cloudplatform.client.enums.SettingType;
import com.nimbits.cloudplatform.client.model.setting.Setting;

import javax.jdo.annotations.*;

@SuppressWarnings("unused")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class ServerSetting implements Setting {

    private static final long serialVersionUID = 8358325780044920423L;
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;



    private ServerSetting() {
    }

    public ServerSetting(final String value, final String setting) {
        this.value = value;
        this.name = setting;
    }

    @Persistent
    private String name;
    @Persistent
    private String value;


    @Override
    public SettingType getSetting() {
        return SettingType.get(name);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(final String newValue) {
        this.value = newValue;
    }

}
