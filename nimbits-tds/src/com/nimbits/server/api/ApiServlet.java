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
import com.nimbits.client.model.common.CommonFactoryLocator;
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
import com.nimbits.server.admin.quota.QuotaManager;
import com.nimbits.server.api.helper.LocationReportingHelperFactory;
import com.nimbits.server.transactions.service.user.UserServerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private static final String BUDGET_ERROR_BUDGET_EXCEEDED = "Maximum daily budget exceeded. Please increase your daily budget";
    private static final String BUDGET_ERROR_ZERO_BALANCE = "Your api call balance has been depleted, please fund your account";
    private static final String BUDGET_INCREASE_ERROR = "Please report this error";
    private static final String BUDGET_ERROR_NOT_PAID = "You have exceeded the max free api call quota. Please enable billing and fund your account to record more data.";
    public static final String MISSING_ACCOUNT_BALANCE_DATA_POINT = "MISSING ACCOUNT BALANCE DATA POINT";

    @Resource(name="entityService")
    protected EntityService entityService;

    @Resource(name="valueService")
    protected ValueService valueService;

    @Resource(name= "userService")
    protected UserServerService userService;

    @Resource(name = "commonFactory")
    protected CommonFactory commonFactory;

    @Resource(name = "settingsService")
    protected SettingsService settingsService;

    @Resource(name = "quotaManager")
    protected QuotaManager quotaManager;


    protected static boolean okToReport(final User u, final Entity c) {

        return c.getProtectionLevel().equals(ProtectionLevel.everyone) ||  (u != null && ! u.isRestricted());
    }
    protected static boolean okToRead(final User u, final Entity c) {

        return (u != null && c.isOwner(u));
    }

    protected boolean isPost(final HttpServletRequest req) {
        return req.getMethod().equals("POST");
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

            processBilling();


        }
        buildParamMap(req);
        addResponseHeaders(resp, type);


    }

    private void buildParamMap(HttpServletRequest req) {
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
    }

    private void processBilling() throws NimbitsException {
        if (settingsService.getBooleanSetting(SettingType.billingEnabled)) {
           final int count = quotaManager.incrementCounter(user.getEmail());
           final int max = quotaManager.getFreeDailyQuota();
           if (count > max) {

               if (user.isBillingEnabled()) {
                   final EntityName name = CommonFactoryLocator.getInstance().createName(Const.ACCOUNT_BALANCE, EntityType.point);
                   log.info("billing enabled");
                   final List<Entity> points =  entityService.getEntityByName(user, name, EntityType.point);
                   if (points.isEmpty()) {
                       log.severe(MISSING_ACCOUNT_BALANCE_DATA_POINT);
                       throw new NimbitsException(MISSING_ACCOUNT_BALANCE_DATA_POINT);
                   }
                   else {
                       final Point accountBalance = (Point) points.get(0);
                       final List<Value> currentBalanceList = valueService.getCurrentValue(accountBalance);
                       if (currentBalanceList.isEmpty()) {
                           log.severe(BUDGET_ERROR_ZERO_BALANCE);
                           throw new NimbitsException(BUDGET_ERROR_ZERO_BALANCE);
                       }
                       else {
                           final Value current = currentBalanceList.get(0);
                           final double spent = BigDecimal.valueOf(valueService.calculateDelta(accountBalance)).setScale(4, RoundingMode.HALF_UP).doubleValue();
                           if (spent > accountBalance.getDeltaAlarm()) {
                               log.severe(BUDGET_ERROR_BUDGET_EXCEEDED);
                               log.severe("current:  " + current.getDoubleValue());
                               log.severe("spent:  " + spent);
                               log.severe("budget:  " + accountBalance.getDeltaAlarm());
                               throw new NimbitsException(BUDGET_ERROR_BUDGET_EXCEEDED);
                           }
                           else {
                               final double currentBalance = round(current.getDoubleValue());

                               final double newValue = round(currentBalance - quotaManager.getCostPerApiCall());

                               if (newValue <= 0.0) {
                                   log.severe(BUDGET_ERROR_ZERO_BALANCE);
                                   throw new NimbitsException(BUDGET_ERROR_ZERO_BALANCE);
                               }
                               else if (newValue > currentBalance) {
                                   log.severe(BUDGET_INCREASE_ERROR);
                                   throw new NimbitsException(BUDGET_INCREASE_ERROR);
                               }
                               else if (newValue == currentBalance) {
                                   //weird
                                   double fixedValue = round(newValue - quotaManager.getCostPerApiCall());
                                   Value value = ValueFactory.createValueModel(fixedValue);
                                   valueService.recordValue(user, accountBalance, value);
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
                   throw new NimbitsException(BUDGET_ERROR_NOT_PAID);
               }
//

           }
       }
    }
    private double round(double value) {
       return
                BigDecimal.valueOf
                        (value)
                        .setScale(4, RoundingMode.HALF_UP).doubleValue();

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

    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    public void setValueService(ValueService valueService) {
        this.valueService = valueService;
    }

    public void setUserService(UserServerService userService) {
        this.userService = userService;
    }

    public void setCommonFactory(CommonFactory commonFactory) {
        this.commonFactory = commonFactory;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setQuotaManager(QuotaManager quotaManager) {
        this.quotaManager = quotaManager;
    }
}
