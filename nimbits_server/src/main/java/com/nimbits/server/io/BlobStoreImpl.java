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


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.enums.ServerSetting;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;

import com.nimbits.server.defrag.Defragmenter;
import com.nimbits.server.defrag.ValueDayHolder;
import com.nimbits.server.transaction.cache.NimbitsCache;
import com.nimbits.server.transaction.settings.SettingsService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.Logger;

@Service
public class BlobStoreImpl implements BlobStore {

    @Autowired
    private NimbitsCache nimbitsCache;

    public static final String SNAPSHOT = "SNAPSHOT";
    public static final int INITIAL_CAPACITY = 10000;
    private final Logger logger = Logger.getLogger(BlobStoreImpl.class.getName());
    private final Gson gson = new GsonBuilder().create();


    @Autowired
    private ValueService valueService;

    @Autowired
    private Defragmenter defragmenter;

    private final String root;

    @Autowired
    public BlobStoreImpl(SettingsService settingsService) {
        root = settingsService.getSetting(ServerSetting.storeDirectory);

    }


    @Override
    public List<Value> getTopDataSeries(final Entity entity)  {

        logger.info("getTopDataSeries 3");



        String path = root + "/" + entity.getKey();
        logger.info("path=" + path);
        List<Value> retObj = new ArrayList<>(INITIAL_CAPACITY);
        List<String> allReadFiles = new ArrayList<>(INITIAL_CAPACITY);
        File file = new File(path);
        if (file.exists()) {

            List<String> dailyFolderPaths = new ArrayList<>();

            for (String dailyFolderPath : file.list()) {

                File node = new File(dailyFolderPath);
                logger.info("found: " + dailyFolderPath + " " + node.isDirectory());

                if (! node.getName().endsWith(SNAPSHOT)) {

                    dailyFolderPaths.add(root + "/" + entity.getKey() + "/" + dailyFolderPath);

                }


            }

            if (!dailyFolderPaths.isEmpty()) {
                Collections.sort(dailyFolderPaths);
                Collections.reverse(dailyFolderPaths);

                for (String sortedDayPath : dailyFolderPaths) {
                    logger.info("processing sub directory: " + sortedDayPath);
                    Iterator result2 = FileUtils.iterateFiles(new File(sortedDayPath), null, false);
                    List<String> filePaths = new ArrayList<>();

                    while (result2.hasNext()) {

                        File listItem = (File) result2.next();
                        String filePath = listItem.getName();
                        logger.info("found data file: " + filePath);
                        if (!filePath.endsWith(SNAPSHOT)) {
                            filePaths.add(sortedDayPath + "/" + filePath);
                        }


                    }
                    Collections.sort(filePaths);
                    Collections.reverse(filePaths);

                    for (String sortedFilePath : filePaths) {
                        logger.info(sortedFilePath);
                        List<Value> values = readValuesFromFile(sortedFilePath);
                        retObj.addAll(values);

                        allReadFiles.add(sortedFilePath);
                        //DEFRAG IF over 1000 values are contained in over 1000 files
                        if (retObj.size() > INITIAL_CAPACITY && filePaths.size() > INITIAL_CAPACITY) {
                            deleteAndRestore(entity, retObj, allReadFiles);
                            return retObj;
                        }
                    }


                }


            }


            deleteAndRestore(entity, retObj, allReadFiles);
            return retObj;
        }
        else {
            logger.info("file not found");
            return Collections.emptyList();
        }


    }

