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

package com.nimbits.server.orm;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.billing.Billing;
import com.nimbits.client.model.user.User;

import javax.jdo.annotations.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 8/14/12
 * Time: 1:59 PM
 */
@PersistenceCapable
public class BillingEntity implements Billing {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    protected Key key;


    @Persistent
    private Boolean billingEnabled;

    @Persistent
    private BigDecimal accountBalance;

    @Persistent
    private BigDecimal maxDailyAllowance;

    @Persistent
    private UserEntity user;

    @NotPersistent
    private Date lastSaved;

    @NotPersistent
    private double billedToday;

    public BillingEntity() {
    }

    public BillingEntity(User user, Billing billing) {
        this.billingEnabled = billing.isBillingEnabled();
        this.accountBalance =BigDecimal.valueOf(billing.getAccountBalance());
        this.maxDailyAllowance = BigDecimal.valueOf(billing.getMaxDailyAllowance());
        this.billedToday = billing.getBilledToday();
        this.key = KeyFactory.createKey(BillingEntity.class.getSimpleName(), user.getKey());

    }

    @Override
    public double getAccountBalance() {
        return this.accountBalance == null ? 0.00 : this.accountBalance.doubleValue();

    }

    @Override
    public void setAccountBalance(double accountBalance) {
        this.accountBalance = BigDecimal.valueOf(accountBalance);
    }

    @Override
    public boolean isBillingEnabled() {
        return this.billingEnabled == null ? false : this.billingEnabled;
    }

    @Override
    public void setBillingEnabled(boolean billingEnabled) {
        this.billingEnabled = billingEnabled;
    }


    @Override
    public double getMaxDailyAllowance() {
        return maxDailyAllowance == null ? 0.00 : maxDailyAllowance.doubleValue();
    }
    @Override
    public void setMaxDailyAllowance(double maxDailyAllowance) {
        this.maxDailyAllowance = BigDecimal.valueOf(maxDailyAllowance);
    }
    @Override
    public double getBilledToday() {
        return billedToday;
    }
    @Override
    public void setBilledToday(double billedToday) {
        this.billedToday = billedToday;
    }

    @Override
    public void update(Billing billing) {
        this.accountBalance = BigDecimal.valueOf(billing.getAccountBalance());
        this.billedToday = billing.getBilledToday();
        this.billingEnabled = billing.isBillingEnabled();
        this.maxDailyAllowance = BigDecimal.valueOf(billing.getMaxDailyAllowance());
    }

    @Override
    public Date getLastSaved() {
        return lastSaved == null ?  new Date(0) : new Date(lastSaved.getTime());
    }
    @Override
    public void setLastSaved(final Date lastSaved) {
        this.lastSaved = new Date(lastSaved.getTime());
    }
    @Override
    public void validate() throws NimbitsException {
        if (accountBalance.doubleValue() < 0.00) {

            throw new NimbitsException("account balance is less than zero");

        }

    }
}
