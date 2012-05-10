package com.nimbits.server.orm;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.server.NimbitsServletTest;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 5/10/12
 * Time: 11:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class PointEntityTest extends NimbitsServletTest {

    @Test
    public void testValidation() throws NimbitsException {
        PointEntity e = new PointEntity(point);
        e.setIdleAlarmOn(true);
        e.validate();

    }
    @Test(expected=NimbitsException.class)
    public void testValidation2() throws NimbitsException {
        PointEntity e = new PointEntity(point);
        e.setIdleAlarmOn(true);
        e.setHighAlarmOn(true);
        e.setLowAlarmOn(true);
        e.setHighAlarm(0.0);
        e.setLowAlarm(100);
        e.validate();

    }
    @Test
    public void testValidation3() throws NimbitsException {
        PointEntity e = new PointEntity(point);
        e.setIdleAlarmOn(true);
        e.setHighAlarmOn(false);
        e.setLowAlarmOn(true);
        e.setHighAlarm(0.0);
        e.setLowAlarm(100);
        e.validate();

    }

}
