/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.io;


import com.google.common.collect.Range;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.common.Utils;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueModel;
import com.nimbits.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.client.model.valueblobstore.ValueBlobStoreFactory;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.io.blob.BlobStore;
import com.nimbits.server.orm.store.ValueBlobStoreEntity;
import com.nimbits.server.transaction.value.dao.ValueDayHolder;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;

public class BlobStoreImpl implements BlobStore {
    private final Logger log = Logger.getLogger(BlobStoreImpl.class.getName());
    private final PersistenceManagerFactory pmf;
    private final static String folder = "nimbits_data/";
    public BlobStoreImpl(PersistenceManagerFactory pmf) {
        this.pmf = pmf;
        File file = new File(folder);
        if (! file.exists()) {
            file.mkdir();
        }
    }


    @Override
    public List<Value> getTopDataSeries(final Entity entity, final int maxValues, final Date endDate)   {
        final PersistenceManager pm = pmf.getPersistenceManager();
        try {

            final List<Value> retObj = new ArrayList<Value>(maxValues);

            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
            q.setFilter("minTimestamp <= t && entity == k");
            q.declareParameters("String k, Long t");
            q.setOrdering("minTimestamp desc");
            q.setRange(0, maxValues);

            final List<ValueBlobStoreEntity> result = (List<ValueBlobStoreEntity>) q.execute(entity.getKey(), endDate.getTime());

            for (final ValueBlobStoreEntity e : result) {
                List<Value> values =  readValuesFromFile(e.getBlobKey(), e.getLength());
                //log.info("extracted " + entity.getKey() + " " + values.size());
                for (final Value vx : values) {
                    if (vx.getTimestamp().getTime() <= endDate.getTime()) {
                        retObj.add(vx);
                    }

                    if (retObj.size() >= maxValues) {
                        break;
                    }
                }
            }
            return retObj;
        } finally {
            pm.close();
        }
    }




    @Override
    public List<Value> getDataSegment(final Entity entity, final Range<Date> timespan) {
        final PersistenceManager pm = pmf.getPersistenceManager();
        try {
            final List<Value> retObj = new ArrayList<Value>();
            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
            q.setFilter("entity == k && minTimestamp <= et && minTimestamp >= st ");
            q.declareParameters("String k, Long et, Long st");
            q.setOrdering("minTimestamp desc");

            final Iterable<ValueBlobStore> result = (Iterable<ValueBlobStore>) q.execute(entity.getKey(), timespan.upperEndpoint().getTime(), timespan.lowerEndpoint().getTime());
            for (final ValueBlobStore e : result) {    //todo break out of loop when range is met
                List<Value> values = readValuesFromFile(e.getBlobKey(), e.getLength());
                for (final Value vx : values) {
                    if (timespan.contains(vx.getTimestamp())) {
                        // if (vx.getTimestamp().getTime() <= timespan.upperEndpoint().getTime() && vx.getTimestamp().getTime() >= timespan.lowerEndpoint()) {
                        retObj.add(vx);
                    }
                }
            }
            return retObj;
        } finally {
            pm.close();
        }
    }


    @Override
    public List<ValueBlobStore> getAllStores(final Entity entity) {
        final PersistenceManager pm = pmf.getPersistenceManager();

        try {

            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
            q.setFilter("entity == k");
            q.declareParameters("String k");
            q.setOrdering("timestamp descending");

            final Collection<ValueBlobStore> result = (Collection<ValueBlobStore>) q.execute(entity.getKey());
            log.info("Got all stores for " + entity.getName().getValue() + " total::" + result.size());
            return ValueBlobStoreFactory.createValueBlobStores(result);
        } finally {
            pm.close();
        }
    }

    @Override
    public List<Value> consolidateDate(final Entity entity, final Date timestamp) {


        final PersistenceManager pm = pmf.getPersistenceManager();
        try {

            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
            q.setFilter("timestamp == t && entity == k");
            q.declareParameters("String k, Long t");
            final List<ValueBlobStore> result = (List<ValueBlobStore>) q.execute(entity.getKey(), timestamp.getTime());

            final List<Value> values = new ArrayList<Value>();
            for (final ValueBlobStore store : result) {
                values.addAll( readValuesFromFile((store.getBlobKey()), store.getLength()));
            }

            deleteBlobs(result);

            pm.deletePersistentAll(result);
            return values;

        } finally {
            pm.close();
        }
    }


