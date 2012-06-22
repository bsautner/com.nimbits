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
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.transactions.service.entity.EntityServiceFactory;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transactions.service.value.ValueServiceFactory;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/11/12
 * Time: 11:11 AM
 */
public class RecordValueTaskTest extends NimbitsServletTest {
    private void addAuth() {
        String userJson = GsonFactory.getInstance().toJson(user);
        req.addParameter(Parameters.pointUser.getText(), userJson);
        String keyJson = GsonFactory.getInstance().toJson(user.getAccessKeys());
        req.addParameter(Parameters.key.getText(), keyJson);
    }
    @Test
    public void testPostWithCalcs() throws NimbitsException {
        RecordValueTask task = new RecordValueTask();
        addAuth();
        Entity e = EntityModelFactory.createEntity(CommonFactoryLocator.getInstance().createName("calc1", EntityType.point),
                "", EntityType.calculation, ProtectionLevel.onlyMe, point.getKey(), user.getKey());

        Calculation c = CalculationModelFactory.createCalculation(e, point.getKey(), true, "x+1", pointChild.getKey(), point.getKey(),
                null, null);
        EntityServiceFactory.getInstance().addUpdateEntity(c);
        Value v = ValueFactory.createValueModel(1.12);
        ValueServiceFactory.getInstance().recordValue(point,v);
        List<Value> vr = ValueServiceFactory.getInstance().getCurrentValue(point);
        assertFalse(vr.isEmpty());
        assertEquals(v.getDoubleValue(), vr.get(0).getDoubleValue(), 0.001);
        String vj = GsonFactory.getInstance().toJson(vr.get(0));
        req.addParameter(Parameters.valueJson.getText(), vj);

        task.doPost(req, resp);

        List<Value> vx = ValueServiceFactory.getInstance().getCurrentValue(pointChild);
        assertEquals(vx.get(0).getDoubleValue(), v.getDoubleValue() + 1, 0.001);



    }
}
