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

package com.nimbits.server.admin;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.nimbits.PMF;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.server.admin.legacy.orm.CalcEntity;
import com.nimbits.server.admin.legacy.orm.EntityStore;
import com.nimbits.server.admin.legacy.orm.SubscriptionEntity;
import com.nimbits.server.admin.legacy.orm.UserEntity;
import com.nimbits.server.orm.FileEntity;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/7/12
 * Time: 1:32 PM
 */
@SuppressWarnings("unchecked")
public class RecoveryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //fixUsers(resp);
        //dopoints(resp);
        PrintWriter out = resp.getWriter();
        //fixfiles(resp);
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q1 = pm.newQuery(SubscriptionEntity.class);
        List<SubscriptionEntity> oldC = (List<SubscriptionEntity>) q1.execute();


        final Query q2 = pm.newQuery(com.nimbits.server.orm.SubscriptionEntity.class);
        List<com.nimbits.server.orm.SubscriptionEntity> newC = (List<com.nimbits.server.orm.SubscriptionEntity>) q2.execute();

        final Query q3 = pm.newQuery( EntityStore.class);
        q3.setFilter("entityType==5");
        List< EntityStore> oldE
                = (List< EntityStore>)
                q3.execute();

        Map<String, EntityStore> enMap = new HashMap<String, EntityStore>();
        for (EntityStore m : oldE) {
            enMap.put(m.getKey(), m);
        }
        out.println("xStarting " + oldC.size() + "  " + newC.size()  + "  " + oldE.size() +  "<BR>");
