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

import com.nimbits.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.admin.legacy.orm.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.admin.settings.*;
import com.nimbits.server.transactions.dao.entity.*;
import com.nimbits.server.user.*;

import javax.jdo.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

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
        PrintWriter out = resp.getWriter();
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        Query q = pm.newQuery(CalcEntity.class);
        List<CalcEntity> all = (List<CalcEntity>) q.execute();

        for (CalcEntity c : all) {

            try {
                if (c.isEnabled()) {
                    c.validate();
                    out.println("<BR><P>" + c.getFormula() + " " + c.getOwner() + " is OK!" + "</P>");
                }

            }
            catch (NimbitsException ex) {
                Transaction tx = pm.currentTransaction();
                c.setEnabled(false);
                tx.commit();

                out.println("<BR><P>Failed to validate: " + c.getFormula() + " " + c.getOwner() + " "
                        + c.getTrigger() + ">>" + c.getTarget()
                        + ex.getMessage() + "</P>");
            }

        }
        pm.close();

//
//
//        for (UserEntity u : all) {
//
//
//
//            try {
//                EntityName name = CommonFactoryLocator.getInstance().createName("Secret API Key", EntityType.accessKey);
//                Entity n = EntityModelFactory.createEntity(name, "", EntityType.accessKey, ProtectionLevel.onlyMe,
//                        u.getKey(), u.getKey());
//                String secret = u.getSecret();
//                if (Utils.isEmptyString(secret)) {
//                    secret = UUID.randomUUID().toString();
//                }
//                out.println(secret + "<br>");
//                AccessKey model = AccessKeyFactory.createAccessKey(n, secret, u.getKey(), AuthLevel.readWriteAll);
//                AccessKeyEntity en = new AccessKeyEntity(model);
//                pm.makePersistent(en);
//            } catch (NimbitsException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//
//        }





    }

    private void upgradeCalcs(HttpServletResponse resp) throws IOException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        PrintWriter out = resp.getWriter();
        Query q = pm.newQuery(EntityStore.class);
        q.setFilter("entityType == 6");
        List<EntityStore> old = (List<EntityStore>) q.execute();
        List<ConnectionEntity> newE = (List<ConnectionEntity>) pm.newQuery(ConnectionEntity.class).execute();
        out.println("Starting : " + old.size() + "  " + newE.size()+ "<br>");
        List<String> done = new ArrayList<String>(newE.size());
        for (ConnectionEntity c : newE) {
            done.add(c.getOwner() + c.getName());
        }


        for (EntityStore x : old) {
            if (! done.contains(x.getOwner() + x.getName())) {
                //  if (x.getOwner().equals("bsautner@gmail.com")) {
                Entity e = EntityModelFactory.createEntity(x.getName(), "", EntityType.userConnection,
                        ProtectionLevel.onlyMe, x.getOwner(), x.getOwner());

                Entity ex = EntityModelFactory.createEntity(x.getName(), "", EntityType.userConnection,
                        ProtectionLevel.onlyMe, x.getOwner(), x.getOwner());
                try {
                    ConnectionEntity cx = new ConnectionEntity(ex);
                    out.println(ex.getName() + " " + ex.getOwner() + "<br>");
                    EntityServiceFactory.getInstance().addUpdateEntity(cx);
                    pm.deletePersistent(x);

                } catch (NimbitsException e1) {
                    out.println(e1.getMessage());
                }


                //   }


            }


        }
    }

    private void fixCalcsUsingOldEntity(HttpServletResponse resp) throws IOException {
        //don't delete this it;s a good 3.1.9 upgrade
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        PrintWriter out = resp.getWriter();
        final Query q1 = pm.newQuery(DataPoint.class);
        try {

            List<CalcEntity> calcEntities = (List<CalcEntity>) pm.newQuery(CalcEntity.class).execute();
            out.println("deleting" + calcEntities.size());
            pm.deletePersistentAll(calcEntities);



            List<DataPoint> oldC = (List<DataPoint>) q1.execute();
            String adminStr = SettingsServiceFactory.getInstance().getSetting(SettingType.admin);
            EmailAddress emailAddress = null;

            emailAddress = CommonFactoryLocator.getInstance().createEmailAddress(adminStr);

            User admin = UserServiceFactory.getInstance().getUserByKey("bsautner@gmail.com", AuthLevel.admin);
            // admin.setAuthLevel(AuthLevel.admin);
            for (DataPoint p : oldC) {
                if (p.getCalculationEntity() != null) {

                    EntityDaoImpl impl = new EntityDaoImpl(null);
                    try {
                        final Query q2 = pm.newQuery(NimbitsUser.class);
                        q2.setRange(0, 1);
                        q2.setFilter("id == " + p.getUserFK());
                        List<NimbitsUser> oldU = (List<NimbitsUser>) q2.execute();
                        if (oldU.size() > 0) {
                            NimbitsUser u = oldU.get(0);
                            User ux = (User) EntityTransactionFactory.getDaoInstance(null).getEntityByKey(u.getEmail().getValue(), UserEntity.class).get(0);
                            out.println(p.getCalculationEntity().getFormula() + "<br>");
                            out.println(ux.getEmail().getValue() + "</br>");
                            String tvar = null, xvar, yvar, zvar, targetVar;

                            Point trigger = (Point) EntityTransactionFactory.getDaoInstance(admin).getEntityByKey(u.getEmail().getValue() + '/' + p.getName(), PointEntity.class).get(0);
                            if (trigger != null) {
                                out.println("tigger" + trigger.getName().getValue() + "</br>");
                                tvar = trigger.getKey();
                            }
                            xvar = getXVar(pm, out, admin, p, u);
                            yvar = getYVar(pm, out, admin, p, u);
                            zvar = getZVar(pm, out, admin, p, u);
                            targetVar = getTVar(pm, out, admin, p, u);
                            EntityName name = CommonFactoryLocator.getInstance().createName(p.getName() + " calc", EntityType.calculation);

                            Entity e = EntityModelFactory.createEntity(name, "", EntityType.calculation, ProtectionLevel.onlyMe,
                                    trigger.getKey(), u.getEmail().getValue());
                            CalcEntity cv = new CalcEntity(e, p.getCalculationEntity().getFormula(),tvar,  p.getCalculationEntity().getEnabled()
                                    , xvar, yvar, zvar, targetVar);
                            pm.makePersistent(cv);

                        }
                        // Point point = (Point) impl.getEntityByUUID(p.getUUID(), PointEntity.class);


                        // out.println("Trigger: " + point.getName().getValue() + "<br>");
                    } catch (NimbitsException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }

            }


        } catch (NimbitsException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private String getXVar(PersistenceManager pm, PrintWriter out, User admin, DataPoint p, NimbitsUser u) throws NimbitsException {
        String xvar;
        if (p.getCalculationEntity().getX() != null && p.getCalculationEntity().getX() > 0) {
            Query qx = pm.newQuery(DataPoint.class);
            qx.setFilter("id == " + p.getCalculationEntity().getX());
            qx.setRange(0, 1);
            List<DataPoint> xs = (List<DataPoint>) qx.execute();
            if (!xs.isEmpty()) {
                DataPoint X = xs.get(0);
                Entity xp = EntityTransactionFactory.getDaoInstance(admin).getEntityByKey(u.getEmail().getValue() + '/' + X.getName(), PointEntity.class).get(0);
                if (xp != null) {
                    out.println("X" + xp.getName().getValue() + "</br>");
                    return  xp.getKey();
                }
            }
        }
        return null;

    }
    private String getYVar(PersistenceManager pm, PrintWriter out, User admin, DataPoint p, NimbitsUser u) throws NimbitsException {
        String xvar;
        if (p.getCalculationEntity().getY() != null && p.getCalculationEntity().getY() > 0) {
            Query q = pm.newQuery(DataPoint.class);
            q.setFilter("id == " + p.getCalculationEntity().getY());
            q.setRange(0, 1);
            List<DataPoint> s = (List<DataPoint>) q.execute();
            if (!s.isEmpty()) {
                DataPoint P = s.get(0);
                Entity xp = EntityTransactionFactory.getDaoInstance(admin).getEntityByKey(u.getEmail().getValue() + '/' + P.getName(), PointEntity.class).get(0);
                if (xp != null) {
                    out.println("Y" + xp.getName().getValue() + "</br>");
                    return  xp.getKey();
                }
            }
        }
        return null;

    }
    private String getZVar(PersistenceManager pm, PrintWriter out, User admin, DataPoint p, NimbitsUser u) throws NimbitsException {
        String xvar;
        if (p.getCalculationEntity().getZ() != null && p.getCalculationEntity().getZ() > 0) {
            Query q = pm.newQuery(DataPoint.class);
            q.setFilter("id == " + p.getCalculationEntity().getZ());
            q.setRange(0, 1);
            List<DataPoint> s = (List<DataPoint>) q.execute();
            if (!s.isEmpty()) {
                DataPoint P = s.get(0);
                Entity xp = EntityTransactionFactory.getDaoInstance(admin).getEntityByKey(u.getEmail().getValue() + '/' + P.getName(), PointEntity.class).get(0);
                if (xp != null) {
                    out.println("Z" + xp.getName().getValue() + "</br>");
                    return  xp.getKey();
                }
            }
        }
        return null;

    }

    private String getTVar(PersistenceManager pm, PrintWriter out, User admin, DataPoint p, NimbitsUser u) throws NimbitsException {
        String xvar;
        if (p.getCalculationEntity().getTarget() != null && p.getCalculationEntity().getTarget() > 0) {
            Query q = pm.newQuery(DataPoint.class);
            q.setFilter("id == " + p.getCalculationEntity().getTarget());
            q.setRange(0, 1);
            List<DataPoint> s = (List<DataPoint>) q.execute();
            if (!s.isEmpty()) {
                DataPoint P = s.get(0);
                Entity xp = EntityTransactionFactory.getDaoInstance(admin).getEntityByKey(u.getEmail().getValue() + '/' + P.getName(), PointEntity.class).get(0);
                if (xp != null) {
                    out.println("getTarget" + xp.getName().getValue() + "</br>");
                    return  xp.getKey();
                }
            }
        }
        return null;

    }
//    private void doCalc(HttpServletResponse resp) throws IOException {
//        //fixUsers(resp);
//        //dopoints(resp);
//        PrintWriter out = resp.getWriter();
//        //fixfiles(resp);
//        final PersistenceManager pm = PMF.get().getPersistenceManager();
//        final Query q1 = pm.newQuery(CalcEntity.class);
//        List<CalcEntity> oldC = (List<CalcEntity>) q1.execute();
//
//
//        final Query q2 = pm.newQuery(CalcEntity.class);
//        List<com.nimbits.server.orm.CalcEntity> newC = (List<com.nimbits.server.orm.CalcEntity>) q2.execute();
//
//        final Query q3 = pm.newQuery( EntityStore.class);
//        q3.setFilter("entityType==7");
//        List< EntityStore> oldE
//                = (List< EntityStore>)
//                q3.execute();
//
//        Map<String, EntityStore> enMap = new HashMap<String, EntityStore>();
//        for (EntityStore m : oldE) {
//            enMap.put(m.getKey(), m);
//        }
//        out.println("xStarting " + oldC.size() + "  " + newC.size()  + "  " + oldE.size() +  "<BR>");
//        for (CalcEntity c : oldC) {
//            if (enMap.containsKey(c.getKey())) {
//                Key key = KeyFactory.createKey(com.nimbits.server.orm.CalcEntity.class.getSimpleName(), c.getKey());
//                EntityStore e  = enMap.get(c.getKey());
//                Entity ec = new com.nimbits.server.orm.EntityStore(key, e.getName().getValue(), e.getUUID(), e.getDescription(), EntityType.calculation.getCode(),
//                        e.getProtectionLevel().getCode(), e.getParent(), e.getOwner(), 0 );
//                try {
//                    com.nimbits.server.orm.CalcEntity cx = new com.nimbits.server.orm.CalcEntity(ec, c.getFormula(), c.getTrigger(), c.isEnabled(), c.getX()
//                            , c.getY(), c.getZ(), c.getTarget());
//                    out.println(c.getFormula() + "<BR>");
//                    pm.deletePersistent(e);
//                    pm.deletePersistent(c);
//                    pm.makePersistent(cx);
//
//                } catch (NimbitsException e1) {
//                    out.println(e1.getMessage());
//                }
//
//            }
//            else {
//                pm.deletePersistent(c);
//            }
//
//
//
//        }
//        pm.close();
//    }

//    private void fixfiles(HttpServletResponse resp) throws IOException {
//        PrintWriter out = resp.getWriter();
//
//        final PersistenceManager pm = PMF.get().getPersistenceManager();
//        final Query q1 = pm.newQuery(FileEntity.class);
//        List<FileEntity> newC = (List<FileEntity>) q1.execute();
//
//
//        final Query q2 = pm.newQuery(EntityStore.class);
//        q2.setFilter("entityType==4");
//        List<EntityStore> old = (List<EntityStore>)
//                q2.execute();
//
//
//        Map<String, FileEntity> map = new HashMap<String,FileEntity>();
//        out.println("xStarting " + old.size() + "  " + newC.size() +  "<BR>");
//
//        for (FileEntity c : newC) {
//            map.put(c.getKey(), c);
//            out.println(c.getName() + "  " + c.getKey() + ' ' + c.getEntityType().name() +  "<br>");
//        }
//        for (EntityStore e : old) {
//            if (! map.containsKey(e.getKey())) {
//                // if (e.getName().getValue().equals("Nimbits Stats")) {
//                Key k = KeyFactory.createKey(FileEntity.class.getSimpleName(), e.getKey());
//                com.nimbits.server.orm.EntityStore store = new com.nimbits.server.orm.EntityStore(
//                        k, e.getName().getValue(), e.getUUID(), e.getDescription(), EntityType.file.getCode(),
//                        e.getProtectionLevel().getCode(), e.getParent(), e.getOwner(), 0, null);
//                try {
//                    FileEntity commit = new FileEntity(store);
//                    out.println(e.getName() + "  " + commit.getKey() + "<br>");
//                    pm.makePersistent(commit);
//                    pm.deletePersistent(e);
//                } catch (NimbitsException e1) {
//                    out.println(e1);
//                    //    }
//                }
//
//
//            }
//
//        }
//    }

//    private void dopoints(HttpServletResponse resp) throws IOException {
//        PrintWriter out = resp.getWriter();
//
//        final PersistenceManager pm = PMF.get().getPersistenceManager();
//        final Query q1 = pm.newQuery(PointEntity.class);
//        List<PointEntity> old = (List<PointEntity>) q1.execute();
//
//
////        final Query q2 = pm.newQuery(com.nimbits.server.orm.PointEntity.class);
////        List<com.nimbits.server.orm.PointEntity> oldEntities = (List<com.nimbits.server.orm.PointEntity>) q1.execute();
//
//        final Query q2 = pm.newQuery(EntityStore.class);
//        q2.setFilter("entityType==2");
//        List<EntityStore> oldEntities = (List<EntityStore>)
//                q2.execute();
//        Map<String, EntityStore> map = new HashMap<String,EntityStore>();
//
//        for ( EntityStore s : oldEntities) {
//
//            out.println("!" + s.getName() + " " + s.getKey() + "</br>");
//            map.put(s.getKey(), s);
//        }
//
//        final Query q3 = pm.newQuery(PointEntity.class);
//        List<com.nimbits.server.orm.PointEntity> newP = (List<com.nimbits.server.orm.PointEntity>) q1.execute();
//
//
//        out.println("xStarting " + old.size() + "  " + oldEntities.size() + " " + newP.size() +  "<BR>");
//
//
//        for (PointEntity p : old) {
//            if (map.containsKey(p.getKey())) {
//                out.println("****" + p.getName().getValue() + " " + p.getKey() + "<br>");
//            }
//            else {
//                out.println(p.getName().getValue() + " " + p.getKey() + "<br>");
//            }
//
//            Key k = KeyFactory.createKey(com.nimbits.server.orm.PointEntity.class.getSimpleName(), p.getKey());
//            Entity e = new com.nimbits.server.orm.EntityStore(k, p.getName().getValue(), p.getUUID(), p.getDescription(),
//                    EntityType.point.getCode(), p.getProtectionLevel().getCode(), p.getParent(), p.getOwner(),
//                    p.getAlertType().getCode(), null);
//
//            try {
//                com.nimbits.server.orm.PointEntity np = new com.nimbits.server.orm.PointEntity(e,
//                        p.getHighAlarm(), p.getExpire(), p.getUnit(), p.getFilterValue(), p.getFilterType().getCode(),
//                        p.getLowAlarm(), p.isHighAlarmOn(), p.isLowAlarmOn(), p.idleAlarmOn,p.getIdleSeconds(), p.getIdleAlarmSent(),
//                        null, null);
//
//                if (p.getName().getValue().equals("Data Feed Channel")) {
//                    pm.deletePersistent(p);
//                    //pm.makePersistent(np);
//                    if (map.containsKey(p.getKey())) {
//                        pm.deletePersistent(map.get(p.getKey()));
//                    }
//                }
//
//
//            } catch (NimbitsException e1) {
//                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//        }
//        pm.close();
//    }
//


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
