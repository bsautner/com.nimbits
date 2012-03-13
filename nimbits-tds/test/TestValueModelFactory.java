import com.nimbits.client.model.*;
import com.nimbits.client.model.value.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/13/12
 * Time: 12:31 PM
 */
public class TestValueModelFactory {

    @Test
    public void testStringToValue1() {
        String sample = "3.12 hello world";
        Value value = ValueModelFactory.createValueModel(sample, new Date(), null);
        assertEquals(3.12,value.getNumberValue(), 0.0);
        assertEquals("hello world",value.getNote());
    }
    @Test
    public void testStringToValue2() {
        String sample = "3.12";
        Value value = ValueModelFactory.createValueModel(sample, new Date(), null);
        assertEquals(3.12,value.getNumberValue(), 0.0);
        assertEquals("",value.getNote());

    }

    @Test
    public void testStringToValue3() {
        String sample = "hello world";
        Value value = ValueModelFactory.createValueModel(sample, new Date(), null);
        assertEquals(Const.CONST_IGNORED_NUMBER_VALUE,value.getNumberValue(), 0.0);
        assertEquals("hello world",value.getNote());
    }
    @Test
    public void testStringToValue4() {
        String sample = "helloWorld";
        Value value = ValueModelFactory.createValueModel(sample, new Date(), null);
        assertEquals(Const.CONST_IGNORED_NUMBER_VALUE,value.getNumberValue(), 0.0);
        assertEquals("helloWorld",value.getNote());
    }
    @Test
    public void testStringToValue6() {
        String sample = ".12 ";
        Value value = ValueModelFactory.createValueModel(sample, new Date(), null);
        assertEquals(0.12,value.getNumberValue(), 0.0);
        assertEquals("",value.getNote());

    }
    @Test
    public void testStringToValue8() {
        String sample = "-.12 ";
        Value value = ValueModelFactory.createValueModel(sample, new Date(), null);
        assertEquals(-0.12,value.getNumberValue(), 0.0);
        assertEquals("",value.getNote());

    }
    @Test
    public void testStringToValue7() {
        String sample = "0";
        Value value = ValueModelFactory.createValueModel(sample, new Date(), null);
        assertEquals(0,value.getNumberValue(), 0.0);
        assertEquals("",value.getNote());

    }
}
