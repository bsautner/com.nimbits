package client.model.value;

import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.model.location.LocationFactory;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.server.gson.GsonFactory;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/28/12
 * Time: 5:03 PM
 */
public class ValueModelFactoryTest {

    @Test
    public void testHandlingInfinity() {
        Value v = ValueFactory.createValueModel(LocationFactory.createLocation(), 0.0, new Date(), "", ValueFactory.createValueData(""), AlertType.OK);
        // 0000&p2=IN2&v2=0000&p3=IN3&v3=0000&p4=IN4&v4=0088&p5=IN5&v5=0328&p6=IN6&v6=0070&p7=INP1&v7=000&p8=INP2&v8=0&p9=Temp&v9=23.3&p10=Bat&v10=0.3&p11=Frq&v11=00.0&p12=Phi&v12=00.0" "AppEngine-Google; (+http://code.google.com/appengine)" "cloud.nimbits.com" ms=191 cpu_ms=287 api_cpu_ms=217 cpm_usd=0.008349 queue_name=recordvaluequeue task_name=10957361277155433609 instance=00c61b117cb1364c5c6603c8348714ca19165c
        String j = GsonFactory.getInstance().toJson(v);
        System.out.println(j);

        assertFalse(Double.valueOf("0000").isInfinite());
        assertFalse(Double.valueOf("0088").isInfinite());
        assertFalse(Double.valueOf("000").isInfinite());
        assertFalse(Double.valueOf("00.0").isInfinite());
    }
    @Test
    public void testStringToValue1() {
        String sample = "3.12 hello world";
        Value value = ValueFactory.createValueModel(sample, new Date());
        assertEquals(3.12,value.getDoubleValue(), 0.0);
        assertEquals("hello world",value.getNote());
    }
    @Test
    public void testStringToValue2() {
        String sample = "3.12";
        Value value = ValueFactory.createValueModel(sample, new Date());
        assertEquals(3.12,value.getDoubleValue(), 0.0);
        assertEquals("",value.getNote());

    }

    @Test
    public void testStringToValue3() {
        String sample = "hello world";
        Value value = ValueFactory.createValueModel(sample, new Date());
        assertEquals(Const.CONST_IGNORED_NUMBER_VALUE,value.getDoubleValue(), 0.0);
        assertEquals("hello world",value.getNote());
    }
    @Test
    public void testStringToValue4() {
        String sample = "helloWorld";
        Value value = ValueFactory.createValueModel(sample, new Date());
        assertEquals(Const.CONST_IGNORED_NUMBER_VALUE,value.getDoubleValue(), 0.0);
        assertEquals("helloWorld",value.getNote());
    }
    @Test
    public void testStringToValue6() {
        String sample = ".12 ";
        Value value = ValueFactory.createValueModel(sample, new Date());
        assertEquals(0.12,value.getDoubleValue(), 0.0);
        assertEquals("",value.getNote());

    }
    @Test
    public void testStringToValue8() {
        String sample = "-.12 ";
        Value value = ValueFactory.createValueModel(sample, new Date());
        assertEquals(-0.12,value.getDoubleValue(), 0.0);
        assertEquals("",value.getNote());

    }
    @Test
    public void testStringToValue7() {
        String sample = "0";
        Value value = ValueFactory.createValueModel(sample, new Date());
        assertEquals(0,value.getDoubleValue(), 0.0);
        assertEquals("",value.getNote());

    }
}
