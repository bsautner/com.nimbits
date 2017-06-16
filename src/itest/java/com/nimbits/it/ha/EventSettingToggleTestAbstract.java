package com.nimbits.it.ha;


import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.user.User;
import com.nimbits.it.AbstractNimbitsTest;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Change event settings.
 Short description: any changes on event settings in one of the instances don't get into another.

 Scenario:
 Create topic on one of the instances.
 Check that it is visible from another instance (either Web UI or REST)
 Change any event setting (high/low/idle value/enabled) either using WebUI or Java API  on topic from one of the instances.
 Read event settings from another instance (either Web UI or REST).
 [Expected]: Event settings expected to be the same.
 [Actual]: settings on another nimbits are not changed.
 */
public class EventSettingToggleTestAbstract extends AbstractNimbitsTest {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testChangeEvents() {

        int count = 10;
        List<Topic> topicList = new ArrayList<>(count);
        User me = nimbits.getMe();

        for (int c = 0; c < count; c++) {

            topicList.add(nimbits.addPoint(me,
                    new Topic.Builder().create()));


        }

        for (Topic topic : topicList) {

            nimbits.updateEntity(topic);
            nap();
            Topic update = nimbits.getPoint(topic.getId());
            //TODO URGENT - add and test events
        }





    }

}
