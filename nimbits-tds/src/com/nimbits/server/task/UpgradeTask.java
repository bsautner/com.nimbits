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

import com.nimbits.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.Calculation;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.model.user.User;
import com.nimbits.server.dao.datapoint.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.orm.entity.*;
import com.nimbits.server.point.PointTransactionsFactory;
import com.nimbits.server.subscription.*;
import com.nimbits.server.user.UserTransactionFactory;
import com.nimbits.shared.*;

import javax.jdo.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 1/14/12
 * Time: 10:48 AM
 */
public class UpgradeTask  extends HttpServlet

{
    private final String N= "Nimbits_Unsorted";
    private static final Logger log = Logger.getLogger(UpgradeTask.class.getName());
    private static final long serialVersionUID = 1L;

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {
        int set = 0;
        int results = -1;
        Action action = Action.get(req.getParameter(Const.PARAM_ACTION));

        try {
            if (action.equals(Action.start)){
                log.info("Started upgrade task");
                doStart(set, results);
            }
            else if (action.equals(Action.user)) {
                try {
                    log.info("Started upgrade user task");
                    doUser(req);
                } catch (NimbitsException e) {
                    log.severe(e.getMessage());
                }

            }
            else if (action.equals(Action.category)) {
                log.info("Started upgrade category task");
                doCategory(req);

            }
            else if (action.equals(Action.point)) {
                log.info("Started upgrade Point task");
                try {
                    doPoint(req);
                } catch (NimbitsException e) {
                    log.severe(e.getMessage());
                }

            }
        } catch (Exception e) {
            log.severe(e.getMessage());
            e.printStackTrace();
        }


    }

    private void doPoint(HttpServletRequest req) throws NimbitsException {

        Entity pointEntity = GsonFactory.getInstance().fromJson(req.getParameter(Const.PARAM_JSON), EntityModel.class);
        User u = UserTransactionFactory.getDAOInstance().getUserByUUID(pointEntity.getOwner());
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query c = pm.newQuery(DataPoint.class, "uuid==o");
        c.declareParameters("String o");
        List<DataPoint> cList = (List<DataPoint>) c.execute(pointEntity.getEntity());

        if (cList.size() > 0) {
            //create subscriptions

            DataPoint p = cList.get(0);
            log.info("Started creating subscritptions");
            createSubscriptions(u, p);
            log.info("fixing calculations");


            if (p.calculationEntity != null) {

                String x = null, y = null, z = null, target = null;

                if (p.calculationEntity.x != null && p.calculationEntity.x > 0) {
                    try {
                        DataPoint px = pm.getObjectById(DataPoint.class, p.calculationEntity.x);
                     if (px != null) {
                       x=(px.getUUID());
                    }
                } catch (JDOObjectNotFoundException ex) {
                    log.info("Point not found");

                }
                }
                if (p.calculationEntity.y != null && p.calculationEntity.y > 0) {
                  try {
                    DataPoint py =  pm.getObjectById(DataPoint.class, p.calculationEntity.y);
                    if (py != null) {
                       y=(py.getUUID());
                    }
                  } catch (JDOObjectNotFoundException ex) {
                      log.info("Point not found");

                  }
                }
                if (p.calculationEntity.z != null && p.calculationEntity.z > 0) {
                  try {
                      DataPoint pz =  pm.getObjectById(DataPoint.class, p.calculationEntity.z);

                    if (pz != null) {
                        z=(pz.getUUID());
                    }
                  } catch (JDOObjectNotFoundException ex) {
                      log.info("Point not found");

                  }
                }
                if (p.calculationEntity.target != null && p.calculationEntity.target > 0) {
                   try {
                    DataPoint pt = pm.getObjectById(DataPoint.class, p.calculationEntity.target);
                    if (pt != null && ! pt.getUUID().equals(p.getUUID())) {
                       target= (pt.getUUID());
                    }
                   } catch (JDOObjectNotFoundException ex) {
                       log.info("Point not found");

                   }
                }
                else {
                   // p.calculationEntity.setEnabled(false);
                }
                Transaction tx = pm.currentTransaction();
                tx.begin();
                p.calculationEntity.setX(x);
                p.calculationEntity.setY(y);
                p.calculationEntity.setZ(z);
                p.calculationEntity.setTarget(target);
                tx.commit();

            }
        }

        pm.close();

    }


