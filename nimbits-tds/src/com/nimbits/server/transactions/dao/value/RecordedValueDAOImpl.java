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

package com.nimbits.server.transactions.dao.value;

import com.nimbits.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.timespan.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.model.valueblobstore.*;
import com.nimbits.server.admin.legacy.orm.*;

import javax.jdo.*;
import java.util.*;
import java.util.logging.*;
@SuppressWarnings("unchecked")
@Deprecated
public class RecordedValueDAOImpl   {
    private static final Logger log = Logger.getLogger(RecordedValueDAOImpl.class.getName());
    private final DataPoint point;

    public RecordedValueDAOImpl(final DataPoint point) {
        this.point = point;
    }





    @SuppressWarnings(Const.WARNING_UNCHECKED)

    public Value getRecordedValuePrecedingTimestamp(final Date timestamp) {
        Value retObj;
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {

            final Query q = pm.newQuery(RecordedValue.class,
                    "pointFK == k  && timestamp <= d");
            q.declareImports("import java.util.Date;");
            final Map<String, Object> args = new HashMap<String, Object>(2);
            args.put("k", point.getId());
            args.put("d", timestamp);
            q.declareParameters("String k, Date d");
            q.setOrdering("timestamp descending");
            q.setRange(0, 1);
            final List<Value> data = (List<Value>) q.executeWithMap(args);
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





    public List<Value> getTopDataSeries(final int maxValues) {

        List<Value> retObj = null;
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(RecordedValue.class, "pointFK== k");
            q.declareImports("import java.util.Date");
            final Map<String, Long> args = new HashMap<String, Long>(1);
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


    public List<Value> getTopDataSeries(final int maxValues, final Date endDate) {
        List<Value> retObj = null;
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(RecordedValue.class, "pointFK== k && timestamp <= d");
            q.declareImports("import java.util.Date");
            final Map<String, Object> args = new HashMap<String, Object>(2);
            args.put("k", point.getId());
            args.put("d", endDate);
            q.setRange(0, maxValues);
            q.declareParameters("Long k");
            q.setOrdering("timestamp descending");
            @SuppressWarnings(Const.WARNING_UNCHECKED) final
            List<Value> data = (List<Value>) q
                    .executeWithMap(args);
            retObj = ValueModelFactory.createValueModels(data);

        } finally {
            pm.close();

        }
        return retObj;
    }

    @SuppressWarnings(Const.WARNING_UNCHECKED)

    public List<Value> getDataSegment(final Timespan timespan) {
        List<Value> retObj = null;
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        Query q = pm.newQuery(RecordedValue.class,
                "pointFK== k && timestamp >= sd && timestamp <= ed");
        q.declareImports("import java.util.Date");
        final Map<String, Object> args = new HashMap<String, Object>(3);
        args.put("k", point.getId());
        //args.put(Const.PARAM_START_DATE, timespan.getStart());
        //args.put(Const.PARAM_END_DATE, timespan.getEnd());
        args.put(Parameters.sd.getText(), new Date(timespan.getStart().getTime() - 1000));
        args.put(Parameters.ed.getText(), new Date(timespan.getEnd().getTime() + 1000));

        q.declareParameters("String k, Date sd, Date ed");
        q.setOrdering("timestamp descending");

        try {
            final List<Value> data = (List<Value>) q.executeWithMap(args);

            retObj = ValueModelFactory.createValueModels(data);

        } catch (Exception e) {
            log.severe(e.getMessage());

        } finally {
            pm.close();
        }
        return retObj;
    }


    public List<Value> getDataSegment(final Timespan timespan, final int start, final int end) {
        final List<Value> retObj;

        final Map<String, Object> args = new HashMap<String, Object>(3);
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q = pm.newQuery(RecordedValue.class,
                "pointFK== k && " +
                        "timestamp >= sd && " +
                        "timestamp <= ed");

        q.declareImports("import java.util.Date");
        q.setRange(start, end);

        args.put("k", point.getId());
        args.put(Parameters.sd.getText(), new Date(timespan.getStart().getTime() - 1000));
        args.put(Parameters.ed.getText(), new Date(timespan.getEnd().getTime() + 1000));


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


    public List<Value> getBuffer() {
        return null;  //auto generated
    }


    public Value recordValue(final Value v)   {
        return null;

    }


    public void recordValues(final List<Value> values) {
//        final PersistenceManager pm = PMF.get().getPersistenceManager();
//        final List<RecordedValue> jdoVals = new ArrayList<RecordedValue>(values.size());
//        for (final Value v : values) {
//            jdoVals.add(new RecordedValue(point, v));
//        }
//        try {
//            pm.makePersistentAll(jdoVals);
//
//        } finally {
//            pm.close();
//        }

    }


    public void moveValuesFromCacheToStore() throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }


    public List<Value> getCache(final Timespan timespan) throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }


    public List<ValueBlobStore> getAllStores() throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }


    public void consolidateDate(final Date timestamp) throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }
}
