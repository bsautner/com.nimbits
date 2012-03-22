package com.nimbits.server.dao.value;

import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.files.*;
import com.nimbits.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.timespan.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.gson.*;
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

    public ValueDAOImpl(Point point) {
        this.point = point;
    }

    @Override
    public Value getRecordedValuePrecedingTimestamp(Date timestamp) throws NimbitsException {

        List<Value> values =  getTopDataSeries(1, timestamp);
        if (values.size() > 0) {
            return values.get(0);
        }
        else {
            return null;
        }

    }


    @Override
    public List<Value> getTopDataSeries(int maxValues) throws NimbitsException {
        return getTopDataSeries(maxValues, new Date());

    }

    @Override
    public List<Value> getTopDataSeries(int maxValues, Date endDate) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            Map<String, Object> args;
            List<Value> retObj = new ArrayList<Value>();
            Query q = pm.newQuery(ValueBlobStoreEntity.class,
                    "entity == k && minTimestamp <= :t");
            q.declareImports("import java.util.Date;");
            args = new HashMap<String, Object>();
            args.put("k", point.getUUID());
            args.put("t", endDate);
            q.setOrdering("minTimestamp descending");
            q.setRange(0, 1000);
            List<ValueBlobStoreEntity> result = (List<ValueBlobStoreEntity>) q.executeWithMap(args);
            for (ValueBlobStoreEntity e : result) {
                List<Value> values = readValuesFromFile(e.getPath());
                for (Value vx : values) {
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
    public List<Value> getDataSegment(Timespan timespan) throws NimbitsException {
       return getDataSegment(timespan, 0, 1000);
    }

    @Override
    public List<Value> getDataSegment(Timespan timespan, int start, int end) throws NimbitsException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            Map<String, Object> args;
            List<Value> retObj = new ArrayList<Value>();
            Query q = pm.newQuery(ValueBlobStoreEntity.class,
                    "entity == k && minTimestamp <= :et && minTimestamp >= :st ");
            q.declareImports("import java.util.Date;");
            args = new HashMap<String, Object>();
            args.put("k", point.getUUID());
            args.put("et", timespan.getEnd());
            args.put("st", timespan.getStart());
            q.setOrdering("minTimestamp descending");
            q.setRange(start, end);
            List<ValueBlobStoreEntity> result = (List<ValueBlobStoreEntity>) q.executeWithMap(args);
            for (ValueBlobStoreEntity e : result) {
                List<Value> values = readValuesFromFile(e.getPath());
                for (Value vx : values) {
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
    public void recordValues(List<Value> values) throws NimbitsException {


        if (values.size() > 0) {

            Map<Long, List<Value>> map = new HashMap<Long, List<Value>>();
            Map<Long, Long> maxMap = new HashMap<Long, Long>();
            Map<Long, Long> minMap = new HashMap<Long, Long>();
            for (Value value : values) {
                Date zero = TimespanServiceFactory.getInstance().zeroOutDate(value.getTimestamp());
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
                    List<Value> list = new ArrayList<Value>();
                    list.add(value);
                    map.put(zero.getTime(),list);
                    maxMap.put(zero.getTime(), value.getTimestamp().getTime());
                    minMap.put(zero.getTime(), value.getTimestamp().getTime());
                }




            }

            for (Long l : map.keySet()) {
                String json = GsonFactory.getInstance().toJson(map.get(l));

                try {
                    createBlobStoreEntity(maxMap, minMap, l, json);
                } catch (IOException e) {
                    throw new NimbitsException(e);
                }


            }



        }


    }

    private void createBlobStoreEntity(Map<Long, Long> maxMap, Map<Long, Long> minMap, Long l, String json) throws IOException {
        final PersistenceManager pm = PMF.get().getPersistenceManager();
        FileService fileService = FileServiceFactory.getFileService();
        try {
            AppEngineFile file;
            BlobKey key;
            String path;
            FileWriteChannel writeChannel;
            PrintWriter out;
            ValueBlobStoreEntity currentStoreEntity;

            file = fileService.createNewBlobFile(Const.CONTENT_TYPE_PLAIN);
            key = fileService.getBlobKey(file);
            path = file.getFullPath();
            writeChannel = fileService.openWriteChannel(file, true);
            out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
            out.println(json);
            out.close();
            writeChannel.closeFinally();
            Date mostRecentTimeForDay = new Date(maxMap.get(l));
            Date earliestForDay = new Date(minMap.get(l));

            currentStoreEntity = new ValueBlobStoreEntity(point.getUUID(),new Date(l), mostRecentTimeForDay, earliestForDay, key, path );
            pm.makePersistent(currentStoreEntity);

        } finally {
            pm.close();
        }
    }


    @Override
    public void moveValuesFromCacheToStore() throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }

    @Override
    public List<Value> getCache(Timespan timespan) throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }
    @Override
    public List<Value> getCache() throws NimbitsException {
        throw new NimbitsException("Not Implimented");
    }
    @Override
    public Value recordValue(Value v) throws NimbitsException {
        throw new NimbitsException("Not Implimented");
    }


    private List<Value> readValuesFromFile(final String path) throws NimbitsException {
        FileService fileService = FileServiceFactory.getFileService();
        AppEngineFile file = new AppEngineFile(path);
        FileReadChannel readChannel;
        try {
            readChannel = fileService.openReadChannel(file, false);

            BufferedReader reader =
                    new BufferedReader(Channels.newReader(readChannel, "UTF8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            List<Value> models =  GsonFactory.getInstance().fromJson(sb.toString(), GsonFactory.valueListType);
            Collections.sort(models);
            return models;

        } catch (IOException e) {
            throw new NimbitsException(e);
        }

    }



}
