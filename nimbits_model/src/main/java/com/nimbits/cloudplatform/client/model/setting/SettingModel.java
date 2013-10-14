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

package com.nimbits.cloudplatform.client.model.setting;

import com.nimbits.cloudplatform.client.enums.SettingType;

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

    public SettingModel() {
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
