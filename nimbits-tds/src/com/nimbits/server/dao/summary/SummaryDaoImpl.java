package com.nimbits.server.dao.summary;

import com.nimbits.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.summary.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.summary.*;

import javax.jdo.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 11:13 AM
 */
public class SummaryDaoImpl implements SummaryTransactions {

    private final User u;

    public SummaryDaoImpl(User u) {
        this.u = u;
    }

    @Override
    public void addOrUpdateSummary(final Entity entity,final Summary summary)  {

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        List<SummaryEntity> results;


        try {

            Query q = pm.newQuery(SummaryEntity.class, "uuid==u");
            q.declareParameters("String u");
            q.setRange(0, 1);
            results = (List<SummaryEntity>) q.execute(entity.getEntity());
            if (results.size() > 0) {
                SummaryEntity result = results.get(0);
                Transaction tx = pm.currentTransaction();
                tx.begin();
                result.setLastProcessed(new Date());
                result.setSummaryIntervalMs(summary.getSummaryIntervalMs());
                result.setSummaryType(summary.getSummaryType());
                tx.commit();
                pm.flush();
            }
            else {
                SummaryEntity s = new SummaryEntity(summary);
                pm.makePersistent(s);

            }


        }
        finally {
            pm.close();
        }

    }

    @Override
    public Summary readSummary(Entity entity) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        List<SummaryEntity> results;


        try {

            Query q = pm.newQuery(SummaryEntity.class, "uuid==u");
            q.declareParameters("String u");
            q.setRange(0, 1);
            results = (List<SummaryEntity>) q.execute(entity.getEntity());
            if (results.size() > 0) {
                SummaryEntity result = results.get(0);
                return SummaryModelFactory.createSummary(result);
            }
            else {
                return null;
            }
        }
        finally {
            pm.close();
        }
    }
}
