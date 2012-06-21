package com.nimbits.server.process.task;

import com.nimbits.client.constants.Const;
import com.nimbits.client.constants.Path;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.SettingType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.server.admin.logging.LogHelper;
import com.nimbits.server.http.HttpCommonFactory;
import com.nimbits.server.settings.SettingsServiceFactory;
import com.nimbits.shared.Utils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

public class CoreTask extends HttpServlet {

    private static final Logger log = Logger.getLogger(CoreTask.class.getName());

    private static final long serialVersionUID = 1L;

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {
        log.info("Starting core task");
        final String entity = req.getParameter(Parameters.entity.name());
        final String action = req.getParameter(Parameters.action.name());
        final String instance = req.getParameter(Parameters.instance.name());
        final String location = req.getParameter(Parameters.location.name());
        try {
            if (!Utils.isEmptyString(entity) && !Utils.isEmptyString(instance) && ! Utils.isEmptyString(action) && SettingsServiceFactory.getInstance().getBooleanSetting(SettingType.serverIsDiscoverable)) {

                if (!com.nimbits.client.common.Utils.isEmptyString(instance)) {
                    final String params =  Parameters.entity.getText() + '=' + entity
                            + '&' + Parameters.action.getText() + '=' + action
                            + '&' + Parameters.instance.getText() + '=' + instance;

                    log.info(Path.PATH_NIMBITS_CORE_ENTITY_DESC_URL + '?' + params);
                    HttpCommonFactory.getInstance().doPost(Path.PATH_NIMBITS_CORE_ENTITY_DESC_URL, params);

                    reportLocation(entity, location);
                    // resp.addHeader(Const.HTTP_HEADER_RESPONSE, response);
                }
            }
            else if (!Utils.isEmptyString(entity) && !Utils.isEmptyString(location)) {
                reportLocation(entity, location);

            }
            //40.127883,-75.431853
            else {
                resp.setStatus(Const.HTTP_STATUS_BAD_REQUEST);
            }
        }
        catch (NimbitsException ex) {
            LogHelper.logException(this.getClass(), ex);
            resp.setStatus(Const.HTTP_STATUS_INTERNAL_SERVER_ERROR);

        }
    }

    private void reportLocation(final String entity, final String location) {
        if (! Utils.isEmptyString(location) && ! Utils.isEmptyString(entity)) {
            log.info("Reporting location");
            final String params =  Parameters.entity.getText() + '=' + entity
                    + '&' + Parameters.location.getText() + '=' + location;
            log.info(params);
            HttpCommonFactory.getInstance().doPost(Path.PATH_NIMBITS_CORE_ENTITY_LOCATION_URL, params);
        }
    }

}