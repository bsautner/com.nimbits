package com.nimbits.server.transactions.memcache.value;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.value.*;
import helper.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.*;

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
        RecordedValueTransactionFactory.getInstance(point).recordValue(v);
        Thread.sleep(1000);
        Value vr = RecordedValueTransactionFactory.getInstance(point).getRecordedValuePrecedingTimestamp(new Date());
        Value dv = RecordedValueTransactionFactory.getDaoInstance(point).getRecordedValuePrecedingTimestamp(new Date());
        assertNull(dv);

        assertNotNull(vr);
        assertEquals(v.getDoubleValue(), vr.getDoubleValue(), DELTA);
        List<Value> vz = impl.getBuffer();
        assertEquals(vz.size(), 1);
        impl.moveValuesFromCacheToStore();
        vz = impl.getBuffer();
        assertEquals(vz.size(), 0);
        dv = RecordedValueTransactionFactory.getDaoInstance(point).getRecordedValuePrecedingTimestamp(new Date());
        assertEquals(v.getDoubleValue(), dv.getDoubleValue(), DELTA);

    }

}
