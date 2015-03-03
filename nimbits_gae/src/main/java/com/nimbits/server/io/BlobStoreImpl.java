/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required  @Override

    } applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.io;


import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import com.google.appengine.tools.cloudstorage.*;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.channels.Channels;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class BlobStoreImpl implements BlobStore {
    private final Logger log = Logger.getLogger(BlobStoreImpl.class.getName());
    private PersistenceManagerFactory persistenceManagerFactory;
    private final String BUCKETNAME = "nimbits02-bucket";

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
        try {
            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
            q.setFilter("minTimestamp <= t && entity == k");
            q.declareParameters("String k, Long t");
            q.setOrdering("minTimestamp desc");


            final List<ValueBlobStoreEntity> result = (List<ValueBlobStoreEntity>) q.execute(entity.getKey(), endDate.getTime());

            for (final ValueBlobStoreEntity e : result) {
                if (validateOwnership(entity, e)) {
                    List<Value> values = readValuesFromFile(e);

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
        } finally {
            pm.close();
        }

    }


    @Override
    public List<Value> getTopDataSeries(final Entity entity, final int maxValues) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();


        final List<Value> retObj = new ArrayList<Value>(maxValues);

        final Query q = pm.newQuery(ValueBlobStoreEntity.class);
        q.setFilter("entity == k");
        q.declareParameters("String k");
        q.setOrdering("minTimestamp desc");

        try {
            final List<ValueBlobStoreEntity> result = (List<ValueBlobStoreEntity>) q.execute(entity.getKey());

            for (final ValueBlobStoreEntity e : result) {
                if (validateOwnership(entity, e)) {
                    List<Value> values = readValuesFromFile(e);

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
        } finally {
            pm.close();
        }

    }

    @Override
    public List<Value> getDataSegment(final Entity entity, final Range<Date> timespan) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        final List<Value> retObj = new ArrayList<Value>();
        final Query q = pm.newQuery(ValueBlobStoreEntity.class);
        q.setFilter("entity == k && minTimestamp <= et && minTimestamp >= st ");
        q.declareParameters("String k, Long et, Long st");
        q.setOrdering("minTimestamp desc");
        try {
            final Iterable<ValueBlobStore> result = (Iterable<ValueBlobStore>) q.execute(entity.getKey(), timespan.upperEndpoint().getTime(), timespan.lowerEndpoint().getTime());
            for (final ValueBlobStore e : result) {    //todo break out of loop when range is met
                if (validateOwnership(entity, e)) {
                    List<Value> values = readValuesFromFile(e);
                    for (final Value vx : values) {
                        if (timespan.contains(vx.getTimestamp())) {
                            // if (vx.getTimestamp().getTime() <= timespan.upperEndpoint().getTime() && vx.getTimestamp().getTime() >= timespan.lowerEndpoint()) {
                            retObj.add(vx);
                        }
                    }
                }
            }
            return retObj;
        } finally {
            pm.close();
        }

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
        q.setRange(0, 1000);


        try {
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
        } finally {
            pm.close();
        }

    }

    @Override
    public List<ValueBlobStore> getLegacy() {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        final Query q = pm.newQuery(ValueBlobStoreEntity.class);
        q.setFilter("version != 1");
        //  q.declareParameters("Integer v");

        //  q.setOrdering("version descending, timestamp descending");
        q.setRange(0, 1);

        try {
            final List<ValueBlobStore> result = (List<ValueBlobStore>) q.execute();

            return result;
        } finally {
            pm.close();
        }
    }

    @Override
    public void deleteStores(final Entity entity, final Date timestamp) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        log.info("deleting " + entity.getName().getValue() );

        final Query q = pm.newQuery(ValueBlobStoreEntity.class);
        q.setFilter("timestamp == t && entity == k");
        q.declareParameters("String k, Long t");

      //  Transaction tx = pm.currentTransaction();
        try {

         //   tx.begin();
            final List<ValueBlobStoreEntity> result = (List<ValueBlobStoreEntity>) q.execute(entity.getKey(), timestamp.getTime());
            log.info("deleting " + result.size());
            pm.deletePersistentAll(result);

          //  tx.commit();

        }
        catch (Exception ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex.getMessage());
            ex.printStackTrace();
          //  tx.rollback();


        } finally {
            pm.close();
        }
    }

    @Override
    public List<Value> consolidateDate(final Entity entity, final Date timestamp) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        log.info("consolidating " + entity.getName().getValue() );

        final Query q = pm.newQuery(ValueBlobStoreEntity.class);
        q.setFilter("timestamp == t && entity == k");
        q.declareParameters("String k, Long t");
        q.setOrdering("timestamp desc");



        try {

            final List<ValueBlobStore> result = (List<ValueBlobStore>) q.execute(entity.getKey(), timestamp.getTime());
            log.info("consolidating " + result.size());

            final List<Value> values = new ArrayList<>(Const.CONST_DEFAULT_LIST_SIZE);
            for (final ValueBlobStore store : result) {
                if (validateOwnership(entity, store)) {
                    values.addAll(readValuesFromFile(store));
                }
            }

            deleteBlobs(result);

            log.info("defragmenting " + values.size());

            return values;
        }
        catch (Exception ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
            return Collections.emptyList();  //TODO if anything goes wrong with this process you'll lose an entire day's worth of data

        } finally {
            pm.close();
        }


    }


    @Override
    public int deleteExpiredData(final Entity entity) {

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {
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
        } finally {
            pm.close();
        }
    }

    @Override
    public List<Value> readValuesFromFile(ValueBlobStore valueBlobStore) {

        final Type valueListType = new TypeToken<List<ValueModel>>() {
        }.getType();
        List<Value> models;
        String segment = null;
        if (valueBlobStore.getVersion() == 0 && StringUtils.isNotEmpty(valueBlobStore.getBlobKey())) {
            BlobKey blobKey = new BlobKey(valueBlobStore.getBlobKey());

            try {
                BlobstoreService blobStoreService = BlobstoreServiceFactory.getBlobstoreService();

                segment = new String(blobStoreService.fetchData(blobKey, 0, valueBlobStore.getLength()));

            } catch (IllegalArgumentException ex) {
                return Collections.emptyList();
            }
        }
        else {


            GcsService gcsService = GcsServiceFactory.createGcsService();
            String FILENAME =  valueBlobStore.getEntity()  + "/" + valueBlobStore.getId();
            GcsFilename filename = new GcsFilename(BUCKETNAME, FILENAME);

            GcsInputChannel readChannel;
            BufferedReader reader = null;
            try {

                readChannel = gcsService.openReadChannel(filename, 0);

                reader = new BufferedReader(Channels.newReader(readChannel, "UTF8"));
                String line;
                StringBuilder stringBuilder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);

                }
                segment = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        log.severe(e.getMessage());
                    }
                }
            }


        }

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

    }

    @Override
    public void deleteBlobs(List<ValueBlobStore> result) {
        for (ValueBlobStore store : result) {
            try {
                if (store.getVersion() == 0 && StringUtils.isNotEmpty(store.getBlobKey())) {
                    final BlobKey blobKey = new BlobKey(store.getBlobKey());
                    final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
                    blobstoreService.delete(blobKey);

                } else if (store.getVersion() == 1) {
                    GcsService gcsService = GcsServiceFactory.createGcsService();
                    String FILENAME = store.getEntity() + "/" + store.getId();
                    GcsFilename file = new GcsFilename(BUCKETNAME, FILENAME);
                    try {
                        gcsService.delete(file);
                    } catch (IOException e) {
                        log.log(Level.SEVERE, " gcsService.delete", e);
                    }
                }
            } catch (Exception ex) {
                log.log(Level.SEVERE, "delete blob failed", ex);
            }
        }
    }

    @Override
    public void delete(final String key) {
        if (StringUtils.isNotEmpty(key)) {
            final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
            blobstoreService.delete(new BlobKey(key));
        }
    }

    @Override
    public List<ValueBlobStore> createBlobStoreEntity(final Entity entity, final ValueDayHolder holder) throws IOException {

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
        final BlobKey key = null; //TODO remove once all v0 are migrated
        final String json = gson.toJson(holder.getValues());

        try {
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
            writeFile(entity, currentStoreEntity, json);

            pm.flush();
            List<ValueBlobStore> result = ValueBlobStoreFactory.createValueBlobStore(currentStoreEntity);




            return result;
        } finally {
            pm.close();
        }


    }

    private void writeFile(Entity entity, ValueBlobStore store, String data) {

        String FILENAME =  entity.getKey() + "/" + store.getId();
        GcsService gcsService = GcsServiceFactory.createGcsService();
        GcsFilename filename = new GcsFilename(BUCKETNAME, FILENAME);
        GcsFileOptions options = new GcsFileOptions.Builder()
                .mimeType("text/html")
                .acl("public-read")
                        //  .addUserMetadata("myfield1", "my field value")
                .build();

        try {

            GcsOutputChannel writeChannel = gcsService.createOrReplace(filename, options);
            // You can write to the channel using the standard Java methods.
            // Here we use a PrintWriter:
            PrintWriter writer = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
            writer.println(data);

            writer.flush();


            writeChannel.close();
        }
        catch (Exception e) {
            log.severe(e.getMessage());
        }
    }
    @Override
    public List<ValueBlobStore> mergeTimespan(final Entity entity, final Range<Date> timespan) {


        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        final Query q = pm.newQuery(ValueBlobStoreEntity.class);

        q.setFilter("entity == k && minTimestamp <= et && minTimestamp >= st ");
        q.declareParameters("String k, Long et, Long st");
        q.setOrdering("minTimestamp desc");
        try {
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
                    List<Value> read = readValuesFromFile(store);
                    combined.addAll(read);
                    if (StringUtils.isNotEmpty(store.getBlobKey())) {
                        delete(store.getBlobKey());
                    }
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

            ValueBlobStore currentStoreEntity = new ValueBlobStoreEntity(
                    entity.getKey(),
                    timestamp,
                    new Date(max),
                    new Date(min),
                    null,
                    json.length(), BlobStore.storageVersion, entity.getUUID());

            currentStoreEntity.validate();
            pm.makePersistent(currentStoreEntity);
            writeFile(entity, currentStoreEntity, json);
            pm.flush();
            return ValueBlobStoreFactory.createValueBlobStore(currentStoreEntity);

        } finally {
            pm.close();
        }


    }

    @Override
    public void delete(List<ValueBlobStore> result) {
        for (ValueBlobStore store : result) {
            delete(store.getBlobKey());
        }
    }

    @Override
    public void deleteBlobStoreEntity(List<ValueBlobStore> s) {

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try {

            tx.begin();
            ValueBlobStore st = s.get(0);
            final ValueBlobStore result = pm.getObjectById(ValueBlobStoreEntity.class, st.getId());
            pm.deletePersistent(result);
            log.info("deleted store entity for " + st.getEntity());
            tx.commit();
        }catch (Exception ex) {
            log.severe(ex.getMessage());
            tx.rollback();


        } finally {
            pm.close();
        }

    }

    @Override
    public List<Value> upgradeStore(Entity entity, ValueBlobStore v) {
        List<Value> values = readValuesFromFile(v);
        if (! values.isEmpty()) {
            log.info("upgrading " + v.getEntity() + " " + values.size());

        }
        deleteBlobs(Arrays.asList(v));
        deleteBlobStoreEntity(Arrays.asList(v));
        return values;
    }


}
