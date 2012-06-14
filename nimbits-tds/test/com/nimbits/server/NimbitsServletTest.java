/*
 * Copyright (c) 2010 Tonic Solutions LLC.
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
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.accesskey.AccessKeyFactory;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.datapoints.PointService;
import com.nimbits.client.service.settings.SettingsService;
import com.nimbits.server.api.impl.ValueServletImpl;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.settings.SettingTransactions;
import com.nimbits.server.settings.SettingTransactionsFactory;
import com.nimbits.server.settings.SettingsServiceFactory;
import com.nimbits.server.transactions.dao.value.ValueDAOImpl;
import com.nimbits.server.transactions.service.entity.EntityServiceFactory;
import com.nimbits.server.transactions.service.point.PointServiceFactory;
import com.nimbits.server.transactions.service.user.UserServiceFactory;
import com.nimbits.server.transactions.service.user.UserTransactionFactory;
import com.nimbits.server.transactions.service.user.UserTransactions;
import org.junit.After;
import org.junit.Before;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/29/12
 * Time: 9:27 AM
 */
public class NimbitsServletTest {
    public static final String email = "support@nimbits.com";
    public final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig(),
            new LocalTaskQueueTestConfig(),
            new LocalBlobstoreServiceTestConfig(),
            new LocalUserServiceTestConfig()).setEnvIsLoggedIn(true).setEnvEmail(email).setEnvAuthDomain("nimbits.com");



    public MockHttpServletRequest req;
    public MockHttpServletResponse resp;


    public EntityName pointName;
    public EntityName pointChildName;
    public EntityName groupName;
    public User user;
    public EmailAddress emailAddress;

    public ValueServletImpl valueServlet;

    public PointService pointService;
    public SettingsService settingsService;
    public UserTransactions userTransactionsDao;
    public SettingTransactions settingsDAO;
    public Point point;
    public Point pointChild;
    public Entity pointEntity;
    public Entity pointChildEntity;
    public Category group;
    public ValueDAOImpl valueDao;

    @Before
    public void setUp() throws NimbitsException {
        req = new MockHttpServletRequest();
        resp = new MockHttpServletResponse();



        helper.setUp();
        SettingsServiceFactory.getInstance().addSetting(SettingType.admin, email);
        SettingsServiceFactory.getInstance().addSetting(SettingType.serverIsDiscoverable,true);
        pointService = PointServiceFactory.getInstance();
        settingsService = SettingsServiceFactory.getInstance();
        emailAddress = CommonFactoryLocator.getInstance().createEmailAddress(email);
        valueServlet= new ValueServletImpl();

        pointName = CommonFactoryLocator.getInstance().createName("point", EntityType.point);
        pointChildName = CommonFactoryLocator.getInstance().createName("pointChild", EntityType.point);
        groupName = CommonFactoryLocator.getInstance().createName("group1", EntityType.point);

        userTransactionsDao = UserTransactionFactory.getDAOInstance();
        User r = UserServiceFactory.getServerInstance().createUserRecord(emailAddress);
        assertNotNull(r);


        List<Entity> result = EntityServiceFactory.getInstance().getEntityByKey(emailAddress.getValue(), EntityType.user);
        assertFalse(result.isEmpty());
        user = (User) result.get(0);


        Entity accessKey = EntityModelFactory.createEntity(pointName, "", EntityType.accessKey, ProtectionLevel.onlyMe, user.getKey(), user.getKey());
        AccessKey ak = AccessKeyFactory.createAccessKey(accessKey, "AUTH", user.getKey(), AuthLevel.admin);
        EntityServiceFactory.getInstance().addUpdateEntity(ak);


        Map<String, Entity> map = EntityServiceFactory.getInstance().getEntityMap(user, EntityType.accessKey, 1000) ;

        assertFalse(map.isEmpty());
        user.addAccessKey((AccessKey) map.values().iterator().next());
        assertNotNull(user);
        settingsDAO = SettingTransactionsFactory.getDaoInstance();

        Entity c = EntityModelFactory.createEntity(groupName, "", EntityType.category, ProtectionLevel.everyone, user.getKey(), user.getKey(), UUID.randomUUID().toString());
        group = (Category) EntityServiceFactory.getInstance().addUpdateEntity(c);

        pointEntity = EntityModelFactory.createEntity(pointName, "", EntityType.point, ProtectionLevel.everyone,  group.getKey(), user.getKey(), UUID.randomUUID().toString());
        Point newPoint = PointModelFactory.createPointModel(pointEntity);
        point = (Point) EntityServiceFactory.getInstance().addUpdateEntity(user, newPoint);
        // point = pointService.addPoint(user, pointEntity);
        valueDao = new ValueDAOImpl(point);
        pointChildEntity = EntityModelFactory.createEntity(pointChildName, "", EntityType.point, ProtectionLevel.everyone, point.getKey(), user.getKey(), UUID.randomUUID().toString());
        Point newChild = PointModelFactory.createPointModel(pointChildEntity);
        pointChild = (Point) EntityServiceFactory.getInstance().addUpdateEntity(user, newChild);
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
        helper.tearDown();

    }
}