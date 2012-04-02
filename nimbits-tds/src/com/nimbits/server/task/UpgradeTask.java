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

import com.google.appengine.api.memcache.*;
import com.nimbits.PMF;
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
import com.nimbits.server.entity.*;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.orm.*;
import com.nimbits.server.orm.PointEntity;
import com.nimbits.server.orm.UserEntity;
import com.nimbits.server.point.PointServiceFactory;
import com.nimbits.server.subscription.SubscriptionTransactionFactory;
import com.nimbits.server.user.*;
import com.nimbits.server.value.RecordedValueTransactionFactory;
import com.nimbits.server.value.RecordedValueTransactions;
import com.nimbits.shared.Utils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.jdo.*;
import javax.jdo.Query;
import javax.jdo.Transaction;
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
                    doCategory2(req);
                    break;
                case point:
                    doPoint2(req);
                    break;
                case value:
                    doValue(req);
                    break;
                case calculation:
                    break;
                case diagram:
                    break;

                case subscribe:

                    break;
            }
        } catch (NimbitsException e) {
            clog(e.getMessage());
        }

    }
    protected static void doCategory2(final HttpServletRequest req) throws NimbitsException {
        clog("Upgrading categories");
        final PersistenceManager pm;
        pm = PMF.get().getPersistenceManager();
        final Query catQuery = pm.newQuery(PointCatagory.class);
        catQuery.setFilter("name != 'System' && name != 'Nimbits_Unsorted'");
        List<PointCatagory> list = (List<PointCatagory>) catQuery.execute();
        HashMap<Long, NimbitsUser> map = new HashMap<Long, NimbitsUser>(2500);
        HashMap<EntityName, Entity> emap = new HashMap<EntityName, Entity>(2500);
        NimbitsUser u;
        Entity userEntity;
        for (PointCatagory c : list) {
            try {
                if (c.getUserFK() != null) {


                    if (map.containsKey(c.getUserFK())) {
                        u = map.get(c.getUserFK());
                    }
                    else {
                        u = getLegUser(pm, c.getUserFK());
                        map.put(c.getUserFK(), u);
                    }

                    if (u != null) {
                        EntityName email = CommonFactoryLocator.getInstance().createName(u.getEmail().getValue(), EntityType.user);
                        EntityName name = CommonFactoryLocator.getInstance().createName(c.getName(), EntityType.category);
                        clog(email.getValue() + "  " + name.getValue());

                        if (emap.containsKey(email)) {
                            userEntity = emap.get(email);
                        }
                        else {
                            userEntity = EntityTransactionFactory.getDaoInstance(null).getEntityByName(email);
                            emap.put(email, userEntity);
                        }

                        ProtectionLevel protectionLevel = c.getProtectionLevel() == null ? ProtectionLevel.onlyMe : ProtectionLevel.get(c.getProtectionLevel());
                        User user = UserServiceFactory.getInstance().getUserByUUID(userEntity.getKey());
                        Entity newCat = EntityModelFactory.createEntity(name, "", EntityType.category,protectionLevel,
                                user.getKey(), user.getKey());

                        EntityServiceFactory.getInstance().addUpdateEntity(user, newCat);
                    }
                    else {
                        log.severe(c.getName() + "wasn't in the db");
                    }
                }
                else {
                    log.severe(c.getName() + "had a null user fk");
                }
            } catch (NimbitsException e) {

                log.severe(c.getName() + " caused an error");

                log.severe(e.getMessage());
            }

        }
        TaskFactory.getInstance().startUpgradeTask(Action.point, null, 0, 1000);


    }
    protected static void doPoint2(final HttpServletRequest req) throws NimbitsException {
        final PersistenceManager pm;
        pm = PMF.get().getPersistenceManager();
        final Query q = pm.newQuery(DataPoint.class);
        int s = Integer.valueOf(req.getParameter("s"));
        int e = Integer.valueOf(req.getParameter("e"));
        q.setRange(s, e);
        List<DataPoint> points = (List<DataPoint>) q.execute();

        clog("Upgrading points");

        HashMap<Long, NimbitsUser> map = new HashMap<Long, NimbitsUser>(2500);
        HashMap<EntityName, Entity> umap = new HashMap<EntityName, Entity>(2500);

        NimbitsUser u;
        Entity userEntity;
        PointCatagory cx = null;
        EntityName name = null;
        try {
            if (points.size() > 0) {
                for (DataPoint p : points) {


                    name = CommonFactoryLocator.getInstance().createName(p.getName(), EntityType.point);
                    Entity doneEntity;
                    boolean skip = false;

                    if (! Utils.isEmptyString(p.getTag()) ) {
                        doneEntity = EntityTransactionFactory.getDaoInstance(null).getEntityByKey(p.getTag());
                        if (doneEntity != null && doneEntity.getName().getValue().equals(name.getValue())) {
                            skip = true;
                        }
                    }
                    if (! skip) {
                        final ProtectionLevel protectionLevel = (p.getPublic() != null && p.getPublic()) ? ProtectionLevel.everyone : ProtectionLevel.onlyMe;

                        cx = getLegCat(pm, p.getCatID());
                        if (cx != null) {
                            if (map.containsKey(p.getUserFK())) {
                                u = map.get(p.getUserFK());
                            }
                            else {
                                u = getLegUser(pm, p.getUserFK());
                                map.put(p.getUserFK(), u);
                            }

                            if (u != null) {
                                EntityName email = CommonFactoryLocator.getInstance().createName(u.getEmail().getValue(), EntityType.user);



                                if (umap.containsKey(email)) {
                                    userEntity = umap.get(email);
                                }
                                else {
                                    userEntity = EntityTransactionFactory.getDaoInstance(null).getEntityByName(email);
                                    umap.put(email, userEntity);
                                }
                                User user = UserServiceFactory.getInstance().getUserByUUID(userEntity.getKey());
                                String parent;
                                if (cx.getName().equals(N)) {
                                    parent = userEntity.getKey();
                                }
                                else {
                                    final EntityName cname = CommonFactoryLocator.getInstance().createName(cx.getName(), EntityType.category);

                                    Entity cEntity = EntityTransactionFactory.getDaoInstance(user).getEntityByName(cname);
                                    if (cEntity != null) {
                                        parent = cEntity.getKey();
                                    }
                                    else {
                                        log.severe(cx.getName() + " was an old category that didn't get converted");
                                        parent = userEntity.getKey();
                                    }

                                }
                                final Entity pointEntity = EntityModelFactory.createEntity(name, p.getDescription(), EntityType.point,
                                        protectionLevel,  parent, userEntity.getKey());
                                final Entity newPoint = EntityServiceFactory.getInstance().addUpdateEntity(user, pointEntity);


                                log.info("new point: " + email.getValue() + " " + newPoint.getName().getValue());
                                String key = makePointEntity(p, newPoint);
                                Transaction tx = pm.currentTransaction();
                                tx.begin();
                                p.setTag(key);
                                tx.commit();
                                Entity npoint = EntityTransactionFactory.getDaoInstance(user).getEntityByKey(key);
                                if (npoint != null) {
                                   // TaskFactory.getInstance().startUpgradeTask(Action.value, npoint, 0, 1000);
                                }
                            }
                        }
                    }

                }
                TaskFactory.getInstance().startUpgradeTask(Action.point,null, s+1000, e+1000);
            }
            else {
                TaskFactory.getInstance().startUpgradeTask(Action.subscribe,null, 0, 1000);
                TaskFactory.getInstance().startUpgradeTask(Action.calculation, null, 0, 1000);
                TaskFactory.getInstance().startUpgradeTask(Action.diagram, null, 0, 1000);
            }
                } catch (NimbitsException ex) {
                    if (name != null) {
                        log.severe("Error caused by " + name );
                    }
                    log.severe(ex.getMessage());
                }





        pm.close();

    }
    protected static void doCalc(final HttpServletRequest req) {
        final PersistenceManager pm;
        pm = PMF.get().getPersistenceManager();

        try {

            final Query c = pm.newQuery(DataPoint.class);
            List<DataPoint> points = (List<DataPoint>) c.execute();
            for (DataPoint p : points) {
                if (p.getCalculationEntity() != null) {
                    CalculationEntity co = p.getCalculationEntity();
                    clog("doing calc: " + co.formula);
                    //  getLegUser()

                    //CalculationModelFactory.createCalculation()



                }
            }





        } finally {
            pm.close();
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
            clog("doing value");
            final Entity pointEntity = GsonFactory.getInstance().fromJson(req.getParameter(Parameters.json.getText()), EntityModel.class);
            final User u = UserTransactionFactory.getDAOInstance().getUserByKey(pointEntity.getOwner());
            if (u != null) {
                clog(u.getEmail().getValue() + pointEntity.getName().getValue());
                final NimbitsUser nu = getLegUser(pm, u.getEmail().getValue());
                final DataPoint leg = getLegPoint(pm, nu.getId(), pointEntity.getName().getValue());
                if (leg != null) {

                    //  final Query c = pm.newQuery(DataPoint.class);
                    //  c.setFilter("uuid==o");
                    //  c.declareParameters("String o");
                    //  final List<DataPoint> pList = (List<DataPoint>) c.execute(pointEntity.getEntity());

                    //  final Point point = pList.get(0);
                    final Point point = PointServiceFactory.getInstance().getPointByKey(pointEntity.getKey());
                    final RecordedValueTransactions old =  RecordedValueTransactionFactory.getLegacyInstance(leg);
                    final RecordedValueTransactions dao =  RecordedValueTransactionFactory.getDaoInstance(point);
                    final Timespan timespan = TimespanModelFactory.createTimespan(leg.getCreateDate(), new Date());

                    int cx = -1;
                    int seg = 0;
                    while (cx != 0) {

                        final List<Value> values= old.getDataSegment(timespan,seg, seg + 1000 );
                        cx = values.size();
                        if (values.size() > 0) {
                            dao.recordValues(values);
                            clog("Saved " + values.size() + " values");
                        }

                        seg += 1000;


                    }
                }
                else {
                    log.severe(pointEntity.getName() + " didn't exist");
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
            final List<DataPoint> pList = (List<DataPoint>) c.execute(legecy.getUUID());
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
                TaskFactory.getInstance().startUpgradeTask(Action.value, null,0 , 1000);

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
        boolean enabled = p.isHighAlarmOn() ||p.isLowAlarmOn() || p.getIdleAlarmOn();

        try {
            int delay = (p.getAlarmDelay() > 5) ?  p.getAlarmDelay() : 30;
            EntityName name = CommonFactoryLocator.getInstance().createName(p.getName() + " alert");
            if ( p.getAlarmToFacebook()) {
                createSubscription(u, x, p, enabled, delay, name, SubscriptionType.anyAlert, SubscriptionNotifyMethod.facebook);
            }
            if ( p.getSendAlarmTweet()) {
                createSubscription(u, x, p, enabled, delay, name, SubscriptionType.anyAlert, SubscriptionNotifyMethod.twitter);
            }
            if (p.getSendAlarmIM() != null && p.getSendAlarmIM()) {
                createSubscription(u, x, p, enabled, delay, name, SubscriptionType.anyAlert, SubscriptionNotifyMethod.instantMessage);
            }
            if (p.getAlarmToEmail() != null && p.getAlarmToEmail()) {
                createSubscription(u, x, p, enabled, delay, name, SubscriptionType.anyAlert, SubscriptionNotifyMethod.email);
            }
            if (p.getIdleAlarmOn() != null && p.getIdleAlarmOn()) {
                createSubscription(u, x, p, p.getIdleAlarmOn(),
                        delay, name, SubscriptionType.idle, SubscriptionNotifyMethod.email);
            }
            if (p.getPostToFacebook() != null && p.getPostToFacebook()) {
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
                new Date(), legacy.getSendAlertsAsJson(), enabled);

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

                final Query pointQuery = pm.newQuery(DataPoint.class);
                clog("B");

                pointQuery.setFilter("catID==i");
                pointQuery.declareParameters("Long i");
                clog("Getting Cat ID : " + legecyCat.getId()  + " " + legecyCat.getName());
                final List<DataPoint> points = (List<DataPoint>) pointQuery.execute(legecyCat.getId());

                clog("d");


                if (points.size() > 0) {

                    for (final DataPoint p : points) {
                        log.info(p.getName());


                        String parent;
                        if (legecyCat.getName().equals(N)) {
                            parent = u.getKey();

                        }
                        else {
                            parent = categoryEntity.getKey();
                        }
                        if (Utils.isEmptyString(parent)) {
                            parent = u.getKey();
                        }


                        final ProtectionLevel protectionLevel = (p.getPublic() != null && p.getPublic()) ? ProtectionLevel.everyone : ProtectionLevel.onlyMe;
                        final EntityName name = CommonFactoryLocator.getInstance().createName(p.getName());
                        final Entity pointEntity = EntityModelFactory.createEntity(name, p.getDescription(), EntityType.point,
                                protectionLevel,  parent, u.getKey());
                        final Entity r = EntityServiceFactory.getInstance().addUpdateEntity(u, pointEntity);

                        String key = makePointEntity(p, r);

                        Transaction tx = pm.currentTransaction();
                        tx.begin();
                        p.setTag(key);
                        tx.commit();
                        clog("created point " + name.getValue() + " key = " + key);

                        TaskFactory.getInstance().startUpgradeTask(Action.point, null,0, 1000);

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

    private static String makePointEntity(DataPoint p, Entity r) {
        final PersistenceManager pm1 = PMF.get().getPersistenceManager();
        PointEntity pe = new PointEntity(r);
        log.info("Creating new Point Entity");
        pe.setFilterType(FilterType.fixedHysteresis);
        pe.setFilterValue(p.getCompression());
        pe.setExpire(p.getExpire());
        pe.setHighAlarm(p.getHighAlarm());
        pe.setHighAlarmOn(p.isHighAlarmOn());
        pe.setLowAlarm(p.getLowAlarm());
        pe.setLowAlarmOn(p.getLowAlarmOn());
        pe.setIdleAlarmOn(p.getSendAlarmIM());
        pe.setIdleSeconds(p.getIdleSeconds());
        pe.setIdleAlarmOn(p.getIdleAlarmOn());
        pe.setUnit(p.getUnit());
        pm1.makePersistent(pe);
        String key = pe.getKey();
        pm1.close();
        return key;
    }

    protected static void doDiagram(PointCatagory catagory, Entity newCat, User u ) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final Query d = pm.newQuery(DiagramEntity.class);
            d.setFilter("categoryFk==o");
            d.declareParameters("Long o");
            List<DiagramEntity> diagrams = (List<DiagramEntity>) d.execute(catagory.getId());
            for (DiagramEntity diagramEntity : diagrams) {
                String parent;
                if (catagory.getName().equals(N)) {
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
                log.info(c.getName());
                final EntityName name = CommonFactoryLocator.getInstance().createName(c.getName());
                ProtectionLevel protectionLevel = c.getProtectionLevel() == null ? ProtectionLevel.onlyMe : ProtectionLevel.get(c.getProtectionLevel());
                final Entity entity = EntityModelFactory.createEntity(name, c.getName(), EntityType.category, protectionLevel,
                        userEntity.getKey(), userEntity.getKey());
                Entity r = entity;
                if (! name.getValue().equals(N)) {
                    r = EntityServiceFactory.getInstance().addUpdateEntity(user, entity);
                }
                log.info("created category " + name.getValue());
                TaskFactory.getInstance().startUpgradeTask(Action.category, null, 0, 1000);
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
    private static DataPoint getLegPoint(PersistenceManager pm, Long l) {

        return pm.getObjectById(DataPoint.class, l);
    }
    private static DataPoint getLegPoint(PersistenceManager pm, Long owner, String name) {
        final Query cq = pm.newQuery(DataPoint.class);
        cq.setFilter("name==o && userFK==l");
        cq.declareParameters("String o, Long l");
        List<DataPoint> list =  ((List<DataPoint>) cq.execute(name, owner)) ;
        if (list.size() > 0) {
            return list.get(0);
        }
        else {
            log.severe("get leg point could not find " + name);
            return null;
        }
    }


    private static PointCatagory getLegCat(PersistenceManager pm, Long l) {
        try {
            return pm.getObjectById(PointCatagory.class, l);
        }
        catch (JDOObjectNotFoundException ex) {
            return null;
        }
    }

    protected static NimbitsUser getLegUser(PersistenceManager pm, Long l) {
        try {
            return pm.getObjectById(NimbitsUser.class, l);

        }
        catch (Exception ex) {
            return null;
        }
//        final Query cq = pm.newQuery(NimbitsUser.class);
//        cq.setFilter("id==o");
//        cq.declareParameters("Long o");
//        return ((List<NimbitsUser>) cq.execute(l)).get(0);
    }



    private static NimbitsUser getLegUser(PersistenceManager pm, String email) {
        final Query cq = pm.newQuery(NimbitsUser.class);
        cq.setFilter("email==o");
        cq.declareParameters("String o");
        return ((List<NimbitsUser>) cq.execute(email)).get(0);
    }
    protected static int doStart() throws NimbitsException {

        int results = -1;

        List<NimbitsUser> users;

        final PersistenceManager pm = PMF.get().getPersistenceManager();


        users = (List<NimbitsUser>) pm.newQuery(NimbitsUser.class).execute();

        //  users = UserTransactionFactory.getInstance().getUsers();
        //   final List<User> users = UserTransactionFactory.getInstance().getAllUsers("lastLoggedIn desc", 3000);
        results = users.size();
        clog(results + " users found");
        for (final NimbitsUser u : users) {
            try {
                clog("Upgrading user: " + u.getEmail().getValue());

                final Entity entity = EntityModelFactory.createEntity(u.getName(), "", EntityType.user, ProtectionLevel.onlyMe, "", "");
                final Entity r = EntityTransactionFactory.getDaoInstance(null).addUpdateEntity(entity);
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
            } catch (NimbitsException e) {
                log.severe(u.getEmail() + " " + e.getMessage());
            }


        }
        TaskFactory.getInstance().startUpgradeTask(Action.category, null, 0, 10000);
        return results;
    }


}
