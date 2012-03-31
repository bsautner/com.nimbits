/*
 * Copyright (c) 2010 Tonic Solutions LLC.
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

package com.nimbits.server.orm;

import com.nimbits.client.enums.SettingType;

import javax.jdo.annotations.*;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class ServerSetting {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    public Long id;

    private static final long serialVersionUID = 1L;
    // Constructors


    public ServerSetting() {
    }

    public ServerSetting(final String value, final SettingType setting) {
        this.value = value;
        this.name = setting.getName();
    }

    @Persistent
    private String name;
    @Persistent
    private String value;

    public SettingType getSetting() {
        return SettingType.get(name);
    }



    public void setName(SettingType setting) {
        this.name = setting.getName();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
