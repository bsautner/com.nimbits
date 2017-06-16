package com.nimbits.it.crud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.topic.TopicType;
import com.nimbits.it.AbstractBaseNimbitsTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class UpdateEntityCrudSync extends AbstractBaseNimbitsTest {

    @Test
    public void testSyncUpdates() {


        Topic updated = adminClient.addPoint(adminUser, new Topic.Builder()
                .create());

        assertNotNull(updated.getId());

        //TODO urgent - test updates to events

//        assertFalse(updated.isHighAlarmOn());
//
//        updated.setHighAlarm(100.0);
//        updated.setHighAlarmOn(true);
//
//        Topic result = (Topic) adminClient.updateEntitySync(updated);
//        assertTrue(result.isHighAlarmOn());



    }


    @Test
    public void testPointType() throws JsonProcessingException {
        Topic topic = new Topic.Builder().pointType(TopicType.flag)
                .create();
        ObjectMapper objectMapper = new ObjectMapper();
        Topic updated = adminClient.addPoint(adminUser, topic);
        String json = objectMapper.writeValueAsString(topic);
        System.out.println(json);

        assertNotNull(updated.getId());
        assertEquals(TopicType.flag, updated.getTopicType());




    }

}
