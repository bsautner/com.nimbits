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

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.files.*;
import com.google.apphosting.api.*;
import com.nimbits.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.timespan.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.model.valueblobstore.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.logging.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.time.*;
import com.nimbits.server.value.*;

import javax.jdo.*;
import java.io.*;
import java.nio.channels.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/22/12
 * Time: 11:05 AM
 */
@SuppressWarnings("unchecked")
public class ValueDAOImpl implements RecordedValueTransactions {
    private final Point point;

    public ValueDAOImpl(final Point aPoint) {
        this.point = aPoint;
    }

    @Override
    public Value getRecordedValuePrecedingTimestamp(final Date timestamp) throws NimbitsException {

        final List<Value> values =  getTopDataSeries(1, timestamp);
        return values.isEmpty() ? null : values.get(0);

    }

    @Override
    public List<Value> getTopDataSeries(final int maxValues) throws NimbitsException {
        return getTopDataSeries(maxValues, new Date());

    }

    @SuppressWarnings("ObjectAllocationInLoop")
    @Override
    public List<Value> getTopDataSeries(final int maxValues, final Date endDate) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {

            final List<Value> retObj = new ArrayList<Value>(maxValues);

            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
            q.setFilter("minTimestamp <= t && entity == k");
            q.declareParameters("String k, Long t");
            q.setOrdering("minTimestamp desc");

            q.setRange(0, maxValues);
            final Iterable<ValueBlobStore> result = (Iterable<ValueBlobStore>) q.execute(point.getKey(), endDate.getTime());
            List<Value> values;
            for (final ValueBlobStore e : result) {
                values = readValuesFromFile(new BlobKey(e.getBlobKey()), e.getLength());
                for (final Value vx : values) {
                    if (vx.getTimestamp().getTime() <= endDate.getTime()) {
                        retObj.add(vx);
                    }
                    if (retObj.size() >= maxValues) {
                        return retObj;
                    }
                }
            }
            return retObj;
        } finally {
            pm.close();
        }
    }

    @Override
    public List<Value> getDataSegment(final Timespan timespan) throws NimbitsException {
        return getDataSegment(timespan, 0, 1000);
    }

    @SuppressWarnings("ObjectAllocationInLoop")
    @Override
    public List<Value> getDataSegment(final Timespan timespan, final int start, final int end) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            final List<Value> retObj = new ArrayList<Value>(end - start);
            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
            q.setFilter("entity == k && minTimestamp <= et && minTimestamp >= st ");
            q.declareParameters("String k, Long et, Long st");
            q.setOrdering("minTimestamp desc");
            q.setRange(start, end);
            final Iterable<ValueBlobStore> result = (Iterable<ValueBlobStore>) q.execute(point.getKey(), timespan.getEnd().getTime(), timespan.getStart().getTime());
            List<Value> values;
            for (final ValueBlobStore e : result) {
                values = readValuesFromFile(new BlobKey(e.getBlobKey()), e.getLength());
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
    public List<ValueBlobStore> getAllStores() throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {

            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
            q.setFilter("entity == k");
            q.declareParameters("String k");
            q.setRange(0, 1000);
            q.setOrdering("timestamp descending");

            final List<ValueBlobStore> result = (List<ValueBlobStore>) q.execute(point.getKey());

            return ValueBlobStoreFactory.createValueBlobStores(result);
        } finally {
            pm.close();
        }
    }

    @SuppressWarnings("ObjectAllocationInLoop")
    @Override
    public void consolidateDate(final Date timestamp) throws NimbitsException {

        final PersistenceManagerFactory persistenceManagerFactory = PMF.get();
        final PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
        final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        try {
            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
            q.setFilter("timestamp == t && entity == k");
            q.declareParameters("String k, Long t");
            final Collection<ValueBlobStore> result = (Collection<ValueBlobStore>) q.execute(point.getKey(), timestamp.getTime());
            BlobKey key;
            final List<Value> values = new ArrayList<Value>(Const.CONST_DEFAULT_LIST_SIZE);
            for (final ValueBlobStore store : result) {
                values.addAll(readValuesFromFile(new BlobKey(store.getBlobKey()), store.getLength()));
                key = new BlobKey(store.getBlobKey());
                try {
                    blobstoreService.delete(key);
                } catch (BlobstoreFailureException e) {
                    LogHelper.logException(ValueDAOImpl.class, e);
                }
            }
            pm.deletePersistentAll(result);
            recordValues(values);

        } finally {
            pm.close();
        }
    }


    @Override
    public void recordValues(final List<Value> values) throws NimbitsException {


        if (!values.isEmpty()) {

            final Map<Long, List<Value>> map = new HashMap<Long, List<Value>>(Const.CONST_MAX_CACHED_VALUE_SIZE);
            final Map<Long, Long> maxMap = new HashMap<Long, Long>(Const.CONST_MAX_CACHED_VALUE_SIZE);
            final Map<Long, Long> minMap = new HashMap<Long, Long>(Const.CONST_MAX_CACHED_VALUE_SIZE);
            Date zero;
            List<Value> list;
            for (final Value value : values) {
                if (valueHealthy(value)) {
                    zero= TimespanServiceFactory.getInstance().zeroOutDate(value.getTimestamp());
                    if (map.containsKey(zero.getTime())) {
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
                        list = new ArrayList<Value>(Const.CONST_MAX_CACHED_VALUE_SIZE);
                        list.add(value);
                        map.put(zero.getTime(),list);
                        maxMap.put(zero.getTime(), value.getTimestamp().getTime());
                        minMap.put(zero.getTime(), value.getTimestamp().getTime());
                    }


                }

            }

            for (final Map.Entry<Long, List<Value>> longListEntry : map.entrySet()) {
                if (!longListEntry.getValue().isEmpty()) {
                    final String json = GsonFactory.getInstance().toJson(longListEntry.getValue());

                    try {
                        createBlobStoreEntity(maxMap, minMap, longListEntry.getKey(), json);
                    } catch (IOException e) {
                        throw new NimbitsException(e);
                    }


                }
            }



        }


    }

    private static boolean valueHealthy(final Value value) {

        return !Double.isInfinite(value.getDoubleValue())
                && !Double.isNaN(value.getDoubleValue());


    }

    private void createBlobStoreEntity(final Map<Long, Long> maxMap, final Map<Long, Long> minMap, final Long l, final String json) throws IOException, NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final FileService fileService = FileServiceFactory.getFileService();
        try {
            final AppEngineFile file;
            final BlobKey key;
            final String path;
            final FileWriteChannel writeChannel;
            final PrintWriter out;
            final ValueBlobStore currentStoreEntity;

            file = fileService.createNewBlobFile(Const.CONTENT_TYPE_PLAIN);
            path = file.getFullPath();

            writeChannel = fileService.openWriteChannel(file, true);
            out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
            out.println(json);
            out.close();
            writeChannel.closeFinally();
            key = fileService.getBlobKey(file);
            final Date mostRecentTimeForDay = new Date(maxMap.get(l));
            final Date earliestForDay = new Date(minMap.get(l));
            currentStoreEntity = new
                    ValueBlobStoreEntity(point.getKey(),new Date(l), mostRecentTimeForDay, earliestForDay, path, key, json.length() );

            pm.makePersistent(currentStoreEntity);
            pm.flush();
        }
        catch (Exception ex) {
            throw new NimbitsException(ex);

        } finally {
            pm.close();
        }
    }

    @Override
    public void moveValuesFromCacheToStore() throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }

    @Override
    public List<Value> getCache(final Timespan timespan) throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }

    @Override
    public List<Value> getBuffer() throws NimbitsException {
        throw new NimbitsException("Not Implimented");
    }

    @Override
    public Value recordValue(final Value v) throws NimbitsException {
        throw new NimbitsException("Not Implimented");
    }


