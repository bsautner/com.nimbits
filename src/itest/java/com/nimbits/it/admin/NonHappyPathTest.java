package com.nimbits.it.admin;


import java.util.Optional;
import com.nimbits.client.io.Nimbits;
import com.nimbits.client.io.http.NimbitsClientException;
import com.nimbits.client.model.*;
import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.it.AbstractBaseNimbitsTest;
import org.junit.Test;

import java.util.UUID;

import static junit.framework.TestCase.assertFalse;

public class NonHappyPathTest extends AbstractBaseNimbitsTest {


    @Test
    public void testNotFound() {

        Optional<Topic> a = adminClient.findTopicByName(UUID.randomUUID().toString());
        Optional<WebHook> b = adminClient.findWebHook(UUID.randomUUID().toString());
        Optional<Group> c = adminClient.findCategory(UUID.randomUUID().toString());

        Optional<Calculation> e = adminClient.findCalculation(UUID.randomUUID().toString());
        Optional<Summary> f = adminClient.findSummary(UUID.randomUUID().toString());
        Optional<Sync> g = adminClient.findSync(UUID.randomUUID().toString());



        assertFalse(a.isPresent());
        assertFalse(b.isPresent());
        assertFalse(c.isPresent());
        assertFalse(e.isPresent());
        assertFalse(f.isPresent());
        assertFalse(g.isPresent());


    }

    @Test(expected = NimbitsClientException.class)
    public void testServerDown() {
        Nimbits bad = new Nimbits.Builder().email(admin).token(password).instance("http://foo.bar").create();
        Optional<Topic> optional = bad.findTopicByName(UUID.randomUUID().toString());
        assertFalse(optional.isPresent());

    }




}
