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

package com.nimbits.cloudplatform.server.transactions.value.dao;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.gson.reflect.TypeToken;
import com.nimbits.cloudplatform.client.common.Utils;
import com.nimbits.cloudplatform.client.constants.Const;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueModel;
import com.nimbits.cloudplatform.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.cloudplatform.client.model.valueblobstore.ValueBlobStoreModel;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.orm.ValueBlobStoreEntity;
import com.nimbits.cloudplatform.server.process.task.TaskImpl;
import org.apache.commons.lang3.Range;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.channels.Channels;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/22/12
 * Time: 11:05 AM
 */

public class ValueDaoImpl implements ValueDao {
    private static final int INT = 1024;
    private static final int TOP = 1;
    private final PersistenceManagerFactory pmf;

    public ValueDaoImpl(PersistenceManagerFactory pmf) {
        this.pmf = pmf;
    }

    @Override
    public List<ValueBlobStore> createValueBlobStores(final Collection<ValueBlobStore> store) {
        final List<ValueBlobStore> retObj = new ArrayList<ValueBlobStore>(store.size());
        for (final ValueBlobStore v : store) {
            if (v.getLength() > 0) {
                retObj.add(createValueBlobStore(v));
            }
        }
        return retObj;

    }

    @Override
    public ValueBlobStore createValueBlobStore(final ValueBlobStore store) {
        return new ValueBlobStoreModel(store);

    }


    @Override
    public List<Value> getRecordedValuePrecedingTimestamp(final Entity entity, final Date timestamp)   {

        return getTopDataSeries(entity, TOP, timestamp);


    }


