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

package com.nimbits.server.transactions.dao.value;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.apphosting.api.ApiProxy;
import com.nimbits.PMF;
import com.nimbits.client.constants.Const;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.client.model.valueblobstore.ValueBlobStoreModel;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.orm.ValueBlobStoreEntity;
import com.nimbits.server.process.task.TaskFactory;
import com.nimbits.server.time.TimespanServiceFactory;
import com.nimbits.server.transactions.service.value.ValueTransactions;
import org.springframework.stereotype.Repository;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/22/12
 * Time: 11:05 AM
 */
@SuppressWarnings("unchecked")
@Repository("valueDao")
public class ValueDAOImpl implements ValueTransactions {
    private static final int INT = 1024;
    public static final int MAX_VALUES = 1;

    private final Logger log = Logger.getLogger(ValueDAOImpl.class.getName());

    public static  List<ValueBlobStore> createValueBlobStores(final Collection<ValueBlobStore> store) {
        final List<ValueBlobStore> retObj = new ArrayList<ValueBlobStore>(store.size());
        for (final ValueBlobStore v : store) {
            if ( v.getLength() > 0) {
                retObj.add(createValueBlobStore(v));
            }
        }
        return retObj;

    }

    public static ValueBlobStore createValueBlobStore(final ValueBlobStore store) {
        return new ValueBlobStoreModel(store);

    }

    @Override
    public List<List<Value>> splitUpList(List<Value> original) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Value> getRecordedValuePrecedingTimestamp(final Entity entity,final Date timestamp) throws NimbitsException {

        return getTopDataSeries(entity, MAX_VALUES, timestamp);


    }

    @Override
    public List<Value> getTopDataSeries(final Entity entity, final int maxValues) throws NimbitsException {
        return getTopDataSeries(entity, maxValues, new Date());

    }


