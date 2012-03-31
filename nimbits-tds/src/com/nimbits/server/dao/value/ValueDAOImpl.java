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

package com.nimbits.server.dao.value;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.files.*;
import com.nimbits.PMF;
import com.nimbits.client.constants.Const;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.client.model.valueblobstore.ValueBlobStoreFactory;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.orm.ValueBlobStoreEntity;
import com.nimbits.server.time.TimespanServiceFactory;
import com.nimbits.server.value.RecordedValueTransactions;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.Channels;
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

    public ValueDAOImpl(final Point point) {
        this.point = point;
    }

    @Override
    public Value getRecordedValuePrecedingTimestamp(final Date timestamp) throws NimbitsException {

        final List<Value> values =  getTopDataSeries(1, timestamp);
        if (values.size() > 0) {
            return values.get(0);
        }
        else {
            return null;
        }

    }

    @Override
    public List<Value> getTopDataSeries(final int maxValues) throws NimbitsException {
        return getTopDataSeries(maxValues, new Date());

    }

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
            final List<ValueBlobStoreEntity> result = (List<ValueBlobStoreEntity>) q.execute(point.getUUID(), endDate.getTime());
            List<Value> values;
            for (final ValueBlobStoreEntity e : result) {
                values = readValuesFromFile(e.getPath());
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

    @Override
    public List<Value> getDataSegment(final Timespan timespan, final int start, final int end) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {

            final List<Value> retObj = new ArrayList<Value>(end - start);
            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
            q.setFilter("entity == k && minTimestamp <= et && minTimestamp >= st ");
            q.declareParameters("String k, Long et, Long st");
            q.setOrdering("minTimestamp desc");


//
//            final Query q = pm.newQuery(ValueBlobStoreEntity.class,
//                    "entity == k && minTimestamp <= et && minTimestamp >= st ");
//            args = new HashMap<String, Object>(3);
//            args.put("String k", point.getUUID());
//            args.put("Long et", timespan.getEnd().getTime());
//            args.put("Long st", timespan.getStart().getTime());
//            q.setOrdering("minTimestamp descending");
            q.setRange(start, end);
            final List<ValueBlobStore> result = (List<ValueBlobStore>) q.execute(point.getUUID(), timespan.getEnd().getTime(), timespan.getStart().getTime());
            List<Value> values;
            for (final ValueBlobStore e : result) {
                values = readValuesFromFile(e.getPath());
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
            final Map<String, Object> args;

            final Query q = pm.newQuery(ValueBlobStoreEntity.class,
                    "entity == k");
            args = new HashMap<String, Object>(1);
            args.put("k", point.getUUID());
            final List<ValueBlobStore> result = (List<ValueBlobStore>) q.executeWithMap(args);

            return ValueBlobStoreFactory.createValueBlobStores(result);
        } finally {
            pm.close();
        }
    }

    @Override
    public void consolidateDate(final Date timestamp) throws NimbitsException {

        final PersistenceManager pm = PMF.get().getPersistenceManager();
        final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        try {
            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
            q.setFilter("timestamp == t && entity == k");
            q.declareParameters("String k, Long t");
            final List<ValueBlobStore> result = (List<ValueBlobStore>) q.execute(point.getUUID(), timestamp.getTime());
            BlobKey key;
            final List<Value> values = new ArrayList<Value>(1024);
            for (final ValueBlobStore store : result) {
                values.addAll(readValuesFromFile(store.getPath()));
                key = new BlobKey(store.getKey());
                blobstoreService.delete(key);
            }
            pm.deletePersistentAll(result);
            recordValues(values);

        } finally {
            pm.close();
        }
    }


    @Override
    public void recordValues(final List<Value> values) throws NimbitsException {


        if (values.size() > 0) {

            final Map<Long, List<Value>> map = new HashMap<Long, List<Value>>(Const.CONST_MAX_CACHED_VALUE_SIZE);
            final Map<Long, Long> maxMap = new HashMap<Long, Long>(Const.CONST_MAX_CACHED_VALUE_SIZE);
            final Map<Long, Long> minMap = new HashMap<Long, Long>(Const.CONST_MAX_CACHED_VALUE_SIZE);
            Date zero;
            List<Value> list;
            for (final Value value : values) {
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

            for (final Map.Entry<Long, List<Value>> longListEntry : map.entrySet()) {
                final String json = GsonFactory.getInstance().toJson(longListEntry.getValue());

                try {
                    createBlobStoreEntity(maxMap, minMap, longListEntry.getKey(), json);
                } catch (IOException e) {
                    throw new NimbitsException(e);
                }


            }



        }


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
            final ValueBlobStoreEntity currentStoreEntity;

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
                    ValueBlobStoreEntity(point.getUUID(),new Date(l), mostRecentTimeForDay, earliestForDay, path, key );

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


    private static List<Value> readValuesFromFile(final String path) throws NimbitsException {
        final FileService fileService = FileServiceFactory.getFileService();
        final AppEngineFile file = new AppEngineFile(path);
        final FileReadChannel readChannel;
        try {
            readChannel = fileService.openReadChannel(file, false);

            final BufferedReader reader =
                    new BufferedReader(Channels.newReader(readChannel, "UTF8"));
            final StringBuilder sb = new StringBuilder(1024);
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            final List<Value> models =  GsonFactory.getInstance().fromJson(sb.toString(), GsonFactory.valueListType);
            Collections.sort(models);
            return models;

        } catch (IOException e) {
            throw new NimbitsException(e);
        }

    }



}
