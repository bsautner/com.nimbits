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

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 8/14/12
 * Time: 2:01 PM
 */
public interface Billing extends Serializable {


    void update(Billing billing);

    void validate() throws NimbitsException;


    void setAccountBalance(double accountBalance);

    boolean isBillingEnabled();

    void setBillingEnabled(boolean billingEnabled);

    double getMaxDailyAllowance();

    void setMaxDailyAllowance(double maxDailyAllowance);

    double getBilledToday();

    void setBilledToday(double billedToday);

    double getAccountBalance();

    Date getLastSaved();

    void setLastSaved(Date lastSaved);
}
