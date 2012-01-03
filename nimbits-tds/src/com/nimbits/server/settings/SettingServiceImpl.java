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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.settings;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.service.settings.SettingsService;

import java.util.Map;

public class SettingServiceImpl extends RemoteServiceServlet implements
        SettingsService {


    private static final long serialVersionUID = 1L;


    @Override
    public String getServerSecret() throws NimbitsException {
        return SettingTransactionsFactory.getInstance().getServerSecret();
    }


    @Override
    public Map<String, String> getSettings() throws NimbitsException {
        return SettingTransactionsFactory.getInstance().getSettings();
    }

    @Override
    public String getSetting(final String paramName) throws NimbitsException {
        return SettingTransactionsFactory.getInstance().getSetting(paramName);
    }

}
