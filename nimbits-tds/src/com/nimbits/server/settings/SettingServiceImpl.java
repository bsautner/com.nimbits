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

package com.nimbits.server.settings;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.SettingType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.service.settings.SettingsService;
import com.nimbits.server.api.openid.UserInfo;
import com.nimbits.shared.Utils;


import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Map;

public class SettingServiceImpl extends RemoteServiceServlet implements
        SettingsService {

   // private boolean billingEnabled;
    private static final long serialVersionUID = 2L;




    @Override
    public Map<SettingType, String> getSettings() throws NimbitsException {
        return SettingTransactionsFactory.getInstance().getSettings();
    }

    @Override
    public void updateSetting(final SettingType setting,final  String newValue) throws NimbitsException {
        SettingTransactionsFactory.getInstance().updateSetting(setting, newValue);
    }

    @Override
    public void addSetting(final SettingType setting,final String value) throws NimbitsException {
        SettingTransactionsFactory.getInstance().addSetting(setting, value);
    }

    @Override
    public void addSetting(SettingType setting, boolean defaultValue) throws NimbitsException {
        addSetting(setting, defaultValue ? Const.TRUE : Const.FALSE);
    }

    @Override
    public String getSetting(final SettingType paramName) throws NimbitsException {
        return SettingTransactionsFactory.getInstance().getSetting(paramName);
    }

    @Override
    public boolean getBooleanSetting(final SettingType paramName)   {
        String s;
        try {
            s = SettingTransactionsFactory.getInstance().getSetting(paramName);

            if (Utils.isEmptyString(s)) {
                s = paramName.getDefaultValue();
            }
            return s != null && s.equals(Const.TRUE);
        } catch (NimbitsException e) {
            return false;
        }
    }


}