    @Override
    public void deleteExpiredData(final Entity entity) {


        final PersistenceManager pm = pmf.getPersistenceManager();

        int exp = ((Point) entity).getExpire();
        if (exp > 0) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, exp * -1);
            try {
                final Query q = pm.newQuery(ValueBlobStoreEntity.class);
                q.setFilter("entity == k && maxTimestamp <= et");
                q.declareParameters("String k, Long et");

                final List<ValueBlobStore> result = (List<ValueBlobStore>) q.execute(
                        entity.getKey(), c.getTime().getTime());
                delete(result);
                pm.deletePersistentAll(result);
            } finally {
                pm.close();
            }

        }
    }
    private String readFile(String fn) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(folder + fn));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append('\n');
                line = br.readLine();
            }
            String everything = sb.toString();
            return everything;
        }  finally {
            br.close();
        }

    }

    @Override
    public List<Value> readValuesFromFile(final String key, final long length)  {

        final Type valueListType = new TypeToken<List<ValueModel>>() {
        }.getType();
        List<Value> models;


        try {

            String segment = readFile(key);
            if (!Utils.isEmptyString(segment)) {
                models = GsonFactory.getInstance().fromJson(segment, valueListType);
                if (models != null) {
                    Collections.sort(models);
                }
                else {
                    models = Collections.emptyList();
                }
            }
            else {
                models = Collections.emptyList();
            }
            return models;
        } catch (IllegalArgumentException ex) {
            return Collections.emptyList();
        } catch (IOException e) {
            return Collections.emptyList();
        }

    }

    @Override
    public void deleteBlobs(List<ValueBlobStore> result) {
        for (ValueBlobStore store : result) {
            final String blobKey =  store.getBlobKey();
            File file = new File(folder + blobKey);
            file.delete();

        }
    }

    @Override
    public void delete(final String key) {
        File file = new File(folder + key);
        file.delete();

    }

    @Override
    public List<ValueBlobStore> createBlobStoreEntity(final Entity entity, final ValueDayHolder holder)  {
        final PersistenceManager pm = pmf.getPersistenceManager();

        try {
            final String json = GsonFactory.getInstance().toJson(holder.getValues());
            String fn = UUID.randomUUID().toString();
            PrintWriter out = new PrintWriter(folder + fn);
            out.println(json);
            out.close();


            Range<Date> range = holder.getTimeRange();
            final Date mostRecentTimeForDay = range.upperEndpoint();
            final Date earliestForDay = range.lowerEndpoint();
            final ValueBlobStoreEntity currentStoreEntity = new
                    ValueBlobStoreEntity(entity.getKey(),
                    holder.getStartOfDay(),
                    mostRecentTimeForDay,
                    earliestForDay,
                    fn,
                    json.length(),
                    false
            );

            currentStoreEntity.validate();

            pm.makePersistent(currentStoreEntity);

            pm.flush();
            List<ValueBlobStore>  result = ValueBlobStoreFactory.createValueBlobStore(currentStoreEntity);
            out.close();
            return result;

        } catch (IOException e) {

            return Collections.emptyList();
        } finally {


            pm.close();
        }

    }
    @Override
    public List<ValueBlobStore> mergeTimespan(final Entity entity, final Range<Date> timespan)   {
        final PersistenceManager pm = pmf.getPersistenceManager();


        PrintWriter out = null;
        try {
            String fn = UUID.randomUUID().toString();

            out = new PrintWriter(fn);

            final Query q = pm.newQuery(ValueBlobStoreEntity.class);

            q.setFilter("entity == k && minTimestamp <= et && minTimestamp >= st ");
            q.declareParameters("String k, Long et, Long st");
            q.setOrdering("minTimestamp desc");

            final List<ValueBlobStore> result = (List<ValueBlobStore>) q.execute(
                    entity.getKey(),
                    timespan.upperEndpoint().getTime(),
                    timespan.lowerEndpoint().getTime());


            Collection<Value> combined = new ArrayList<Value>();
            Date timestamp = null;
            for (ValueBlobStore store : result) {
                if (timestamp == null || timestamp.getTime() > store.getTimestamp().getTime()) {
                    timestamp = store.getTimestamp();

                }
                List<Value> read =  readValuesFromFile((store.getBlobKey()), store.getLength());
                combined.addAll(read);
                delete(store.getBlobKey());

            }


            pm.deletePersistentAll(result);


            long max = 0;
            long min = 0;
            for (Value v : combined) {
                if (v.getTimestamp().getTime() > max) {
                    max = v.getTimestamp().getTime();
                }
                if (v.getTimestamp().getTime() < min || min == 0) {
                    min = v.getTimestamp().getTime();
                }
            }

            String json = GsonFactory.getInstance().toJson(combined);
            // byte[] compressed = CompressionImpl.compressBytes(json);
            out.print(json);
            out.close();


            ValueBlobStore currentStoreEntity = new ValueBlobStoreEntity(
                    entity.getKey(),
                    timestamp,
                    new Date(max),
                    new Date(min),
                    fn,
                    json.length(), false);

            currentStoreEntity.validate();
            pm.makePersistent(currentStoreEntity);
            pm.flush();
            return ValueBlobStoreFactory.createValueBlobStore(currentStoreEntity);
        } catch (IOException ex) {
            return Collections.emptyList();

        } finally {
            if (out != null) {
                out.close();
            }
            pm.close();
        }

    }

    @Override
    public void delete(List<ValueBlobStore> result) {
        for (ValueBlobStore store: result) {
            delete(store.getBlobKey());
        }
    }
}