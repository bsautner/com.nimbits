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

package com.nimbits.client.model.billing;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.user.User;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 8/14/12
 * Time: 2:14 PM
 */
public class BillingModel implements Serializable, Billing {

 
     String key;


    private boolean billingEnabled;

    
    private double accountBalance;

     
    private double maxDailyAllowance;

      private Date lastSaved;


    private double billedToday;

    public BillingModel(boolean billingEnabled, double accountBalance, double maxDailyAllowance, User user, Date lastSaved, double billedToday) {
        this.billingEnabled = billingEnabled;
        this.accountBalance = accountBalance;
        this.maxDailyAllowance = maxDailyAllowance;
     //   this.user = user;
        this.lastSaved = lastSaved;
        this.billedToday = billedToday;
    }

    public BillingModel(Billing billing) {
        this.billingEnabled = billing.isBillingEnabled();
        this.accountBalance = (billing.getAccountBalance());
        this.maxDailyAllowance =  (billing.getMaxDailyAllowance());
        this.billedToday = billing.getBilledToday();
    }

    public BillingModel() {
    }

    @Override
    public double getAccountBalance() {
        return this.accountBalance;

    }

    @Override
    public void setAccountBalance(double accountBalance) {
        this.accountBalance =  (accountBalance);
    }

    @Override
    public boolean isBillingEnabled() {
        return this.billingEnabled ;
    }

    @Override
    public void setBillingEnabled(boolean billingEnabled) {
        this.billingEnabled = billingEnabled;
    }


    @Override
    public double getMaxDailyAllowance() {
        return maxDailyAllowance;
    }
    @Override
    public void setMaxDailyAllowance(double maxDailyAllowance) {
        this.maxDailyAllowance =  (maxDailyAllowance);
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
        this.accountBalance = (billing.getAccountBalance());
        this.billedToday = billing.getBilledToday();
        this.billingEnabled = billing.isBillingEnabled();
        this.maxDailyAllowance = (billing.getMaxDailyAllowance());
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
        if (accountBalance < 0.00) {

            throw new NimbitsException("account balance is less than zero");

        }

    }
}
