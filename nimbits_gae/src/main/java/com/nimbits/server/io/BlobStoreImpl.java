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


import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.common.collect.Range;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.Const;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueModel;
import com.nimbits.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.client.model.valueblobstore.ValueBlobStoreFactory;
import com.nimbits.server.io.blob.ValueBlobStoreEntity;
import com.nimbits.server.transaction.value.dao.ValueDayHolder;
import org.springframework.stereotype.Service;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.channels.Channels;
import java.util.*;

@Service
public class BlobStoreImpl implements BlobStore {
    //  private final Logger log = Logger.getLogger(BlobStoreImpl.class.getName());
    private PersistenceManagerFactory persistenceManagerFactory;

    private Gson gson = new GsonBuilder().create();

    public BlobStoreImpl() {


    }

    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }

    @Override
    public List<Value> getTopDataSeries(final Entity entity, final int maxValues, final Date endDate) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();


        final List<Value> retObj = new ArrayList<Value>(maxValues);

        final Query q = pm.newQuery(ValueBlobStoreEntity.class);
        q.setFilter("minTimestamp <= t && entity == k");
        q.declareParameters("String k, Long t");
        q.setOrdering("minTimestamp desc");


        final List<ValueBlobStoreEntity> result = (List<ValueBlobStoreEntity>) q.execute(entity.getKey(), endDate.getTime());

        for (final ValueBlobStoreEntity e : result) {
            if (validateOwnership(entity, e)) {
                List<Value> values = readValuesFromFile(e.getBlobKey(), e.getLength());

                for (final Value vx : values) {
                    if (vx.getTimestamp().getTime() <= endDate.getTime()) {
                        retObj.add(vx);
                    }
                    if (retObj.size() >= maxValues) {
                        break;
                    }

                }
                if (retObj.size() >= maxValues) {
                    break;
                }
            }
        }
        return retObj;

    }


    @Override
    public List<Value> getTopDataSeries(final Entity entity, final int maxValues) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();


        final List<Value> retObj = new ArrayList<Value>(maxValues);

        final Query q = pm.newQuery(ValueBlobStoreEntity.class);
        q.setFilter("entity == k");
        q.declareParameters("String k");
        q.setOrdering("minTimestamp desc");


        final List<ValueBlobStoreEntity> result = (List<ValueBlobStoreEntity>) q.execute(entity.getKey());

        for (final ValueBlobStoreEntity e : result) {
            if (validateOwnership(entity, e)) {
                List<Value> values = readValuesFromFile(e.getBlobKey(), e.getLength());

                for (final Value vx : values) {

                    retObj.add(vx);

                    if (retObj.size() >= maxValues) {
                        break;
                    }

                }
                if (retObj.size() >= maxValues) {
                    break;
                }
            }
        }
        return retObj;

    }

    @Override
    public List<Value> getDataSegment(final Entity entity, final Range<Date> timespan) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        final List<Value> retObj = new ArrayList<Value>();
        final Query q = pm.newQuery(ValueBlobStoreEntity.class);
        q.setFilter("entity == k && minTimestamp <= et && minTimestamp >= st ");
        q.declareParameters("String k, Long et, Long st");
        q.setOrdering("minTimestamp desc");

        final Iterable<ValueBlobStore> result = (Iterable<ValueBlobStore>) q.execute(entity.getKey(), timespan.upperEndpoint().getTime(), timespan.lowerEndpoint().getTime());
        for (final ValueBlobStore e : result) {    //todo break out of loop when range is met
            if (validateOwnership(entity, e)) {
                List<Value> values = readValuesFromFile(e.getBlobKey(), e.getLength());
                for (final Value vx : values) {
                    if (timespan.contains(vx.getTimestamp())) {
                        // if (vx.getTimestamp().getTime() <= timespan.upperEndpoint().getTime() && vx.getTimestamp().getTime() >= timespan.lowerEndpoint()) {
                        retObj.add(vx);
                    }
                }
            }
        }
        return retObj;

    }

    private boolean validateOwnership(Entity entity, ValueBlobStore e) {
        return e.getEntityUUID().equals("") || e.getEntityUUID().equals(entity.getUUID());
    }


    @Override
    public List<ValueBlobStore> getAllStores(final Entity entity) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        final Query q = pm.newQuery(ValueBlobStoreEntity.class);
        q.setFilter("entity == k");
        q.declareParameters("String k");
        q.setOrdering("timestamp descending");


        final Collection<ValueBlobStore> result = (Collection<ValueBlobStore>) q.execute(entity.getKey());
        List<ValueBlobStore> checked = new ArrayList<>(result.size());
        {
            for (ValueBlobStore e : result) {
                if (validateOwnership(entity, e)) {
                    checked.add(e);
                }
            }
        }
        return ValueBlobStoreFactory.createValueBlobStores(checked);

    }

    @Override
    public List<Value> consolidateDate(final Entity entity, final Date timestamp) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();



        final Query q = pm.newQuery(ValueBlobStoreEntity.class);
        q.setFilter("timestamp == t && entity == k");
        q.declareParameters("String k, Long t");
        final List<ValueBlobStore> result = (List<ValueBlobStore>) q.execute(entity.getKey(), timestamp.getTime());

        final List<Value> values = new ArrayList<Value>(Const.CONST_DEFAULT_LIST_SIZE);
        for (final ValueBlobStore store : result) {
            if (validateOwnership(entity, store)) {
                values.addAll(readValuesFromFile((store.getBlobKey()), store.getLength()));
            }
        }

        deleteBlobs(result);

        pm.deletePersistentAll(result);

        return values;


    }


    @Override
    public int deleteExpiredData(final Entity entity) {

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();


        int exp = ((Point) entity).getExpire();
        int deleted = 0;
        if (exp > 0) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, exp * -1);

            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
            q.setFilter("entity == k && maxTimestamp <= et");
            q.declareParameters("String k, Long et");
            q.setRange(0, 1000);


            final List<ValueBlobStore> result = (List<ValueBlobStore>) q.execute(
                    entity.getKey(), c.getTime().getTime());
            deleted = result.size();

            delete(result);
            pm.deletePersistentAll(result);



        }
        return deleted;
    }

    @Override
    public List<Value> readValuesFromFile(final String key, final long length) {
        //TODO Delete file
        final Type valueListType = new TypeToken<List<ValueModel>>() {
        }.getType();
        List<Value> models;
        BlobKey blobKey = new BlobKey(key);

        try {
            BlobstoreService blobStoreService = BlobstoreServiceFactory.getBlobstoreService();

            String segment = new String(blobStoreService.fetchData(blobKey, 0, length));
            if (!Utils.isEmptyString(segment)) {
                models = gson.fromJson(segment, valueListType);
                if (models != null) {
                    Collections.sort(models);
                } else {
                    models = Collections.emptyList();
                }
            } else {
                models = Collections.emptyList();
            }
            return models;
        } catch (IllegalArgumentException ex) {
            return Collections.emptyList();
        }

    }

    @Override
    public void deleteBlobs(List<ValueBlobStore> result) {
        for (ValueBlobStore store : result) {
            final BlobKey blobKey = new BlobKey(store.getBlobKey());
            final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
            blobstoreService.delete(blobKey);
        }
    }

    @Override
    public void delete(final String key) {
        final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        blobstoreService.delete(new BlobKey(key));
    }

    @Override
    public List<ValueBlobStore> createBlobStoreEntity(final Entity entity, final ValueDayHolder holder) throws IOException {

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

            final String json = gson.toJson(holder.getValues());
            final FileService fileService = FileServiceFactory.getFileService();
            final AppEngineFile file = fileService.createNewBlobFile(Const.CONTENT_TYPE_PLAIN);
            final FileWriteChannel writeChannel = fileService.openWriteChannel(file, true);
            PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
            out.println(json);
            out.close();
            writeChannel.closeFinally();
            final BlobKey key = fileService.getBlobKey(file);
            Range<Date> range = holder.getTimeRange();
            final Date mostRecentTimeForDay = range.upperEndpoint();
            final Date earliestForDay = range.lowerEndpoint();
            final ValueBlobStoreEntity currentStoreEntity = new
                    ValueBlobStoreEntity(entity.getKey(),
                    holder.getStartOfDay(),
                    mostRecentTimeForDay,
                    earliestForDay,
                    key,
                    json.length(), BlobStore.storageVersion, entity.getUUID()
            );

            currentStoreEntity.validate();

            pm.makePersistent(currentStoreEntity);

            pm.flush();
            List<ValueBlobStore> result = ValueBlobStoreFactory.createValueBlobStore(currentStoreEntity);
            out.close();
            return result;


    }

    @Override
    public List<ValueBlobStore> mergeTimespan(final Entity entity, final Range<Date> timespan) {


        final FileService fileService = FileServiceFactory.getFileService();
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

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
                    timespan.upperEndpoint().getTime(),
                    timespan.lowerEndpoint().getTime());


            Collection<Value> combined = new ArrayList<Value>();
            Date timestamp = null;
            for (ValueBlobStore store : result) {
                if (validateOwnership(entity, store)) {
                    if (timestamp == null || timestamp.getTime() > store.getTimestamp().getTime()) {
                        timestamp = store.getTimestamp();

                    }
                    List<Value> read = readValuesFromFile((store.getBlobKey()), store.getLength());
                    combined.addAll(read);
                    delete(store.getBlobKey());
                }

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

            String json = gson.toJson(combined);
            // byte[] compressed = CompressionImpl.compressBytes(json);
            out.print(json);

            writeChannel.closeFinally();
            final BlobKey key = fileService.getBlobKey(file);

            ValueBlobStore currentStoreEntity = new ValueBlobStoreEntity(
                    entity.getKey(),
                    timestamp,
                    new Date(max),
                    new Date(min),
                    key,
                    json.length(), BlobStore.storageVersion, entity.getUUID());

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

        }

    }

    @Override
    public void delete(List<ValueBlobStore> result) {
        for (ValueBlobStore store : result) {
            delete(store.getBlobKey());
        }
    }

}