    private void createSubscriptions(User u, DataPoint p) {
        boolean enabled = p.isHighAlarmOn() || p.isLowAlarmOn() || p.isIdleAlarmOn();
        int delay = p.getAlarmDelay() > 5 ?  p.getAlarmDelay() : 5;
        EntityName name = CommonFactoryLocator.getInstance().createName(p.getName().getValue() + " alert");
        if (p.getAlarmToFacebook()) {
            createSubscription(u, p, enabled, delay, name, SubscriptionType.anyAlert, SubscriptionNotifyMethod.facebook);
        }
        if (p.getSendAlarmTweet()) {
            createSubscription(u, p, enabled, delay, name, SubscriptionType.anyAlert, SubscriptionNotifyMethod.twitter);
        }
        if (p.getSendAlarmIM()) {
            createSubscription(u, p, enabled, delay, name, SubscriptionType.anyAlert, SubscriptionNotifyMethod.instantMessage);
        }
        if (p.isAlarmToEmail()) {
            createSubscription(u, p, enabled, delay, name, SubscriptionType.anyAlert, SubscriptionNotifyMethod.email);
        }
        if (p.isIdleAlarmOn()) {
            createSubscription(u, p, p.isIdleAlarmOn(),
                    delay, name, SubscriptionType.idle, SubscriptionNotifyMethod.email);
        }
        if (p.isPostToFacebook()) {
            createSubscription(u, p, enabled, delay, name, SubscriptionType.newValue, SubscriptionNotifyMethod.facebook);
        }
    }

    private void createSubscription(User u, DataPoint p, boolean enabled, int delay, EntityName name,
                                    SubscriptionType type, SubscriptionNotifyMethod method ) {
        Subscription subscription = SubscriptionFactory.createSubscription(p.getUUID(),
                type,method ,delay,
                new Date(), p.getSendAlertsAsJson(), enabled);

        subscription.setUuid(UUID.randomUUID().toString());

        Entity entity = EntityModelFactory.createEntity(name, "",EntityType.subscription,
                ProtectionLevel.onlyMe,subscription.getUuid(), p.getUUID(), u.getUuid());
        log.info("created subscription " + name.getValue());
        Entity r = EntityTransactionFactory.getInstance(u).addUpdateEntity(entity);
        SubscriptionTransactionFactory.getInstance(u).subscribe(r, subscription);
    }

    private void doCategory(HttpServletRequest req) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        Entity categoryEntity = GsonFactory.getInstance().fromJson(req.getParameter(Const.PARAM_JSON), EntityModel.class);
        User u = UserTransactionFactory.getDAOInstance().getUserByUUID(categoryEntity.getOwner());

        final Query c = pm.newQuery(PointCatagory.class, "userFK==o");
        c.declareParameters("String o");
        List<PointCatagory> cList = (List<PointCatagory>) c.execute(u.getId());

