package com.nimbits.it.ha;

import com.nimbits.client.io.http.NimbitsClientException;
import com.nimbits.client.model.topic.Topic;
import com.nimbits.it.AbstractBaseNimbitsTest;
import org.junit.Test;

import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;

public class DuplicateNameTest extends AbstractBaseNimbitsTest {

    @Test(expected = NimbitsClientException.class)
    public void testAddingDuplicatesFails() {

        String name = UUID.randomUUID().toString();

        Topic topic = adminClient.addPoint(adminUser, new Topic.Builder().name(name).create());
        assertNotNull(topic.getId());
        String id = topic.getId();

        log("added a topic", topic);

         adminClient.addPoint(adminUser, new Topic.Builder().name(name).create());




    }
}
