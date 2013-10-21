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
import com.nimbits.PMF;
import com.nimbits.client.constants.Const;
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
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.io.blob.BlobStore;
import com.nimbits.server.io.blob.BlobStoreFactory;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.transaction.cache.NimbitsCache;
import com.nimbits.server.transaction.entity.EntityServiceFactory;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.settings.SettingServiceFactory;
import com.nimbits.server.transaction.settings.SettingsService;
import com.nimbits.server.transaction.user.AuthenticationServiceFactory;
import com.nimbits.server.cache.CacheFactory;
import com.nimbits.server.transaction.value.ValueServiceFactory;
import com.nimbits.server.transaction.value.dao.ValueDao;
import com.nimbits.server.transaction.value.dao.ValueDaoImpl;
import com.nimbits.server.transaction.value.service.ValueService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import javax.jdo.PersistenceManagerFactory;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/29/12
 * Time: 9:27 AM
 */

public class NimbitsServletTest extends BaseTest {
    public static final String email = Const.TEST_ACCOUNT;
    public final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig(),
            new LocalTaskQueueTestConfig(),
            new LocalBlobstoreServiceTestConfig(),
            new LocalUserServiceTestConfig())
            .setEnvIsLoggedIn(true).setEnvEmail(email).setEnvAuthDomain("nimbits.com");


    public PersistenceManagerFactory pmf = PMF.get();
    public NimbitsCache cache = CacheFactory.getInstance();
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


    public ValueDao valueDao;
    public NimbitsEngine engine = ApplicationListener.createEngine();
    public TaskService taskService = ApplicationListener.getTaskService(engine);
    public SettingsService settingsService = SettingServiceFactory.getServiceInstance(engine);
    public EntityService entityService = EntityServiceFactory.getInstance(engine);
    public ValueService valueService = ValueServiceFactory.getInstance(engine, taskService);
    public BlobStore blobStore = BlobStoreFactory.getInstance(engine.getPmf());

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
                PointType.basic, 0, false, 0.0);
        newPoint.setExpire(5);
        point = (Point) entityService.addUpdateEntity(user, newPoint).get(0);
        return point;
    }


    @Before
    public void setup()  {
        super.setup();
        SystemProperty.environment.set(SystemProperty.Environment.Value.Development);
        req = new MockHttpServletRequest();
        resp = new MockHttpServletResponse();
        valueDao = new ValueDaoImpl(engine);
        context = new MockServletContext();








        helper.setUp();


        settingsService.addSetting(SettingType.admin.getName(), email);
        settingsService.addSetting(SettingType.serverIsDiscoverable.getName(), SettingType.serverIsDiscoverable.getDefaultValue());

        emailAddress = CommonFactory.createEmailAddress(email);


        pointName = CommonFactory.createName("point", EntityType.point);
        pointChildName = CommonFactory.createName("pointChild", EntityType.point);
        groupName = CommonFactory.createName("group1", EntityType.point);


        User r = AuthenticationServiceFactory.getInstance(engine).createUserRecord(emailAddress);
        assertNotNull(r);


        List<Entity> result = entityService.getEntityByKey(user, emailAddress.getValue(), EntityType.user);
        assertFalse(result.isEmpty());
        user = (User) result.get(0);


        Entity accessKey = EntityModelFactory.createEntity(CommonFactory.createName("access Key", EntityType.accessKey), "", EntityType.accessKey, ProtectionLevel.onlyMe, user.getKey(), user.getKey());
        AccessKey ak = AccessKeyFactory.createAccessKey(accessKey, "AUTH", user.getKey(), AuthLevel.admin);
        entityService.addUpdateSingleEntity(ak);


        Map<String, Entity> map = entityService.getEntityModelMap(user, EntityType.accessKey, 1000);

        assertFalse(map.isEmpty());
        user.addAccessKey((AccessKey) map.values().iterator().next());
        assertNotNull(user);

        Entity c = EntityModelFactory.createEntity(groupName, "", EntityType.category, ProtectionLevel.everyone, user.getKey(), user.getKey(), UUID.randomUUID().toString());
        group = (Category) entityService.addUpdateSingleEntity(c);

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
                PointType.basic, 0, false, 0.0);
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
                PointType.basic, 0, false, 0.0);
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

    @After
    public void tearDown() {
//        EntitySearchService.deleteAll();
//        if (helper != null) {
//            try {
//                helper.tearDown();
//            } catch (Exception ignored) {
//
//            }
//        }

    }


    @Test
    public void makeSpringHappy() {
        assertTrue(true);
    }
}