        if (cList.size() > 0) {
            PointCatagory catagory = cList.get(0);
            List<DataPoint> points;

            final Query q = pm.newQuery(DataPoint.class, "catID==o");

            points = (List<DataPoint>) q.execute(catagory.id);
            for (DataPoint p : points) {
                String parent;
                if (catagory.name.equals(N)) {
                    parent = u.getUuid();

                }
                else {
                    parent = catagory.uuid;
                }
                if (Utils.isEmptyString(parent)) {
                    parent = u.getUuid();
                }
                ProtectionLevel protectionLevel = p.isPublic() ? ProtectionLevel.everyone : ProtectionLevel.onlyMe;
                EntityName name = CommonFactoryLocator.getInstance().createName(p.getName().getValue());
                Entity pointEntity = EntityModelFactory.createEntity(name, p.getDescription(), EntityType.point,
                        protectionLevel, p.getUUID(), parent, u.getUuid());
                Entity r = EntityTransactionFactory.getInstance(u).addUpdateEntity(pointEntity);
                log.info("created point " + name.getValue());
                TaskFactoryLocator.getInstance().startUpgradeTask(Action.point,r );

            }

            final Query d = pm.newQuery(DiagramEntity.class, "categoryFk==o");

            List<DiagramEntity> diagrams = (List<DiagramEntity>) d.execute(catagory.id);
            for (DiagramEntity e : diagrams) {
                String parent;
                if (catagory.name.equals(N)) {
                    parent = u.getUuid();
                }
                else {
                    parent = catagory.uuid;
                }
                if (Utils.isEmptyString(parent)) {
                    parent = u.getUuid();
                }
                ProtectionLevel protectionLevel = ProtectionLevel.get(e.protectionLevel);
                EntityName name = CommonFactoryLocator.getInstance().createName(e.name + ".svg");
                Entity pointEntity = EntityModelFactory.createEntity(name, "", EntityType.file,
                        protectionLevel, UUID.randomUUID().toString(), parent, u.getUuid(), e.blobKey.getKeyString());

                Entity r = EntityTransactionFactory.getInstance(u).addUpdateEntity(pointEntity);
                log.info("created diagram " + name.getValue());
                // TaskFactoryLocator.getInstance().startUpgradeTask(Action.point,r );

            }



        }




        pm.close();
    }

    private void doUser(HttpServletRequest req) throws NimbitsException {
        Entity userEntity = GsonFactory.getInstance().fromJson(req.getParameter(Const.PARAM_JSON), EntityModel.class);
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q = pm.newQuery(NimbitsUser.class, "uuid==o");

        List<NimbitsUser> users = (List<NimbitsUser>) q.execute(userEntity.getEntity());
        if (users.size() > 0) {
            NimbitsUser user = users.get(0);


            final Query q1 = pm.newQuery(PointCatagory.class, "userFK==o");
            q1.declareParameters("String o");
            List<PointCatagory> list = (List<PointCatagory>) q1.execute(user.getId());
            for (PointCatagory c : list) {
                EntityName name = CommonFactoryLocator.getInstance().createName(c.name);
                Entity entity = EntityModelFactory.createEntity(name, c.name, EntityType.category, ProtectionLevel.get(c.protectionLevel),
                        UUID.randomUUID().toString(), userEntity.getEntity(), userEntity.getEntity());
                Entity r = entity;
                if (! name.getValue().equals(N)) {
                    r = EntityTransactionFactory.getInstance(user).addUpdateEntity(entity);
                }
                log.info("created category " + name.getValue());
                TaskFactoryLocator.getInstance().startUpgradeTask(Action.category,r );
            }


            if (user.getConnections() != null && user.getConnections().size() > 0) {
                for (Long l : user.getConnections()) {
                    User connection = UserTransactionFactory.getDAOInstance().getNimbitsUserByID(l);
                    EntityName name = CommonFactoryLocator.getInstance().createName(connection.getEmail().getValue());
                    Entity entity = EntityModelFactory.createEntity(name, "",EntityType.userConnection, ProtectionLevel.onlyMe,
                            connection.getUuid(), user.getUuid(), user.getUuid());
                    log.info("created connection " + name.getValue());
                    Entity r =   EntityTransactionFactory.getInstance(user).addUpdateEntity(entity);

                }
            }

        }
        pm.close();
    }

    private void doStart(int set, int results) {
        while (results != 0) {
            final List<User> users = UserTransactionFactory.getInstance().getUsers(set, set + Const.CONST_QUERY_CHUNK_SIZE);
            results = users.size();

            set += Const.CONST_QUERY_CHUNK_SIZE;
            for (User u : users) {
                Entity entity = EntityModelFactory.createEntity(u);
                Entity r = EntityTransactionFactory.getInstance(u).addUpdateEntity(entity);
                TaskFactoryLocator.getInstance().startUpgradeTask(Action.user,r );

            }

        }
    }


}
