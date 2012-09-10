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
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.SettingType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.admin.quota.QuotaFactory;
import com.nimbits.server.orm.UserEntity;
import com.nimbits.server.process.cron.SystemMaint;
import com.nimbits.server.settings.SettingsServiceFactory;
import com.nimbits.server.transactions.dao.entity.EntityDaoImpl;
import com.nimbits.server.transactions.service.user.UserServiceFactory;
import com.nimbits.server.transactions.service.value.ValueServiceFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 8/14/12
 * Time: 10:56 AM
 */
public class BillingQuotaTest  extends NimbitsServletTest {


    @Test
   public void chargeSomeMoneyTst() throws NimbitsException, IOException {



//        SystemMaint systemMaint = new SystemMaint();
//
//        systemMaint.doGet(req, resp);
//        SettingsServiceFactory.getInstance().updateSetting(SettingType.quotaEnabled, Const.TRUE);
//
//        user.getBilling().setAccountBalance(1.00);
//        user.getBilling().setBillingEnabled(true);
//        user.getBilling().setMaxDailyAllowance(.50);
//        entityService.addUpdateEntity(user, user, true);
//
//        double calls = (0.01 /  QuotaFactory.getInstance(emailAddress).getCostPerApiCall());
//        for (int i = 0; i < QuotaFactory.getInstance(emailAddress).getMaxDailyQuota()+calls; i++) {
//            valueServlet.processGet(req, resp);
//        }
//        User u = (User) entityService.getEntityByKey(user.getKey(), UserEntity.class).get(0);
//        System.out.println(u.getBilling().getAccountBalance());
//        Assert.assertEquals(0.99, u.getBilling().getAccountBalance(), .001);

    }

    @Test(expected = NimbitsException.class)
    public void outOfMoneyTest() throws NimbitsException, IOException {




        SettingsServiceFactory.getInstance().updateSetting(SettingType.quotaEnabled, Const.TRUE);

        user.setBillingEnabled(true);

//
//        user.getBilling().setAccountBalance(0.05);
//        user.getBilling().setBillingEnabled(true);
//        user.getBilling().setMaxDailyAllowance(1.50);



        entityService.addUpdateEntity(user, user);
        EntityName name = CommonFactoryLocator.getInstance().createName(Const.ACCOUNT_BALANCE, EntityType.point);
        List<Entity> list = entityService.getEntityByName(user,name, EntityType.point );
        assertFalse(list.isEmpty());
        Point accountBalance = (Point) list.get(0);
        accountBalance.setDeltaAlarm(1.50);
        accountBalance.setDeltaAlarmOn(true);
        entityService.addUpdateEntity(user, accountBalance);



        double calls = (0.06 /  QuotaFactory.getInstance(emailAddress).getCostPerApiCall());
        for (int i = 0; i < QuotaFactory.getInstance(emailAddress).getMaxDailyQuota()+calls; i++) {
            valueServlet.processGet(req, resp);
        }
        User u = (User) entityService.getEntityByKey(user.getKey(), EntityType.user).get(0);
       // System.out.println(u.getBilling().getAccountBalance());
        List<Value> currentValueSample = ValueServiceFactory.getInstance().getCurrentValue(accountBalance);
        assertFalse(currentValueSample.isEmpty());
        Value currentValue = currentValueSample.get(0);
        Assert.assertEquals(0.99,currentValue.getDoubleValue(), .001);

    }

    @Test(expected = NimbitsException.class)
    public void overDailyBudgetTest() throws NimbitsException, IOException {

        SettingsServiceFactory.getInstance().updateSetting(SettingType.quotaEnabled, Const.TRUE);

        user.setBillingEnabled(true);

//
//        user.getBilling().setAccountBalance(0.05);
//        user.getBilling().setBillingEnabled(true);
//        user.getBilling().setMaxDailyAllowance(1.50);

        double startingBalance = 5.00;

        entityService.addUpdateEntity(user, user);
        EntityName name = CommonFactoryLocator.getInstance().createName(Const.ACCOUNT_BALANCE, EntityType.point);
        List<Entity> list = entityService.getEntityByName(user,name, EntityType.point );
        assertFalse(list.isEmpty());
        Point accountBalance = (Point) list.get(0);
        accountBalance.setDeltaAlarm(0.02);
        accountBalance.setDeltaAlarmOn(true);

        entityService.addUpdateEntity(user, accountBalance);

        UserServiceFactory.getServerInstance().fundAccount(user, BigDecimal.valueOf(startingBalance));

        List<Value> sample = ValueServiceFactory.getInstance().getCurrentValue(accountBalance);
        assertFalse(sample.isEmpty());
        Value balance = sample.get(0);


        assertEquals(startingBalance, balance.getDoubleValue(), 0.0001);
        double nickle = 0.05;   //try to write a nickles worth on a 2 cent budget


        double calls = (nickle /  QuotaFactory.getInstance(emailAddress).getCostPerApiCall());
        for (int i = 0; i < QuotaFactory.getInstance(emailAddress).getMaxDailyQuota()+calls; i++) {
            valueServlet.processGet(req, resp);
        }






    }

    @Test
    public void businessAsUsual() throws NimbitsException, IOException {

        SettingsServiceFactory.getInstance().updateSetting(SettingType.quotaEnabled, Const.TRUE);

        user.setBillingEnabled(true);

//
//        user.getBilling().setAccountBalance(0.05);
//        user.getBilling().setBillingEnabled(true);
//        user.getBilling().setMaxDailyAllowance(1.50);

        double startingBalance = 5.00;

        entityService.addUpdateEntity(user, user);
        EntityName name = CommonFactoryLocator.getInstance().createName(Const.ACCOUNT_BALANCE, EntityType.point);
        List<Entity> list = entityService.getEntityByName(user,name, EntityType.point );
        assertFalse(list.isEmpty());
        Point accountBalance = (Point) list.get(0);
        accountBalance.setDeltaAlarm(1.50);
        accountBalance.setDeltaAlarmOn(true);

        entityService.addUpdateEntity(user, accountBalance);

        UserServiceFactory.getServerInstance().fundAccount(user, BigDecimal.valueOf(startingBalance));

        List<Value> sample = ValueServiceFactory.getInstance().getCurrentValue(accountBalance);
        assertFalse(sample.isEmpty());
        Value balance = sample.get(0);


        assertEquals(startingBalance, balance.getDoubleValue(), 0.0001);
        double penny = 0.01;


        double calls = (penny /  QuotaFactory.getInstance(emailAddress).getCostPerApiCall());
        for (int i = 0; i < QuotaFactory.getInstance(emailAddress).getMaxDailyQuota()+calls; i++) {
            valueServlet.processGet(req, resp);
        }

        User u = (User) entityService.getEntityByKey(user.getKey(), EntityType.user).get(0);
        // System.out.println(u.getBilling().getAccountBalance());
        List<Value> currentValueSample = ValueServiceFactory.getInstance().getCurrentValue(accountBalance);
        assertFalse(currentValueSample.isEmpty());
        Value currentValue = currentValueSample.get(0);
        Assert.assertEquals(startingBalance - penny,currentValue.getDoubleValue(), .001);



    }



}
