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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.ServerSetting;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.defrag.ValueDayHolder;
import com.nimbits.server.gson.deserializer.ValueDeserializer;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.transaction.settings.SettingsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.jdo.PersistenceManagerFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Repository
public class BlobStoreImpl implements BlobStore {
    private final Logger logger = Logger.getLogger(BlobStoreImpl.class.getName());

    private PersistenceManagerFactory persistenceManagerFactory;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private TaskService taskService;

    private final Gson gson = new GsonBuilder()
            .setDateFormat(Const.GSON_DATE_FORMAT)
            .registerTypeAdapter(Value.class, new ValueDeserializer())
            .excludeFieldsWithoutExposeAnnotation()
            .create();


    public BlobStoreImpl() {

    }

    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }



//    @Override
//    public List<Value> getTopDataSeries(final Entity entity, final int maxValues ) {
//        return Collections.emptyList();
////        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
////        try {
////
////            final List<Value> retObj = new ArrayList<Value>(maxValues);
////
////            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
////            q.setFilter("minTimestamp <= t && entity == k");
////            q.declareParameters("String k, Long t");
////            q.setOrdering("minTimestamp desc");
////            q.setRange(0, maxStores);
////
////            final List<ValueBlobStoreEntity> result = (List<ValueBlobStoreEntity>) q.execute(entity.getKey(), endDate.getTime());
////
////            for (final ValueBlobStoreEntity e : result) {
////                if (validateOwnership(entity, e)) {
////                    List<Value> values = readValuesFromFile(e);
////
////                    for (final Value vx : values) {
////                        if (vx.getTimestamp().getTime() <= endDate.getTime()) {
////                            retObj.add(vx);
////                        }
////
////                        if (retObj.size() >= maxValues) {
////                            break;
////                        }
////                    }
////                }
////            }
////            return retObj;
////        } finally {
////           pm.close();
////        }
//    }


    @Override
    public List<Value> getTopDataSeries(final Entity entity) {
       return Collections.emptyList();
//        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
//        try {
//
//            final List<Value> retObj = new ArrayList<Value>(maxValues);
//
//            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
//            q.setFilter("entity == k");
//            q.declareParameters("String k");
//            q.setOrdering("minTimestamp desc");
//            q.setRange(0, 1000);
//
//            final List<ValueBlobStoreEntity> result = (List<ValueBlobStoreEntity>) q.execute(entity.getKey());
//
//            for (final ValueBlobStoreEntity e : result) {
//                if (validateOwnership(entity, e)) {
//                    List<Value> values = readValuesFromFile(e);
//
//                    for (final Value vx : values) {
//                        retObj.add(vx);
//
//                        if (retObj.size() >= maxValues) {
//                            break;
//                        }
//                    }
//                }
//            }
//            return ImmutableList.copyOf(retObj);
//        } finally {
//           pm.close();
//        }
    }

    @Override
    public List<Value> getDataSegment(final Entity entity, final Range<Date> timespan) {
        return Collections.emptyList();
//        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
//        try {
//            final List<Value> retObj = new ArrayList<Value>();
//            final Query q = pm.newQuery(ValueBlobStoreEntity.class);
//            q.setFilter("entity == k && minTimestamp <= et && maxTimestamp >= st ");
//            q.declareParameters("String k, Long et, Long st");
//            q.setOrdering("minTimestamp desc");
//
//            final Iterable<ValueBlobStore> result = (Iterable<ValueBlobStore>) q.execute(entity.getKey(), timespan.upperEndpoint().getTime(), timespan.lowerEndpoint().getTime());
//            for (final ValueBlobStore e : result) {    //todo break out of loop when range is met
//                if (validateOwnership(entity, e)) {
//                    List<Value> values = readValuesFromFile(e);
//                    for (final Value vx : values) {
//                        if (timespan.contains(vx.getTimestamp())) {
//                            retObj.add(vx);
//
//                        }
//                    }
//                }
//            }
//            return retObj;
//        } finally {
//            pm.close();
//        }
    }




    private String readFile(final Entity store) throws IOException {
        return "";
//        String path = getPath(store);
//
//
//        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
//
//            StringBuilder sb = new StringBuilder();
//            String line = br.readLine();
//
//            while (line != null) {
//                sb.append(line);
//                sb.append('\n');
//                line = br.readLine();
//            }
//            return sb.toString();
//        }

    }

    private String getFolder() {
        String failover = "/tmp/";
        if (settingsService == null) {
            return failover;
        } else {

            String folder = settingsService.getSetting(ServerSetting.storeDirectory);
            if (folder == null) {
                folder = failover;
            }
            if (!folder.endsWith("/")) {
                folder += "/";
            }
            return folder;
        }
    }


//
//
//    @Override
//    public List<Value> readValuesFromFile(final ValueBlobStore store) {
//
//        final Type valueListType = new TypeToken<List<ValueModel>>() {
//        }.getType();
//        List<Value> models;
//
//
//        try {
//
//            String segment = readFile(store);
//            if (!Utils.isEmptyString(segment)) {
//                models = gson.fromJson(segment, valueListType);
//                if (models != null) {
//                    Collections.sort(models);
//                } else {
//                    models = Collections.emptyList();
//                }
//            } else {
//                models = Collections.emptyList();
//            }
//            return models;
//        } catch (IllegalArgumentException ex) {
//            return Collections.emptyList();
//        } catch (IOException e) {
//            return Collections.emptyList();
//        }
//
//    }
//
//    @Override
//    public void deleteFiles(List<ValueBlobStore> result) {
//        for (ValueBlobStore store : result) {
//            String FILENAME = getPath(store);
//            File file = new File(FILENAME);
//            file.delete();
//
//        }
//    }

    private void writeFile(Entity store, String json) {
        FileWriter out;

//        try {
////            String path = getPath(store);
////            File file = new File(path);
////            file.getParentFile().mkdirs();
////            out = new FileWriter(file);
////            out.write(json);
////            out.flush();
////            out.close();
//        } catch (IOException ex) {
//            logger.log(Level.SEVERE, "Error writing file", ex);
//        }



    }


    @Override
    public void createBlobStoreEntity(final Entity entity, final ValueDayHolder holder) throws IOException {


//        logger.info("createBlobStoreEntity");
//        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
//
         final String json = gson.toJson(holder.getValues());
//
//        try {
//            Range<Date> range = holder.getTimeRange();
//            final Date mostRecentTimeForDay = range.upperEndpoint();
//            final Date earliestForDay = range.lowerEndpoint();
//            final ValueBlobStoreEntity currentStoreEntity = new
//                    ValueBlobStoreEntity(entity.getKey(),
//                    holder.getStartOfDay(),
//                    mostRecentTimeForDay,
//                    earliestForDay,  BlobStore.storageVersion, entity.getUUID()
//            );
//
//            currentStoreEntity.validate();
//
//            pm.makePersistent(currentStoreEntity);
//            pm.flush();
//            writeFile(currentStoreEntity, json);
//
//
//            List<ValueBlobStore> result = ValueBlobStoreFactory.createValueBlobStore(currentStoreEntity);
//
//
//
//
//            return ImmutableList.copyOf(result);
//        } finally {
//            pm.close();
//        }


    }




}