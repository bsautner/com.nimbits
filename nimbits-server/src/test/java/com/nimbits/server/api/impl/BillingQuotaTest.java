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

import com.nimbits.server.NimbitsServletTest;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


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
        "classpath:META-INF/applicationContext-task.xml",
        "classpath:META-INF/applicationContext-factory.xml"

})
public class BillingQuotaTest  extends NimbitsServletTest {

//    @Resource(name = "value")
//    ValueServletImpl valueServlet;
//
//
//
//    @Test
//    @Ignore
//    public void outOfMoneyTest()
//
////        SettingsServiceImpl.updateSetting(SettingType.billingEnabled, Const.TRUE);
////        user.setBillingEnabled(true);
////        EntityServiceImpl.addUpdateSingleEntity(user, user);
////
////        List<Entity> sample = EntityServiceImpl.getEntityByKey(user, user.getKey(), EntityType.user);
////        assertFalse(sample.isEmpty());
////        User u = (User) sample.get(0);
////        assertTrue(u.isBillingEnabled());
////
////        EntityName name = CommonFactory.createName(Const.ACCOUNT_BALANCE, EntityType.point);
////        List<Entity> list = EntityServiceImpl.getEntityByName(user,name, EntityType.point );
////        assertFalse(list.isEmpty());
////        Point accountBalance = (Point) list.get(0);
////        accountBalance.setDeltaAlarm(1.50);
////        accountBalance.setDeltaAlarmOn(true);
////        EntityServiceImpl.addUpdateSingleEntity(user, accountBalance);
////        UserTransactionImpl.fundAccount(user, BigDecimal.valueOf(0.01));
////        req.setMethod("GET");
////        double calls = (0.02 /  QuotaManager.getCostPerApiCall());
////        double paid =   (0.01 /  QuotaManager.getCostPerApiCall());
////        for (int i = 0; i < QuotaManager.getFreeDailyQuota()+calls; i++) {
////            valueServlet.doGet(req, resp);
////            if (i <= QuotaManager.getFreeDailyQuota() + paid) {
////                System.out.println(resp.getHeader("ERROR"));
////                assertEquals(resp.getStatus(),  HttpServletResponse.SC_OK);
////                System.out.println(i);
////            }
////            else {
////                assertEquals(resp.getStatus(),  HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
////            }
////        }
////       // User u = (User) EntityServiceImpl.getEntityByKey(UserServiceImpl.getAnonUser(), user.getKey(), EntityType.user).get(0);
////       // System.out.println(u.getBilling().getAccountBalance());
////        List<Value> currentValueSample = ValueTransaction.getCurrentValue(accountBalance);
////        assertFalse(currentValueSample.isEmpty());
////        Value currentValue = currentValueSample.get(0);
////        Assert.assertEquals(0.0,currentValue.getDoubleValue(), .001);
//
//    }
//
//    @Test
//    @Ignore
//    public void overDailyBudgetTest() , IOException {
//
//        SettingsServiceImpl.updateSetting(SettingType.billingEnabled, Const.TRUE);
//
//        user.setBillingEnabled(true);
//
//        double startingBalance = 5.00;
//
//        EntityServiceImpl.addUpdateSingleEntity(user, user);
//        EntityName name = CommonFactory.createName(Const.ACCOUNT_BALANCE, EntityType.point);
//        List<Entity> list = EntityServiceImpl.getEntityByName(user,name, EntityType.point );
//        assertFalse(list.isEmpty());
//        Point accountBalance = (Point) list.get(0);
//        accountBalance.setDeltaAlarm(0.01);
//        accountBalance.setDeltaAlarmOn(true);
//
//        EntityServiceImpl.addUpdateSingleEntity(user, accountBalance);
//
//        UserTransactionImpl.fundAccount(user, BigDecimal.valueOf(startingBalance));
//
//        List<Value> sample = ValueTransaction.getCurrentValue(accountBalance);
//        assertFalse(sample.isEmpty());
//        Value balance = sample.get(0);
//
//
//        assertEquals(startingBalance, balance.getDoubleValue(), 0.0001);
//        double nickle = 0.05;   //try to write a nickles worth on a 2 cent budget
//
//        req.setMethod("GET");
//        double calls = (nickle /  QuotaManager.getCostPerApiCall());
//        double quotaCalled = (accountBalance.getDeltaAlarm() / QuotaManager.getCostPerApiCall());
//        for (int i = 0; i < QuotaManager.getFreeDailyQuota()+calls; i++) {
//            valueServlet.doGet(req, resp);
//            if (i <= QuotaManager.getFreeDailyQuota() + quotaCalled) {
//                assertEquals(resp.getStatus(),  HttpServletResponse.SC_OK);
//
//            }
//            else {
//                assertEquals(resp.getStatus(),  HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            }
//        }
//
//
//
//
//
//
//    }
//
//    @Test
//    public void fundAccountTest() , IOException {
//
//        SettingsServiceImpl.updateSetting(SettingType.billingEnabled, Const.TRUE);
//
//        user.setBillingEnabled(true);
//
////
////        user.getBilling().setAccountBalance(0.05);
////        user.getBilling().setBillingEnabled(true);
////        user.getBilling().setMaxDailyAllowance(1.50);
//
//        double startingBalance = 5.00;
//
//        EntityServiceImpl.addUpdateSingleEntity(user, user);
//        EntityName name = CommonFactory.createName(Const.ACCOUNT_BALANCE, EntityType.point);
//        List<Entity> list = EntityServiceImpl.getEntityByName(user,name, EntityType.point );
//        assertFalse(list.isEmpty());
//        Point accountBalance = (Point) list.get(0);
//        accountBalance.setDeltaAlarm(0.01);
//        accountBalance.setDeltaAlarmOn(true);
//
//        EntityServiceImpl.addUpdateSingleEntity(user, accountBalance);
//
//       UserTransactionImpl.fundAccount(user, BigDecimal.valueOf(startingBalance));
//
//
//
//        for (int i = 0; i < 10; i++) {
//            List<Value> sample = ValueTransaction.getCurrentValue(accountBalance);
//            assertFalse(sample.isEmpty());
//            Value balance = sample.get(0);
//            assertEquals(startingBalance, balance.getDoubleValue(), 0.0001);
//
//        }
//
//
//
//
//
//    }
//
//
//
//
//    @Test
//    public void businessAsUsual() , IOException {
//
//       SettingsServiceImpl.updateSetting(SettingType.billingEnabled, Const.TRUE);
//
//        user.setBillingEnabled(true);
//
////
////        user.getBilling().setAccountBalance(0.05);
////        user.getBilling().setBillingEnabled(true);
////        user.getBilling().setMaxDailyAllowance(1.50);
//
//        double startingBalance = 5.00;
//
//        EntityServiceImpl.addUpdateSingleEntity(user, user);
//        EntityName name = CommonFactory.createName(Const.ACCOUNT_BALANCE, EntityType.point);
//        List<Entity> list = EntityServiceImpl.getEntityByName(user,name, EntityType.point );
//        assertFalse(list.isEmpty());
//        Point accountBalance = (Point) list.get(0);
//        accountBalance.setDeltaAlarm(1.50);
//        accountBalance.setDeltaAlarmOn(true);
//
//        EntityServiceImpl.addUpdateSingleEntity(user, accountBalance);
//
//        UserTransactionImpl.fundAccount(user, BigDecimal.valueOf(startingBalance));
//
//        List<Value> sample = ValueTransaction.getCurrentValue(accountBalance);
//        assertFalse(sample.isEmpty());
//        Value balance = sample.get(0);
//
//
//        assertEquals(startingBalance, balance.getDoubleValue(), 0.0001);
//        double penny = 0.01;
//
//
//        double calls = (penny /  QuotaManager.getCostPerApiCall());
//        for (int i = 0; i < QuotaManager.getFreeDailyQuota()+calls; i++) {
//            valueServlet.doGet(req, resp);
//        }
//
//        User u = (User) EntityServiceImpl.getEntityByKey(user, user.getKey(), EntityType.user).get(0);
//        // System.out.println(u.getBilling().getAccountBalance());
//        List<Value> currentValueSample = ValueTransaction.getCurrentValue(accountBalance);
//        assertFalse(currentValueSample.isEmpty());
//        Value currentValue = currentValueSample.get(0);
//        Assert.assertEquals(startingBalance - penny,currentValue.getDoubleValue(), .001);
//
//
//
//    }



}
