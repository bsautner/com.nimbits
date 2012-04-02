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

import com.nimbits.PMF;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.subscription.SubscriptionFactory;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.timespan.TimespanModelFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.entity.EntityServiceFactory;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transactions.orm.PointEntity;
import com.nimbits.server.transactions.orm.UserEntity;
import com.nimbits.server.transactions.orm.legacy.DataPoint;
import com.nimbits.server.transactions.orm.legacy.DiagramEntity;
import com.nimbits.server.transactions.orm.legacy.NimbitsUser;
import com.nimbits.server.transactions.orm.legacy.PointCatagory;
import com.nimbits.server.point.PointServiceFactory;
import com.nimbits.server.subscription.SubscriptionTransactionFactory;
import com.nimbits.server.user.UserTransactionFactory;
import com.nimbits.server.value.RecordedValueTransactionFactory;
import com.nimbits.server.value.RecordedValueTransactions;
import com.nimbits.shared.Utils;
import org.apache.commons.lang3.exception.ExceptionUtils;

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
//@SuppressWarnings({"deprecation", "unchecked"})
public class UpgradeTask  extends HttpServlet

{
    private final static String N= "Nimbits_Unsorted";

    private static final Logger log = Logger.getLogger(UpgradeTask.class.getName());
    private static final long serialVersionUID = 1L;

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {

        final Action action = Action.get(req.getParameter(Parameters.action.getText()));
        try {
            switch (action) {
                case start:


                    doStart();
                    break;
                case user:


                   doUser(req);
                    break;
                case category:
                   doCategory(req);

                    break;
                case point:

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

    private static void clog(final String string) {
        log.info(string);
    }
//
    protected static void doValue(final HttpServletRequest req) {
        final PersistenceManager pm;
        pm = PMF.get().getPersistenceManager();

        try {
            final Entity pointEntity = GsonFactory.getInstance().fromJson(req.getParameter(Parameters.json.getText()), EntityModel.class);
            final User u = UserTransactionFactory.getDAOInstance().getUserByKey(pointEntity.getOwner());

          //  final Query c = pm.newQuery(DataPoint.class);
          //  c.setFilter("uuid==o");
          //  c.declareParameters("String o");
          //  final List<DataPoint> pList = (List<DataPoint>) c.execute(pointEntity.getEntity());
            final DataPoint legecy =  GsonFactory.getInstance().fromJson(req.getParameter(Parameters.point.getText()), DataPoint.class);

            if (legecy != null) {
              //  final Point point = pList.get(0);
                final Point point = PointServiceFactory.getInstance().getPointByKey(pointEntity.getKey());
                final RecordedValueTransactions old =  RecordedValueTransactionFactory.getLegacyInstance(legecy);
                final RecordedValueTransactions dao =  RecordedValueTransactionFactory.getDaoInstance(point);
                final Timespan timespan = TimespanModelFactory.createTimespan(legecy.getCreateDate(), new Date());

                int cx = -1;
                int seg = 0;
                while (cx != 0) {

                    final List<Value> values= old.getDataSegment(timespan,seg, seg + 1000 );
                    cx = values.size();
                    dao.recordValues(values);
                    seg += 1000;
                }

            }

        } catch (NimbitsException e) {
            log.severe(e.getMessage());
        } finally {
            pm.close();
        }

    }
//
//
//    DataPoint getPoint(long id) {
//        final PersistenceManager pm = PMF.get().getPersistenceManager();
//        final Query c = pm.newQuery(DataPoint.class);
//        try {
//            c.setFilter( "id==o");
//            c.declareParameters("Long o");
//            final List<DataPoint> pList = (List<DataPoint>) c.execute(id);
//            if (pList.size() > 0) {
//                //create subscriptions
//
//                DataPoint p = pList.get(0);
//                return p;
//            }
//            else {
//                return null;
//            }
//        } finally {
//            pm.close();
//        }
//
//
//    }
//
//
    private void doPoint(final HttpServletRequest req) {

        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Entity pointEntity = GsonFactory.getInstance().fromJson(req.getParameter(Parameters.json.getText()), EntityModel.class);
            final DataPoint legecy =  GsonFactory.getInstance().fromJson(req.getParameter(Parameters.point.getText()), DataPoint.class);


            final User u = UserTransactionFactory.getDAOInstance().getUserByKey(pointEntity.getOwner());
            clog(req.getParameter(Parameters.json.getText()));
            if (u==null) {
                clog("User not found" + pointEntity.getOwner());
            }
            clog(u.getEmail().getValue());
            final Query c = pm.newQuery(DataPoint.class);
            c.setFilter( "uuid==o");
            c.declareParameters("String o");
            final List<DataPoint> pList = (List<DataPoint>) c.execute(legecy.uuid);
            if (pList.size() > 0) {
                //create subscriptions

                DataPoint p = pList.get(0);
                clog("Started creating subscritptions");
                createSubscriptions(u, legecy, pointEntity);
                clog("fixing calculations");
            //   createCalcs(pm, u, pointEntity, legecy);
//                if (p.dataPointIntelligenceEntity != null) {
//                    final EntityName name = CommonFactoryLocator.getInstance().createName(p.name + "Intelligence");
//
//                    final DataPoint target =  getPoint(p.dataPointIntelligenceEntity.targetPointId);
//
//                    if (target != null) {
//                        final Entity iEntity = EntityModelFactory.createEntity(name,"",EntityType.intelligence,
//                                ProtectionLevel.onlyMe, p.getEntity(), u.getKey() );
//                        Entity result=  EntityServiceFactory.getInstance().addUpdateEntity(u, iEntity);
//
//                        final Intelligence i = IntelligenceModelFactory.createIntelligenceModel(uuid, p.dataPointIntelligenceEntity.getEnabled(),
//                                p.dataPointIntelligenceEntity.getResultTarget(), target.getUUID(), p.dataPointIntelligenceEntity.getInput(),
//                                p.dataPointIntelligenceEntity.getNodeId(), p.dataPointIntelligenceEntity.getResultsInPlainText(),
//                                p.getEntity());
//                        IntelligenceServiceFactory.getDaoInstance().addUpdateIntelligence(i);
//                    }
//                }
                TaskFactory.getInstance().startUpgradeTask(Action.value,pointEntity, legecy );

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

//    private static void createCalcs(final PersistenceManager pm, final User u, final Entity e, DataPoint p) throws NimbitsException {
//        if (p.calculationEntity != null) {
//
//            String x = null, y = null, z = null, target = null;
//
//            if (p.calculationEntity.x != null && p.calculationEntity.x > 0) {
//                try {
//                    final PointEntity px = pm.getObjectById(PointEntity.class, p.calculationEntity.x);
//                    if (px != null) {
//                        x=(px.getUUID());
//                    }
//                } catch (JDOObjectNotFoundException ex) {
//                    clog("Point not found");
//
//                }
//            }
//            if (p.calculationEntity.y != null && p.calculationEntity.y > 0) {
//                try {
//                    final PointEntity py =  pm.getObjectById(PointEntity.class, p.calculationEntity.y);
//                    if (py != null) {
//                        y=(py.getUUID());
//                    }
//                } catch (JDOObjectNotFoundException ex) {
//                    clog("Point not found");
//
//                }
//            }
//            if (p.calculationEntity.z != null && p.calculationEntity.z > 0) {
//                try {
//                    final PointEntity pz =  pm.getObjectById(PointEntity.class, p.calculationEntity.z);
//
//                    if (pz != null) {
//                        z=(pz.getUUID());
//                    }
//                } catch (JDOObjectNotFoundException ex) {
//                    clog("Point not found");
//
//                }
//            }
//            if (p.calculationEntity.target != null && p.calculationEntity.target > 0) {
//                try {
//                    final PointEntity pt = pm.getObjectById(PointEntity.class, p.calculationEntity.target);
//                    if (pt != null && ! pt.getUUID().equals(p.getEntity())) {
//                        target= (pt.getUUID());
//                    }
//                } catch (JDOObjectNotFoundException ex) {
//                    clog("Point not found");
//
//                }
//            }
//            else {
//                // p.calculationEntity.setEnabled(false);
//            }
//
//            pm.close();
//            clog("Done getting point vars");
//            EntityName name = CommonFactoryLocator.getInstance().createName(p.name + " calc");
//            String uuid = UUID.randomUUID().toString();
//            Entity entity = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe,
//                    uuid, p.getEntity(), u.getKey());
//
//
//            clog("creating calc");
//            Calculation calc = CalculationModelFactory.createCalculation(p.getEntity(),
//                    uuid, true, p.calculationEntity.getFormula(), target, x, y, z);
//
//            clog("saving entity");
//            EntityServiceFactory.getInstance().addUpdateEntity(u, entity);
//
//            clog("saving calc");
//            CalculationServiceFactory.getDaoInstance(u).addUpdateCalculation(calc);
//            clog("saving done");
//
//
//        }
//    }
//
    private void createSubscriptions(User u,  DataPoint p, Entity x) {
        boolean enabled = p.isHighAlarmOn() ||p.isLowAlarmOn() || p.idleAlarmOn;

        try {
            int delay = (p.alarmDelay > 5) ?  p.alarmDelay : 5;
            EntityName name = CommonFactoryLocator.getInstance().createName(p.name + " alert");
            if (p.alarmToFacebook != null && p.alarmToFacebook) {
                createSubscription(u, x, p, enabled, delay, name, SubscriptionType.anyAlert, SubscriptionNotifyMethod.facebook);
            }
            if (p.sendAlarmTweet != null && p.sendAlarmTweet) {
                createSubscription(u, x, p, enabled, delay, name, SubscriptionType.anyAlert, SubscriptionNotifyMethod.twitter);
            }
            if (p.sendAlarmIM != null && p.sendAlarmIM) {
                createSubscription(u, x, p, enabled, delay, name, SubscriptionType.anyAlert, SubscriptionNotifyMethod.instantMessage);
            }
            if (p.alarmToEmail != null && p.alarmToEmail) {
                createSubscription(u, x, p, enabled, delay, name, SubscriptionType.anyAlert, SubscriptionNotifyMethod.email);
            }
            if (p.idleAlarmOn != null && p.idleAlarmOn) {
                createSubscription(u, x, p, p.idleAlarmOn,
                        delay, name, SubscriptionType.idle, SubscriptionNotifyMethod.email);
            }
            if (p.postToFacebook != null && p.postToFacebook) {
                createSubscription(u,x,  p, enabled, delay, name, SubscriptionType.newValue, SubscriptionNotifyMethod.facebook);
            }
        } catch (Exception ex) {
            clog(ex.getMessage());
        }
    }
//
    private void createSubscription(User u, Entity p,DataPoint legacy, boolean enabled, int delay, EntityName name,
                                    SubscriptionType type, SubscriptionNotifyMethod method ) throws NimbitsException {


        Subscription subscription = SubscriptionFactory.createSubscription(p.getKey(),
                type, method, delay,
                new Date(), legacy.sendAlertsAsJson, enabled);

        subscription.setUuid(UUID.randomUUID().toString());

        Entity entity = EntityModelFactory.createEntity(name, "",EntityType.subscription,
                ProtectionLevel.onlyMe,subscription.getKey(), p.getKey(), u.getKey());
        clog("created subscription " + name.getValue());
        Entity r = EntityServiceFactory.getInstance().addUpdateEntity(u, entity);
        SubscriptionTransactionFactory.getInstance(u).subscribe(r, subscription);
    }
//
    protected static void doCategory(final HttpServletRequest req) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Entity categoryEntity = GsonFactory.getInstance().fromJson(req.getParameter(Parameters.json.getText()), EntityModel.class);


            final User u = UserTransactionFactory.getDAOInstance().getUserByKey(categoryEntity.getOwner());
            log.info(u.getEmail().getValue());
            final Query catQuery = pm.newQuery(PointCatagory.class);
            catQuery.setFilter("userFK==o && name==n");
            catQuery.declareParameters("Long o, String n");
            NimbitsUser lu = getLegUser(pm, u.getEmail().getValue());
            final List<PointCatagory> cList = (List<PointCatagory>) catQuery.execute(lu.getId(), categoryEntity.getName().getValue());




            log.info( categoryEntity.getName().getValue());
            if (cList.size() > 0) {
                final PointCatagory legecyCat = cList.get(0);
                clog("A");

                final Query pointQuery = pm.newQuery(com.nimbits.server.transactions.orm.legacy.DataPoint.class);
                clog("B");

                pointQuery.setFilter("catID==i");
                pointQuery.declareParameters("Long i");
                clog("A");
                final List<com.nimbits.server.transactions.orm.legacy.DataPoint> points = (List<com.nimbits.server.transactions.orm.legacy.DataPoint>) pointQuery.execute(legecyCat.id);

                clog("d");


                if (points.size() > 0) {

                    for (final com.nimbits.server.transactions.orm.legacy.DataPoint p : points) {
                        log.info(p.name);
                        String parent;
                        if (legecyCat.name.equals(N)) {
                            parent = u.getKey();

                        }
                        else {
                            parent = categoryEntity.getKey();
                        }
                        if (Utils.isEmptyString(parent)) {
                            parent = u.getKey();
                        }


                        final ProtectionLevel protectionLevel = (p.isPublic != null && p.isPublic) ? ProtectionLevel.everyone : ProtectionLevel.onlyMe;
                        final EntityName name = CommonFactoryLocator.getInstance().createName(p.name);
                        final Entity pointEntity = EntityModelFactory.createEntity(name, p.description, EntityType.point,
                                protectionLevel,  parent, u.getKey());
                        final Entity r = EntityServiceFactory.getInstance().addUpdateEntity(u, pointEntity);
                        PointEntity pe = new PointEntity(r);

                        pe.setFilterType(FilterType.fixedHysteresis);
                        pe.setFilterValue(p.getCompression());
                        pe.setExpire(p.getExpire());
                        pe.setHighAlarm(p.getHighAlarm());
                        pe.setHighAlarmOn(p.isHighAlarmOn());
                        pe.setLowAlarm(p.getLowAlarm());
                        pe.setLowAlarmOn(p.lowAlarmOn);
                        pe.setIdleAlarmOn(p.getSendAlarmIM());
                        pe.setIdleSeconds(p.idleSeconds);
                        pe.setIdleAlarmOn(p.idleAlarmOn);
                        pe.setUnit(p.unit);
                        pm.makePersistent(pe);


                        clog("created point " + name.getValue());
                        TaskFactory.getInstance().startUpgradeTask(Action.point,r, p );

                    }
                }
                doDiagram(legecyCat,categoryEntity,  u);



            }
        } catch (Exception e) {

            e.printStackTrace();
            log.severe(e.getMessage());
            log.severe(ExceptionUtils.getStackTrace(e));
        } finally {
            pm.close();
        }



    }

    protected static void doDiagram(PointCatagory catagory, Entity newCat, User u ) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final Query d = pm.newQuery(DiagramEntity.class);
            d.setFilter("categoryFk==o");
            d.declareParameters("Long o");
            List<DiagramEntity> diagrams = (List<DiagramEntity>) d.execute(catagory.id);
            for (DiagramEntity diagramEntity : diagrams) {
                String parent;
                if (catagory.name.equals(N)) {
                    parent = u.getKey();
                }
                else {
                    parent = newCat.getKey();
                }
                if (Utils.isEmptyString(parent)) {
                    parent = u.getKey();
                }
                ProtectionLevel protectionLevel = ProtectionLevel.get(diagramEntity.protectionLevel);
                EntityName name = CommonFactoryLocator.getInstance().createName(diagramEntity.name + ".svg");
                Entity e = EntityModelFactory.createEntity(name, "", EntityType.file,
                        protectionLevel, parent, u.getKey(), diagramEntity.blobKey.getKeyString());

                Entity r = EntityServiceFactory.getInstance().addUpdateEntity(u, e);
                clog("created diagram " + name.getValue());
                // TaskFactoryLocator.getInstance().startUpgradeTask(Action.point,r );

            }
        } catch (NimbitsException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            pm.close();
        }

    }

    protected static int doUser(final HttpServletRequest req) {
        final Entity userEntity = GsonFactory.getInstance().fromJson(req.getParameter(Parameters.json.getText()), EntityModel.class);
        final PersistenceManager pm = PMF.get().getPersistenceManager();




        int categories = 0;

        try {

            final UserEntity user = pm.getObjectById(UserEntity.class, userEntity.getKey());

            final Query legUserQuery = pm.newQuery(NimbitsUser.class);
            legUserQuery.setFilter("email==o");
            legUserQuery.declareParameters("String o");
            List<NimbitsUser> legu = (List<NimbitsUser>) legUserQuery.execute(user.getEmail().getValue());
            NimbitsUser lu = legu.get(0);



                log.info(user.getEmail().getValue());

                final Query cQuery = pm.newQuery(PointCatagory.class);
                cQuery.setFilter("userFK==o");
                cQuery.declareParameters("Long o");




                final List<PointCatagory> list = (List<PointCatagory>) cQuery.execute(lu.getId());
                categories = list.size();
                for (final PointCatagory c : list) {
                    log.info(c.name);
                    final EntityName name = CommonFactoryLocator.getInstance().createName(c.name);
                    ProtectionLevel protectionLevel = c.protectionLevel == null ? ProtectionLevel.onlyMe : ProtectionLevel.get(c.protectionLevel);
                    final Entity entity = EntityModelFactory.createEntity(name, c.name, EntityType.category, protectionLevel,
                            userEntity.getKey(), userEntity.getKey());
                    Entity r = entity;
                    if (! name.getValue().equals(N)) {
                        r = EntityServiceFactory.getInstance().addUpdateEntity(user, entity);
                    }
                    log.info("created category " + name.getValue());
                    TaskFactory.getInstance().startUpgradeTask(Action.category,r, null );
                }


                if (lu.getConnections() != null && lu.getConnections().size() > 0) {
                    for (final Long l : lu.getConnections()) {
                        NimbitsUser connection = getLegUser(pm, l);



                        final EntityName name = CommonFactoryLocator.getInstance().createName(connection.getEmail().getValue());
                        final Entity entity = EntityModelFactory.createEntity(name, "",EntityType.userConnection, ProtectionLevel.onlyMe,
                                user.getKey(), user.getKey());
                        clog("created connection " + name.getValue());
                        EntityServiceFactory.getInstance().addUpdateEntity(user, entity);

                    }
                }


            return categories;
        } catch (Exception e) {
            log.severe("ERROR" + e.getMessage());
            return -1;

        } finally {
            pm.close();
        }

    }

    private static NimbitsUser getLegUser(PersistenceManager pm, Long l) {
        final Query cq = pm.newQuery(NimbitsUser.class);
        cq.setFilter("id==o");
        cq.declareParameters("Long o");
        return ((List<NimbitsUser>) cq.execute(l)).get(0);
    }
    private static NimbitsUser getLegUser(PersistenceManager pm, String email) {
        final Query cq = pm.newQuery(NimbitsUser.class);
        cq.setFilter("email==o");
        cq.declareParameters("String o");
        return ((List<NimbitsUser>) cq.execute(email)).get(0);
    }
    protected static int doStart() throws NimbitsException {
        int set = 0;
        int results = -1;
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final List<NimbitsUser> users;

        users = (List<NimbitsUser>) pm.newQuery(NimbitsUser.class).execute();

      //  users = UserTransactionFactory.getInstance().getUsers();
        //   final List<User> users = UserTransactionFactory.getInstance().getAllUsers("lastLoggedIn desc", 3000);
        results = users.size();

        set += Const.CONST_QUERY_CHUNK_SIZE;
        for (final NimbitsUser u : users) {
            //  clog("Upgrading user: " + u.getEmail().getValue());

            final Entity entity = EntityModelFactory.createEntity(u.getName(), "", EntityType.user, ProtectionLevel.onlyMe, "", "");
            final Entity r = EntityServiceFactory.getInstance().addUpdateEntity(entity);
            UserEntity userEntity = new UserEntity(r, u.getEmail());
            userEntity.setFacebookID(u.getFacebookID());
            userEntity.setFacebookToken(u.getFacebookToken());
            userEntity.setLastLoggedIn(u.getLastLoggedIn());
            userEntity.setSecret(u.getSecret());
            userEntity.setTwitterToken(u.getTwitterToken());
            userEntity.setTwitterTokenSecret(u.getTwitterTokenSecret());
            userEntity.setFacebookID(u.getFacebookID());
            userEntity.setDateCreated(u.getDateCreated());

            pm.makePersistent(userEntity);
            TaskFactory.getInstance().startUpgradeTask(Action.user,r, null );

        }

        return results;
    }


}
