/*
 * Copyright (c) 2010 Nimbits Inc.
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

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.xmpp.XmppResource;
import com.nimbits.client.model.xmpp.XmppResourceFactory;
import com.nimbits.server.communication.xmpp.XmppTransaction;
import com.nimbits.server.orm.XmppResourceEntity;
import org.springframework.stereotype.Repository;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/15/12
 * Time: 1:13 PM
 */
@SuppressWarnings("unchecked")
@Repository("xmppDao")
public class XmppDaoImpl implements XmppTransaction {


    private PersistenceManagerFactory pmf;

    public XmppDaoImpl() {

    }


    @Override
    public List<XmppResource> getPointXmppResources(final Point point) throws NimbitsException {

        final PersistenceManager pm = pmf.getPersistenceManager();


        try {
            final Query q = pm.newQuery(XmppResourceEntity.class, "entity==u");
            q.declareParameters("String u");
            q.setRange(0, 1);
            final Collection<XmppResource> results = (Collection<XmppResource>) q.execute(point.getKey());
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
        final PersistenceManager pm =pmf.getPersistenceManager();


        try {
            final Query q = pm.newQuery(XmppResourceEntity.class, "entity==u");
            q.declareParameters("String u");
            final Collection<XmppResource> results = (Collection<XmppResource>) q.execute(entity.getKey());
            pm.deletePersistentAll(results);

        }
        finally {
            pm.close();
        }
    }


    public void setPmf(PersistenceManagerFactory pmf) {
        this.pmf = pmf;
    }
}
