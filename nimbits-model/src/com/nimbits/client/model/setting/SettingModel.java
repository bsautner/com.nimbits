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

package com.nimbits.client.model.setting;

import com.nimbits.client.enums.SettingType;

/**
 * Created by Benjamin Sautner
 * User: benjamin
 * Date: 5/5/11
 * Time: 3:41 PM
 */
public class SettingModel implements Setting {
    private static final long serialVersionUID = 4376024128461099128L;
    private String name;
    private String value;

    public SettingModel(final Setting setting) {
        this.name = setting.getValue();
        this.value = setting.getValue();
    }

    @Override
    public SettingType getSetting() {
        return SettingType.get(this.name);
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
