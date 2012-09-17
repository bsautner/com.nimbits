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

package com.nimbits.server.transactions.service.value;

import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.enums.SettingType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.service.value.ValueService;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.settings.SettingsServiceFactory;


import junit.framework.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 3/30/12
 * Time: 8:51 AM
 */
public class RecordedValueServiceImplTest extends NimbitsServletTest {


    private static final double D = 1.23;
    private static final double D1 = 2.23;
    private static final int D2 = 11;
    private static final double VALUE = 0.1;


    @Resource(name = "valueService")
    ValueService valueService;

    @Test
    public void ignoreByCompressionTest() throws NimbitsException, InterruptedException {
        point.setFilterValue(VALUE);
        entityService.addUpdateEntity(point);


        Value value = ValueFactory.createValueModel(D);
        Value value2 = ValueFactory.createValueModel(D);
        Value value3 = ValueFactory.createValueModel(D1);

        valueService.recordValue(user, point, value);


        assertTrue(valueService.ignoreByFilter(point, value2));
        assertFalse(valueService.ignoreByFilter(point, value3));

        point.setFilterValue(10);
        point.setFilterType(FilterType.ceiling);

        assertFalse(valueService.ignoreByFilter(point, ValueFactory.createValueModel(D1)));
        assertTrue(valueService.ignoreByFilter(point, ValueFactory.createValueModel(D2)));

        point.setFilterType(FilterType.floor);
        assertTrue(valueService.ignoreByFilter(point, ValueFactory.createValueModel(D1)));
        assertFalse(valueService.ignoreByFilter(point, ValueFactory.createValueModel(D2)));

        point.setFilterType(FilterType.none);
        assertFalse(valueService.ignoreByFilter(point, ValueFactory.createValueModel(D2)));

        point.setFilterType(FilterType.percentageHysteresis);
        entityService.addUpdateEntity(point);
      //  pointService.updatePoint(point);
        valueService.recordValue(user, point, ValueFactory.createValueModel(100));
        Thread.sleep(10);
        assertTrue(valueService.ignoreByFilter(point, ValueFactory.createValueModel(105)));
        assertTrue(valueService.ignoreByFilter(point, ValueFactory.createValueModel(95)));
        assertFalse(valueService.ignoreByFilter(point, ValueFactory.createValueModel(111)));
        assertFalse(valueService.ignoreByFilter(point, ValueFactory.createValueModel(80)));

    }

    @Test
    public void testDelta() throws NimbitsException {

        SettingsServiceFactory.getInstance().updateSetting(SettingType.billingEnabled, Const.TRUE);

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
        accountBalance.setDeltaAlarm(0.01);
        accountBalance.setDeltaAlarmOn(true);

        entityService.addUpdateEntity(user, accountBalance);

        userService.fundAccount(user, BigDecimal.valueOf(startingBalance));

        double b = valueService.calculateDelta(accountBalance);
        Assert.assertEquals(b, 0.0, 0.0001);
        Value v = ValueFactory.createValueModel(startingBalance - 0.01);

        valueService.recordValue(user, accountBalance, v);

        List<Value> vx = valueService.getTopDataSeries(accountBalance, 10);


        double b2 = valueService.calculateDelta(accountBalance);
        Assert.assertEquals(0.01, b2, 0.0001);
//        for (int i = 0; i < 10; i++) {
//            List<Value> sample = valueService.getCurrentValue(accountBalance);
//            assertFalse(sample.isEmpty());
//            Value balance = sample.get(0);
//            assertEquals(startingBalance, balance.getDoubleValue(), 0.0001);
//
//        }




    }
}
