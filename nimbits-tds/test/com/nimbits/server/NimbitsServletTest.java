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

import com.google.appengine.tools.development.testing.*;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.*;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.accesskey.AccessKeyFactory;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.common.CommonFactory;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.datapoints.PointService;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.settings.SettingsService;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.process.cron.SystemCron;
import com.nimbits.server.transactions.service.entity.EntityTransactions;
import com.nimbits.server.transactions.service.user.UserServerService;
import com.nimbits.server.transactions.service.user.UserTransactions;
import com.nimbits.server.transactions.service.value.ValueTransactions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.IOException;
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
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml",
        "classpath:META-INF/applicationContext-api.xml",
        "classpath:META-INF/applicationContext-cache.xml",
        "classpath:META-INF/applicationContext-cron.xml",
        "classpath:META-INF/applicationContext-dao.xml",
        "classpath:META-INF/applicationContext-service.xml",
        "classpath:META-INF/applicationContext-task.xml"

})
public class NimbitsServletTest {
    public static final String email = Const.TEST_ACCOUNT;
    public final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig(),
            new LocalTaskQueueTestConfig(),
            new LocalBlobstoreServiceTestConfig(),
            new LocalUserServiceTestConfig())
            .setEnvIsLoggedIn(true).setEnvEmail(email).setEnvAuthDomain("nimbits.com");

    @Resource(name="entityService")
    public EntityService entityService;

    @Resource(name="userService")
    public UserServerService userService;

    @Resource(name="entityCache")
    public EntityTransactions entityTransactions;

    @Resource(name="systemCron")
    public SystemCron systemCron;


    @Resource(name="commonFactory")
    public CommonFactory commonFactory;


    public MockHttpServletRequest req;
    public MockHttpServletResponse resp;


    public EntityName pointName;
    public EntityName pointChildName;
    public EntityName groupName;
    public User user;
    public EmailAddress emailAddress;


    @Resource(name="pointService")
    public PointService pointService;

    @Resource(name="settingsService")
    public SettingsService settingsService;

    @Resource(name="userDao")
    public UserTransactions userTransactionsDao;



    public Point point;
    public Point pointChild;
    public Entity pointEntity;
    public Entity pointChildEntity;
    public Category group;

    @Resource(name="valueDao")
    public ValueTransactions valueDao;

    @Before
    public void setUp() throws NimbitsException {
        req = new MockHttpServletRequest();
        resp = new MockHttpServletResponse();



        helper.setUp();


        try {
            systemCron.doGet(null,null);

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }



        settingsService.addSetting(SettingType.admin, email);
        settingsService.addSetting(SettingType.serverIsDiscoverable,true);

       emailAddress = CommonFactoryLocator.getInstance().createEmailAddress(email);


        pointName = CommonFactoryLocator.getInstance().createName("point", EntityType.point);
        pointChildName = CommonFactoryLocator.getInstance().createName("pointChild", EntityType.point);
        groupName = CommonFactoryLocator.getInstance().createName("group1", EntityType.point);


        User r = userService.createUserRecord(emailAddress);
        assertNotNull(r);


        List<Entity> result = entityService.getEntityByKey(user, emailAddress.getValue(), EntityType.user);
        assertFalse(result.isEmpty());
        user = (User) result.get(0);


        Entity accessKey = EntityModelFactory.createEntity(pointName, "", EntityType.accessKey, ProtectionLevel.onlyMe, user.getKey(), user.getKey());
        AccessKey ak = AccessKeyFactory.createAccessKey(accessKey, "AUTH", user.getKey(), AuthLevel.admin);
        entityService.addUpdateEntity(ak);


        Map<String, Entity> map = entityService.getEntityMap(user, EntityType.accessKey, 1000) ;

        assertFalse(map.isEmpty());
        user.addAccessKey((AccessKey) map.values().iterator().next());
        assertNotNull(user);

        Entity c = EntityModelFactory.createEntity(groupName, "", EntityType.category, ProtectionLevel.everyone, user.getKey(), user.getKey(), UUID.randomUUID().toString());
        group = (Category) entityService.addUpdateEntity(c);

        pointEntity = EntityModelFactory.createEntity(pointName, "", EntityType.point, ProtectionLevel.everyone,  group.getKey(), user.getKey(), UUID.randomUUID().toString());
        Point newPoint   =  PointModelFactory.createPointModel(
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
                PointType.basic , 0, false, 0.0);
        newPoint.setExpire(5);
        point = (Point) entityService.addUpdateEntity(user, newPoint);
        // point = pointService.addPoint(user, pointEntity);

        pointChildEntity = EntityModelFactory.createEntity(pointChildName, "", EntityType.point, ProtectionLevel.everyone, point.getKey(), user.getKey(), UUID.randomUUID().toString());
        Point newChild =  PointModelFactory.createPointModel(
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
                PointType.basic, 0, false, 0.0 );
        pointChild = (Point) entityService.addUpdateEntity(user, newChild);
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
      if (helper != null) {
          try {
        helper.tearDown();
          }
          catch (Exception ignored) {

          }
      }

    }


    @Test
    public void makeSpringHappy() {
        assertTrue(true);
    }
}