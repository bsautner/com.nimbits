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
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;


public class BlobStoreImpl implements BlobStore {


    private final Logger logger = Logger.getLogger(BlobStoreImpl.class.getName());
    private NimbitsCache nimbitsCache;
    private StorageIOImpl storageIO;
    private Defragmenter defragmenter;


    private SettingsService settingsService;

    public BlobStoreImpl(NimbitsCache nimbitsCache,
                         StorageIOImpl storageIO,
                         Defragmenter defragmenter,
                         SettingsService settingsService) {

        this.nimbitsCache = nimbitsCache;
        this.storageIO = storageIO;
        this.defragmenter = defragmenter;
        this.settingsService = settingsService;
    }

    @Override
    public List<Value> getSeries(final ValueService valueService,
                                 final Entity entity,
                          final Optional<Range<Date>> timespan,
                          final Optional<Range<Integer>> range,
                          final Optional<String> mask) {

        String root = settingsService.getSetting(ServerSetting.storeDirectory);
        String path = root + "/" + entity.getKey();
        List<Value> allvalues = new ArrayList<>();
        List<String> allReadFiles = new ArrayList<>();
        File file = new File(path);

        Range<Date> maxRange = timespan.isPresent() ?
                Range.closed(defragmenter.zeroOutDateToStart(timespan.get().lowerEndpoint()), defragmenter.zeroOutDateToStart(timespan.get().upperEndpoint())) :
                Range.closed(defragmenter.zeroOutDateToStart(new Date(0)), defragmenter.zeroOutDateToStart(new Date())); // all dates

        if (file.exists()) {

            List<String> dailyFolderPaths = new ArrayList<>();

            for (String dailyFolderPath : file.list()) {

                File node = new File(dailyFolderPath);

                if (! node.getName().endsWith(StorageIO.SNAPSHOT)) {
                    Long timestamp = Long.valueOf(dailyFolderPath);
                    if (maxRange.contains(new Date(timestamp))) {

                        dailyFolderPaths.add(root + "/" + entity.getKey() + "/" + dailyFolderPath);
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
                        if (!filePath.endsWith(StorageIO.SNAPSHOT)) {
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
                deleteAndRestore(storageIO, valueService, entity, allvalues, allReadFiles);
            }
            return ImmutableList.copyOf(filtered);
        }
        else {
            logger.info("file not found");
            return Collections.emptyList();
        }


    }



    @Override
    public List<Value> readValuesFromFile(String path) {


        try {
            File file = new File(path);
          //  boolean isCompressed = isGzipped(file);
          //  System.out.println("******ISCOMPRESSED: " + isCompressed);
            String segment = FileUtils.readFileToString(file);
            List<Value> models = storageIO.getValues( segment);
            return ImmutableList.copyOf(models);
        } catch (Exception e) {
            logger.severe(e.getMessage());

            return Collections.emptyList();

        }



    }

    @Override
    public void deleteAllData(Point point) {
        final String key = point.getKey() + StorageIO.SNAPSHOT;
        try {
            String root = settingsService.getSetting(ServerSetting.storeDirectory);
            FileUtils.deleteDirectory(new File(root + "/" + point.getKey()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        nimbitsCache.delete(key);

    }

    @Override
    public void writeFile(String body, String filename) {

        try {
            FileUtils.writeStringToFile(new File(filename), body);
        }
        catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

    private void deleteAndRestore(StorageIO storageIO, ValueService valueService, Entity entity,  List<Value> retObj, List<String> allReadFiles) {


        if (allReadFiles.size() > 1) {
            for (String s : allReadFiles) {
                FileUtils.deleteQuietly(new File(s));


            }
            valueService.storeValues(storageIO, entity, retObj);
        }

    }


}
