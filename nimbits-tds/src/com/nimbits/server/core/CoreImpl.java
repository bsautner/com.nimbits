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

package com.nimbits.server.core;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.server.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.http.*;
import com.nimbits.server.settings.*;
import com.nimbits.shared.*;

import java.util.logging.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/31/11
 * Time: 10:29 AM
 */
public class CoreImpl implements Core {
    private static final Logger log = Logger.getLogger(CoreImpl.class.getName());

    public void reportDeleteToCore(final Entity entity) {
        try {
            if (SettingTransactionsFactory.getInstance().getSetting(Const.SETTING_SERVER_IS_DISCOVERABLE).equals("1")) {
                String json = GsonFactory.getInstance().toJson(entity);

                final String params = Const.PARAM_ENTITY + "=" + json
                        + "&" + Const.PARAM_ENTITY_TYPE + "=" + entity.getEntityType()
                        + "&" + Const.Params.PARAM_ACTION + "=" + Action.delete.name();


                HttpCommonFactory.getInstance().doPost(Const.PATH_NIMBITS_CORE_ENTITY_DESC_URL, params);

            }
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
        }

    }


    public void reportUpdateToCore(final String serverUrl, final Entity entity) {
        try {
            if (!Utils.isEmptyString(serverUrl) && SettingTransactionsFactory.getInstance().getSetting(Const.SETTING_SERVER_IS_DISCOVERABLE).equals("1")) {
                final String email = SettingTransactionsFactory.getInstance().getSetting(Const.SETTING_ADMIN);
                final EmailAddress emailAddress = CommonFactoryLocator.getInstance().createEmailAddress(email);
                final Server server = ServerModelFactory.createServer(serverUrl, emailAddress, Const.CONST_SERVER_VERSION);
                final String serverJson = GsonFactory.getInstance().toJson(server);
                String json = GsonFactory.getInstance().toJson(entity);
                final String params = Const.PARAM_SERVER + "=" + serverJson
                        + "&" + Const.PARAM_ENTITY + "=" + json
                        + "&" + Const.PARAM_ENTITY_TYPE + "=" + entity.getEntityType()
                        + "&" + Const.Params.PARAM_ACTION + "=" + Action.update.name();

                log.info(Const.PATH_NIMBITS_CORE_ENTITY_DESC_URL + "?" + params);
                String response = HttpCommonFactory.getInstance().doPost(Const.PATH_NIMBITS_CORE_ENTITY_DESC_URL, params);
                log.info("response from core: " + response);

            }

        } catch (NimbitsException e) {
            log.severe(e.getMessage());

        }

    }


}
