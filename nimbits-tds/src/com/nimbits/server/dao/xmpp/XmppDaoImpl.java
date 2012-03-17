package com.nimbits.server.dao.xmpp;

import com.nimbits.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.xmpp.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.xmpp.*;

import javax.jdo.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/15/12
 * Time: 1:13 PM
 */
@SuppressWarnings("unchecked")
public class XmppDaoImpl implements XmppTransaction {
    final private User user;

    public XmppDaoImpl(User u) {
        this.user = u;
    }
    public void addResource(XmppResource resource)  {

        final PersistenceManager pm = PMF.get().getPersistenceManager();


        try {
            XmppResourceEntity s = new XmppResourceEntity(resource);
            pm.makePersistent(s);

        }
        finally {
            pm.close();
        }

    }

    @Override
    public List<XmppResource> getPointXmppResources(Point point) {

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        List<XmppResource> results;
        List<XmppResource> retObj = new ArrayList<XmppResource>();

        try {
            Query q = pm.newQuery(XmppResourceEntity.class, "entity==u");
            q.declareParameters("String u");
            q.setRange(0, 1);
            results = (List<XmppResource>) q.execute(point.getUUID());
            for (XmppResource resource : results) {
                retObj.add(XmppResourceFactory.createXmppResource(resource));
            }
            return retObj;
        }
        finally {
            pm.close();
        }
    }


}
