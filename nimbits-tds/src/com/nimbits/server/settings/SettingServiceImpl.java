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

package com.nimbits.server.settings;


import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.service.settings.*;
import com.nimbits.shared.*;

import java.util.*;

public class SettingServiceImpl extends RemoteServiceServlet implements
        SettingsService {


    private static final long serialVersionUID = 1L;



    @Override
    public Map<SettingType, String> getSettings() throws NimbitsException {
        return SettingTransactionsFactory.getInstance().getSettings();
    }

    @Override
    public void updateSetting(final SettingType setting,final  String newValue) {
         SettingTransactionsFactory.getInstance().updateSetting(setting, newValue);
    }

    @Override
    public void addSetting(final SettingType setting,final String value) {
        SettingTransactionsFactory.getInstance().addSetting(setting, value);
    }

    @Override
    public String getSetting(final SettingType paramName) throws NimbitsException {
        return SettingTransactionsFactory.getInstance().getSetting(paramName);
    }

    @Override
    public boolean getBooleanSetting(final SettingType paramName) throws NimbitsException {
        String s =  SettingTransactionsFactory.getInstance().getSetting(paramName);
        if (Utils.isEmptyString(s)) {
            s = paramName.getDefaultValue();
        }
        return s != null && s.equals(Const.TRUE);

    }

}
