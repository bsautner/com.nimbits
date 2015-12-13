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
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;

@Service
public class BlobStoreImpl implements BlobStore {

    @Autowired
    private NimbitsCache nimbitsCache;

    @Autowired
    private StorageIOImpl storageIO;

    public static final String SNAPSHOT = "SNAPSHOT";
    public static final int INITIAL_CAPACITY = 10000;
    private final Logger logger = Logger.getLogger(BlobStoreImpl.class.getName());
    private final Gson gson =  GsonFactory.getInstance(true);


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
    public List<Value> getSeries(final Entity entity, final Optional<Range<Date>> timespan, final Optional<Range<Integer>> range, Optional<String> mask) {
        //TODO - some way to test if a count has been reached before reading all files if no timespan is give - like test the list by processing it to see if it's complete
        //enough to return while reading other files.

        String path = root + "/" + entity.getKey();
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
        final String key = point.getKey() + SNAPSHOT;
        FileUtils.deleteDirectory(new File(root + "/" + point.getKey()));
        nimbitsCache.delete(key);

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

//    private static class Closer {
//
//        public static void closeSilently(Object... xs) {
//            // Note: on Android API levels prior to 19 Socket does not implement Closeable
//            for (Object x : xs) {
//                if (x != null) {
//                    try {
//
//                        if (x instanceof Closeable) {
//                            ((Closeable)x).close();
//                        } else if (x instanceof Socket) {
//                            ((Socket)x).close();
//                        } else if (x instanceof DatagramSocket) {
//                            ((DatagramSocket)x).close();
//                        } else {
//
//                            throw new RuntimeException("cannot close "+x);
//                        }
//                    } catch (Throwable e) {
//
//                    }
//                }
//            }
//        }
//    }

}