//    private static List<Value> readValuesFromFile(final String path) throws NimbitsException {
//        final FileService fileService = FileServiceFactory.getFileService();
//        final AppEngineFile file = new AppEngineFile(path);
//        final FileReadChannel readChannel;
//        try {
//            readChannel = fileService.openReadChannel(file, false);
//
//            final BufferedReader reader =
//                    new BufferedReader(Channels.newReader(readChannel, "UTF8"));
//            final StringBuilder sb = new StringBuilder(1024);
//            String line;
//
//            while ((line = reader.readLine()) != null) {
//                sb.append(line);
//            }
//
//            final List<Value> models =  GsonFactory.getInstance().fromJson(sb.toString(), GsonFactory.valueListType);
//            Collections.sort(models);
//            return models;
//        } catch (ApiProxy.ApiDeadlineExceededException ex) {
//            throw new NimbitsException(ex);
//
//
//        } catch (IOException e) {
//            throw new NimbitsException(e);
//        }
//
//    }

    protected static List<Value> readValuesFromFile(final BlobKey blobKey, final long length) throws NimbitsException {

        try {
            BlobstoreService blobStoreService = BlobstoreServiceFactory.getBlobstoreService();
            String segment = new String(blobStoreService.fetchData(blobKey, 0, length));
            final List<Value> models =  GsonFactory.getInstance().fromJson(segment, GsonFactory.valueListType);
            Collections.sort(models);
            return models;
        } catch (ApiProxy.ApiDeadlineExceededException ex) {
            throw new NimbitsException(ex);


        }

    }

}
