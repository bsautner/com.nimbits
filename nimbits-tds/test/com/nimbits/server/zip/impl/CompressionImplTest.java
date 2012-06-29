package com.nimbits.server.zip.impl;

import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.server.gson.GsonFactory;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.zip.GZIPOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: bsautner
 * Date: 6/27/12
 * Time: 2:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompressionImplTest {

    private  List<Value> loadSomeData() {
        List<Value> values = new ArrayList<Value>();
        Random r = new Random();

        for (int i = 0; i < 10; i++) {
            Value v = ValueFactory.createValueModel(r.nextDouble());
            values.add(v);
        }
        return values;
    }



    @Test
    public void testExtractBytes() throws Exception {

    }
}
