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
import org.junit.Test;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 3/31/12
 * Time: 8:47 AM
 */
public class TestUpgradeTask extends NimbitsServletTest {

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
