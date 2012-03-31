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

package com.nimbits.client.service.settings;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.nimbits.client.enums.SettingType;
import com.nimbits.client.exception.NimbitsException;

import java.util.Map;

@RemoteServiceRelativePath("settings")
public interface SettingsService extends RemoteService {

    String getSetting(final SettingType paramName) throws NimbitsException;

    boolean getBooleanSetting(final SettingType paramName) throws NimbitsException;

    Map<SettingType, String> getSettings() throws NimbitsException;

    void updateSetting(final SettingType setting, final String newValue);

    void addSetting(final SettingType setting,final  String defaultValue);
}
