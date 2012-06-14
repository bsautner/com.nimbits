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

package com.nimbits.server.external.core;

import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.Path;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.instance.Instance;
import com.nimbits.client.model.instance.InstanceModelFactory;
import com.nimbits.server.admin.common.ServerInfoImpl;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.settings.SettingTransactionsFactory;
import com.nimbits.server.settings.SettingsServiceFactory;

import java.util.logging.Logger;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/31/11
 * Time: 10:29 AM
 */
public class CoreImpl implements Core {
    private static final Logger log = Logger.getLogger(CoreImpl.class.getName());



    @Override
    public void reportToCore(final Entity entity, final Action action, final String instanceURL) {
        try {
            final String serverUrl = ServerInfoImpl.getFullServerURL(null);

            if (!Utils.isEmptyString(serverUrl) &&  SettingsServiceFactory.getInstance().getBooleanSetting(SettingType.serverIsDiscoverable)) {
                final String json = GsonFactory.getInstance().toJson(entity);
                final String params =  Parameters.json.getText() + '=' + json
                        + '&' + Parameters.url.getText() + '=' + instanceURL
                        + '&' + Parameters.action.getText() + '=' + Action.update.name();

                log.info(Path.PATH_NIMBITS_CORE_ENTITY_DESC_URL + '?' + params);
              //  final String response = HttpCommonFactory.getInstance().doPost(Path.PATH_NIMBITS_CORE_ENTITY_DESC_URL, params);
               // log.info("response from core: " + response);

            }

        } catch (NimbitsException e) {
            log.severe(e.getMessage());

        }

    }
    @Override
    public void reportInstanceToCore(final String instanceURL) {
        try {
            final String serverUrl = ServerInfoImpl.getFullServerURL(null);

            if (!Utils.isEmptyString(serverUrl) && SettingsServiceFactory.getInstance().getBooleanSetting(SettingType.serverIsDiscoverable)) {
                final String email = SettingTransactionsFactory.getInstance().getSetting(SettingType.admin);
                final EmailAddress emailAddress = CommonFactoryLocator.getInstance().createEmailAddress(email);

                EntityName name = CommonFactoryLocator.getInstance().createName(serverUrl, EntityType.instance);
                final Entity instanceEntity = EntityModelFactory.createEntity(name, "", EntityType.instance, ProtectionLevel.everyone, serverUrl, email);

                final Instance server = InstanceModelFactory.createInstance(instanceEntity, serverUrl, emailAddress, SettingType.serverVersion.getDefaultValue());
                final String serverJson = GsonFactory.getInstance().toJson(server);
                final String json = GsonFactory.getInstance().toJson(instanceEntity);
                final String params = Parameters.json.getText() + '=' + serverJson
                        + '&' + Parameters.action.getText() + '=' + Action.update.name()
                        + '&' + Parameters.url.getText() + '=' + instanceURL;

                log.info(Path.PATH_NIMBITS_CORE_ENTITY_DESC_URL + '?' + params);
               // final String response = HttpCommonFactory.getInstance().doPost(Path.PATH_NIMBITS_CORE_ENTITY_DESC_URL, params);
               // log.info("response from core: " + response);

            }

        } catch (NimbitsException e) {
            log.severe(e.getMessage());

        }

    }

}
