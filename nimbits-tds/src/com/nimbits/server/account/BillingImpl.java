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

package com.nimbits.server.account;

import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.SettingType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.service.settings.SettingsService;
import com.nimbits.server.admin.quota.QuotaManagerImpl;
import com.nimbits.server.transactions.service.entity.EntityServiceImpl;
import com.nimbits.server.transactions.service.settings.SettingsServiceImpl;
import com.nimbits.server.transactions.service.value.ValueServiceImpl;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 10/15/12
 * Time: 2:45 PM
 */
@Component("Billing")
public class BillingImpl implements Billing {
    private final static Logger log = Logger.getLogger(BillingImpl.class.getName());

    private SettingsService settingsService;
    private QuotaManagerImpl quotaManager;
    private EntityServiceImpl entityService;
    private ValueServiceImpl valueService;

    private static final String BUDGET_ERROR_BUDGET_EXCEEDED = "Maximum daily budget exceeded. Please increase your daily budget";
    private static final String BUDGET_ERROR_ZERO_BALANCE = "Your api call balance has been depleted, please fund your account";
    private static final String BUDGET_INCREASE_ERROR = "Please report this error";
    private static final String BUDGET_ERROR_NOT_PAID = "You have exceeded the max free api call quota. Please enable billing and fund your account to record more data.";
    private static final String MISSING_ACCOUNT_BALANCE_DATA_POINT = "MISSING ACCOUNT BALANCE DATA POINT";


    @Override
    public void processBilling(final User user) throws NimbitsException {
        if (settingsService.getBooleanSetting(SettingType.billingEnabled)) {
            final int count = quotaManager.incrementCounter(user.getEmail());
            final int max = quotaManager.getFreeDailyQuota();
            if (count > max) {

                if (user.isBillingEnabled()) {
                    final EntityName name = CommonFactory.createName(Const.ACCOUNT_BALANCE, EntityType.point);
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
                                    log.severe("weird equality issue");
                                    double fixedValue = round(newValue - quotaManager.getCostPerApiCall());
                                    log.severe("fixed: " + fixedValue);
                                    log.severe("newValue: " + newValue);
                                    log.severe("currentBalance: " + currentBalance);
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
                        .setScale(5, RoundingMode.HALF_UP).doubleValue();

    }
    public void setSettingsService(SettingsServiceImpl settingsService) {
        this.settingsService = settingsService;
    }

    public void setQuotaManager(QuotaManagerImpl quotaManager) {
        this.quotaManager = quotaManager;
    }

    public void setEntityService(EntityServiceImpl entityService) {
        this.entityService = entityService;
    }

    public void setValueService(ValueServiceImpl valueService) {
        this.valueService = valueService;
    }
}
