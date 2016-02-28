/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.process;


import com.google.appengine.tools.cloudstorage.*;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.defrag.ValueDayHolder;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transaction.cache.NimbitsCache;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


public class BlobStoreImpl implements BlobStore {


    private NimbitsCache nimbitsCache;




    private StorageIOImpl storageIO;


    public static final String SNAPSHOT = "SNAPSHOT";
    public static final int INITIAL_CAPACITY = 100;
    private final Logger logger = Logger.getLogger(BlobStoreImpl.class.getName());


    private final Gson gson =  GsonFactory.getInstance(true);


   // private final AppIdentityService appIdentity = AppIdentityServiceFactory.getAppIdentityService();
    private final String BUCKETNAME = "nimbits-bucket-yodel";//appIdentity.getDefaultGcsBucketName();
   // private final String BUCKETNAME = appIdentity.getDefaultGcsBucketName();
    private final GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());


    public BlobStoreImpl(NimbitsCache nimbitsCache, StorageIOImpl storageIO ) {
        this.nimbitsCache = nimbitsCache;

        this.storageIO = storageIO;
    }

    private void deleteAndRestore(ValueService valueService, BlobStore blobStore, Entity entity, List<Value> retObj, List<String> allReadFiles) {
        try {
            if (allReadFiles.size() > 1) {
                for (String s : allReadFiles) {
                    GcsFilename deleteFile = new GcsFilename(BUCKETNAME, s);
                    gcsService.delete(deleteFile);

                }
                valueService.storeValues(blobStore, entity, retObj);
            }
        } catch (IOException ex) {
            logger.severe(ExceptionUtils.getStackTrace(ex));
        }
    }

    @Override
    public Value getSnapshot(final Entity entity) {
        final Value value;
        final String key = entity.getKey() + SNAPSHOT;
        if (nimbitsCache.get(key) != null) {

            value = (Value) nimbitsCache.get(key);

        } else {
            List<Value> values = readValuesFromFile(entity.getKey() + "/" + SNAPSHOT);

            if (values.isEmpty()) {
                value = new Value.Builder().doubleValue(0.0).timestamp(new Date(0)).create();
                createSnapshot(entity, value);
            } else {
                value = values.get(0);
            }
            nimbitsCache.put(key, value);
        }
        return value;


    }

    private void createSnapshot(final Entity entity, final Value value) {
        final String key = entity.getKey() + SNAPSHOT;
        final String json = gson.toJson(Collections.singletonList(value));
        nimbitsCache.put(key, value);
        writeFile(json, entity.getKey() + "/" + SNAPSHOT);


    }

    @Override
    public void saveSnapshot(final Entity entity, final Value value) {
        final String key = entity.getKey() + SNAPSHOT;
        Value old = getSnapshot(entity);
        if (value.getTimestamp().getTime() > old.getTimestamp().getTime()) {
            final String json = gson.toJson(Collections.singletonList(value));
            nimbitsCache.put(key, value);
            writeFile(json, entity.getKey() + "/" + SNAPSHOT);
        }

    }

    @Override
    public List<Value> getSeries(final ValueService valueService,
                                 final Entity entity,
                          final Optional<Range<Date>> timespan,
                          final Optional<Range<Integer>> range,
                          final Optional<String> mask) {

//        Range<Date> maxRange = timespan.isPresent() ?
//                Range.closed(defragmenter.zeroOutDateToStart(timespan.get().lowerEndpoint()), defragmenter.zeroOutDateToStart(timespan.get().upperEndpoint())) :
//                Range.closed(defragmenter.zeroOutDateToStart(new Date(0)), defragmenter.zeroOutDateToStart(new Date())); // all dates


        ListOptions.Builder b = new ListOptions.Builder();
        b.setRecursive(false);
        b.setPrefix(entity.getKey() + "/");

        List<Value> allvalues = new ArrayList<>(INITIAL_CAPACITY);
        List<String> allReadFiles = new ArrayList<>(INITIAL_CAPACITY);
        try {
            ListResult result = gcsService.list(BUCKETNAME, b.build());
            List<String> names = new ArrayList<>();

            while (result.hasNext()) {

                ListItem listItem = result.next();
                String name = listItem.getName();
                names.add(name);


            }

            if (!names.isEmpty()) {
                Collections.sort(names);
                Collections.reverse(names);

                for (String sortedDayPath : names) {
                    ListOptions.Builder b2 = new ListOptions.Builder();
                    b2.setRecursive(false);
                    b2.setPrefix(sortedDayPath);

                    ListResult result2 = gcsService.list(BUCKETNAME, b2.build());
                    List<String> filePaths = new ArrayList<>();

                    while (result2.hasNext()) {

                        ListItem listItem = result2.next();
                        String filePath = listItem.getName();
                        if (!filePath.endsWith(SNAPSHOT)) {
                            filePaths.add(filePath);
                        }
                    }
                    Collections.sort(filePaths);
                    Collections.reverse(filePaths);

                    for (String sortedFilePath : filePaths) {

                        List<Value> values = readValuesFromFile(sortedFilePath);
                        allvalues.addAll(values);

                        allReadFiles.add(sortedFilePath);
                        //DEFRAG IF over INITIAL_CAPACITY values are contained in over INITIAL_CAPACITY files

                    }

                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Value> filtered = storageIO.filterValues(allvalues, timespan, range, mask);
        if (allReadFiles.size() > INITIAL_CAPACITY) {  //TODO will break if # of days = initial capacity
            deleteAndRestore(valueService, this, entity, allvalues, allReadFiles);
        }
        return ImmutableList.copyOf(filtered);





    }

    private List<Value> readValuesFromFile(String path) {

        final Type valueListType = new TypeToken<List<Value>>() {
        }.getType();

        GcsFilename filename = new GcsFilename(BUCKETNAME, path);

        GcsInputChannel readChannel;
        BufferedReader reader = null;
        String segment = null;
        try {
            readChannel = gcsService.openReadChannel(filename, 0);

            reader = new BufferedReader(Channels.newReader(readChannel, "UTF8"));
            String line;
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);

            }
            segment = stringBuilder.toString();
        } catch (Exception e) {

            logger.severe(e.getMessage());

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.severe(e.getMessage());
                }
            }
        }


        List<Value> models;
        if (!StringUtils.isEmpty(segment)) {
            models = gson.fromJson(segment, valueListType);
            if (models != null) {
                Collections.sort(models);
            } else {
                models = Collections.emptyList();
            }
        } else {
            models = Collections.emptyList();
        }
        return ImmutableList.copyOf(models);

    }

    @Override
    public void createBlobStoreEntity(final Entity entity, final ValueDayHolder holder) {


        final String json = gson.toJson(holder.getValues());

        Value mostRecent = null;
        for (Value value : holder.getValues()) {
            if (mostRecent == null) {
                mostRecent = value;
            } else if (mostRecent.getTimestamp().getTime() < value.getTimestamp().getTime()) {
                mostRecent = value;
            }

        }
        saveSnapshot(entity, mostRecent);
        Range<Date> range = holder.getTimeRange();
       // final Date mostRecentTimeForDay = range.upperEndpoint();
        final Date earliestForDay = range.lowerEndpoint();


        String FILENAME = entity.getKey() + "/" + holder.getStartOfDay().getTime() + "/" + earliestForDay.getTime();//store.getId();
        // GcsService gcsService = GcsServiceFactory.createGcsService();
        writeFile(json, FILENAME);
    }

    @Override
    public void deleteAllData(Point point)  {
        GcsFilename path = new GcsFilename(BUCKETNAME, point.getKey());
        try {
            gcsService.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeFile(String json, String FILENAME) {
        logger.info("writing file: " + BUCKETNAME + " " + FILENAME);
        GcsFilename filename = new GcsFilename(BUCKETNAME, FILENAME);
        GcsFileOptions options = new GcsFileOptions.Builder()
                .mimeType("text/html")
                .acl("public-read") //TODO !
                        //  .addUserMetadata("myfield1", "my field value")
                .build();

        try {

            GcsOutputChannel writeChannel = gcsService.createOrReplace(filename, options);
            // You can write to the channel using the standard Java methods.
            // Here we use a PrintWriter:
            PrintWriter writer = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
            writer.println(json);

            writer.flush();


            writeChannel.close();
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }


}
