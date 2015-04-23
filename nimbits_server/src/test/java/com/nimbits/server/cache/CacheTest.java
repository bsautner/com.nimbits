package com.nimbits.server.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CacheTest {

    @Test
    public void testAdd() {

        Cache<String, List<Value>> cache = CacheBuilder.newBuilder().build();
        cache.put("FOO", new ArrayList<Value>());
        cache.getIfPresent("FOO").add(ValueFactory.createValueModel(42.0));
        assertEquals(1, cache.getIfPresent("FOO").size());


    }


}
