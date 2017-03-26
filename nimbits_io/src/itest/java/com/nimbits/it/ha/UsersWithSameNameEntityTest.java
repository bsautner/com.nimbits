package com.nimbits.it.ha;


import com.google.common.base.Optional;
import com.nimbits.client.io.Nimbits;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.client.model.webhook.DataChannel;
import com.nimbits.client.model.webhook.HttpMethod;
import com.nimbits.client.model.webhook.WebHook;
import com.nimbits.client.model.webhook.WebHookModel;
import com.nimbits.it.AbstractBaseNimbitsTest;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        String commonPointName = "FOO";
        String commonHost = "http://test.com";

        for (int i = 0; i < count; i++) {
            emails.add(Pair.of(
                    String.format("%s@nimbits.com",
                            UUID.randomUUID().toString()),
                    UUID.randomUUID().toString()));

        }

        for (Pair<String, String> pair : emails) {
            users.add(
                    adminClient.addUser(new UserModel.Builder().email(pair.getLeft()).password(pair.getRight()).create() )
            );

        }

        assertEquals(count, users.size());

        for (Pair<String, String> pair : emails) {

            Nimbits userClient = new Nimbits.Builder()
                    .email(pair.getLeft())
                    .instance(host)
                    .token(pair.getRight()).create();

            User user = userClient.getMe();

            assertEquals(pair.getLeft(), user.getEmail().getValue());
            Point sameNamePoint = userClient.addPoint(user, new PointModel.Builder()
                    .name(commonPointName).create());

            Point uniquePoint = userClient.addPoint(user, new PointModel.Builder()
                    .name(String.valueOf(user.getEmail().hashCode())).create());

            WebHook webHook = userClient.addWebHook(user, new WebHookModel.Builder()
                    .pathChannel(DataChannel.number)
                    .method(HttpMethod.GET)
                    .enabled(true)
                    .name(commonPointName)
                    .url(String.format("%s/%s", commonHost, user.getId()))
                    .create()
            );


            log(String.format("Added %s %s", sameNamePoint.getName(), uniquePoint.getName()) );
            log(String.format("Added Hook: %s %s", webHook.getUrl(), user.getId()) );


            clients.add(Pair.of(user, userClient));


        }

        for (Pair<User, Nimbits> clientPair : clients) {

            Nimbits client = clientPair.getRight();
            User user = clientPair.getKey();
            Optional<Point> optional = client.findPointByName(commonPointName);
            assertTrue(optional.isPresent());
            Point foo = optional.get();
            assertEquals(user.getId(), foo.getParent());
            assertEquals(commonPointName, foo.getName().getValue());

            Optional<WebHook> webHook = client.findWebHook(commonPointName);
            assertTrue(webHook.isPresent());
            assertEquals(webHook.get().getUrl().getUrl(), String.format("%s/%s", commonHost, user.getId()));

        }


    }



}
