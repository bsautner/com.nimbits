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

package com.nimbits.server.transaction.settings;


import com.nimbits.client.enums.ServerSetting;
import com.nimbits.server.secure.StringEncryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {


    private SettingsDao settingsDao;

    private StringEncryption stringEncryption;


    @Autowired
    public SettingsService(SettingsDao settingsDao, StringEncryption stringEncryption) {
        this.settingsDao = settingsDao;
        this.stringEncryption = stringEncryption;
    }


    public String getSetting(final ServerSetting paramName) {
        String stored = settingsDao.getSetting(paramName);
        if (paramName.isEncrypted()) {
            try {
                return stringEncryption.decrypt(stored);
            } catch (Exception e) {
                return null;
            }
        } else {
            return stored;
        }
    }


    public void updateSetting(final ServerSetting setting, final String newValue) {
        if (setting.isEncrypted()) {
            try {
                settingsDao.updateSetting(setting, stringEncryption.encrypt(newValue, false));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            settingsDao.updateSetting(setting, newValue);
        }
    }





}
