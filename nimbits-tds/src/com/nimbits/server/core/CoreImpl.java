package com.nimbits.server.core;

import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerModelFactory;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.http.HttpCommonFactory;
import com.nimbits.server.settings.SettingTransactionsFactory;
import com.nimbits.shared.Utils;

import java.util.logging.Logger;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/31/11
 * Time: 10:29 AM
 */
public class CoreImpl implements Core {
    private static final Logger log = Logger.getLogger(CoreImpl.class.getName());

    public void reportDeleteToCore(final String json, final EntityType entityType) {
        try {
            if (SettingTransactionsFactory.getInstance().getSetting(Const.SETTING_SERVER_IS_DISCOVERABLE).equals("1")) {


                final String params = Const.PARAM_ENTITY + "=" + json
                        + "&" + Const.PARAM_ENTITY_TYPE + "=" + entityType.getCode()
                        + "&" + Const.PARAM_ACTION + "=" + Action.delete.name();


                HttpCommonFactory.getInstance().doPost(Const.PATH_NIMBITS_CORE_ENTITY_DESC_URL, params);

            }
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
        }

    }

    //    public void reportCategoryUpdateToCore(final HttpServletRequest req, Category category)  {
//        String json = GsonFactory.getInstance().toJson(category);
//        try {
//            if (SettingTransactionsFactory.getInstance().getSetting(Const.SETTING_SERVER_IS_DISCOVERABLE).equals("1")) {
//                final String email = SettingTransactionsFactory.getInstance().getSetting(Const.SETTING_ADMIN);
//                final EmailAddress emailAddress = CommonFactoryLocator.getInstance().createEmailAddress(email);
//                final Server server = ServerModelFactory.createServer(ServerInfoImpl.getFullServerURL(req), emailAddress, Const.CONST_SERVER_VERSION);
//                final String serverJson = GsonFactory.getInstance().toJson(server);
//
//                final String params = Const.PARAM_SERVER + "=" + serverJson
//                        + "&" + Const.PARAM_ENTITY + "=" + json
//                        + "&" + Const.PARAM_ENTITY_TYPE + "=" + EntityType.category.getCode()
//                        + "&" + Const.PARAM_ACTION + "=" + Action.update.name();
//
//
//                String response = HttpCommonFactory.getInstance().doPost(Const.PATH_NIMBITS_CORE_ENTITY_DESC_URL, params);
//
//
//            }
//        } catch (NimbitsException e) {
//            log.severe(e.getMessage());
//
//        }
//
//    }
    public void reportUpdateToCore(final String serverUrl, final String json, final EntityType entityType) {
        try {
            if (!Utils.isEmptyString(serverUrl) && SettingTransactionsFactory.getInstance().getSetting(Const.SETTING_SERVER_IS_DISCOVERABLE).equals("1")) {
                final String email = SettingTransactionsFactory.getInstance().getSetting(Const.SETTING_ADMIN);
                final EmailAddress emailAddress = CommonFactoryLocator.getInstance().createEmailAddress(email);
                final Server server = ServerModelFactory.createServer(serverUrl, emailAddress, Const.CONST_SERVER_VERSION);
                final String serverJson = GsonFactory.getInstance().toJson(server);

                final String params = Const.PARAM_SERVER + "=" + serverJson
                        + "&" + Const.PARAM_ENTITY + "=" + json
                        + "&" + Const.PARAM_ENTITY_TYPE + "=" + entityType.getCode()
                        + "&" + Const.PARAM_ACTION + "=" + Action.update.name();

                log.info(Const.PATH_NIMBITS_CORE_ENTITY_DESC_URL + "?" + params);
                String response = HttpCommonFactory.getInstance().doPost(Const.PATH_NIMBITS_CORE_ENTITY_DESC_URL, params);
                log.info("response from core: " + response);

            }

        } catch (NimbitsException e) {
            log.severe(e.getMessage());

        }

    }


}
