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

import com.nimbits.PMF;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.server.entity.EntityTransactionFactory;
import com.nimbits.server.logging.LogHelper;
import com.nimbits.server.orm.EntityStore;

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
        PrintWriter out = resp.getWriter();

        out.println("Starting");
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            int c = 0;
            out.print(c +  "<BR>");
            if (req.getParameter("c") != null) {
                c = Integer.getInteger(req.getParameter("c"));
            }
            final Query q1 = pm.newQuery(com.nimbits.server.admin.PointEntity.class);
            final Query q2 = pm.newQuery(EntityStore.class);
            q2.setFilter("entityType==" + EntityType.point.getCode()+ " || entityType==" + EntityType.feed.getCode());

          //  final Query q3 = pm.newQuery(com.nimbits.server.orm.PointEntity.class);


            //final List<com.nimbits.server.orm.PointEntity> done = (List<com.nimbits.server.orm.PointEntity>) q3.execute();

           // q1.setRange(c, c+100);
            final List<com.nimbits.server.admin.PointEntity> oldPoints = (List<com.nimbits.server.admin.PointEntity>) q1.execute(com.nimbits.server.admin.PointEntity.class);
            final List<EntityStore> eResult = (List<EntityStore>) q2.execute(EntityStore.class);


            final Map<String, Entity> map = new HashMap<String, Entity>(eResult.size()) ;
            for (final EntityStore e : eResult) {
                map.put(e.getKey(), e);

            }
            out.print(  eResult.size() + ' ' + oldPoints.size() + "<BR>");

            LogHelper.log(this.getClass(), "system wide point search found " + oldPoints.size());

            for (final com.nimbits.server.admin.PointEntity px : oldPoints) {
                if (map.containsKey(px.getKey())) {
                    final Entity e = map.get(px.getKey());
                   // if (e.getKey().equals("bsautner@gmail.com/TempC")) {
                        Entity existing = null;
                        try {
                            existing = EntityTransactionFactory.getDaoInstance(null).getEntityByKey(e.getKey(), com.nimbits.server.orm.PointEntity.class);
                            out.println(existing.getName() == null);
                        } catch (NullPointerException e1) {
                            out.println(e1.getMessage() + "fixing bad point <br>");

                            final com.nimbits.server.orm.PointEntity newPoint = new com.nimbits.server.orm.PointEntity(e, px);
                            if (px.getLegacyKey() != null)  {
                                newPoint.setUUID(px.getLegacyKey());
                            }
                            else {
                                newPoint.setUUID(UUID.randomUUID().toString());
                            }
                            pm.deletePersistent(e);
                            pm.deletePersistent(px);

                            pm.makePersistent(newPoint);


                     //   }

                        out.println("[" + px.getKey() + "]  <br />");
                    }

                }




            }
//            for (final com.nimbits.server.orm.PointEntity d : done) {
//                if (d.getName() != null) {
//                    out.println("{" +d.getKey() + "  " + d.getName() + "}</br>");
//                }
//            }


        } catch (NimbitsException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            pm.close();
        }


    }



}
