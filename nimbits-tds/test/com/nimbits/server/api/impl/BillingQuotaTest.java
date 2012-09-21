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
import com.nimbits.client.model.common.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.service.settings.SettingsService;
import com.nimbits.client.service.value.ValueService;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.admin.quota.QuotaManager;
import com.nimbits.server.transactions.service.user.UserServerService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 8/14/12
 * Time: 10:56 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml",
        "classpath:META-INF/applicationContext-api.xml",
        "classpath:META-INF/applicationContext-cache.xml",
        "classpath:META-INF/applicationContext-cron.xml",
        "classpath:META-INF/applicationContext-dao.xml",
        "classpath:META-INF/applicationContext-service.xml",
        "classpath:META-INF/applicationContext-task.xml"

})
public class BillingQuotaTest  extends NimbitsServletTest {

    @Resource(name = "valueApi")
    ValueServletImpl valueServlet;

    @Resource(name = "commonFactory")
    CommonFactory commonFactory;

    @Resource(name="valueService")
    ValueService valueService;

    @Resource(name="settingsService")
    SettingsService settingsService;

    @Resource(name="userService")
    UserServerService userService;

    @Resource(name="quotaManager")
    QuotaManager quotaManager;


    @Test
    public void outOfMoneyTest() throws NimbitsException, IOException {


        settingsService.updateSetting(SettingType.billingEnabled, Const.TRUE);
        user.setBillingEnabled(true);
        entityService.addUpdateEntity(user, user);

        List<Entity> sample = entityService.getEntityByKey(user, user.getKey(), EntityType.user);
        assertFalse(sample.isEmpty());
        User u = (User) sample.get(0);
        assertTrue(u.isBillingEnabled());

        EntityName name = commonFactory.createName(Const.ACCOUNT_BALANCE, EntityType.point);
        List<Entity> list = entityService.getEntityByName(user,name, EntityType.point );
        assertFalse(list.isEmpty());
        Point accountBalance = (Point) list.get(0);
        accountBalance.setDeltaAlarm(1.50);
        accountBalance.setDeltaAlarmOn(true);
        entityService.addUpdateEntity(user, accountBalance);
        userService.fundAccount(user, BigDecimal.valueOf(0.01));
        req.setMethod("GET");
        double calls = (0.02 /  quotaManager.getCostPerApiCall());
        double paid =   (0.01 /  quotaManager.getCostPerApiCall());
        for (int i = 0; i < quotaManager.getFreeDailyQuota()+calls; i++) {
            valueServlet.doGet(req, resp);
            if (i <= quotaManager.getFreeDailyQuota() + paid) {
                System.out.println(resp.getHeader("ERROR"));
                assertEquals(resp.getStatus(),  HttpServletResponse.SC_OK);
                System.out.println(i);
            }
            else {
                assertEquals(resp.getStatus(),  HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
       // User u = (User) entityService.getEntityByKey(userService.getAnonUser(), user.getKey(), EntityType.user).get(0);
       // System.out.println(u.getBilling().getAccountBalance());
        List<Value> currentValueSample = valueService.getCurrentValue(accountBalance);
        assertFalse(currentValueSample.isEmpty());
        Value currentValue = currentValueSample.get(0);
        Assert.assertEquals(0.0,currentValue.getDoubleValue(), .001);

    }

    @Test
    public void overDailyBudgetTest() throws NimbitsException, IOException {

        settingsService.updateSetting(SettingType.billingEnabled, Const.TRUE);

        user.setBillingEnabled(true);

        double startingBalance = 5.00;

        entityService.addUpdateEntity(user, user);
        EntityName name =commonFactory.createName(Const.ACCOUNT_BALANCE, EntityType.point);
        List<Entity> list = entityService.getEntityByName(user,name, EntityType.point );
        assertFalse(list.isEmpty());
        Point accountBalance = (Point) list.get(0);
        accountBalance.setDeltaAlarm(0.01);
        accountBalance.setDeltaAlarmOn(true);

        entityService.addUpdateEntity(user, accountBalance);

        userService.fundAccount(user, BigDecimal.valueOf(startingBalance));

        List<Value> sample = valueService.getCurrentValue(accountBalance);
        assertFalse(sample.isEmpty());
        Value balance = sample.get(0);


        assertEquals(startingBalance, balance.getDoubleValue(), 0.0001);
        double nickle = 0.05;   //try to write a nickles worth on a 2 cent budget

        req.setMethod("GET");
        double calls = (nickle /  quotaManager.getCostPerApiCall());
        double quotaCalled = (accountBalance.getDeltaAlarm() / quotaManager.getCostPerApiCall());
        for (int i = 0; i < quotaManager.getFreeDailyQuota()+calls; i++) {
            valueServlet.doGet(req, resp);
            if (i <= quotaManager.getFreeDailyQuota() + quotaCalled) {
                assertEquals(resp.getStatus(),  HttpServletResponse.SC_OK);

            }
            else {
                assertEquals(resp.getStatus(),  HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }






    }

    @Test
    public void fundAccountTest() throws NimbitsException, IOException {

        settingsService.updateSetting(SettingType.billingEnabled, Const.TRUE);

        user.setBillingEnabled(true);

//
//        user.getBilling().setAccountBalance(0.05);
//        user.getBilling().setBillingEnabled(true);
//        user.getBilling().setMaxDailyAllowance(1.50);

        double startingBalance = 5.00;

        entityService.addUpdateEntity(user, user);
        EntityName name = commonFactory.createName(Const.ACCOUNT_BALANCE, EntityType.point);
        List<Entity> list = entityService.getEntityByName(user,name, EntityType.point );
        assertFalse(list.isEmpty());
        Point accountBalance = (Point) list.get(0);
        accountBalance.setDeltaAlarm(0.01);
        accountBalance.setDeltaAlarmOn(true);

        entityService.addUpdateEntity(user, accountBalance);

       userService.fundAccount(user, BigDecimal.valueOf(startingBalance));



        for (int i = 0; i < 10; i++) {
            List<Value> sample = valueService.getCurrentValue(accountBalance);
            assertFalse(sample.isEmpty());
            Value balance = sample.get(0);
            assertEquals(startingBalance, balance.getDoubleValue(), 0.0001);

        }





    }




    @Test
    public void businessAsUsual() throws NimbitsException, IOException {

       settingsService.updateSetting(SettingType.billingEnabled, Const.TRUE);

        user.setBillingEnabled(true);

//
//        user.getBilling().setAccountBalance(0.05);
//        user.getBilling().setBillingEnabled(true);
//        user.getBilling().setMaxDailyAllowance(1.50);

        double startingBalance = 5.00;

        entityService.addUpdateEntity(user, user);
        EntityName name = commonFactory.createName(Const.ACCOUNT_BALANCE, EntityType.point);
        List<Entity> list = entityService.getEntityByName(user,name, EntityType.point );
        assertFalse(list.isEmpty());
        Point accountBalance = (Point) list.get(0);
        accountBalance.setDeltaAlarm(1.50);
        accountBalance.setDeltaAlarmOn(true);

        entityService.addUpdateEntity(user, accountBalance);

        userService.fundAccount(user, BigDecimal.valueOf(startingBalance));

        List<Value> sample = valueService.getCurrentValue(accountBalance);
        assertFalse(sample.isEmpty());
        Value balance = sample.get(0);


        assertEquals(startingBalance, balance.getDoubleValue(), 0.0001);
        double penny = 0.01;


        double calls = (penny /  quotaManager.getCostPerApiCall());
        for (int i = 0; i < quotaManager.getFreeDailyQuota()+calls; i++) {
            valueServlet.doGet(req, resp);
        }

        User u = (User) entityService.getEntityByKey(user, user.getKey(), EntityType.user).get(0);
        // System.out.println(u.getBilling().getAccountBalance());
        List<Value> currentValueSample =valueService.getCurrentValue(accountBalance);
        assertFalse(currentValueSample.isEmpty());
        Value currentValue = currentValueSample.get(0);
        Assert.assertEquals(startingBalance - penny,currentValue.getDoubleValue(), .001);



    }



}