    @Override
    public List<Value> getTopDataSeries(final Entity entity, final int maxValues)  {
        return getTopDataSeries(entity, maxValues, new Date());

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
                List<Value> values = readValuesFromFile(new BlobKey(e.getBlobKey()), e.getLength());
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
    public List<ValueBlobStore> getBlobStoreByBlobKey(BlobKey key) {
        final PersistenceManager pm = pmf.getPersistenceManager();

        try {

            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
            q.setFilter("blobkey == b");
            q.declareImports("import com.google.appengine.api.blobstore.BlobKey");
            q.declareParameters("BlobKey b");
            q.setRange(0, 1);

            final Collection<ValueBlobStore> result = (Collection<ValueBlobStore>) q.execute(key);

            return createValueBlobStores(result);
        } finally {
            pm.close();
        }
    }


    @Override
    public List<Value> getDataSegment(final Entity entity, final Range<Long> timespan) {
        final PersistenceManager pm = pmf.getPersistenceManager();
        try {
            final List<Value> retObj = new ArrayList<Value>();
            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
            q.setFilter("entity == k && minTimestamp <= et && minTimestamp >= st ");
            q.declareParameters("String k, Long et, Long st");
            q.setOrdering("minTimestamp desc");

            final Iterable<ValueBlobStore> result = (Iterable<ValueBlobStore>) q.execute(entity.getKey(), timespan.getMaximum(), timespan.getMinimum());
            for (final ValueBlobStore e : result) {    //todo break out of loop when range is met
                List<Value> values = readValuesFromFile(new BlobKey(e.getBlobKey()), e.getLength());
                for (final Value vx : values) {
                    if (vx.getTimestamp().getTime() <= timespan.getMaximum() && vx.getTimestamp().getTime() >= timespan.getMinimum()) {
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
            q.setRange(0, 1000);
            q.setOrdering("timestamp descending");

            final Collection<ValueBlobStore> result = (Collection<ValueBlobStore>) q.execute(entity.getKey());

            return createValueBlobStores(result);
        } finally {
            pm.close();
        }
    }

    @Override
    public void consolidateDate(final Entity entity, final Date timestamp) throws IOException {


        final PersistenceManager pm = pmf.getPersistenceManager();
        try {

            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
            q.setFilter("timestamp == t && entity == k");
            q.declareParameters("String k, Long t");
            final List<ValueBlobStore> result = (List<ValueBlobStore>) q.execute(entity.getKey(), timestamp.getTime());

            final List<Value> values = new ArrayList<Value>(Const.CONST_DEFAULT_LIST_SIZE);
            for (final ValueBlobStore store : result) {
                values.addAll(readValuesFromFile(new BlobKey(store.getBlobKey()), store.getLength()));
            }
            startBlobDeleteTask(result);
            for (ValueBlobStore store : result) {
                final BlobKey blobKey = new BlobKey(store.getBlobKey());
                final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
                blobstoreService.delete(blobKey);
            }

            pm.deletePersistentAll(result);
            recordValues(entity, values);

        } finally {
            pm.close();
        }
    }




    @Override
    public ValueBlobStore mergeTimespan(final Entity entity, final Range<Date> timespan) throws IOException {
        final PersistenceManager pm = pmf.getPersistenceManager();
        final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        final FileService fileService = FileServiceFactory.getFileService();


        PrintWriter out = null;
        try {
            final AppEngineFile file = fileService.createNewBlobFile(Const.CONTENT_TYPE_PLAIN);
            final FileWriteChannel writeChannel = fileService.openWriteChannel(file, true);
            out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));

            final Query q = pm.newQuery(ValueBlobStoreEntity.class);

            q.setFilter("entity == k && minTimestamp <= et && minTimestamp >= st ");
            q.declareParameters("String k, Long et, Long st");
            q.setOrdering("minTimestamp desc");

            final List<ValueBlobStore> result = (List<ValueBlobStore>) q.execute(
                    entity.getKey(),
                    timespan.getMaximum().getTime(),
                    timespan.getMinimum().getTime());


            Collection<Value> combined = new ArrayList<Value>(INT);
            Date timestamp = null;
            for (ValueBlobStore store : result) {
                if (timestamp == null || timestamp.getTime() > store.getTimestamp().getTime()) {
                    timestamp = store.getTimestamp();

                }
                List<Value> read = readValuesFromFile(new BlobKey(store.getBlobKey()), store.getLength());
                combined.addAll(read);
                blobstoreService.delete(new BlobKey(store.getBlobKey()));
            }
            startBlobDeleteTask(result);

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
            writeChannel.closeFinally();
            final BlobKey key = fileService.getBlobKey(file);

            ValueBlobStore currentStoreEntity = new ValueBlobStoreEntity(
                    entity.getKey(),
                    timestamp,
                    new Date(max),
                    new Date(min),
                    key,
                    json.length(), false);

            currentStoreEntity.validate();
            pm.makePersistent(currentStoreEntity);
            pm.flush();
            return createValueBlobStore(currentStoreEntity);

        } finally {
            if (out != null) {
                out.close();
            }
            pm.close();
        }

    }

    @Override
    public void purgeValues(final Entity entity) {
        final PersistenceManager pm = pmf.getPersistenceManager();
        final Query q = pm.newQuery(ValueBlobStoreEntity.class);

        q.setFilter("entity == k");
        q.declareParameters("String k");

        final List<ValueBlobStore> result = (List<ValueBlobStore>) q.execute(
                entity.getKey());
        try {
            startBlobDeleteTask(result);
            pm.deletePersistentAll(result);
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
                startBlobDeleteTask(result);
                pm.deletePersistentAll(result);
            } finally {
                pm.close();
            }

        }
    }


    private void startBlobDeleteTask(List<ValueBlobStore> result) {

        for (ValueBlobStore st : result) {
            TaskImpl.startDeleteBlobTask(new BlobKey(st.getBlobKey()));
        }
    }
    private static Date zeroOutDateToStart(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MILLISECOND, c.get(Calendar.MILLISECOND) * -1);
        c.add(Calendar.SECOND, c.get(Calendar.SECOND) * -1);
        c.add(Calendar.MINUTE, c.get(Calendar.MINUTE) * -1);
        c.add(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) * -1);
        return c.getTime();
    }


    private Date zeroOutDateToEnd(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(zeroOutDateToStart(date));
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    @Override
    public List<ValueBlobStore> recordValues(final Entity entity, final List<Value> values)  {
        if (!values.isEmpty()) {

            final Map<Long, List<Value>> map = new HashMap<Long, List<Value>>(values.size());
            final Map<Long, Long> maxMap = new HashMap<Long, Long>(values.size());
            final Map<Long, Long> minMap = new HashMap<Long, Long>(values.size());
            //log.info("ValueDAO: recording " + values.size() + " to " + entity.getKey());
            for (final Value value : values) {
                if (valueHealthy(value)) {
                    //zero out the date of the current value we're working with
                    final Date zero =  zeroOutDateToStart(value.getTimestamp());
                    if (map.containsKey(zero.getTime())) {
                        //a new value for an existing day
                        map.get(zero.getTime()).add(value);
                        if (maxMap.get(zero.getTime()) < value.getTimestamp().getTime()) {
                            maxMap.remove(zero.getTime());
                            maxMap.put(zero.getTime(), value.getTimestamp().getTime()); //keep the most recent value in the batch
                        }
                        if (minMap.get(zero.getTime()) > value.getTimestamp().getTime()) {
                            minMap.remove(zero.getTime());
                            minMap.put(zero.getTime(), value.getTimestamp().getTime()); //keep the earliest value in the batch
                        }
                    } else {
                        //create a new list for a new day
                        final List<Value> list = new ArrayList<Value>(Const.CONST_MAX_CACHED_VALUE_SIZE);
                        list.add(value);
                        map.put(zero.getTime(), list);
                        maxMap.put(zero.getTime(), value.getTimestamp().getTime());
                        minMap.put(zero.getTime(), value.getTimestamp().getTime());
                    }
                }
            }

            final List<ValueBlobStore> retObj = new ArrayList<ValueBlobStore>(map.size());
            for (final Map.Entry<Long, List<Value>> longListEntry : map.entrySet()) {
                if (!longListEntry.getValue().isEmpty()) {
                    final String json = GsonFactory.getInstance().toJson(longListEntry.getValue());

                    List<ValueBlobStore> b = createBlobStoreEntity(entity, maxMap, minMap, longListEntry.getKey(), json );
                    if (! b.isEmpty()) {
                        retObj.addAll(b);
                    }

                }
            }
            return retObj;
        }
        return new ArrayList<ValueBlobStore>(0);
    }

    private boolean valueHealthy(final Value value) {

        return !Double.isInfinite(value.getDoubleValue())
                && !Double.isNaN(value.getDoubleValue());


    }

    private List<ValueBlobStore> createBlobStoreEntity(final Entity entity, final Map<Long, Long> maxMap, final Map<Long, Long> minMap, final Long l, final String json )  {
        final PersistenceManager pm = pmf.getPersistenceManager();

        final List<ValueBlobStore> retList = new ArrayList<ValueBlobStore>(1);
        try {
            final FileService fileService = FileServiceFactory.getFileService();
            final AppEngineFile file = fileService.createNewBlobFile(Const.CONTENT_TYPE_PLAIN);
            final FileWriteChannel writeChannel = fileService.openWriteChannel(file, true);
            PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
            // char[] uc = json.toCharArray();
            //log.info("createBlobStoreEntity: path " + path + "  " + entity.getKey());
            out.println(json);
            out.close();
            writeChannel.closeFinally();
            //log.info("createBlobStoreEntity: wrote ok");
            final BlobKey key = fileService.getBlobKey(file);
            //log.info("createBlobStoreEntity: key ok");
            final Date mostRecentTimeForDay = new Date(maxMap.get(l));
            final Date earliestForDay = new Date(minMap.get(l));
            //log.info("createBlobStoreEntity: " + mostRecentTimeForDay + " " + earliestForDay);
            final ValueBlobStoreEntity currentStoreEntity = new
                    ValueBlobStoreEntity(entity.getKey(),
                    new Date(l),
                    mostRecentTimeForDay,
                    earliestForDay,
                    key,
                    json.length(),
                    false
            );
            //log.info("createBlobStoreEntity: currentStoreEntity ok");
            currentStoreEntity.validate();
            //log.info("createBlobStoreEntity: validate ok");
            pm.makePersistent(currentStoreEntity);
            //log.info("createBlobStoreEntity: makePersistent ok");
            pm.flush();
            //log.info("createBlobStoreEntity: flush ok " + entity.getKey());
            //log.info(currentStoreEntity.getEntity() + " " + currentStoreEntity.getKey());
            ValueBlobStore result = createValueBlobStore(currentStoreEntity);
            out.close();
            if (result != null) {
                retList.add(result);
            }
            return retList;

        } catch (IOException e) {

            return Collections.emptyList();
        } finally {


            pm.close();
        }

    }


    @Override
    public List<Value> readValuesFromFile(final BlobKey blobKey, final long length)  {
        //TODO Delete file
        final Type valueListType = new TypeToken<List<ValueModel>>() {
        }.getType();
        List<Value> models;

        try {
            BlobstoreService blobStoreService = BlobstoreServiceFactory.getBlobstoreService();

            String segment = new String(blobStoreService.fetchData(blobKey, 0, length));
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
        }

    }



}
