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
import com.google.apphosting.api.*;
import com.nimbits.PMF;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.calculation.*;
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
import com.nimbits.server.calculation.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.orm.*;
import com.nimbits.server.orm.PointEntity;
import com.nimbits.server.orm.UserEntity;
import com.nimbits.server.point.*;
import com.nimbits.server.subscription.SubscriptionTransactionFactory;
import com.nimbits.server.user.*;
import com.nimbits.server.value.RecordedValueTransactionFactory;
import com.nimbits.server.value.RecordedValueTransactions;
import com.nimbits.shared.Utils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.datanucleus.exceptions.*;

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
        MemcacheServiceFactory.getMemcacheService().clearAll();
        final Action action = Action.get(req.getParameter(Parameters.action.getText()));
        try {
            switch (action) {
                case start:
                    doStart(req);
                    break;
                case user:
                    doConnections(req);
                    break;
                case category:
                    doCategory2(req);
                    break;
                case point:
                    doPoint2(req);
                    break;
                case value:
                   // doValue(req);
                    break;
                case calculation:
                    doCalc3(req);
                    break;
                case diagram:
                    doDiagram();
                    break;

                case subscribe:
                    doSubscriptions(req);
                    break;

                case record:
                    startValue(req) ;

            }
        } catch (NimbitsException e) {
            clog(e.getMessage());
        }
        MemcacheServiceFactory.getMemcacheService().clearAll();
    }
    protected static void doCategory2(final HttpServletRequest req) throws NimbitsException {
        clog("Upgrading categories");
        final PersistenceManager pm;
        pm = PMF.get().getPersistenceManager();


        int s = Integer.valueOf(req.getParameter("s"));
        final Query catQuery = pm.newQuery(PointCatagory.class);
        catQuery.setFilter("name != 'System' && name != 'Nimbits_Unsorted'");
        catQuery.setRange(s, s+100);



        List<PointCatagory> list = (List<PointCatagory>) catQuery.execute();
        HashMap<Long, NimbitsUser> map = new HashMap<Long, NimbitsUser>(s);
        HashMap<EntityName, Entity> emap = new HashMap<EntityName, Entity>(s);
        NimbitsUser u;
        Entity userEntity;
        if (list.size() > 0) {


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
                            clog("Upgrading category: " + email.getValue() + "  " + name.getValue());

                            if (emap.containsKey(email)) {
                                userEntity = emap.get(email);
                            }
                            else {
                                userEntity = EntityTransactionFactory.getDaoInstance(null).getEntityByName(email);
                                emap.put(email, userEntity);
                            }
                            if (userEntity != null) {
                                clog("found upgraded user entity: " + email.getValue());
                                User user = UserTransactionFactory.getDAOInstance().getUserByKey(userEntity.getKey());
                                if (user != null) {

                                    Entity categoryEntity = EntityTransactionFactory.getDaoInstance(user).getEntityByName(name, EntityType.category);
                                    if (categoryEntity == null) {
                                        ProtectionLevel protectionLevel = c.getProtectionLevel() == null ? ProtectionLevel.onlyMe : ProtectionLevel.get(c.getProtectionLevel());

                                        Entity newCat = EntityModelFactory.createEntity(name, "", EntityType.category,protectionLevel,
                                                user.getKey(), user.getKey());

                                        EntityServiceFactory.getInstance().addUpdateEntity(user, newCat);
                                    }
                                    else {
                                        clog("Skipping " + name.getValue() + " already processed");
                                    }


                                }
                                else {
                                    log.severe(email.getValue() + " didn't have a user object, but did have an entity");
                                }




                            }
                            else {
                                log.severe(email.getValue() + " didn't have an entity");
                            }



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
            TaskFactory.getInstance().startUpgradeTask(Action.category, null, s+100 );
        }
        else {

            log.info("Completed category upgrade " + s);
            TaskFactory.getInstance().startUpgradeTask(Action.point, null, 0 );
        }



    }
    protected static void doPoint2(final HttpServletRequest req) throws NimbitsException {
        final PersistenceManager pm;
        pm = PMF.get().getPersistenceManager();
        final Query q = pm.newQuery(DataPoint.class);
        int s = Integer.valueOf(req.getParameter("s"));

        q.setRange(s, s+100);
        List<DataPoint> points = (List<DataPoint>) q.execute();

        clog("Upgrading points");

        HashMap<Long, NimbitsUser> map = new HashMap<Long, NimbitsUser>(s);

        NimbitsUser legacyUser;
        Entity userEntity ;
        PointCatagory cx = null;


        if (points.size() > 0) {
            clog("upgrading points " + s + " to " + (s+100));
            for (DataPoint p : points) {
                try {
                    EntityName name = CommonFactoryLocator.getInstance().createName(p.getName(), EntityType.point);
                    final ProtectionLevel protectionLevel = (p.getPublic() != null && p.getPublic()) ? ProtectionLevel.everyone : ProtectionLevel.onlyMe;
                    legacyUser = getLegUser(pm, p.getUserFK());
                    if (legacyUser != null) {
                        userEntity = EntityTransactionFactory.getDaoInstance(null).getEntityByName(legacyUser.getName(), EntityType.user);
                        User user = UserTransactionFactory.getDAOInstance().getUserByKey(userEntity.getKey());

                        if (user != null) {
                            cx = getLegCat(pm, p.getCatID());
                            if (cx != null) {

                                Entity completedPoint = EntityTransactionFactory.getDaoInstance(user).getEntityByName(name, EntityType.point);
                                if (completedPoint == null) {

                                    String parent;
                                    if (cx.getName().equals(N) || cx.getName().equals("System")) {
                                        parent = userEntity.getKey();
                                    }
                                    else {
                                        final EntityName cname = CommonFactoryLocator.getInstance().createName(cx.getName(), EntityType.category);

                                        Entity cEntity = EntityTransactionFactory.getDaoInstance(user).getEntityByName(cname, EntityType.category);
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


                                    log.info("new point: " + user.getEmail().getValue() + " " + newPoint.getName().getValue());
                                    makePointEntity(p, newPoint);


                                }
                                else {
                                    Point point = PointTransactionsFactory.getDaoInstance(user).getPointByKey(completedPoint.getKey());
                                    if (point != null) {
                                        clog("Skipping " + p.getName() + " already processed");
                                    }
                                    else {
                                        log.severe("found a point entity but no point" + p.getName());
                                    }
                                }
                            }
                            else {
                                log.severe("could not find category for point: " + p.getName());
                            }
                        }
                        else {
                            log.severe("could not find point owner for point: " + p.getName());
                        }
                    }
                    else {
                        log.severe("could not find point owner for point: " + p.getName());
                    }
                }

                catch (NimbitsException ex) {

                    log.severe(ex.getMessage());
                }


            }
            TaskFactory.getInstance().startUpgradeTask(Action.point,null, s+100 );
        }
        else {
            clog("completed point upgrade " + s);
            TaskFactory.getInstance().startUpgradeTask(Action.subscribe,null, 0 );

        }






        pm.close();

    }

    protected static void doSubscriptions(final HttpServletRequest req) throws NimbitsException {
        final PersistenceManager pm;
        pm = PMF.get().getPersistenceManager();
        final Query q = pm.newQuery(DataPoint.class);
        int s = Integer.valueOf(req.getParameter("s"));

        q.setRange(s, s+100);
        List<DataPoint> points = (List<DataPoint>) q.execute();

        clog("Upgrading subscriptions");

        HashMap<Long, NimbitsUser> map = new HashMap<Long, NimbitsUser>(s);

        NimbitsUser legacyUser;
        Entity userEntity ;
        PointCatagory cx = null;


        if (points.size() > 0) {
            clog("upgrading subscriptions " + s + " to " + (s+100));
            for (DataPoint p : points) {
                try {
                    EntityName name = CommonFactoryLocator.getInstance().createName(p.getName(), EntityType.point);
                    final ProtectionLevel protectionLevel = (p.getPublic() != null && p.getPublic()) ? ProtectionLevel.everyone : ProtectionLevel.onlyMe;
                    legacyUser = getLegUser(pm, p.getUserFK());
                    if (legacyUser != null) {
                        userEntity = EntityTransactionFactory.getDaoInstance(null).getEntityByName(legacyUser.getName(), EntityType.user);
                        User user = UserTransactionFactory.getDAOInstance().getUserByKey(userEntity.getKey());

                        if (user != null) {
                            cx = getLegCat(pm, p.getCatID());
                            if (cx != null) {

                                Entity completedPoint = EntityTransactionFactory.getDaoInstance(user).getEntityByName(name, EntityType.point);
                                if (completedPoint == null) {
                                    log.severe("should have found a completed point entity here" + p.getName());
                                }
                                else {
                                    Point point = PointTransactionsFactory.getDaoInstance(user).getPointByKey(completedPoint.getKey());
                                    if (point != null) {
                                        createSubscriptions(user, p, completedPoint);
                                    }
                                    else {
                                        log.severe("should have found a completed point here" + p.getName());
                                    }
                                }
                            }
                            else {
                                log.severe("could not find category for point: " + p.getName());
                            }
                        }
                        else {
                            log.severe("could not find point owner for point: " + p.getName());
                        }
                    }
                    else {
                        log.severe("could not find point owner for point: " + p.getName());
                    }
                }

                catch (NimbitsException ex) {

                    log.severe(ex.getMessage());
                }


            }
            TaskFactory.getInstance().startUpgradeTask(Action.subscribe,null, s+100 );
        }
        else {
            clog("completed subscription upgrade " + s);
            TaskFactory.getInstance().startUpgradeTask(Action.calculation, null, 0 );
            TaskFactory.getInstance().startUpgradeTask(Action.diagram, null, 0 );
            TaskFactory.getInstance().startUpgradeTask(Action.user, null, 0 );
            TaskFactory.getInstance().startUpgradeTask(Action.record,null, 0);
        }






        pm.close();

    }

    protected static void startValue(final HttpServletRequest req) throws NimbitsException {
        final PersistenceManager pm;
        pm = PMF.get().getPersistenceManager();
        final Query q = pm.newQuery(DataPoint.class);
        int s = Integer.valueOf(req.getParameter("s"));

        q.setRange(s, s+100);
        List<DataPoint> points = (List<DataPoint>) q.execute();

        clog("Upgrading value");


        NimbitsUser legacyUser;
        Entity userEntity ;
        PointCatagory cx = null;


        if (points.size() > 0) {
            clog("upgrading value " + s + " to " + (s+100));
            for (DataPoint p : points) {
                try {
                    EntityName name = CommonFactoryLocator.getInstance().createName(p.getName(), EntityType.point);
                    legacyUser = getLegUser(pm, p.getUserFK());
                    if (legacyUser != null) {
                        userEntity = EntityTransactionFactory.getDaoInstance(null).getEntityByName(legacyUser.getName(), EntityType.user);
                        User user = UserTransactionFactory.getDAOInstance().getUserByKey(userEntity.getKey());

                        if (user != null) {
                            cx = getLegCat(pm, p.getCatID());
                            if (cx != null) {

                                Entity completedPoint = EntityTransactionFactory.getDaoInstance(user).getEntityByName(name, EntityType.point);
                                if (completedPoint == null) {
                                    log.severe("should have found a completed point entity here" + p.getName());
                                }
                                else {
                                    Point point = PointTransactionsFactory.getDaoInstance(user).getPointByKey(completedPoint.getKey());
                                    if (point != null) {

                                        TaskFactory.getInstance().startUpgradeTask(Action.value,completedPoint, 0 );

                                    }
                                    else {
                                        log.severe("should have found a completed point here" + p.getName());
                                    }
                                }
                            }
                            else {
                                log.severe("could not find category for point: " + p.getName());
                            }
                        }
                        else {
                            log.severe("could not find point owner for point: " + p.getName());
                        }
                    }
                    else {
                        log.severe("could not find point owner for point: " + p.getName());
                    }
                }

                catch (NimbitsException ex) {

                    log.severe(ex.getMessage());
                }


            }
            TaskFactory.getInstance().startUpgradeTask(Action.record,null, s+100 );
        }
        else {
            clog("completed value upgrade! " + s);

        }






        pm.close();

    }



    protected static void doCalc3(final HttpServletRequest req) throws NimbitsException {
        final PersistenceManager pm;
        pm = PMF.get().getPersistenceManager();
        final Query q = pm.newQuery(DataPoint.class);
        int s = Integer.valueOf(req.getParameter("s"));

        q.setRange(s, s+100);
        List<DataPoint> points = (List<DataPoint>) q.execute();

        clog("Upgrading calcs");


        NimbitsUser legacyUser;
        Entity userEntity ;
        PointCatagory cx = null;


        if (points.size() > 0) {
            clog("upgrading calcs " + s + " to " + (s+100));
            for (DataPoint legacyPoint : points) {
                try {
                    EntityName pname = CommonFactoryLocator.getInstance().createName(legacyPoint.getName(), EntityType.point);
                    legacyUser = getLegUser(pm, legacyPoint.getUserFK());
                    if (legacyUser != null) {
                        userEntity = EntityTransactionFactory.getDaoInstance(null).getEntityByName(legacyUser.getName(), EntityType.user);
                        User user = UserTransactionFactory.getDAOInstance().getUserByKey(userEntity.getKey());

                        if (user != null) {
                            cx = getLegCat(pm, legacyPoint.getCatID());
                            if (cx != null) {

                                Entity completedPoint = EntityTransactionFactory.getDaoInstance(user).getEntityByName(pname, EntityType.point);
                                if (completedPoint == null) {
                                    log.severe("should have found a completed point entity here" + legacyPoint.getName());
                                }
                                else {
                                    Point point = PointTransactionsFactory.getDaoInstance(user).getPointByKey(completedPoint.getKey());
                                    if (point != null) {
                                       if (legacyPoint.getCalculationEntity() != null) {

                                           CalculationEntity calc = legacyPoint.getCalculationEntity();

                                           EntityName triggerName = CommonFactoryLocator.getInstance().createName(completedPoint.getName().getValue(), EntityType.point);
                                           Entity trigger = EntityTransactionFactory.getDaoInstance(user).getEntityByName(triggerName, EntityType.point);
                                           String X = "";
                                           String Y = "";
                                           String Z = "";
                                           String T = "";
                                           if (calc.getX() != null && calc.getX() > 0) {
                                               DataPoint xp = getLegPoint(pm, calc.getX());
                                               if (xp != null) {
                                                   EntityName name = CommonFactoryLocator.getInstance().createName(xp.getName(), EntityType.point);
                                                   Entity p = EntityTransactionFactory.getDaoInstance(user).getEntityByName(name, EntityType.point);
                                                   if (p!= null) {
                                                       X = p.getKey();
                                                   }
                                               }
                                           }
                                           if (calc.getY() != null && calc.getY() > 0) {
                                               DataPoint lp = getLegPoint(pm, calc.getY());
                                               if (lp != null) {
                                                   EntityName name = CommonFactoryLocator.getInstance().createName(lp.getName(), EntityType.point);
                                                   Entity p = EntityTransactionFactory.getDaoInstance(user).getEntityByName(name, EntityType.point);
                                                   if (p!= null) {
                                                       Y = p.getKey();
                                                   }
                                               }
                                           }
                                           if (calc.getZ() != null && calc.getZ() > 0) {
                                               DataPoint lp = getLegPoint(pm, calc.getZ());
                                               if (lp != null) {
                                                   EntityName name = CommonFactoryLocator.getInstance().createName(lp.getName(), EntityType.point);
                                                   Entity p = EntityTransactionFactory.getDaoInstance(user).getEntityByName(name, EntityType.point);
                                                   if (p!= null) {
                                                       Z = p.getKey();
                                                   }
                                               }
                                           }
                                           if (calc.getTarget() != null && calc.getTarget()  > 0) {
                                               DataPoint lp = getLegPoint(pm, calc.getTarget());
                                               if (lp != null) {
                                                   EntityName name = CommonFactoryLocator.getInstance().createName(lp.getName(), EntityType.point);
                                                   Entity p = EntityTransactionFactory.getDaoInstance(user).getEntityByName(name, EntityType.point);
                                                   if (p!= null) {
                                                       T = p.getKey();
                                                   }
                                               }
                                           }
                                           EntityName cName = CommonFactoryLocator.getInstance().createName(legacyPoint.getName() + " Calc", EntityType.calculation);


                                           Entity existing = EntityTransactionFactory.getDaoInstance(user).getEntityByName(cName, EntityType.calculation);
                                           if (existing == null) {
                                               Entity ce = EntityModelFactory.createEntity(cName, "", EntityType.calculation, ProtectionLevel.onlyMe, trigger.getKey(),
                                                       user.getKey());
                                               Entity rce = EntityServiceFactory.getInstance().addUpdateEntity(user, ce);
                                               Calculation calcEntity = CalculationModelFactory.createCalculation(trigger.getKey(), true, calc.getFormula(),
                                                       T, X, Y, Z);
                                               CalculationServiceFactory.getDaoInstance(user).addUpdateCalculation(rce, calcEntity);
                                               clog("Created calc" + legacyPoint.getName());

                                           }
                                           else {
                                               clog("skipping calc " + cName.getValue() + "already processed");
                                           }



                                       }

                                    }
                                    else {
                                        log.severe("should have found a completed point here" + legacyPoint.getName());
                                    }
                                }
                            }
                            else {
                                log.severe("could not find category for point: " + legacyPoint.getName());
                            }
                        }
                        else {
                            log.severe("could not find point owner for point: " + legacyPoint.getName());
                        }
                    }
                    else {
                        log.severe("could not find point owner for point: " + legacyPoint.getName());
                    }
                }
                catch (NucleusObjectNotFoundException ex) {
                    log.severe(ex.getMessage());
                } catch (JDOFatalUserException ex) {
                    log.severe(ex.getMessage());

                } catch (NimbitsException e) {
                    log.severe(e.getMessage());
                }



            }
            TaskFactory.getInstance().startUpgradeTask(Action.calculation,null, s+100 );
        }
        else {
            clog("calc upgrade complete " + s);

        }






        pm.close();

    }







    protected static void doCalc(final HttpServletRequest req) {
        final PersistenceManager pm;
        pm = PMF.get().getPersistenceManager();

        try {

            List<CalculationEntity> calcs;
            Query q = pm.newQuery(CalculationEntity.class);
            calcs = (List<CalculationEntity>) q.execute();
            for (CalculationEntity calc : calcs){

                try {
                    DataPoint point = calc.getPoint();
                    clog("Upgrading calc for point" + point.getName());
                    NimbitsUser legecyUser = getLegUser(pm, point.getUserFK());
                    if (legecyUser != null) {
                        Entity userEntity = EntityTransactionFactory.getDaoInstance(null).getEntityByName(legecyUser.getName(), EntityType.user);
                        User user = UserTransactionFactory.getDAOInstance().getUserByKey(userEntity.getKey());
                        if (user != null) {

                        }
                        else {
                            clog("could not find calc owner" + legecyUser.getName());

                        }
                    }
                    else {
                        clog("could not find calc owner for point " + point.getName());

                    }
                }
                catch (NucleusObjectNotFoundException ex) {
                    log.severe(ex.getMessage());
                } catch (JDOFatalUserException ex) {
                    log.severe(ex.getMessage());

                } catch (NimbitsException e) {
                    log.severe(e.getMessage());
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
        int s = Integer.valueOf(req.getParameter("s"));
        try {
            clog("doing values " + s + (s + 1000));

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



                    final List<Value> values= old.getDataSegment(timespan,s, s + 1000 );
                    int cx = values.size();
                    if (cx > 0) {
                        dao.recordValues(values);
                        clog("Saved " + values.size() + " values");
                        TaskFactory.getInstance().startUpgradeTask(Action.value,pointEntity, s+1000 );

                    }
                    else {
                        clog("Done value transfer" + pointEntity.getName().getValue());
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

    private static void createSubscriptions(User u, DataPoint p, Entity subscivedEntity) {
        boolean enabled = p.isHighAlarmOn() ||p.isLowAlarmOn() || p.getIdleAlarmOn();

        try {
            int delay = (p.getAlarmDelay() > 30) ?  p.getAlarmDelay() : 30;

            if ( p.getAlarmToFacebook()) {
                EntityName name = CommonFactoryLocator.getInstance().createName(p.getName() + " facebook alert", EntityType.subscription);
                createSubscription(u, subscivedEntity, p, enabled, delay, name, SubscriptionType.anyAlert, SubscriptionNotifyMethod.facebook);
            }
            if ( p.getSendAlarmTweet()) {
                EntityName name = CommonFactoryLocator.getInstance().createName(p.getName() + " twitter alert", EntityType.subscription);
                createSubscription(u, subscivedEntity, p, enabled, delay, name, SubscriptionType.anyAlert, SubscriptionNotifyMethod.twitter);
            }
            if (p.getSendAlarmIM() != null && p.getSendAlarmIM()) {
                EntityName name = CommonFactoryLocator.getInstance().createName(p.getName() + " xmpp alert", EntityType.subscription);
                createSubscription(u, subscivedEntity, p, enabled, delay, name, SubscriptionType.anyAlert, SubscriptionNotifyMethod.instantMessage);
            }
            if (p.getAlarmToEmail() != null && p.getAlarmToEmail()) {
                EntityName name = CommonFactoryLocator.getInstance().createName(p.getName() + " email alert", EntityType.subscription);
                createSubscription(u, subscivedEntity, p, enabled, delay, name, SubscriptionType.anyAlert, SubscriptionNotifyMethod.email);
            }
            if (p.getIdleAlarmOn() != null && p.getIdleAlarmOn()) {
                EntityName name = CommonFactoryLocator.getInstance().createName(p.getName() + " idle alert", EntityType.subscription);
                createSubscription(u, subscivedEntity, p, p.getIdleAlarmOn(),
                        delay, name, SubscriptionType.idle, SubscriptionNotifyMethod.email);
            }
            if (p.getPostToFacebook() != null && p.getPostToFacebook()) {
                EntityName name = CommonFactoryLocator.getInstance().createName(p.getName() + " facebook alert", EntityType.subscription);
                createSubscription(u,subscivedEntity,  p, enabled, delay, name, SubscriptionType.newValue, SubscriptionNotifyMethod.facebook);
            }
        } catch (Exception ex) {
            clog(ex.getMessage());
        }
    }
    //
    private static void createSubscription(User u, Entity p, DataPoint legacy, boolean enabled, int delay, EntityName name,
                                           SubscriptionType type, SubscriptionNotifyMethod method) throws NimbitsException {


        Subscription subscription = SubscriptionFactory.createSubscription(p.getKey(),
                type, method, delay,
                new Date(), legacy.getSendAlertsAsJson(), enabled);

        // subscription.setUuid(UUID.randomUUID().toString());

        Entity sentity = EntityModelFactory.createEntity(name, "",EntityType.subscription,
                ProtectionLevel.onlyMe, p.getKey(), u.getKey());


        Entity s = EntityTransactionFactory.getDaoInstance(u).getEntityByName(name, EntityType.subscription);
        if (s==null) {
            clog("created subscription " + name.getValue());
            Entity r = EntityServiceFactory.getInstance().addUpdateEntity(u, sentity);
            SubscriptionTransactionFactory.getInstance(u).subscribe(r, subscription);
        }
        else {
            clog("Skipping" + name.getValue());
        }
    }
    //


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

    protected static void doDiagram(  ) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query d = pm.newQuery(DiagramEntity.class);

            List<DiagramEntity> diagrams = (List<DiagramEntity>) d.execute( );
            clog("processing " + diagrams.size() + "diagrams");
            for (DiagramEntity diagramEntity : diagrams) {

                try {
                    PointCatagory c = getLegCat(pm, diagramEntity.categoryFk);
                    NimbitsUser u = getLegUser(pm, diagramEntity.userFk);
                    EntityName newName = CommonFactoryLocator.getInstance().createName(diagramEntity.name + ".svg");

                    if (c!= null && u != null) {

                        Entity userEntity = EntityTransactionFactory.getDaoInstance(null).getEntityByName(u.getName(), EntityType.user);
                        User user = UserTransactionFactory.getDAOInstance().getUserByKey(userEntity.getKey());

                        Entity existing = EntityTransactionFactory.getInstance(user).getEntityByName(newName, EntityType.file);
                        if (existing == null) {


                            String parent;
                            if (c.getName().equals(N) || c.getName().equals("System")) {
                                parent = user.getKey();
                            }
                            else {
                                EntityName cName = CommonFactoryLocator.getInstance().createName(c.getName());
                                Entity newCat = EntityTransactionFactory.getDaoInstance(user).getEntityByName(cName, EntityType.category);
                                if (newCat != null){
                                    parent = newCat.getKey();
                                }
                                else {
                                    parent = user.getKey();
                                }
                            }
                            if (Utils.isEmptyString(parent)) {
                                parent = u.getKey();
                            }
                            ProtectionLevel protectionLevel = ProtectionLevel.get(diagramEntity.protectionLevel);

                            Entity e = EntityModelFactory.createEntity(newName, "", EntityType.file,
                                    protectionLevel, parent, u.getKey(), diagramEntity.blobKey.getKeyString());

                            Entity r = EntityServiceFactory.getInstance().addUpdateEntity(user, e);
                            clog("created diagram " + newName.getValue());
                        }
                        else {
                            clog("skipping diagram " + newName.getValue());
                        }
                    }
                    // TaskFactoryLocator.getInstance().startUpgradeTask(Action.point,r );
                } catch (NimbitsException e) {
                    log.severe(e.getMessage());
                }

            }
            clog("done processing diagrams");


        } finally {
            pm.close();
        }

    }

    protected static void doConnections(final HttpServletRequest req) {

        final PersistenceManager pm = PMF.get().getPersistenceManager();



        try {

            int s = Integer.valueOf(req.getParameter("s"));
            final Query legUserQuery = pm.newQuery(NimbitsUser.class);
            legUserQuery.setRange(s, s+100);
            List<NimbitsUser> legu = (List<NimbitsUser>) legUserQuery.execute();


            clog("processing connections");

            if (legu.size() > 0) {

                for (NimbitsUser lu : legu) {


                    if (lu.getConnections() != null && lu.getConnections().size() > 0) {
                        for (final Long l : lu.getConnections()) {
                            NimbitsUser connection = getLegUser(pm, l);
                            User user = UserTransactionFactory.getDAOInstance().getNimbitsUser(lu.getEmail());

                            if (connection != null) {
                                final EntityName name = CommonFactoryLocator.getInstance().createName(connection.getEmail().getValue(), EntityType.userConnection);

                                Entity existing = EntityTransactionFactory.getDaoInstance(user).getEntityByName(name, EntityType.userConnection);

                                if (existing == null) {
                                    final Entity entity = EntityModelFactory.createEntity(name, "",EntityType.userConnection, ProtectionLevel.onlyMe,
                                            user.getKey(), user.getKey());
                                    clog("created connection " + name.getValue());
                                    EntityServiceFactory.getInstance().addUpdateEntity(user, entity);
                                }
                                else {
                                    clog("Skipping " + name.getValue() + " already processed");
                                }

                            }




                        }
                    }
                }
                TaskFactory.getInstance().startUpgradeTask(Action.user, null, s+100 );
            }
            else {
                clog("Completed connections");
            }


        } catch (Exception e) {
            log.severe("ERROR" + e.getMessage());

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
    protected static int doStart(final HttpServletRequest req) throws NimbitsException {

        int results = -1;

        List<NimbitsUser> users;

        final PersistenceManager pm = PMF.get().getPersistenceManager();


        Query q =pm.newQuery(NimbitsUser.class);
        int s = Integer.valueOf(req.getParameter("s"));

        q.setRange(s, s+100);
        users = (List<NimbitsUser>) q.execute();

        //  users = UserTransactionFactory.getInstance().getUsers();
        //   final List<User> users = UserTransactionFactory.getInstance().getAllUsers("lastLoggedIn desc", 3000);
        results = users.size();
        clog(results + " users found at range " + s + " "+ (s+100)) ;
        if (results > 0) {
            for (final NimbitsUser u : users) {
                try {
                    clog("Upgrading user: " + u.getEmail().getValue());
                    Entity existingUserEntity = EntityTransactionFactory.getDaoInstance(null).getEntityByName(u.getName());
                    User existingUser = UserTransactionFactory.getDAOInstance().getNimbitsUser(u.getEmail());

                    if (existingUser == null &&  existingUserEntity == null) {

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
                    }
                    else  if (existingUser != null &&  existingUserEntity != null) {
                        clog("skipping" +  u.getEmail().getValue() + " Already processed");

                    }
                    else {
                        log.severe("Found a corrupt users: " + u.getEmail().getValue());
                    }
                } catch (NimbitsException e) {
                    log.severe(u.getEmail() + " " + e.getMessage());
                }

            }
            TaskFactory.getInstance().startUpgradeTask(Action.start, null, s+100 );

            //  TaskFactory.getInstance().startUpgradeTask(Action.category, null, s+100 );

        }
        else {
            clog("Completed user upgrade: " + s);
            TaskFactory.getInstance().startUpgradeTask(Action.category, null, 0);
        }

        return results;
    }


}
