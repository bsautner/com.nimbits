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

package com.nimbits.server.api;

import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.location.Location;
import com.nimbits.client.model.location.LocationFactory;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.settings.SettingsService;
import com.nimbits.client.service.value.ValueService;
import com.nimbits.server.admin.quota.Quota;
import com.nimbits.server.admin.quota.QuotaFactory;
import com.nimbits.server.api.helper.LocationReportingHelperFactory;
import com.nimbits.server.transactions.service.user.UserServerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/20/12
 * Time: 12:58 PM
 */
@Service("apiServlet")
public class ApiServlet extends HttpServlet {

    protected static User user;
    private static Map<Parameters, String> paramMap;
    protected final static Logger log = Logger.getLogger(ApiServlet.class.getName());
    protected static Location location;
    private static final String BUDGET_ERROR = "Maximum daily budget exceeded. Please increase your daily budget";

    @Resource(name="entityService")
    private EntityService entityService;

    @Resource(name="valueService")
    private ValueService valueService;

    @Resource(name= "userService")
    private UserServerService userService;

    @Resource(name = "commonFactory")
    private CommonFactory commonFactory;

    @Resource(name = "settingService")
    private SettingsService settingsService;

    protected static boolean okToReport(final User u, final Entity c) {

        return c.getProtectionLevel().equals(ProtectionLevel.everyone) ||  (u != null && ! u.isRestricted());
    }
    protected static boolean okToRead(final User u, final Entity c) {

        return (u != null && c.isOwner(u));
    }

    //    protected static void reportLocation(HttpServletRequest req, Entity entity) {
//       LocationReportingHelperFactory.getInstance().reportLocation(req, entity);
//    }
    protected static void reportLocation(Entity entity, Location location) {

        LocationReportingHelperFactory.getInstance().reportLocation(entity, location);
    }

    public void doInit(final HttpServletRequest req, final HttpServletResponse resp, final ExportType type) throws NimbitsException {

        user = userService.getHttpRequestUser(req);
        getGPS(req);
        if (user != null) {
            Quota quota = QuotaFactory.getInstance(user.getEmail());
            int count = quota.incrementCounter();
            int max = quota.getMaxDailyQuota();
            log.info("quota call " + count + " of " + max);

            if (settingsService.getBooleanSetting(SettingType.billingEnabled)) {

                if (count > max) {

                    if (user.isBillingEnabled()) {
                        EntityName name = commonFactory.createName(Const.ACCOUNT_BALANCE, EntityType.point);
                        log.info("billing enabled");
                        List<Entity> points =  entityService.getEntityByName(user, name, EntityType.point);
                        if (points.isEmpty()) {
                            throw new NimbitsException(BUDGET_ERROR);
                        }
                        else {
                            Point accountBalance = (Point) points.get(0);
                            List<Value> currentBalanceList = valueService.getCurrentValue(accountBalance);
                            if (currentBalanceList.isEmpty()) {
                                throw new NimbitsException(BUDGET_ERROR);
                            }
                            else {
                                Value current = currentBalanceList.get(0);
                                double spent =valueService.calculateDelta(accountBalance);
                                if (spent > accountBalance.getDeltaAlarm()) {
                                    throw new NimbitsException(BUDGET_ERROR);
                                }
                                else {
                                    Double newValue = current.getDoubleValue() - quota.getCostPerApiCall();
                                    if (newValue <= 0.0) {
                                        throw new NimbitsException(BUDGET_ERROR);
                                    }
                                    else {
                                        Value value = ValueFactory.createValueModel(newValue);
                                        valueService.recordValue(user, accountBalance, value);
                                    }
                                }

                            }
                        }
                    }
                    else {
                        throw new NimbitsException(BUDGET_ERROR);
                    }
//

                }
            }




        }
        else {
            log.info("user was null");
        }



        paramMap = new EnumMap<Parameters, String>(Parameters.class);

        final Parameters[] items = {
                Parameters.point,
                Parameters.value,
                Parameters.json,
                Parameters.note,
                Parameters.lat,
                Parameters.lng,
                Parameters.timestamp,
                Parameters.data,
                Parameters.uuid,
                Parameters.format,
                Parameters.name,
                Parameters.points,
                Parameters.count,
                Parameters.autoscale,
                Parameters.category,
                Parameters.key,
                Parameters.client,
                Parameters.parent,
                Parameters.description,
                Parameters.action,
                Parameters.id

        };




        for (final Parameters s : items) {
            paramMap.put(s, req.getParameter(s.getText()));
        }



        addResponseHeaders(resp, type);


    }

    public static ClientType getClientType() {
        ClientType type;
        if (containsParam(Parameters.client)) {
            type = ClientType.get(getParam(Parameters.client));
            if (type == null) {
                type = ClientType.other;
            }

        }
        else {
            type = ClientType.other;
        }
        return type;
    }

    public static void addResponseHeaders(final HttpServletResponse resp, final ExportType type) {
        if (! type.equals(ExportType.unknown)) {
            resp.setContentType(type.getCode());
        }
        //    resp.setContentType("text/plain");
        resp.addHeader("Cache-Control", "no-cache");
        resp.addHeader("Access-Control-Allow-Origin", "*");
    }

    public static Location getGPS(final HttpServletRequest req) {
        if (req != null) {
            final String gps = req.getHeader("X-AppEngine-CityLatLong");
            if (! Utils.isEmptyString(gps)) {
                location = LocationFactory.createLocation(gps);
            }
            else {
                location = LocationFactory.createLocation();
            }
        }
        else {
            location = LocationFactory.createLocation();
        }
        return location;
    }

    protected static String getParam(final Parameters param) {
        return paramMap.containsKey(param) ? paramMap.get(param) : null;
    }

    protected static boolean containsParam(final Parameters param) {

        return paramMap.containsKey(param) && !Utils.isEmptyString(paramMap.get(param));

    }

}
