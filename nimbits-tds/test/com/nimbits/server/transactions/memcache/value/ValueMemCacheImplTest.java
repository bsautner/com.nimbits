package com.nimbits.server.transactions.memcache.value;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModelFactory;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.transactions.service.value.ValueTransactionFactory;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/5/12
 * Time: 1:49 PM
 */
public class ValueMemCacheImplTest extends NimbitsServletTest {

    private static final double D = 1.23;
    private static final double DELTA = 0.001;

    @Test
    public void testGetPrevValue() throws NimbitsException, InterruptedException {
        ValueMemCacheImpl impl = new ValueMemCacheImpl(point);
        Value v = ValueModelFactory.createValueModel(D);
        ValueTransactionFactory.getInstance(point).recordValue(v);
        Thread.sleep(1000);
        Value vr = ValueTransactionFactory.getInstance(point).getRecordedValuePrecedingTimestamp(new Date());
        Value dv = ValueTransactionFactory.getDaoInstance(point).getRecordedValuePrecedingTimestamp(new Date());
        assertNull(dv);

        assertNotNull(vr);
        assertEquals(v.getDoubleValue(), vr.getDoubleValue(), DELTA);
        List<Value> vz = impl.getBuffer();
        assertEquals(vz.size(), 1);
        impl.moveValuesFromCacheToStore();
        vz = impl.getBuffer();
        assertEquals(vz.size(), 0);
        dv = ValueTransactionFactory.getDaoInstance(point).getRecordedValuePrecedingTimestamp(new Date());
        assertEquals(v.getDoubleValue(), dv.getDoubleValue(), DELTA);

    }

}
