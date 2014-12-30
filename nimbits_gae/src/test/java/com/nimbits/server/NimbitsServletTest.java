/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server;


import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.tools.development.testing.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.accesskey.AccessKeyFactory;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserSource;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.io.BlobStore;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.settings.SettingsService;
import com.nimbits.server.transaction.subscription.SubscriptionService;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.dao.ValueDao;
import com.nimbits.server.transaction.value.service.ValueService;
import org.junit.AfterClass;
import org.junit.Before;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;


public class NimbitsServletTest extends BaseTest {


    @Resource(name = "taskService")
    public TaskService taskService;

    @Resource(name = "settingsService")
    public SettingsService settingsService;

    @Resource(name = "entityService")
    public EntityService entityService;

    @Resource(name = "valueService")
    public ValueService valueService;

    @Resource(name = "blobStore")
    public BlobStore blobStore;

    @Resource(name = "subscriptionService")
    public SubscriptionService subscriptionService;

    @Resource(name = "valueDao")
    public ValueDao valueDao;

    @Resource(name = "userService")
    public UserService userService;

    public static final String email = ServerSetting.admin.getDefaultValue();
    public final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig(),
            new LocalTaskQueueTestConfig(),
            new LocalBlobstoreServiceTestConfig(),
            new LocalUserServiceTestConfig())
            .setEnvIsLoggedIn(true).setEnvEmail(email).setEnvAuthDomain("nimbits.com");


    public MockHttpServletRequest req;
    public MockHttpServletResponse resp;
    public MockServletContext context;

    public EntityName pointName;
    public EntityName pointChildName;
    public EntityName groupName;
    public User user;
    public EmailAddress emailAddress;


    public Point point;
    public Point pointChild;


    public Entity pointEntity;
    public Entity pointChildEntity;
    public Category group;


    public Point createRandomPoint() {
        Point point;
        EntityName pointName;
        pointName = CommonFactory.createName(UUID.randomUUID().toString(), EntityType.point);
        pointEntity = EntityModelFactory.createEntity(pointName, "", EntityType.point, ProtectionLevel.everyone, group.getKey(), user.getKey(), UUID.randomUUID().toString());
        Point newPoint = PointModelFactory.createPointModel(
                pointEntity,
                0.0,
                90,
                "",
                0.0,
                false,
                false,
                false,
                0,
                false,
                FilterType.fixedHysteresis,
                0.1,
                false,
                PointType.basic, 0, false, 0.0, 10);
        newPoint.setExpire(5);
        point = (Point) entityService.addUpdateEntity(user, newPoint).get(0);
        return point;
    }


    @Before
    public void setup() {


        SystemProperty.environment.set(SystemProperty.Environment.Value.Development);
        req = new MockHttpServletRequest();
        resp = new MockHttpServletResponse();

        context = new MockServletContext();


        helper.setUp();


        settingsService.addSetting(ServerSetting.admin, email);


        emailAddress = CommonFactory.createEmailAddress(email);


        pointName = CommonFactory.createName("point", EntityType.point);
        pointChildName = CommonFactory.createName("pointChild", EntityType.point);
        groupName = CommonFactory.createName("group1", EntityType.point);


        user = userService.createUserRecord(emailAddress, UUID.randomUUID().toString(), UserSource.google);
        assertNotNull(user);


        List<Entity> result = entityService.getEntityByKey(user, emailAddress.getValue(), EntityType.user);
        assertFalse(result.isEmpty());
        user = (User) result.get(0);


        Entity accessKey = EntityModelFactory.createEntity(CommonFactory.createName("access Key", EntityType.accessKey), "", EntityType.accessKey, ProtectionLevel.onlyMe, user.getKey(), user.getKey());
        AccessKey ak = AccessKeyFactory.createAccessKey(accessKey, "AUTH", user.getKey(), AuthLevel.admin);
        entityService.addUpdateSingleEntity(user, ak);


        Map<String, Entity> map = entityService.getEntityModelMap(user, EntityType.accessKey, 1000);

        assertFalse(map.isEmpty());
        user.addAccessKey((AccessKey) map.values().iterator().next());
        assertNotNull(user);

        Entity c = EntityModelFactory.createEntity(groupName, "", EntityType.category, ProtectionLevel.everyone, user.getKey(), user.getKey(), UUID.randomUUID().toString());
        group = (Category) entityService.addUpdateSingleEntity(user, c);

        pointEntity = EntityModelFactory.createEntity(pointName, "", EntityType.point, ProtectionLevel.everyone, group.getKey(), user.getKey(), UUID.randomUUID().toString());
        Point newPoint = PointModelFactory.createPointModel(
                pointEntity,
                0.0,
                90,
                "",
                0.0,
                false,
                false,
                false,
                0,
                false,
                FilterType.fixedHysteresis,
                0.1,
                false,
                PointType.basic, 0, false, 0.0, 10);
        newPoint.setExpire(5);
        point = (Point) entityService.addUpdateEntity(user, newPoint).get(0);
        // point = pointService.addPoint(user, pointEntity);

        pointChildEntity = EntityModelFactory.createEntity(pointChildName, "", EntityType.point, ProtectionLevel.everyone, point.getKey(), user.getKey(), UUID.randomUUID().toString());
        Point newChild = PointModelFactory.createPointModel(
                pointChildEntity,
                0.0,
                90,
                "",
                0.0,
                false,
                false,
                false,
                0,
                false,
                FilterType.fixedHysteresis,
                0.1,
                false,
                PointType.basic, 0, false, 0.0, 10);
        pointChild = (Point) entityService.addUpdateEntity(user, newChild).get(0);
        // pointChild =  pointService.addPoint(user, pointChildEntity);
        assertNotNull(pointChild);

        req.addParameter(Parameters.email.getText(), email);
        req.addParameter(Parameters.point.getText(), pointName.getValue());
        req.addParameter(Parameters.value.getText(), "1.234");
        String userJson = GsonFactory.getInstance().toJson(user);
        String pointJson = GsonFactory.getInstance().toJson(point);

        req.addParameter(Parameters.pointUser.getText(), userJson);
        req.addParameter(Parameters.pointJson.getText(), pointJson);


    }

    @AfterClass
    public static void afterClass() {

    }


}