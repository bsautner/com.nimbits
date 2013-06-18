/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.transactions.service.settings;

import com.nimbits.client.enums.SettingType;
import com.nimbits.client.exception.NimbitsException;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 5/4/12
 * Time: 11:54 AM
 * To change this template use File | Settings | File Templates.
 */
public interface SettingTransactions {
    String getSetting(SettingType setting) throws NimbitsException;

    void updateSetting(SettingType name, String newValue) throws NimbitsException;

    String reloadCache() throws NimbitsException;

    Map<SettingType, String> getSettings() throws NimbitsException;

    void addSetting(SettingType name, String value) throws NimbitsException;
}