    private void deleteAndRestore(Entity entity,  List<Value> retObj, List<String> allReadFiles) {

        try {
            if (allReadFiles.size() > 1) {
                for (String s : allReadFiles) {
                    FileUtils.deleteQuietly(new File(s));


                }
                valueService.storeValues(entity, retObj);
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

        }
        else {
            List<Value> values = readValuesFromFile(root + "/" + entity.getKey() + "/" + SNAPSHOT);

            if (values.isEmpty()) {
                value = new Value.ValueBuilder().doubleValue(0.0).timestamp(new Date()).createValue();
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
        final String json = gson.toJson(Arrays.asList(value));
        nimbitsCache.put(key, value);
        writeFile(json, root + "/" + entity.getKey() + "/" + SNAPSHOT);


    }

    @Override
    public void saveSnapshot(final Entity entity, final Value value) {
        final String key = entity.getKey() + SNAPSHOT;
        Value old = getSnapshot(entity);
        if (value.getTimestamp().getTime() > old.getTimestamp().getTime()) {
            final String json = gson.toJson(Arrays.asList(value));
            nimbitsCache.put(key, value);
            writeFile(json, root + "/" + entity.getKey() + "/" + SNAPSHOT);
        }

    }

    @Override
    public List<Value> getDataSegment(final Entity entity, final Range<Date> timespan) {


        String path = root + "/" + entity.getKey();
        logger.info("path=" + path);
        List<Value> allvalues = new ArrayList<>(INITIAL_CAPACITY);
        List<String> allReadFiles = new ArrayList<>(INITIAL_CAPACITY);
        File file = new File(path);
        Range<Date> maxRange = Range.closed(defragmenter.zeroOutDateToStart(timespan.lowerEndpoint()), defragmenter.zeroOutDateToStart(timespan.upperEndpoint()));
        if (file.exists()) {

            List<String> dailyFolderPaths = new ArrayList<>();

            for (String dailyFolderPath : file.list()) {

                File node = new File(dailyFolderPath);
                logger.info("found: " + dailyFolderPath + " " + node.isDirectory());

                if (! node.getName().endsWith(SNAPSHOT)) {
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
                    logger.info("processing sub directory: " + sortedDayPath);
                    Iterator result2 = FileUtils.iterateFiles(new File(sortedDayPath), null, false);
                    List<String> filePaths = new ArrayList<>();

                    while (result2.hasNext()) {

                        File listItem = (File) result2.next();
                        String filePath = listItem.getName();
                        logger.info("found data file: " + filePath);
                        if (!filePath.endsWith(SNAPSHOT)) {
                            filePaths.add(sortedDayPath + "/" + filePath);
                        }


                    }
                    Collections.sort(filePaths);
                    Collections.reverse(filePaths);

                    for (String sortedFilePath : filePaths) {
                        logger.info(sortedFilePath);
                        List<Value> values = readValuesFromFile(sortedFilePath);
                        allvalues.addAll(values);

                        allReadFiles.add(sortedFilePath);

                    }


                }


            }

            List<Value> filtered = new ArrayList<>(allvalues.size());
            for (Value value : allvalues) {
                if (timespan.contains(value.getTimestamp())) {
                    filtered.add(value);
                }
            }
            if (allReadFiles.size() > INITIAL_CAPACITY) {  //TODO will break if # of days = initial capacity
                logger.info("Defragmenting " + allReadFiles.size());
                deleteAndRestore(entity, allvalues, allReadFiles);
            }
            logger.info("****** returning " + filtered.size());
            return ImmutableList.copyOf(filtered);
        }
        else {
            logger.info("file not found");
            return Collections.emptyList();
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
            logger.severe(e.getMessage());

            return Collections.emptyList();

        }



    }


    @Override
    public void createBlobStoreEntity(final Entity entity, final ValueDayHolder holder) throws IOException {


        logger.info("createBlobStoreEntity");

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
        saveSnapshot(entity, mostRecent);
        Range<Date> range = holder.getTimeRange();

        final Date earliestForDay = range.lowerEndpoint();



        String FILENAME =  root + "/" + entity.getKey() + "/" + holder.getStartOfDay().getTime() + "/" + earliestForDay.getTime();//store.getId();
        // GcsService gcsService = GcsServiceFactory.createGcsService();
        writeFile(json, FILENAME);
    }

    @Override
    public void deleteAllData(Point point) throws IOException {
        FileUtils.deleteDirectory(new File(root + "/" + point.getKey()));
    }

    private void writeFile(String json, String FILENAME) {


        try {
            FileUtils.writeStringToFile(new File(FILENAME), json);


        }
        catch (Exception e) {
            logger.info(e.getMessage());
        }
    }


//    public static boolean isGzipped(File f) {
//
//        InputStream is = null;
//        try {
//            is = new FileInputStream(f);
//            byte [] signature = new byte[2];
//            int nread = is.read( signature ); //read the gzip signature
//            return nread == 2 && signature[ 0 ] == (byte) 0x1f && signature[ 1 ] == (byte) 0x8b;
//        } catch (IOException e) {
//
//            return false;
//        } finally {
//            Closer.closeSilently(is);
//        }
//    }

    private static class Closer {

        public static void closeSilently(Object... xs) {
            // Note: on Android API levels prior to 19 Socket does not implement Closeable
            for (Object x : xs) {
                if (x != null) {
                    try {

                        if (x instanceof Closeable) {
                            ((Closeable)x).close();
                        } else if (x instanceof Socket) {
                            ((Socket)x).close();
                        } else if (x instanceof DatagramSocket) {
                            ((DatagramSocket)x).close();
                        } else {

                            throw new RuntimeException("cannot close "+x);
                        }
                    } catch (Throwable e) {

                    }
                }
            }
        }
    }

}
