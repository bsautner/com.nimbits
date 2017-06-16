package com.nimbits.it.ha;


import java.util.Optional;
import com.nimbits.client.io.Nimbits;
import com.nimbits.client.model.topic.Topic;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.webhook.DataChannel;
import com.nimbits.client.model.webhook.HttpMethod;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.it.AbstractBaseNimbitsTest;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * proves that different users with entities of the same name only get their entity
 *
 */

public class UsersWithSameNameEntityTest extends AbstractBaseNimbitsTest {



    List<Pair<String, String>> emails = new ArrayList<>();
    List<User> users = new ArrayList<>();

    List<Pair<User, Nimbits>> clients = new ArrayList<>();


    @After
    public void tearDown() throws Exception {

        super.tearDown();




    }

    @Test
    public void testScenario() {
        int count = 2;
        String commonPointName = UUID.randomUUID().toString();
        String commonHost = "http://test.com";

        for (int i = 0; i < count; i++) {
            emails.add(Pair.of(
                    String.format("%s@nimbits.com",
                            UUID.randomUUID().toString()),
                    UUID.randomUUID().toString()));

        }

        for (Pair<String, String> pair : emails) {
            users.add(
                    adminClient.addUser(new User.Builder().email(pair.getLeft()).password(pair.getRight()).create() )
            );

        }

        assertEquals(count, users.size());

        for (Pair<String, String> pair : emails) {

            Nimbits userClient = new Nimbits.Builder()
                    .email(pair.getLeft())
                    .instance(host)
                    .token(pair.getRight()).create();

            User user = userClient.getMe();

            assertEquals(pair.getLeft(), user.getEmail());
            Topic sameNameTopic = userClient.addPoint(user, new Topic.Builder()
                    .name(commonPointName).create());

            Topic uniqueTopic = userClient.addPoint(user, new Topic.Builder()
                    .name(String.valueOf(user.getEmail().hashCode())).create());

            WebHook webHook = userClient.addWebHook(user, new WebHook.Builder()
                    .pathChannel(DataChannel.number)
                    .method(HttpMethod.GET)
                    .enabled(true)
                    .name(commonPointName)
                    .url(String.format("%s/%s", commonHost, user.getId()))
                    .create()
            );


            log(String.format("Added %s %s", sameNameTopic.getName(), uniqueTopic.getName()) );
            log(String.format("Added Hook: %s %s", webHook.getUrl(), user.getId()) );


            clients.add(Pair.of(user, userClient));


        }

        for (Pair<User, Nimbits> clientPair : clients) {

            Nimbits client = clientPair.getRight();
            User user = clientPair.getKey();
            Optional<Topic> optional = client.findTopicByName(commonPointName);
            assertTrue(optional.isPresent());
            Topic foo = optional.get();
            assertEquals(user.getId(), foo.getParent());
            assertEquals(commonPointName, foo.getName());

            Optional<WebHook> webHook = client.findWebHook(commonPointName);
            assertTrue(webHook.isPresent());
            assertEquals(webHook.get().getUrl(), String.format("%s/%s", commonHost, user.getId()));

        }


    }

    @Test
    public void testAbsentIfNotFound() {
        Optional<Topic> optional = adminClient.findTopicByName(UUID.randomUUID().toString());
        assertFalse(optional.isPresent());
    }



}
