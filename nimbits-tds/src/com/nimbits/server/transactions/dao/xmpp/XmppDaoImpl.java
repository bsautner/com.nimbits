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

package com.nimbits.server.transactions.dao.xmpp;

import com.nimbits.PMF;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.xmpp.XmppResource;
import com.nimbits.client.model.xmpp.XmppResourceFactory;
import com.nimbits.server.orm.XmppResourceEntity;
import com.nimbits.server.xmpp.XmppTransaction;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/15/12
 * Time: 1:13 PM
 */
@SuppressWarnings("unchecked")
public class XmppDaoImpl implements XmppTransaction {


    public XmppDaoImpl(final User u) {

    }
    public void addResource(final XmppResource resource)  {

        final PersistenceManager pm = PMF.get().getPersistenceManager();


        try {
            final XmppResourceEntity s = new XmppResourceEntity(resource);
            pm.makePersistent(s);

        }
        finally {
            pm.close();
        }

    }

    @Override
    public List<XmppResource> getPointXmppResources(final Point point) {

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final List<XmppResource> results;


        try {
            final Query q = pm.newQuery(XmppResourceEntity.class, "entity==u");
            q.declareParameters("String u");
            q.setRange(0, 1);
            results = (List<XmppResource>) q.execute(point.getKey());
            final List<XmppResource> retObj = new ArrayList<XmppResource>(results.size());
            for (final XmppResource resource : results) {
                retObj.add(XmppResourceFactory.createXmppResource(resource));
            }
            return retObj;
        }
        finally {
            pm.close();
        }
    }

    @Override
    public void deleteResource(final Entity entity) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final List<XmppResource> results;


        try {
            final Query q = pm.newQuery(XmppResourceEntity.class, "entity==u");
            q.declareParameters("String u");
            results = (List<XmppResource>) q.execute(entity.getKey());
            pm.deletePersistentAll(results);

        }
        finally {
            pm.close();
        }
    }


}