//        for (CalcEntity c : oldC) {
//            if (enMap.containsKey(c.getKey())) {
//                Key key = KeyFactory.createKey(com.nimbits.server.orm.CalcEntity.class.getSimpleName(), c.getKey());
//                com.nimbits.server.admin.legacy.orm.EntityStore e  = enMap.get(c.getKey());
//                Entity ec = new com.nimbits.server.orm.EntityStore(key, e.getName().getValue(), e.getUUID(), e.getDescription(), EntityType.calculation.getCode(),
//                        e.getProtectionLevel().getCode(), e.getParent(), e.getOwner(), 0, null );
//                try {
//                    com.nimbits.server.orm.CalcEntity cx = new com.nimbits.server.orm.CalcEntity(ec, c.getFormula(), c.getTrigger(), c.getEnabled(), c.getX()
//                            , c.getY(), c.getZ(), c.getTarget());
//                    out.println(c.getFormula() + "<BR>");
//                    pm.deletePersistent(e);
//                    pm.deletePersistent(c);
//                    pm.makePersistent(cx);
//
//                } catch (NimbitsException e1) {
//                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//
//            }
//            else {
//                pm.deletePersistent(c);
//            }



     //   }
        pm.close();



    }

    private void doCalc(HttpServletResponse resp) throws IOException {
        //fixUsers(resp);
        //dopoints(resp);
        PrintWriter out = resp.getWriter();
        //fixfiles(resp);
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q1 = pm.newQuery(CalcEntity.class);
        List<CalcEntity> oldC = (List<CalcEntity>) q1.execute();


        final Query q2 = pm.newQuery(CalcEntity.class);
        List<com.nimbits.server.orm.CalcEntity> newC = (List<com.nimbits.server.orm.CalcEntity>) q2.execute();

        final Query q3 = pm.newQuery( EntityStore.class);
        q3.setFilter("entityType==7");
        List< EntityStore> oldE
                = (List< EntityStore>)
                q3.execute();

        Map<String, EntityStore> enMap = new HashMap<String, EntityStore>();
        for (EntityStore m : oldE) {
            enMap.put(m.getKey(), m);
        }
        out.println("xStarting " + oldC.size() + "  " + newC.size()  + "  " + oldE.size() +  "<BR>");
        for (CalcEntity c : oldC) {
            if (enMap.containsKey(c.getKey())) {
                Key key = KeyFactory.createKey(com.nimbits.server.orm.CalcEntity.class.getSimpleName(), c.getKey());
                EntityStore e  = enMap.get(c.getKey());
                Entity ec = new com.nimbits.server.orm.EntityStore(key, e.getName().getValue(), e.getUUID(), e.getDescription(), EntityType.calculation.getCode(),
                       e.getProtectionLevel().getCode(), e.getParent(), e.getOwner(), 0, null );
                try {
                    com.nimbits.server.orm.CalcEntity cx = new com.nimbits.server.orm.CalcEntity(ec, c.getFormula(), c.getTrigger(), c.getEnabled(), c.getX()
                    , c.getY(), c.getZ(), c.getTarget());
                    out.println(c.getFormula() + "<BR>");
                    pm.deletePersistent(e);
                    pm.deletePersistent(c);
                    pm.makePersistent(cx);

                } catch (NimbitsException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }
            else {
                pm.deletePersistent(c);
            }



        }
        pm.close();
    }

    private void fixfiles(HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q1 = pm.newQuery(FileEntity.class);
        List<FileEntity> newC = (List<FileEntity>) q1.execute();


        final Query q2 = pm.newQuery(EntityStore.class);
        q2.setFilter("entityType==4");
        List<EntityStore> old = (List<EntityStore>)
                q2.execute();


        Map<String, FileEntity> map = new HashMap<String,FileEntity>();
        out.println("xStarting " + old.size() + "  " + newC.size() +  "<BR>");

        for (FileEntity c : newC) {
            map.put(c.getKey(), c);
            out.println(c.getName() + "  " + c.getKey() + " " + c.getEntityType().name() +  "<br>");
        }
        for (EntityStore e : old) {
            if (! map.containsKey(e.getKey())) {
               // if (e.getName().getValue().equals("Nimbits Stats")) {
                    Key k = KeyFactory.createKey(FileEntity.class.getSimpleName(), e.getKey());
                    com.nimbits.server.orm.EntityStore store = new com.nimbits.server.orm.EntityStore(
                            k, e.getName().getValue(), e.getUUID(), e.getDescription(), EntityType.file.getCode(),
                            e.getProtectionLevel().getCode(), e.getParent(), e.getOwner(), 0, null);
                    try {
                        FileEntity commit = new FileEntity(store);
                        out.println(e.getName() + "  " + commit.getKey() + "<br>");
                        pm.makePersistent(commit);
                        pm.deletePersistent(e);
                    } catch (NimbitsException e1) {
                        out.println(e1);
                //    }
                }


            }

        }
    }

    private void dopoints(HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q1 = pm.newQuery(PointEntity.class);
        List<PointEntity> old = (List<PointEntity>) q1.execute();


//        final Query q2 = pm.newQuery(com.nimbits.server.orm.PointEntity.class);
//        List<com.nimbits.server.orm.PointEntity> oldEntities = (List<com.nimbits.server.orm.PointEntity>) q1.execute();

        final Query q2 = pm.newQuery(EntityStore.class);
        q2.setFilter("entityType==2");
        List<EntityStore> oldEntities = (List<EntityStore>)
                q2.execute();
        Map<String, EntityStore> map = new HashMap<String,EntityStore>();

        for ( EntityStore s : oldEntities) {

            out.println("!" + s.getName() + " " + s.getKey() + "</br>");
            map.put(s.getKey(), s);
        }

        final Query q3 = pm.newQuery(PointEntity.class);
        List<com.nimbits.server.orm.PointEntity> newP = (List<com.nimbits.server.orm.PointEntity>) q1.execute();


        out.println("xStarting " + old.size() + "  " + oldEntities.size() + " " + newP.size() +  "<BR>");


        for (PointEntity p : old) {
            if (map.containsKey(p.getKey())) {
                out.println("****" + p.getName().getValue() + " " + p.getKey() + "<br>");
            }
            else {
                out.println(p.getName().getValue() + " " + p.getKey() + "<br>");
            }

            Key k = KeyFactory.createKey(com.nimbits.server.orm.PointEntity.class.getSimpleName(), p.getKey());
            Entity e = new com.nimbits.server.orm.EntityStore(k, p.getName().getValue(), p.getUUID(), p.getDescription(),
                    EntityType.point.getCode(), p.getProtectionLevel().getCode(), p.getParent(), p.getOwner(),
                    p.getAlertType().getCode(), null);

            try {
                com.nimbits.server.orm.PointEntity np = new com.nimbits.server.orm.PointEntity(e,
                        p.getHighAlarm(), p.getExpire(), p.getUnit(), p.getFilterValue(), p.getFilterType().getCode(),
                        p.getLowAlarm(), p.isHighAlarmOn(), p.isLowAlarmOn(), p.idleAlarmOn,p.getIdleSeconds(), p.getIdleAlarmSent(),
                        p.getLegacyKey(), 0.0,null, null);

                if (p.getName().getValue().equals("Data Feed Channel")) {
                    pm.deletePersistent(p);
                    //pm.makePersistent(np);
                    if (map.containsKey(p.getKey())) {
                        pm.deletePersistent(map.get(p.getKey()));
                    }
                }


            } catch (NimbitsException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        pm.close();
    }

    private void fixUsers(HttpServletResponse resp) throws IOException {
        //old(req, resp);
        PrintWriter out = resp.getWriter();
        out.println("Starting");
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q1 = pm.newQuery(UserEntity.class);
        List<UserEntity> old = (List<UserEntity>) q1.execute();

        final Query q2 = pm.newQuery(com.nimbits.server.admin.legacy.orm.EntityStore.class);
        q2.setFilter("entityType==0");
        List<com.nimbits.server.admin.legacy.orm.EntityStore> oldEntities = (List<com.nimbits.server.admin.legacy.orm.EntityStore>)
                q2.execute();

        Map<String, com.nimbits.server.admin.legacy.orm.EntityStore> map = new HashMap<String, com.nimbits.server.admin.legacy.orm.EntityStore>();
        for (EntityStore s : oldEntities) {
            map.put(s.getKey(), s);
        }
        out.println("Starting " + old.size() + "  " + oldEntities.size() + "<BR>");
        for (UserEntity p : old) {
            try {
                if (map.containsKey(p.getEmail().getValue())) {
                    Entity e = new com.nimbits.server.orm.EntityStore(
                            KeyFactory.createKey(com.nimbits.server.orm.UserEntity.class.getSimpleName(), p.getEmail().getValue()),
                            p.getEmail().getValue(),
                            UUID.randomUUID().toString(),
                            "", EntityType.user.getCode(), ProtectionLevel.onlyMe.getCode(),p.getEmail().getValue(), p.getEmail().getValue(),
                            0, null);

                    com.nimbits.server.orm.UserEntity user =
                            new com.nimbits.server.orm.UserEntity(e, p.getDateCreated(), p.getLastLoggedIn(),
                                    p.getFacebookID(), p.getFacebookToken(), p.getSecret(), p.getTwitterToken(), p.getTwitterTokenSecret(),
                                    false);


                    pm.deletePersistent(p);
                    pm.deletePersistent(map.get(p.getEmail().getValue()));
                    pm.makePersistent(user);
                }




                //   }
                out.println(p.getEmail().getValue() + "<BR>");
            } catch (NimbitsException e) {
                out.println(e.getMessage());
            }
        }
    }

//    private void old(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        PrintWriter out = resp.getWriter();
//
//        out.println("Starting");
//        final PersistenceManager pm = PMF.get().getPersistenceManager();
//
//        try {
//            int c = 0;
//            out.print(c +  "<BR>");
//            if (req.getParameter("c") != null) {
//                c = Integer.getInteger(req.getParameter("c"));
//            }
//            final Query q1 = pm.newQuery(PointEntity.class);
//            final Query q2 = pm.newQuery(EntityStore.class);
//            q2.setFilter("entityType==" + EntityType.point.getCode()+ " || entityType==" + EntityType.feed.getCode());
//
//          //  final Query q3 = pm.newQuery(com.nimbits.server.orm.PointEntity.class);
//
//
//            //final List<com.nimbits.server.orm.PointEntity> done = (List<com.nimbits.server.orm.PointEntity>) q3.execute();
//
//           // q1.setRange(c, c+100);
//            final List<PointEntity> oldPoints = (List<PointEntity>) q1.execute(PointEntity.class);
//            final List<EntityStore> eResult = (List<EntityStore>) q2.execute(EntityStore.class);
//
//
//            final Map<String, Entity> map = new HashMap<String, Entity>(eResult.size()) ;
//            for (final EntityStore e : eResult) {
//                map.put(e.getKey(), e);
//
//            }
//            out.print(  eResult.size() + ' ' + oldPoints.size() + "<BR>");
//
//            LogHelper.log(this.getClass(), "system wide point search found " + oldPoints.size());
//
//            for (final PointEntity px : oldPoints) {
//                if (map.containsKey(px.getKey())) {
//                    final Entity e = map.get(px.getKey());
//                   // if (e.getKey().equals("bsautner@gmail.com/TempC")) {
//                        Entity existing = null;
//                        try {
//                            existing = EntityTransactionFactory.getDaoInstance(null).getEntityByKey(e.getKey(), com.nimbits.server.orm.PointEntity.class);
//                            out.println(existing.getName() == null);
//                        } catch (NullPointerException e1) {
//                            out.println(e1.getMessage() + "fixing bad point <br>");
//
//                            final com.nimbits.server.orm.PointEntity newPoint = new com.nimbits.server.orm.PointEntity(e, px);
//                            if (px.getLegacyKey() != null)  {
//                                newPoint.setUUID(px.getLegacyKey());
//                            }
//                            else {
//                                newPoint.setUUID(UUID.randomUUID().toString());
//                            }
//                            pm.deletePersistent(e);
//                            pm.deletePersistent(px);
//
//                            pm.makePersistent(newPoint);
//
//
//                     //   }
//
//                        out.println("[" + px.getKey() + "]  <br />");
//                    }
//
//                }
//
//
//
//
//            }
////            for (final com.nimbits.server.orm.PointEntity d : done) {
////                if (d.getName() != null) {
////                    out.println("{" +d.getKey() + "  " + d.getName() + "}</br>");
////                }
////            }
//
//
//        } catch (NimbitsException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } finally {
//            pm.close();
//        }
//    }


}