    @Override
    public List<Value> getTopDataSeries(final Entity entity, final int maxValues, final Date endDate) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {

            final List<Value> retObj = new ArrayList<Value>(maxValues);

            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
            q.setFilter("minTimestamp <= t && entity == k");
            q.declareParameters("String k, Long t");
            q.setOrdering("minTimestamp desc");

            q.setRange(0, maxValues);
            final Iterable<ValueBlobStore> result = (Iterable<ValueBlobStore>) q.execute(entity.getKey(), endDate.getTime());
            for (final ValueBlobStore e : result) {
                List<Value> values = readValuesFromFile(new BlobKey(e.getBlobKey()), e.getLength());
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
    public List<ValueBlobStore> getBlobStoreByBlobKey(BlobKey key) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {

            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
            q.setFilter("blobkey == b");
            q.declareImports("import com.google.appengine.api.blobstore.BlobKey");
            q.declareParameters("BlobKey b");
            q.setRange(0, 1);

            final Collection<ValueBlobStore> result = (Collection<ValueBlobStore>) q.execute(key);
            log.info(result.size() + " results");
            return createValueBlobStores(result);
        } finally {
            pm.close();
        }
    }

    @Override
    public List<Value> getDataSegment(final Entity entity, final Timespan timespan) throws NimbitsException {
        return getDataSegment(entity, timespan, 0, 1000);
    }

    @SuppressWarnings("ObjectAllocationInLoop")
    @Override
    public List<Value> getDataSegment(final Entity entity, final Timespan timespan, final int start, final int end) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final List<Value> retObj = new ArrayList<Value>(end - start);
            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
            q.setFilter("entity == k && minTimestamp <= et && minTimestamp >= st ");
            q.declareParameters("String k, Long et, Long st");
            q.setOrdering("minTimestamp desc");
            q.setRange(start, end);
            final Iterable<ValueBlobStore> result = (Iterable<ValueBlobStore>) q.execute(entity.getKey(), timespan.getEnd().getTime(), timespan.getStart().getTime());
            for (final ValueBlobStore e : result) {
                List<Value> values = readValuesFromFile(new BlobKey(e.getBlobKey()), e.getLength());
                for (final Value vx : values) {
                    if (vx.getTimestamp().getTime() <= timespan.getEnd().getTime() && vx.getTimestamp().getTime() >= timespan.getStart().getTime()) {
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
    public List<ValueBlobStore> getAllStores(final Entity entity) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

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

    @SuppressWarnings("ObjectAllocationInLoop")
    @Override
    public void consolidateDate(final Entity entity, final Date timestamp) throws NimbitsException {


        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
            q.setFilter("timestamp == t && entity == k");
            q.declareParameters("String k, Long t");
            final List<ValueBlobStore> result = (List<ValueBlobStore>) q.execute(entity.getKey(), timestamp.getTime());
            mergeResults(pm, entity, result);

        } finally {
            pm.close();
        }
    }

    private void mergeResults(final PersistenceManager pm, final Entity entity, final List<ValueBlobStore> result) throws NimbitsException {
        final List<Value> values = new ArrayList<Value>(Const.CONST_DEFAULT_LIST_SIZE);
        for (final ValueBlobStore store : result) {
            values.addAll(readValuesFromFile(new BlobKey(store.getBlobKey()), store.getLength()));
        }
        startBlobDeleteTask(result);
        pm.deletePersistentAll(result);
        recordValues(entity, values);
    }

    @SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
    @Override
    public ValueBlobStore mergeTimespan(final Entity entity, final Timespan timespan) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        final FileService fileService = FileServiceFactory.getFileService();


        PrintWriter out = null;
        try {
            final AppEngineFile file = fileService.createNewBlobFile(Const.CONTENT_TYPE_PLAIN);
            final String path = file.getFullPath();
            final FileWriteChannel writeChannel = fileService.openWriteChannel(file, true);
            out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));

            final Query q = pm.newQuery(ValueBlobStoreEntity.class);

            q.setFilter("entity == k && minTimestamp <= et && minTimestamp >= st ");
            q.declareParameters("String k, Long et, Long st");
            q.setOrdering("minTimestamp desc");

            final List<ValueBlobStore> result = (List<ValueBlobStore>) q.execute(
                    entity.getKey(),
                    timespan.getEnd().getTime(),
                    timespan.getStart().getTime());


            Collection<Value> combined = new ArrayList<Value>(INT);
            Date timestamp = null;
            for (ValueBlobStore store : result) {
                if (timestamp == null|| timestamp.getTime() > store.getTimestamp().getTime()) {
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
                    path, key,
                    json.length(), false);

            currentStoreEntity.validate();
            pm.makePersistent(currentStoreEntity);
            pm.flush();
            return createValueBlobStore(currentStoreEntity);

        } catch (IOException e) {
            throw new NimbitsException(e);
        } finally {
            if (out!= null) {
                out.close();
            }
            pm.close();
        }

    }

    @Override
    public void purgeValues(final Entity entity) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final Query q = pm.newQuery(ValueBlobStoreEntity.class);

        q.setFilter("entity == k");
        q.declareParameters("String k");

        final List<ValueBlobStore> result = (List<ValueBlobStore>) q.execute(
                entity.getKey());
        try{
            startBlobDeleteTask(result);
            pm.deletePersistentAll(result);
        }
        finally {
            pm.close();
        }
    }

