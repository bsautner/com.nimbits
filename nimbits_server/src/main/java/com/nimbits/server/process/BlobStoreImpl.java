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


import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.enums.ServerSetting;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.defrag.Defragmenter;
import com.nimbits.server.defrag.ValueDayHolder;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transaction.cache.NimbitsCache;
import com.nimbits.server.transaction.settings.SettingsService;
import com.nimbits.server.transaction.value.ValueDao;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BlobStoreImpl implements BlobStore {

    public static final String SNAPSHOT = "SNAPSHOT";
    public static final int INITIAL_CAPACITY = 10000;
    private final Logger logger = LoggerFactory.getLogger(BlobStoreImpl.class.getName());
    private final Gson gson =  GsonFactory.getInstance(true);


    private NimbitsCache nimbitsCache;


    private StorageIOImpl storageIO;


    private Defragmenter defragmenter;


    private SettingsService settingsService;

    private ValueDao valueDao;

    public BlobStoreImpl(NimbitsCache nimbitsCache, StorageIOImpl storageIO, Defragmenter defragmenter, SettingsService settingsService, ValueDao valueDao) {

        this.nimbitsCache = nimbitsCache;
        this.storageIO = storageIO;
        this.defragmenter = defragmenter;
        this.settingsService = settingsService;
        this.valueDao = valueDao;
    }

    @Override
    public List<Value> getSeries(final ValueService valueService,
                                 final Entity entity,
                          final Optional<Range<Date>> timespan,
                          final Optional<Range<Integer>> range,
                          final Optional<String> mask) {
        //TODO - some way to test if a count has been reached before reading all files if no timespan is give - like test the list by processing it to see if it's complete
        //enough to return while reading other files.
        String root = settingsService.getSetting(ServerSetting.storeDirectory);
        String path = root + "/" + entity.getId();
        List<Value> allvalues = new ArrayList<>(INITIAL_CAPACITY);
        List<String> allReadFiles = new ArrayList<>(INITIAL_CAPACITY);
        File file = new File(path);

        Range<Date> maxRange = timespan.isPresent() ?
                Range.closed(defragmenter.zeroOutDateToStart(timespan.get().lowerEndpoint()), defragmenter.zeroOutDateToStart(timespan.get().upperEndpoint())) :
                Range.closed(defragmenter.zeroOutDateToStart(new Date(0)), defragmenter.zeroOutDateToStart(new Date())); // all dates

        if (file.exists()) {

            List<String> dailyFolderPaths = new ArrayList<>();

            for (String dailyFolderPath : file.list()) {

                File node = new File(dailyFolderPath);

                if (! node.getName().endsWith(SNAPSHOT)) {
                    Long timestamp = Long.valueOf(dailyFolderPath);
                    if (maxRange.contains(new Date(timestamp))) {

                        dailyFolderPaths.add(root + "/" + entity.getId() + "/" + dailyFolderPath);
                    }

                }


            }

            if (!dailyFolderPaths.isEmpty()) {
                Collections.sort(dailyFolderPaths);
                Collections.reverse(dailyFolderPaths);

                for (String sortedDayPath : dailyFolderPaths) {
                    Iterator result2 = FileUtils.iterateFiles(new File(sortedDayPath), null, false);
                    List<String> filePaths = new ArrayList<>();

                    while (result2.hasNext()) {

                        File listItem = (File) result2.next();
                        String filePath = listItem.getName();
                        if (!filePath.endsWith(SNAPSHOT)) {
                            filePaths.add(sortedDayPath + "/" + filePath);
                        }


                    }
                    Collections.sort(filePaths);
                    Collections.reverse(filePaths);

                    for (String sortedFilePath : filePaths) {
                        List<Value> values = readValuesFromFile(sortedFilePath);
                        allvalues.addAll(values);
                        allReadFiles.add(sortedFilePath);

                    }


                }
            }

            List<Value> filtered = storageIO.filterValues(allvalues, timespan, range, mask);

            if (allReadFiles.size() > INITIAL_CAPACITY) {  //TODO will break if # of days = initial capacity
             //   logger.info("Defragmenting " + allReadFiles.size());
                deleteAndRestore(this, valueService, entity, allvalues, allReadFiles);
            }
          //  logger.info("****** returning " + filtered.size());
            return ImmutableList.copyOf(filtered);
        }
        else {
            logger.info("file not found");
            return Collections.emptyList();
        }


    }

    private void deleteAndRestore(BlobStore blobStore, ValueService valueService, Entity entity,  List<Value> retObj, List<String> allReadFiles) {


            if (allReadFiles.size() > 1) {
                for (String s : allReadFiles) {
                    FileUtils.deleteQuietly(new File(s));


                }
                valueService.storeValues(blobStore, entity, retObj);
            }

    }




    private List<Value> readValuesFromFile(String path) {

        final Type valueListType = new TypeToken<List<Value>>() {
        }.getType();




        try {
            File file = new File(path);
          //  boolean isCompressed = isGzipped(file);
          //  System.out.println("******ISCOMPRESSED: " + isCompressed);
            String segment = FileUtils.readFileToString(file);
            List<Value> models;
            if (! StringUtils.isEmpty(segment)) {
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
        } catch (Exception e) {
            logger.error(e.getMessage());

            return Collections.emptyList();

        }



    }


    @Override
    public void createBlobStoreEntity(final Entity entity, final ValueDayHolder holder) {


     //   logger.info("createBlobStoreEntity");

        final String json = gson.toJson(holder.getValues());

        Value mostRecent = null;
        for (Value value : holder.getValues()) {
            if (mostRecent == null) {
                mostRecent = value;
            }
            else if (mostRecent.getTimestamp().getTime() < value.getTimestamp().getTime()) {
                mostRecent = value;
            }

        }
        valueDao.setSnapshot(entity, mostRecent);

        Range<Date> range = holder.getTimeRange();

        final Date earliestForDay = range.lowerEndpoint();


        String root = settingsService.getSetting(ServerSetting.storeDirectory);
        String FILENAME =  root + "/" + entity.getId() + "/" + holder.getStartOfDay().getTime() + "/" + earliestForDay.getTime();//store.getId();
        // GcsService gcsService = GcsServiceFactory.createGcsService();
        writeFile(json, FILENAME);
    }

    @Override
    public void deleteAllData(Point point) {
        final String key = point.getId() + SNAPSHOT;
        try {
            String root = settingsService.getSetting(ServerSetting.storeDirectory);
            FileUtils.deleteDirectory(new File(root + "/" + point.getId()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        nimbitsCache.delete(key);

    }

    @Override
    public void saveSnapshot(Point point, Value value) {
        valueDao.setSnapshot(point, value);
    }

    private void writeFile(String json, String FILENAME) {


        try {
            FileUtils.writeStringToFile(new File(FILENAME), json);


        }
        catch (Exception e) {
            logger.info(e.getMessage());
        }
    }


}
