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

package com.nimbits.server.task;

import com.nimbits.client.exception.NimbitsException;
import helper.NimbitsServletTest;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 3/31/12
 * Time: 8:47 AM
 */
public class TestUpgradeTask extends NimbitsServletTest {

    @Test
    @Ignore
    public void doUserTest() throws NimbitsException {

//        final PersistenceManager pm = PMF.get().getPersistenceManager();
//
//        for (int i = 0; i < 10; i++) {
//
//            EmailAddress em = CommonFactoryLocator.getInstance().createEmailAddress("b" + i + "@test.com");
//            NimbitsUser nimbitsUser = new NimbitsUser(em, UUID.randomUUID().toString());
//            NimbitsUser r = pm.makePersistent(nimbitsUser);
//            System.out.println(r.getId());
//            NimbitsUser rx = UpgradeTask.getLegUser(pm, r.getId());
//            assertNotNull(rx);
//            for (int x = 0; x < 2; x++) {
//                PointCatagory cx = new PointCatagory(r.getId(), UUID.randomUUID().toString(), "", UUID.randomUUID().toString(), 0);
//                pm.makePersistent(cx);
//                for (int px = 0; px < 2; px++ ) {
//                    DataPoint dp = new DataPoint();
//                    dp.setUserFK(r.getId());
//                    dp.setCatID(cx.getId());
//                    dp.setName(UUID.randomUUID().toString());
//                    pm.makePersistent(dp);
//                }
//            }
//
//
//        }
//        pm.close();
//        final PersistenceManager pm2 = PMF.get().getPersistenceManager();
//        req.addParameter("s", "0");
//        UpgradeTask.doStart(req);
//
//        Query q = pm2.newQuery(UserEntity.class);
//        List<User> users = (List<User>) q.execute();
//        assertEquals(11, users.size());
//        UpgradeTask.doCategory2(req);
//        UpgradeTask.doPoint2(req);
//        for (User u : users) {
//            Map<String, Entity> cmap = EntityServiceFactory.getInstance().getEntityMap(u, EntityType.category, 1000);
//            Map<String, Entity> pmap = EntityServiceFactory.getInstance().getEntityMap(u, EntityType.point, 1000);
//            if (u.getEmail().getValue().contains("test.com")) {
//                assertEquals(2, cmap.size());
//                assertEquals(4, pmap.size());
//            }
//            else {
//                assertEquals(1, cmap.size());
//            }
//            for (Entity cx : cmap.values()) {
//
//            }
//        }



    }

//    @Test
//    public void doUserTest() throws NimbitsException, InterruptedException {
//        final PersistenceManager pm = PMF.get().getPersistenceManager();
//
//        UserEntity u;
//        for (int i = 0; i < 100; i++) {
//            u = new UserEntity(CommonFactoryLocator.getInstance().createEmailAddress("test" + i + "@example.com"), UUID.randomUUID().toString());
//            u = pm.makePersistent(u);
//            for (int x = 0; x < 10; x++) {
//                PointCatagory c = new PointCatagory();
//                c.name = UUID.randomUUID().toString();
//                c.userFK = u.getId();
//                c.uuid = c.name;
//                pm.makePersistent(c);
//            }
//
//        }
//
//        assertEquals(101, UpgradeTask.doStart());
//        Thread.sleep(1000);
//        final Entity userEntity = EntityServiceFactory.getInstance().getEntityByName(CommonFactoryLocator.getInstance().createName(("test@example.com"), EntityType.user));
//        for (int x = 0; x < 10; x++) {
//            PointCatagory c = new PointCatagory();
//            c.name = UUID.randomUUID().toString();
//            c.userFK = user.getId();
//            c.uuid = c.name;
//            pm.makePersistent(c);
//            for (int r = 0; r< 10; r++) {
//
//
//                DataPoint point1 = new DataPoint();
//                point1.setUserFK(user.getId());
//                point1.name = UUID.randomUUID().toString();
//                point1.catID =c.id;
//                pm.makePersistent(point1);
//
//            }
//
//
//
//        }
//        pm.close();
//        assertNotNull(userEntity);
//        final String json = GsonFactory.getInstance().toJson(userEntity);
//        req.addParameter(Parameters.json.getText(), json);
//
//        assertEquals(10, UpgradeTask.doUser(req));
//
//
//        List<Entity> categories = EntityServiceFactory.getInstance().getChildren(userEntity, EntityType.category);
//        for (Entity entity : categories){
//
//            String j = GsonFactory.getInstance().toJson(entity);
//            req.setParameter(Parameters.json.getText(), j);
//            UpgradeTask.doCategory(req);
//
//        }
//        assertEquals(13, categories.size());
//
//
//
//
//
//
//    }



    @Test
    public void doValueTest() throws NimbitsException, InterruptedException {
//
//        RecordedValue v;
//
//
//        RecordedValueTransactions t = RecordedValueTransactionFactory.getLegacyInstance(point);
//        Thread.sleep(1000);
//        List<Value> values = new ArrayList<Value>(10000);
//        for (int i = 0; i < 10000; i++)
//        {
//            v = new RecordedValue(0.0, 0.0, i, new Date(), point.getId(), "", "");
//            values.add(v);
//
//        }
//        t.recordValues(values);
//        final Timespan timespan = TimespanModelFactory.createTimespan(point.getCreateDate(), new Date());
//
//        List<Value> result = t.getDataSegment(timespan, 0, 10000);
//        assertEquals(values.size(), result.size());
//
//        String json = GsonFactory.getInstance().toJson(pointEntity);
//        req.addParameter(Parameters.json.getText(), json);
//        UpgradeTask.doValue(req);
//        Thread.sleep(3000);
//        List<Value> newList = RecordedValueTransactionFactory.getDaoInstance(point).getDataSegment(timespan, 0, 10000);
//        assertEquals(values.size(), newList.size());


    }

}