    @Override
    public void deleteExpiredData(final Entity entity) {


        final PersistenceManager pm = PMF.get().getPersistenceManager();

        int exp = ((Point)entity).getExpire();
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
            }
            finally {
                pm.close();
            }

        }
    }

    @Override
    public int preloadTimespan(final Entity entity, Timespan timespan) throws NimbitsException {
        throw new NimbitsException("not implemented");
    }

    @Override
    public List<Value> getPreload(final Entity entity,int start) throws NimbitsException {
        throw new NimbitsException("not implemented");
    }

    private void startBlobDeleteTask(List<ValueBlobStore> result) {
        log.info("Deleting " + result.size() + "blobs");
        for (ValueBlobStore st : result) {
            TaskFactory.getInstance().startDeleteBlobTask(new BlobKey(st.getBlobKey()));
        }
    }

    @Override
    public List<ValueBlobStore> recordValues(final Entity entity, final List<Value> values) throws NimbitsException {
        if (!values.isEmpty()) {

            final Map<Long, List<Value>> map = new HashMap<Long, List<Value>>(values.size());
            final Map<Long, Long> maxMap = new HashMap<Long, Long>(values.size());
            final Map<Long, Long> minMap = new HashMap<Long, Long>(values.size());

            for (final Value value : values) {
                if (valueHealthy(value)) {
                    //zero out the date of the current value we're working with
                    final Date zero = TimespanServiceFactory.getInstance().zeroOutDate(value.getTimestamp());
                    if (map.containsKey(zero.getTime())) {
                        //a new value for an existing day
                        map.get(zero.getTime()).add(value);
                        if (maxMap.get(zero.getTime()) < value.getTimestamp().getTime()) {
                            maxMap.remove(zero.getTime());
                            maxMap.put(zero.getTime(),value.getTimestamp().getTime()); //keep the most recent value in the batch
                        }
                        if (minMap.get(zero.getTime()) > value.getTimestamp().getTime()) {
                            minMap.remove(zero.getTime());
                            minMap.put(zero.getTime(),value.getTimestamp().getTime()); //keep the earliest value in the batch
                        }
                    }
                    else {
                        //create a new list for a new day
                        final List<Value> list = new ArrayList<Value>(Const.CONST_MAX_CACHED_VALUE_SIZE);
                        list.add(value);
                        map.put(zero.getTime(),list);
                        maxMap.put(zero.getTime(), value.getTimestamp().getTime());
                        minMap.put(zero.getTime(), value.getTimestamp().getTime());
                    }
                }
            }

            final List<ValueBlobStore> retObj = new ArrayList<ValueBlobStore>(map.size());
            for (final Map.Entry<Long, List<Value>> longListEntry : map.entrySet()) {
                if (!longListEntry.getValue().isEmpty()) {
                    final String json = GsonFactory.getInstance().toJson(longListEntry.getValue());

                    try {
                        ValueBlobStore b = createBlobStoreEntity(entity, maxMap, minMap, longListEntry.getKey(), json, 0);
                        retObj.add(b);
                    } catch (IOException e) {
                        throw new NimbitsException(e);
                    }
                }
            }
            return retObj;
        }
        return new ArrayList<ValueBlobStore>(0);
    }

    private static boolean valueHealthy(final Value value) {

        return !Double.isInfinite(value.getDoubleValue())
                && !Double.isNaN(value.getDoubleValue());


    }

    private ValueBlobStore createBlobStoreEntity(final Entity entity, final Map<Long, Long> maxMap, final Map<Long, Long> minMap, final Long l, final String json, final int retryCount) throws IOException, NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final FileService fileService = FileServiceFactory.getFileService();
        final AppEngineFile file = fileService.createNewBlobFile(Const.CONTENT_TYPE_PLAIN);
        final String path = file.getFullPath();
        final FileWriteChannel writeChannel = fileService.openWriteChannel(file, true);
        PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
        try {
            // char[] uc = json.toCharArray();

            out.println(json);
            out.close();
            writeChannel.closeFinally();
            final BlobKey key = fileService.getBlobKey(file);
            final Date mostRecentTimeForDay = new Date(maxMap.get(l));
            final Date earliestForDay = new Date(minMap.get(l));
            final ValueBlobStore currentStoreEntity = new
                    ValueBlobStoreEntity(entity.getKey(),
                    new Date(l),
                    mostRecentTimeForDay,
                    earliestForDay,
                    path,
                    key,
                    json.length(),
                    false
            );
            currentStoreEntity.validate();
            pm.makePersistent(currentStoreEntity);
            pm.flush();
            return createValueBlobStore(currentStoreEntity);
        } catch (ApiProxy.ApiDeadlineExceededException ex)  {
            if (retryCount < 10) {
                log.info("data store unavailable - trying again.  Retry count: " + retryCount);
                return  createBlobStoreEntity(entity, maxMap, minMap, l, json, retryCount + 1);
            }
            else {

                log.severe(ex.getMessage() + " need to start a new task");
                throw (ex);
            }


        }

        finally {
            out.close();
            pm.close();
        }
    }

    @Override
    public void moveValuesFromCacheToStore(final Entity entity) throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }

    @Override
    public List<Value> getCache(Entity entity, Timespan timespan) throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }

    @Override
    public List<Value> getBuffer(final Entity entity) throws NimbitsException {
        throw new NimbitsException("Not Implimented");
    }

    @Override
    public Value recordValue(final Entity entity,final Value v) throws NimbitsException {
        throw new NimbitsException("Not Implimented");
    }

    protected static List<Value> readValuesFromFile(final BlobKey blobKey, final long length) throws NimbitsException {

        try {
            BlobstoreService blobStoreService = BlobstoreServiceFactory.getBlobstoreService();

            String segment = new String(blobStoreService.fetchData(blobKey, 0, length));
            final List<Value> models =  GsonFactory.getInstance().fromJson(segment, GsonFactory.valueListType);
            Collections.sort(models);
            return models;
        } catch (IllegalArgumentException ex) {
            return new ArrayList<Value>(0);
        } catch (ApiProxy.ApiDeadlineExceededException ex) {
            throw new NimbitsException(ex);


        }

    }

}
