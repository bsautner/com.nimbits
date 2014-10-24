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

package com.nimbits.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.enums.ServerSetting;
import com.nimbits.client.service.settings.SettingsServiceRpc;
import com.nimbits.server.transaction.settings.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.annotation.Resource;
import javax.servlet.ServletException;

@Service
public class SettingServiceRpcImpl extends RemoteServiceServlet  implements SettingsServiceRpc {

    @Autowired
    SettingsService settingsService;

    @Override
    public void init() throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);


    }

    @Override
    public String getSetting(String setting) {
        ServerSetting s = ServerSetting.get(setting);
        return settingsService.getSetting(s);
    }

    @Override
    public void updateSetting(String setting, String value) {
        ServerSetting s = ServerSetting.get(setting);
        if (s != null) {
            settingsService.updateSetting(s, value);
        }
    }



}
