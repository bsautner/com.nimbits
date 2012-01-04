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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.dao.recordedvalue;

import com.nimbits.PMF;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModelFactory;
import com.nimbits.server.orm.RecordedValue;
import com.nimbits.server.recordedvalue.RecordedValueTransactions;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.*;
import java.util.logging.Logger;

public class RecordedValueDAOImpl implements RecordedValueTransactions {
    private static final Logger log = Logger.getLogger(RecordedValueDAOImpl.class.getName());
    private final Point point;

    public RecordedValueDAOImpl(Point point) {
        this.point = point;
    }

    @SuppressWarnings(Const.WARNING_UNCHECKED)
    @Override
    public Value getRecordedValuePrecedingTimestamp(final Date timestamp) {
        Value retObj;
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {

            Query q = pm.newQuery(RecordedValue.class,
                    "pointFK == k  && timestamp <= d");
            q.declareImports("import java.util.Date;");
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("k", point.getId());
            args.put("d", timestamp);
            q.declareParameters("String k, Date d");
            q.setOrdering("timestamp descending");
            q.setRange(0, 1);
            List<Value> data = (List<Value>) q.executeWithMap(args);
            if (data.size() > 0) {

                retObj = ValueModelFactory.createValueModel(data.get(0));
            } else {
                retObj = null;
            }
        } finally {
            pm.close();
        }


        return retObj;

    }

    @Override
    public List<Value> getTopDataSeries(final int maxValues) {

        List<Value> retObj = null;
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(RecordedValue.class, "pointFK== k");
            q.declareImports("import java.util.Date");
            final Map<String, Long> args = new HashMap<String, Long>();
            args.put("k", point.getId());

            q.setRange(0, maxValues);
            q.declareParameters("Long k");
            q.setOrdering("timestamp descending");
            @SuppressWarnings(Const.WARNING_UNCHECKED)
            final List<Value> data = (List<Value>) q
                    .executeWithMap(args);
            retObj = ValueModelFactory.createValueModels(data);

        } finally {
            pm.close();

        }
        return retObj;
    }

    @Override
    public List<Value> getTopDataSeries(final int maxValues, final Date endDate) {
        List<Value> retObj = null;
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            Query q = pm.newQuery(RecordedValue.class, "pointFK== k && timestamp <= d");
            q.declareImports("import java.util.Date");
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("k", point.getId());
            args.put("d", endDate);
            q.setRange(0, maxValues);
            q.declareParameters("Long k");
            q.setOrdering("timestamp descending");
            @SuppressWarnings(Const.WARNING_UNCHECKED)
            List<Value> data = (List<Value>) q
                    .executeWithMap(args);
            retObj = ValueModelFactory.createValueModels(data);

        } finally {
            pm.close();

        }
        return retObj;
    }

    @SuppressWarnings(Const.WARNING_UNCHECKED)
    @Override
    public List<Value> getDataSegment(final Timespan timespan) {
        List<Value> retObj = null;
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        Query q = pm.newQuery(RecordedValue.class,
                "pointFK== k && timestamp >= sd && timestamp <= ed");
        q.declareImports("import java.util.Date");
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("k", point.getId());
        //args.put(Const.PARAM_START_DATE, timespan.getStart());
        //args.put(Const.PARAM_END_DATE, timespan.getEnd());
        args.put(Const.PARAM_START_DATE, new Date(timespan.getStart().getTime() - 1000));
        args.put(Const.PARAM_END_DATE, new Date(timespan.getEnd().getTime() + 1000));

        q.declareParameters("String k, Date sd, Date ed");
        q.setOrdering("timestamp descending");

        try {
            List<Value> data = (List<Value>) q.executeWithMap(args);

            retObj = ValueModelFactory.createValueModels(data);

        } catch (Exception e) {
            log.severe(e.getMessage());

        } finally {
            pm.close();
        }
        return retObj;
    }

    @Override
    public List<Value> getDataSegment(final Timespan timespan, final int start, final int end) {
        List<Value> retObj;

        final Map<String, Object> args = new HashMap<String, Object>();
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q = pm.newQuery(RecordedValue.class,
                "pointFK== k && " +
                        "timestamp >= sd && " +
                        "timestamp <= ed");

        q.declareImports("import java.util.Date");
        q.setRange(start, end);

        args.put("k", point.getId());
        args.put(Const.PARAM_START_DATE, new Date(timespan.getStart().getTime() - 1000));
        args.put(Const.PARAM_END_DATE, new Date(timespan.getEnd().getTime() + 1000));


        q.declareParameters("String k, Date sd, Date ed");
        q.setOrdering("timestamp descending");

        try {

            final List<Value> data = (List<Value>) q.executeWithMap(args);

            retObj = ValueModelFactory.createValueModels(data);
            return retObj;

        } finally {
            pm.close();
        }

    }

    @Override
    public List<Value> getCache() {
        return null;  //auto generated
    }

    @Override
    public Value recordValue(final Value v) throws NimbitsException {
        throw new NimbitsException("Not Implemented");
//        final PersistenceManager pm = PMF.get().getPersistenceManager();
//        final RecordedValue jdoVal = new RecordedValue(point, v);
//
//        final Transaction tx = pm.currentTransaction();
//        Value retObj;
//        try {
//            tx.begin();
//            pm.makePersistent(jdoVal);
//            tx.commit();
//            retObj = ValueModelFactory.createValueModel(jdoVal);
//        } catch (JDOException ex) {
//            log.severe(ex.getMessage());
//            throw new NimbitsException(Const.ERROR_RETRY);
//
//        } finally {
//            pm.close();
//        }
//        return retObj;
    }

    @Override
    public void recordValues(final List<Value> values) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final List<RecordedValue> jdoVals = new ArrayList<RecordedValue>();
        for (final Value v : values) {
            jdoVals.add(new RecordedValue(point, v));
        }
        try {
            pm.makePersistentAll(jdoVals);

        } finally {
            pm.close();
        }

    }

    @Override
    public void moveValuesFromCacheToStore() throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }

    @Override
    public List<Value> getCache(Timespan timespan) throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }
}
