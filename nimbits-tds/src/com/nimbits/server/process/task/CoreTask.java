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

package com.nimbits.server.process.task;

import com.nimbits.client.constants.Path;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.service.settings.SettingsService;
import com.nimbits.server.http.HttpCommonFactory;
import com.nimbits.shared.Utils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;


@Service("coreTask")
@Transactional
public class CoreTask extends HttpServlet  implements org.springframework.web.HttpRequestHandler{

    private static final Logger log = Logger.getLogger(CoreTask.class.getName());

    private static final long serialVersionUID = 1L;
    private SettingsService settingsService;

    @Override
    public void handleRequest(final HttpServletRequest req, final HttpServletResponse resp) {

//        log.info("Starting core task");
//        final String entity = req.getParameter(Parameters.entity.name());
//        final String action = req.getParameter(Parameters.action.name());
//        final String instance = req.getParameter(Parameters.instance.name());
//        final String location = req.getParameter(Parameters.location.name());
//
//        try {
//            if (!Utils.isEmptyString(entity) && !Utils.isEmptyString(instance) && ! Utils.isEmptyString(action) && settingsService.getBooleanSetting(SettingType.serverIsDiscoverable)) {
//
//                if (!com.nimbits.client.common.Utils.isEmptyString(instance)) {
//                    final String params =  Parameters.entity.getText() + '=' + entity
//                            + '&' + Parameters.action.getText() + '=' + action
//                            + '&' + Parameters.instance.getText() + '=' + instance;
//
//                    log.info(Path.PATH_NIMBITS_CORE_ENTITY_DESC_URL + '?' + params);
//                    HttpCommonFactory.getInstance().doPost(Path.PATH_NIMBITS_CORE_ENTITY_DESC_URL, params);
//
//                    reportLocation(entity, location);
                     resp.setStatus(HttpServletResponse.SC_OK);
//                    // resp.addHeader(Const.HTTP_HEADER_RESPONSE, response);
//                }
//            }
//            else if (!Utils.isEmptyString(entity) && !Utils.isEmptyString(location)) {
//                reportLocation(entity, location);
//
//            }
//            //40.127883,-75.431853
//            else {
//               resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            }
//        } catch (NimbitsException e) {
//            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//        }


    }

    private void reportLocation(final String entity, final String location) {
        if (! Utils.isEmptyString(location) && ! Utils.isEmptyString(entity)) {
            log.info("Reporting Location");
            final String params =  Parameters.entity.getText() + '=' + entity
                    + '&' + Parameters.location.getText() + '=' + location;
            log.info(params);
            HttpCommonFactory.getInstance().doPost(Path.PATH_NIMBITS_CORE_ENTITY_LOCATION_URL, params);
        }
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public SettingsService getSettingsService() {
        return settingsService;
    }
}