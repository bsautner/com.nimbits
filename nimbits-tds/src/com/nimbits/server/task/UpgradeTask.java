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

import com.google.gson.JsonSyntaxException;
import com.nimbits.PMF;
import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.CalculationModelFactory;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.intelligence.Intelligence;
import com.nimbits.client.model.intelligence.IntelligenceModelFactory;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.subscription.SubscriptionFactory;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.timespan.TimespanModelFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.calculation.CalculationServiceFactory;
import com.nimbits.server.entity.EntityServiceFactory;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.intelligence.IntelligenceServiceFactory;
import com.nimbits.server.orm.DataPoint;
import com.nimbits.server.orm.DiagramEntity;
import com.nimbits.server.orm.NimbitsUser;
import com.nimbits.server.orm.PointCatagory;
import com.nimbits.server.point.PointServiceFactory;
import com.nimbits.server.subscription.SubscriptionTransactionFactory;
import com.nimbits.server.user.UserTransactionFactory;
import com.nimbits.server.value.RecordedValueTransactionFactory;
import com.nimbits.server.value.RecordedValueTransactions;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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

        Action action = Action.get(req.getParameter(Parameters.action.getText()));
        try {
            switch (action) {
                case start:

                    clog("Started upgrade task");
                    doStart();
                    break;
                case user:

                    clog("Started upgrade user task");
                    doUser(req);
                    break;
                case category:

                    clog("Started upgrade category task");
                    doCategory(req);
                    break;
                case point:

                    clog("Started upgrade Point task");
                    doPoint(req);
                    break;

                case value:
                      doValue(req);
                    break;
            }
        } catch (NimbitsException e) {
            clog(e.getMessage());
        }

    }


    private void clog(String string) {
       log.info(string);
    }

    private void doValue(HttpServletRequest req) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            Entity pointEntity = GsonFactory.getInstance().fromJson(req.getParameter(Parameters.json.getText()), EntityModel.class);
            User u = UserTransactionFactory.getDAOInstance().getUserByUUID(pointEntity.getOwner());

            final Query c = pm.newQuery(DataPoint.class, "uuid==o");
            c.declareParameters("String o");
            List<DataPoint> pList = (List<DataPoint>) c.execute(pointEntity.getEntity());

            if (pList.size() > 0) {
                Point point = pList.get(0);
                RecordedValueTransactions old =  RecordedValueTransactionFactory.getLegacyInstance(point);
                RecordedValueTransactions dao =  RecordedValueTransactionFactory.getDaoInstance(point);
                Timespan timespan = TimespanModelFactory.createTimespan(point.getCreateDate(), new Date());

                int count = -1;
                int seg = 0;
                while (count > 0) {
                    seg += 1000;
                    List<Value> values= dao.getDataSegment(timespan,seg, seg + 1000 );
                    dao.recordValues(values);

                }

            }

        } catch (NimbitsException e) {
            log.severe(e.getMessage());
        } finally {
            pm.close();
        }

        }

    private void doPoint(HttpServletRequest req) {

        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            Entity pointEntity = GsonFactory.getInstance().fromJson(req.getParameter(Parameters.json.getText()), EntityModel.class);
            User u = UserTransactionFactory.getDAOInstance().getUserByUUID(pointEntity.getOwner());
            clog(req.getParameter(Parameters.json.getText()));
            if (u==null) {
                clog("User not found" + pointEntity.getOwner());
            }
            clog(u.getEmail().getValue());
            final Query c = pm.newQuery(DataPoint.class, "uuid==o");
            c.declareParameters("String o");
            List<DataPoint> pList = (List<DataPoint>) c.execute(pointEntity.getEntity());
            if (pList.size() > 0) {
                //create subscriptions

                DataPoint p = pList.get(0);
                clog("Started creating subscritptions");
                createSubscriptions(u, p);
                clog("fixing calculations");
                createCalcs(pm, u, p);
                if (p.dataPointIntelligenceEntity != null) {
                    EntityName name = CommonFactoryLocator.getInstance().createName(p.name + "Intelligence");
                    String uuid = UUID.randomUUID().toString();
                    Point target = PointServiceFactory.getInstance().getPointByID(p.dataPointIntelligenceEntity.targetPointId);
                    if (target != null) {
                    Entity iEntity = EntityModelFactory.createEntity(name,"",EntityType.intelligence,
                            ProtectionLevel.onlyMe, uuid, p.getUUID(), u.getUuid() );
                    EntityServiceFactory.getInstance().addUpdateEntity(u, iEntity);

                    Intelligence i = IntelligenceModelFactory.createIntelligenceModel(uuid, p.dataPointIntelligenceEntity.getEnabled(),
                            p.dataPointIntelligenceEntity.getResultTarget(),target.getUUID(), p.dataPointIntelligenceEntity.getInput(),
                            p.dataPointIntelligenceEntity.getNodeId(), p.dataPointIntelligenceEntity.getResultsInPlainText(),
                            p.getUUID());
                        IntelligenceServiceFactory.getDaoInstance().addUpdateIntelligence(i);
                    }
                }
                TaskFactoryLocator.getInstance().startUpgradeTask(Action.value,pointEntity );

            }
        } catch (Exception e) {
            e.printStackTrace();
            clog("ERROR : " + e.getMessage());
        } finally {
            if (! pm.isClosed()) {
                pm.close();
            }
        }



    }

    private void createCalcs(PersistenceManager pm, User u, DataPoint p) throws NimbitsException {
        if (p.calculationEntity != null) {

            String x = null, y = null, z = null, target = null;

            if (p.calculationEntity.x != null && p.calculationEntity.x > 0) {
                try {
                    DataPoint px = pm.getObjectById(DataPoint.class, p.calculationEntity.x);
                    if (px != null) {
                        x=(px.getUUID());
                    }
                } catch (JDOObjectNotFoundException ex) {
                    clog("Point not found");

                }
            }
            if (p.calculationEntity.y != null && p.calculationEntity.y > 0) {
                try {
                    DataPoint py =  pm.getObjectById(DataPoint.class, p.calculationEntity.y);
                    if (py != null) {
                        y=(py.getUUID());
                    }
                } catch (JDOObjectNotFoundException ex) {
                    clog("Point not found");

                }
            }
            if (p.calculationEntity.z != null && p.calculationEntity.z > 0) {
                try {
                    DataPoint pz =  pm.getObjectById(DataPoint.class, p.calculationEntity.z);

                    if (pz != null) {
                        z=(pz.getUUID());
                    }
                } catch (JDOObjectNotFoundException ex) {
                    clog("Point not found");

                }
            }
            if (p.calculationEntity.target != null && p.calculationEntity.target > 0) {
                try {
                    DataPoint pt = pm.getObjectById(DataPoint.class, p.calculationEntity.target);
                    if (pt != null && ! pt.getUUID().equals(p.getUUID())) {
                        target= (pt.getUUID());
                    }
                } catch (JDOObjectNotFoundException ex) {
                    clog("Point not found");

                }
            }
            else {
                // p.calculationEntity.setEnabled(false);
            }

            pm.close();
            clog("Done getting point vars");
            EntityName name = CommonFactoryLocator.getInstance().createName(p.name + " calc");
            String uuid = UUID.randomUUID().toString();
            Entity entity = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe,
                    uuid, p.getUUID(), u.getUuid());


            clog("creating calc");
            Calculation calc = CalculationModelFactory.createCalculation(p.getUUID(),
                    uuid, true, p.calculationEntity.getFormula(), target, x, y, z);

            clog("saving entity");
            EntityServiceFactory.getInstance().addUpdateEntity(u, entity);

            clog("saving calc");
            CalculationServiceFactory.getDaoInstance(u).addUpdateCalculation(calc);
            clog("saving done");


        }
    }

    private void createSubscriptions(User u, DataPoint p) {
        boolean enabled = p.isHighAlarmOn() || p.isLowAlarmOn() || p.isIdleAlarmOn();

        try {
            int delay = (p.alarmDelay > 5) ?  p.alarmDelay : 5;
            EntityName name = CommonFactoryLocator.getInstance().createName(p.name + " alert");
            if (p.alarmToFacebook != null && p.alarmToFacebook) {
                createSubscription(u, p, enabled, delay, name, SubscriptionType.anyAlert, SubscriptionNotifyMethod.facebook);
            }
            if (p.sendAlarmTweet != null && p.sendAlarmTweet) {
                createSubscription(u, p, enabled, delay, name, SubscriptionType.anyAlert, SubscriptionNotifyMethod.twitter);
            }
            if (p.sendAlarmIM != null && p.sendAlarmIM) {
                createSubscription(u, p, enabled, delay, name, SubscriptionType.anyAlert, SubscriptionNotifyMethod.instantMessage);
            }
            if (p.alarmToEmail != null && p.alarmToEmail) {
                createSubscription(u, p, enabled, delay, name, SubscriptionType.anyAlert, SubscriptionNotifyMethod.email);
            }
            if (p.idleAlarmOn != null && p.idleAlarmOn) {
                createSubscription(u, p, p.isIdleAlarmOn(),
                        delay, name, SubscriptionType.idle, SubscriptionNotifyMethod.email);
            }
            if (p.postToFacebook != null && p.postToFacebook) {
                createSubscription(u, p, enabled, delay, name, SubscriptionType.newValue, SubscriptionNotifyMethod.facebook);
            }
        } catch (Exception e) {
            clog(e.getMessage());
        }
    }

    private void createSubscription(User u, DataPoint p, boolean enabled, int delay, EntityName name,
                                    SubscriptionType type, SubscriptionNotifyMethod method ) throws NimbitsException {
        Subscription subscription = SubscriptionFactory.createSubscription(p.getUUID(),
                type,method ,delay,
                new Date(), p.sendAlertsAsJson, enabled);

        subscription.setUuid(UUID.randomUUID().toString());

        Entity entity = EntityModelFactory.createEntity(name, "",EntityType.subscription,
                ProtectionLevel.onlyMe,subscription.getUuid(), p.getUUID(), u.getUuid());
        clog("created subscription " + name.getValue());
        Entity r = EntityServiceFactory.getInstance().addUpdateEntity(u, entity);
        SubscriptionTransactionFactory.getInstance(u).subscribe(r, subscription);
    }

    private void doCategory(HttpServletRequest req) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            Entity categoryEntity = GsonFactory.getInstance().fromJson(req.getParameter(Parameters.json.getText()), EntityModel.class);
            User u = UserTransactionFactory.getDAOInstance().getUserByUUID(categoryEntity.getOwner());

            final Query catQuery = pm.newQuery(PointCatagory.class, "userFK==o && name==n");
            catQuery.declareParameters("String o, String n");
            List<PointCatagory> cList = (List<PointCatagory>) catQuery.execute(u.getId(), categoryEntity.getName().getValue());

            if (cList.size() > 0) {
                PointCatagory catagory = cList.get(0);


                final Query pointQuery = pm.newQuery(DataPoint.class, "catID==o");

                pointQuery.declareParameters("String o");

                List<DataPoint> points = (List<DataPoint>) pointQuery.execute(catagory.id);
                if (points.size() > 0) {

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

                        p.setFilterValue(p.getCompression());
                        p.setFilterType(FilterType.fixedHysteresis);
                        PointServiceFactory.getInstance().updatePoint(u, p);

                        ProtectionLevel protectionLevel = p.isPublic ? ProtectionLevel.everyone : ProtectionLevel.onlyMe;
                        EntityName name = CommonFactoryLocator.getInstance().createName(p.name);
                        Entity pointEntity = EntityModelFactory.createEntity(name, p.description, EntityType.point,
                                protectionLevel, p.getUUID(), parent, u.getUuid());
                        Entity r = EntityServiceFactory.getInstance().addUpdateEntity(u, pointEntity);
                        clog("created point " + name.getValue());
                        TaskFactoryLocator.getInstance().startUpgradeTask(Action.point,r );

                    }
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

                    Entity r = EntityServiceFactory.getInstance().addUpdateEntity(u, pointEntity);
                    clog("created diagram " + name.getValue());
                    // TaskFactoryLocator.getInstance().startUpgradeTask(Action.point,r );

                }



            }
        } catch (JsonSyntaxException e) {

            e.printStackTrace();
            clog(e.getMessage());

        } finally {
            pm.close();
        }



    }

    private void doUser(HttpServletRequest req) {
        Entity userEntity = GsonFactory.getInstance().fromJson(req.getParameter(Parameters.json.getText()), EntityModel.class);
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query userQuery = pm.newQuery(NimbitsUser.class, "uuid==o");

        try {
            List<NimbitsUser> users = (List<NimbitsUser>) userQuery.execute(userEntity.getEntity());
            if (users.size() > 0) {
                NimbitsUser user = users.get(0);


                final Query cQuery = pm.newQuery(PointCatagory.class, "userFK==o");
                cQuery.declareParameters("String o");
                List<PointCatagory> list = (List<PointCatagory>) cQuery.execute(user.getId());
                for (PointCatagory c : list) {
                    EntityName name = CommonFactoryLocator.getInstance().createName(c.name);
                    Entity entity = EntityModelFactory.createEntity(name, c.name, EntityType.category, ProtectionLevel.get(c.protectionLevel),
                            UUID.randomUUID().toString(), userEntity.getEntity(), userEntity.getEntity());
                    Entity r = entity;
                    if (! name.getValue().equals(N)) {
                        r = EntityServiceFactory.getInstance().addUpdateEntity(user, entity);
                    }
                    clog("created category " + name.getValue());
                    TaskFactoryLocator.getInstance().startUpgradeTask(Action.category,r );
                }


                if (user.getConnections() != null && user.getConnections().size() > 0) {
                    for (Long l : user.getConnections()) {
                        User connection = UserTransactionFactory.getDAOInstance().getNimbitsUserByID(l);
                        EntityName name = CommonFactoryLocator.getInstance().createName(connection.getEmail().getValue());
                        Entity entity = EntityModelFactory.createEntity(name, "",EntityType.userConnection, ProtectionLevel.onlyMe,
                                connection.getUuid(), user.getUuid(), user.getUuid());
                        clog("created connection " + name.getValue());
                        EntityServiceFactory.getInstance().addUpdateEntity(user, entity);

                    }
                }

            }
        } catch (NimbitsException e) {
            clog("ERROR" + e.getMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            pm.close();
        }

    }

    private void doStart() throws NimbitsException {
        int set = 0;
        int results = -1;
        while (results != 0) {
            final List<User> users = UserTransactionFactory.getInstance().getUsers(set, set + Const.CONST_QUERY_CHUNK_SIZE);
            //   final List<User> users = UserTransactionFactory.getInstance().getAllUsers("lastLoggedIn desc", 3000);
            results = users.size();

            set += Const.CONST_QUERY_CHUNK_SIZE;
            for (User u : users) {
                clog("Upgrading user: " + u.getEmail().getValue());
                Entity entity = EntityModelFactory.createEntity(u);
                Entity r = EntityServiceFactory.getInstance().addUpdateEntity(u, entity);
                TaskFactoryLocator.getInstance().startUpgradeTask(Action.user,r );

            }

        }
    }


}
