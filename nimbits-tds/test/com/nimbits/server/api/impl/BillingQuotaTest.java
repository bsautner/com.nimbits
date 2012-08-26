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

package com.nimbits.server.api.impl;

import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.SettingType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.user.User;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.admin.quota.QuotaFactory;
import com.nimbits.server.orm.UserEntity;
import com.nimbits.server.process.cron.SystemMaint;
import com.nimbits.server.settings.SettingsServiceFactory;
import com.nimbits.server.transactions.dao.entity.EntityDaoImpl;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 8/14/12
 * Time: 10:56 AM
 */
public class BillingQuotaTest  extends NimbitsServletTest {


    @Test
   public void chargeSomeMoneyTst() throws NimbitsException, IOException {



        SystemMaint systemMaint = new SystemMaint();

        systemMaint.doGet(req, resp);
        SettingsServiceFactory.getInstance().updateSetting(SettingType.quotaEnabled, Const.TRUE);

        user.getBilling().setAccountBalance(1.00);
        user.getBilling().setBillingEnabled(true);
        user.getBilling().setMaxDailyAllowance(.50);
        entityTransactions.addUpdateEntity(user, true);

        double calls = (0.01 /  QuotaFactory.getInstance(emailAddress).getCostPerApiCall());
        for (int i = 0; i < QuotaFactory.getInstance(emailAddress).getMaxDailyQuota()+calls; i++) {
            valueServlet.processGet(req, resp);
        }
        User u = (User) entityTransactions.getEntityByKey(user.getKey(), UserEntity.class).get(0);
        System.out.println(u.getBilling().getAccountBalance());
        Assert.assertEquals(0.99, u.getBilling().getAccountBalance(), .001);

    }

    @Test(expected = NimbitsException.class)
    public void outOfMoneyTest() throws NimbitsException, IOException {



        SystemMaint systemMaint = new SystemMaint();

        systemMaint.doGet(req, resp);
        SettingsServiceFactory.getInstance().updateSetting(SettingType.quotaEnabled, Const.TRUE);

        user.getBilling().setAccountBalance(0.05);
        user.getBilling().setBillingEnabled(true);
        user.getBilling().setMaxDailyAllowance(1.50);
        entityTransactions.addUpdateEntity(user, true);

        double calls = (0.06 /  QuotaFactory.getInstance(emailAddress).getCostPerApiCall());
        for (int i = 0; i < QuotaFactory.getInstance(emailAddress).getMaxDailyQuota()+calls; i++) {
            valueServlet.processGet(req, resp);
        }
        User u = (User) entityTransactions.getEntityByKey(user.getKey(), UserEntity.class).get(0);
        System.out.println(u.getBilling().getAccountBalance());
        Assert.assertEquals(0.99, u.getBilling().getAccountBalance(), .001);

    }

    @Test(expected = NimbitsException.class)
    public void overDailyBudgetTest() throws NimbitsException, IOException {



        SystemMaint systemMaint = new SystemMaint();

        systemMaint.doGet(req, resp);
        SettingsServiceFactory.getInstance().updateSetting(SettingType.quotaEnabled, Const.TRUE);

        user.getBilling().setAccountBalance(110.05);
        user.getBilling().setBillingEnabled(true);
        user.getBilling().setMaxDailyAllowance(0.03);
        entityTransactions.addUpdateEntity(user, true);

        double calls = (0.05 /  QuotaFactory.getInstance(emailAddress).getCostPerApiCall());
        for (int i = 0; i < QuotaFactory.getInstance(emailAddress).getMaxDailyQuota()+calls; i++) {
            valueServlet.processGet(req, resp);
        }
        User u = (User) entityTransactions.getEntityByKey(user.getKey(), UserEntity.class).get(0);
        System.out.println(u.getBilling().getAccountBalance());
        Assert.assertEquals(0.99, u.getBilling().getAccountBalance(), .001);

    }
}
