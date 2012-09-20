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

package com.nimbits.server.process.task;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.CalculationModelFactory;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.value.ValueService;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.gson.GsonFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/11/12
 * Time: 11:11 AM
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
public class valueTaskTest extends NimbitsServletTest {
    
    @Resource(name="valueService")
    ValueService valueService;
   
    @Resource(name="entityService")
    EntityService entityService;

    @Resource(name="valueTask")
    ValueTask valueTask;

    private void addAuth() {
        String userJson = GsonFactory.getInstance().toJson(user);
        req.addParameter(Parameters.pointUser.getText(), userJson);
        String keyJson = GsonFactory.getInstance().toJson(user.getAccessKeys());
        req.addParameter(Parameters.key.getText(), keyJson);
    }
    @Test
    public void testPostWithCalcs() throws NimbitsException {

        addAuth();
        Entity e = EntityModelFactory.createEntity(CommonFactoryLocator.getInstance().createName("calc1", EntityType.point),
                "", EntityType.calculation, ProtectionLevel.onlyMe, point.getKey(), user.getKey());

        Calculation c = CalculationModelFactory.createCalculation(e, point.getKey(), true, "x+1", pointChild.getKey(), point.getKey(),
                null, null);
        entityService.addUpdateEntity(c);
        Value v = ValueFactory.createValueModel(1.12);
        valueService.recordValue(point,v);
        List<Value> vr = valueService.getCurrentValue(point);
        assertFalse(vr.isEmpty());
        assertEquals(v.getDoubleValue(), vr.get(0).getDoubleValue(), 0.001);
        String vj = GsonFactory.getInstance().toJson(vr.get(0));
        req.addParameter(Parameters.valueJson.getText(), vj);

        valueTask.handleRequest(req, resp);

        List<Value> vx =valueService.getCurrentValue(pointChild);
        assertEquals(vx.get(0).getDoubleValue(), v.getDoubleValue() + 1, 0.001);



    }
}
