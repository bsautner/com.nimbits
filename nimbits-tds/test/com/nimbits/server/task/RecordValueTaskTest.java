package com.nimbits.server.task;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.calculation.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.calculation.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.value.*;
import helper.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/11/12
 * Time: 11:11 AM
 */
public class RecordValueTaskTest extends NimbitsServletTest {

    @Test
    public void testPostWithCalcs() throws NimbitsException {
        RecordValueTask task = new RecordValueTask();

        Entity e = EntityModelFactory.createEntity(CommonFactoryLocator.getInstance().createName("calc1"),
                "", EntityType.calculation, ProtectionLevel.onlyMe, point.getKey(), user.getKey());

        Calculation c = CalculationModelFactory.createCalculation(e, point.getKey(), true, "x+1", pointChild.getKey(), point.getKey(),
                null, null);
        EntityServiceFactory.getInstance().addUpdateEntity(c);
        Value v = ValueModelFactory.createValueModel(1.12);
        RecordedValueServiceFactory.getInstance().recordValue(point,v);
        Value vr = RecordedValueServiceFactory.getInstance().getCurrentValue(point);
        assertEquals(v.getDoubleValue(), vr.getDoubleValue(), 0.001);
        String vj = GsonFactory.getInstance().toJson(vr);
        req.addParameter(Parameters.valueJson.getText(), vj);

        task.doPost(req, resp);

        Value vx = RecordedValueServiceFactory.getInstance().getCurrentValue(pointChild);
        assertEquals(vx.getDoubleValue(), v.getDoubleValue() + 1, 0.001);



    }
}
