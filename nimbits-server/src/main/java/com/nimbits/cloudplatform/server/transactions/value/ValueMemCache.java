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

package com.nimbits.cloudplatform.server.transactions.value;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.memcache.InvalidValueException;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nimbits.cloudplatform.client.constants.Const;
import com.nimbits.cloudplatform.client.enums.MemCacheKey;
import com.nimbits.cloudplatform.client.enums.ProtectionLevel;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.point.Point;
import com.nimbits.cloudplatform.client.model.timespan.Timespan;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.cloudplatform.server.process.task.TaskImpl;
import org.apache.commons.lang3.Range;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 11/27/11
 * Time: 12:40 PM
 */
@Component("valueCache")
@SuppressWarnings("unchecked") //TODO
public class ValueMemCache {

    private static final Logger log = Logger.getLogger(ValueMemCache.class.getName());
    public static final int SEC = 1000;

    private static MemcacheService cacheFactory;

    static {
        cacheFactory = MemcacheServiceFactory.getMemcacheService();
    }

    private static void updateHotPoints(final Point point, final Value value) {
        Map<String, Point> map;
        if (cacheFactory.contains(MemCacheKey.hotPoints)) {
            map = (HashMap<String, Point>) cacheFactory.get(MemCacheKey.hotPoints);
        }
        else {
            map = new HashMap<String, Point>();
        }
        if (map == null) {
            map = new HashMap<String, Point>();
            cacheFactory.delete(MemCacheKey.hotPoints);
        }
        if (map != null && point != null && map.containsKey(point.getKey())) {
            map.remove(point.getKey());
        }

        if (point != null && value!=null) {
            point.setValue(value);


            HashMap<String, Point> newMap = new HashMap<String, Point>(map.size() +1);
            newMap.putAll(map);
            newMap.put(point.getKey(), point);
            cacheFactory.delete(MemCacheKey.hotPoints);
            cacheFactory.put(MemCacheKey.hotPoints, newMap);
        }




    }

    public static  Set<Point> getHotlist( ) {
        Set<Point> retSet = new TreeSet<Point>(new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                return o2.getValue().getTimestamp().compareTo(o1.getValue().getTimestamp());
            }

        });
        if (cacheFactory.contains(MemCacheKey.hotPoints)) {
            HashMap<String, Point> map = (HashMap<String, Point>) cacheFactory.get(MemCacheKey.hotPoints);
            for (Point p : map.values()) {
                if (p.getProtectionLevel().equals(ProtectionLevel.everyone)) {

                    retSet.add(p);
                }
            }
            return retSet;
        }
        else {
            return Collections.emptySet();
        }

    }

    //todo lookup and delete before put may not be neccesary

    protected static void addPointToActiveList(final Entity point) {
        Map<String, Entity> points;
        if (cacheFactory.contains(MemCacheKey.activePoints)) {
            try {
                points = (Map<String, Entity>) cacheFactory.get(MemCacheKey.activePoints);
                if (points == null) { //contains a null map?
                    points = new HashMap<String, Entity>(1);
                    points.put(point.getKey(), point);
                    cacheFactory.delete(MemCacheKey.activePoints);
                    cacheFactory.put(MemCacheKey.activePoints, points);
                }
                else if (! points.containsKey(point.getKey())) {
                    points.put(point.getKey(), point);
                    cacheFactory.delete(MemCacheKey.activePoints);
                    cacheFactory.put(MemCacheKey.activePoints, points);

                }
            } catch (InvalidValueException e) {
                cacheFactory.clearAll();
                points = new HashMap<String, Entity>(1);
                points.put(point.getKey(), point);
                cacheFactory.delete(MemCacheKey.activePoints);
                cacheFactory.put(MemCacheKey.activePoints, points);
            }

        }
        else {
            points = new HashMap<String, Entity>(1);
            points.put(point.getKey(), point);
            cacheFactory.put(MemCacheKey.activePoints, points);
        }
    }
    protected static void removePointFromActiveList(final Entity point) {
        Map<String, Entity> points;
        if (cacheFactory.contains(MemCacheKey.activePoints)) {
            try {
                points = (Map<String, Entity>) cacheFactory.get(MemCacheKey.activePoints);

                if (points != null && ! points.containsKey(point.getKey())) {
                    points.remove(point.getKey());
                    cacheFactory.delete(MemCacheKey.activePoints);
                    cacheFactory.put(MemCacheKey.activePoints, points);

                }
            } catch (InvalidValueException e) {
                cacheFactory.clearAll();
                points = new HashMap<String, Entity>(1);
                points.put(point.getKey(), point);
                cacheFactory.delete(MemCacheKey.activePoints);
                cacheFactory.put(MemCacheKey.activePoints, points);
            }

        }

    }



    public static List<Value> getRecordedValuePrecedingTimestamp(final Entity entity, final Date timestamp)   {
        final List<Value> result = new ArrayList<Value>(1);

        String currentValueCacheKey = MemCacheKey.getKey(MemCacheKey.currentValueCache, entity.getKey());

        //       try {
        if (cacheFactory.contains(currentValueCacheKey)) {
            final Value value = (Value) cacheFactory.get(currentValueCacheKey);
            if (value == null) {
                cacheFactory.delete(currentValueCacheKey);
                List<Value> sample = ValueDAO.getRecordedValuePrecedingTimestamp(entity, timestamp);
                if (! sample.isEmpty()) {
                    cacheFactory.put(currentValueCacheKey, sample.get(0));
                    result.addAll(sample);
                }

            } else {
                if (timestamp.getTime() >= value.getTimestamp().getTime()) {
                    result.add(value);
                }
                else {
                    List<Value> bufferValues = getBuffer(entity);
                    List<Value> values = ValueDAO.getRecordedValuePrecedingTimestamp(entity, timestamp);
                    result.addAll(values);
                    for (Value v : bufferValues) {
                        if (v.getTimestamp().getTime() < timestamp.getTime()) {
                            result.add(v);
                        }
                    }
                }
            }
        } else {

            log.info("getRecordedValuePrecedingTimestamp");
            List<Value> sample = ValueDAO.getRecordedValuePrecedingTimestamp(entity, timestamp);
            //TODO - keep a memchach list of known empty points to avoid repeated datastore calls here
            if (! sample.isEmpty()) {

                cacheFactory.put(currentValueCacheKey, sample.get(0));
                result.addAll(sample);
            }



        }
//        } catch (InvalidValueException e) {
//            cacheFactory.delete(currentValueCacheKey);
//            List<Value> sample = ValueDAO.getRecordedValuePrecedingTimestamp(entity, timestamp);
//            if (! sample.isEmpty()) {
//                cacheFactory.put(currentValueCacheKey, sample.get(0));
//                result.addAll(sample);
//            }
//        } catch (ClassCastException e) { //old cache data causing a problem when upgrading.
//            cacheFactory.delete(currentValueCacheKey);
//            List<Value> sample = ValueDAO.getRecordedValuePrecedingTimestamp(entity, timestamp);
//            if (! sample.isEmpty()) {
//                cacheFactory.put(currentValueCacheKey, sample.get(0));
//                result.addAll(sample);
//            }
//
//        }


        return getClosestMatchToTimestamp(result, timestamp);
    }


    public static List<Value> getClosestMatchToTimestamp(final List<Value> values, final Date timestamp) {

        List<Value> shucked = new ArrayList<Value>(values.size());
        for (Value v : values) {
            if (v.getTimestamp().getTime() <= timestamp.getTime()) {
                shucked.add(v);
            }
        }


        Collections.sort(shucked);

        return shucked;





    }



    public static Value recordValue(final Point entity, final Value v)  {
        String bufferedListCacheKey = MemCacheKey.getKey(MemCacheKey.bufferedValueList, entity.getKey());
        String currentValueCacheKey = MemCacheKey.getKey(MemCacheKey.currentValueCache, entity.getKey());
        updateHotPoints(entity, v);
        addPointToActiveList(entity);
        //  try {
        final List<Long> stored;
        if (cacheFactory.contains(bufferedListCacheKey)) {
            stored = (List<Long>) cacheFactory.get(bufferedListCacheKey);
            if (stored == null) {
                //fixes npe
                cacheFactory.delete(bufferedListCacheKey);

            }
            else {
                stored.add(v.getTimestamp().getTime() + entity.hashCode()); //TODO timestamped buffered value
                cacheFactory.delete(stored);
                cacheFactory.put(bufferedListCacheKey, stored);
            }
        } else {
            stored = new ArrayList<Long>(10);
            stored.add(v.getTimestamp().getTime() + entity.hashCode());
            cacheFactory.put(bufferedListCacheKey, stored);
        }


        //TODO STORES VALUE WITH TS

        cacheFactory.put(v.getTimestamp().getTime() + entity.hashCode(), v);


        if (stored.size() > Const.CONST_MAX_CACHED_VALUE_SIZE) {
            TaskImpl.startMoveCachedValuesToStoreTask(entity);
        }

        if (cacheFactory.contains(currentValueCacheKey)) {
            final Value mostRecentCache = (Value) cacheFactory.get(currentValueCacheKey);

            if (mostRecentCache == null || v.getTimestamp().getTime() > mostRecentCache.getTimestamp().getTime()) {
                cacheFactory.delete(currentValueCacheKey);
                cacheFactory.put(currentValueCacheKey, v);
            }
        } else {
            cacheFactory.put(currentValueCacheKey, v);
        }
        // } catch (Exception e) {
        //     cacheFactory.delete(currentValueCacheKey);
        //     cacheFactory.delete(bufferedListCacheKey);
        // }

        return v;

    }




    public static  List<Value> getTopDataSeries(final Entity entity,final int maxValues)  {
        //log.info("getting top " + maxValues + " data series for " + entity.getKey());
        final List<Value> cached = getBuffer(entity);
        //log.info("found in buffer:" + cached.size());
        final List<Value> stored = ValueDAO.getTopDataSeries(entity, maxValues);
        //log.info("found in storage:" + stored.size());
        return mergeAndSort(cached, stored, maxValues);
    }


    //gets the most recent values for a entity up to a max count. If  count is in the buffer, just return them otherwise
    //get more values from the store.

    public static List<Value> getTopDataSeries(final Entity entity, final int maxValues, final Date endDate)  {
        final List<Value> cached = getBuffer(entity);
        if (cached != null && cached.size() > maxValues) {
            return cached;
        } else {
            final List<Value> stored = ValueDAO.getTopDataSeries(entity, maxValues, endDate);
            return stored.isEmpty() ? cached : mergeAndSort(stored, cached, endDate);
        }

    }


    public static List<Value> getDataSegment(final Entity entity, final Range<Long> timespan, final Range<Integer> range)  {
        final List<Value> stored = ValueDAO.getDataSegment(entity, timespan);
        final List<Value> cached = getBuffer(entity);
        List<Value> allData =  mergeAndSort(stored, cached, timespan);   //todo cache result
        List<Value> result = new ArrayList<Value>();
        int max = range.getMaximum() > allData.size() ? allData.size() : range.getMaximum();
        for (int i = range.getMinimum(); i < max; i++) {
            result.add(allData.get(i));
        }
        return result;

    }

    public static List<Value> getSeries(final Entity entity, final Range<Long> timespan)  {
        final List<Value> stored = ValueDAO.getDataSegment(entity, timespan);
        final List<Value> cached = getBuffer(entity);
        List<Value> allData =  mergeAndSort(stored, cached, timespan);   //todo cache result

        return allData;

    }


    public static List<ValueBlobStore> recordValues(final Entity entity,final List<Value> values)  {
        return ValueDAO.recordValues(entity, values);
    }


    public static List<Value> getCache(final Entity entity, final Timespan timespan) {
        String bufferedListCacheKey = MemCacheKey.getKey(MemCacheKey.bufferedValueList, entity.getKey());

        List<Value> retObj = null;
        if (cacheFactory.contains(bufferedListCacheKey)) {
            final Collection<Long> x = (Collection<Long>) cacheFactory.get(bufferedListCacheKey);
            final Map<Long, Object> valueMap = cacheFactory.getAll(x);
            final ValueComparator bvc = new ValueComparator(valueMap);
            final Map<Long, Object> sorted_map = new TreeMap<Long, Object>(bvc);
            sorted_map.putAll(valueMap);
            retObj = new ArrayList<Value>(sorted_map.keySet().size());
            for (final Map.Entry<Long, Object> longObjectEntry : sorted_map.entrySet()) {
                if (longObjectEntry.getKey() >= timespan.getStart().getTime() || longObjectEntry.getKey() <= timespan.getStart().getTime()) {
                    retObj.add((Value) longObjectEntry.getValue());
                }
            }
        }

        return retObj;
    }


    public static List<ValueBlobStore> getAllStores(final Entity entity)  {
        return ValueDAO.getAllStores(entity);
    }


    public static void consolidateDate(final Entity entity, final Date timestamp) throws IOException {
        ValueDAO.consolidateDate(entity, timestamp);
    }


    public static List<ValueBlobStore> getBlobStoreByBlobKey(BlobKey key)  {
        return ValueDAO.getBlobStoreByBlobKey(key);
    }


    public static ValueBlobStore mergeTimespan(final Entity entity, final Timespan timespan) throws IOException {
        return ValueDAO.mergeTimespan(entity, timespan);

    }


    public static void purgeValues(final Entity entity)  {
        String bufferedListCacheKey = MemCacheKey.getKey(MemCacheKey.bufferedValueList, entity.getKey());

        if (cacheFactory.contains(bufferedListCacheKey)) {
            cacheFactory.delete(bufferedListCacheKey);
        }
        removePointFromActiveList(entity);
        ValueDAO.purgeValues(entity);
    }


    public static void deleteExpiredData(final Entity entity) {
        ValueDAO.deleteExpiredData(entity);
    }

    public static List<List<Value>> splitUpList(final List<Value> original) {

        if (original.size() < Const.CONST_QUERY_CHUNK_SIZE) {
            List<List<Value>> retObj = new ArrayList<List<Value>>(1);
            retObj.add(original);
            return retObj;

        }
        else {
            List<List<Value>> retObj = new ArrayList<List<Value>>(original.size() / Const.CONST_QUERY_CHUNK_SIZE);
            int s = 0;
            int e = Const.CONST_QUERY_CHUNK_SIZE;

            while (s < original.size()) {
                if (e > original.size()) {
                    e = original.size();
                }
                List<Value> piece = new ArrayList<Value>(Const.CONST_QUERY_CHUNK_SIZE);
                piece.addAll(original.subList(s, e));
                retObj.add(piece);
                s += Const.CONST_QUERY_CHUNK_SIZE;
                e += Const.CONST_QUERY_CHUNK_SIZE;

            }


            return retObj;
        }






    }


    public static int preloadTimespan(final Entity entity, final Range timespan) throws Exception {
        List<Value> stored = getDataSegment(entity, timespan, Range.between(0, SEC));
        String key = MemCacheKey.preload.getText() + entity.getUUID();
        //log.info("Storing " + stored.size());

        List<List<Value>> split = splitUpList(stored);
        //log.info("split up into " + split.size() + " pieces");
        int section = 0;
        int count = 0;
        for (List<Value> small : split) {
            String smallKey = key + section;
            //log.info("Stored key : " + smallKey + "  " + small.size());
            count += small.size();
            if (cacheFactory.contains(smallKey)) {
                cacheFactory.delete(smallKey);
            }
            cacheFactory.put(smallKey, small);
            section += Const.CONST_QUERY_CHUNK_SIZE;
        }
        return count;



    }


    public static List<Value> getPreload(final Entity entity, final int section)  {

        String key = MemCacheKey.preload.getText() + entity.getUUID() + section;
        //log.info(key);

        if (cacheFactory.contains(key)) {
            return  (((List<Value>) cacheFactory.get(key)));
            //  c += Const.CONST_QUERY_CHUNK_SIZE;
        }
        else {
            return Collections.emptyList();
        }



    }


    public  static List<Value> getBuffer(final Entity entity) {
        String bufferedListCacheKey = MemCacheKey.getKey(MemCacheKey.bufferedValueList, entity.getKey());

        if (cacheFactory.contains(bufferedListCacheKey)) {
            final Collection<Long> bufferedValueList = (Collection<Long>) cacheFactory.get(bufferedListCacheKey);
            final Map<Long, Object> valueMap = cacheFactory.getAll(bufferedValueList);
            final ValueComparator bvc = new ValueComparator(valueMap);
            final Map<Long, Object> sorted_map = new TreeMap<Long, Object>(bvc);
            sorted_map.putAll(valueMap);
            final List<Value> retObj  = new ArrayList<Value>( sorted_map.keySet().size());
            for (final Map.Entry<Long, Object> longObjectEntry : sorted_map.entrySet()) {
                retObj.add((Value) longObjectEntry.getValue());
            }
            return retObj;
        }
        else
        {
            return new ArrayList<Value>(0); //return an empty list to avoid a npe
        }



    }


    public static void moveValuesFromCacheToStore(final Entity entity) {
        final String bufferedListCacheKey = MemCacheKey.getKey(MemCacheKey.bufferedValueList, entity.getKey());
        //log.info("moveValuesFromCacheToStore moving: " + entity.getKey());
        try {
            if (cacheFactory.contains(bufferedListCacheKey)) {
                final Collection<Long> x = (Collection<Long>) cacheFactory.get(bufferedListCacheKey);
                if (x != null && ! x.isEmpty()) {
                    cacheFactory.delete(bufferedListCacheKey);
                    final Map<Long, Object> valueMap = cacheFactory.getAll(x);
                    cacheFactory.deleteAll(x);
                    final List<Value> values = new ArrayList<Value>(valueMap.keySet().size());
                    //  int count = values.size();
                    for (final Map.Entry<Long, Object> longObjectEntry : valueMap.entrySet()) {
                        values.add((Value) longObjectEntry.getValue());
                    }
                    ValueDAO.recordValues(entity, values);
                }
            }
        } catch (Exception e) {
            cacheFactory.delete(bufferedListCacheKey);
        }


    }

    private static List<Value> mergeAndSort(final Collection<Value> first, final Collection<Value> second, final int max) {
        final Map<Long, Object> sorted_map = getLongObjectMap(first, second);
        int c = 0;
        final List<Value> retObj = new ArrayList<Value>( sorted_map.keySet().size());
        for (final Map.Entry<Long, Object> longObjectEntry : sorted_map.entrySet()) {
            c++;
            retObj.add((Value) longObjectEntry.getValue());
            if (c >= max) {
                break;
            }
        }
        return retObj;
    }

    private static Map<Long, Object> getLongObjectMap(Collection<Value> first, Collection<Value> second) {
        first.addAll(second);
        final Map<Long, Object> valueMap = new TreeMap<Long, Object>();
        for (final Value v : first) {
            valueMap.put(v.getTimestamp().getTime(), v);
        }


        final ValueComparator bvc = new ValueComparator(valueMap);
        final Map<Long, Object> sorted_map = new TreeMap<Long, Object>(bvc);
        sorted_map.putAll(valueMap);
        return sorted_map;
    }

    private static List<Value> mergeAndSort(final Collection<Value> first, final Collection<Value> second, final Date endDate) {
        final Map<Long, Object> sorted_map = getLongObjectMap(first, second);
        final List<Value> retObj = new ArrayList<Value>(sorted_map.keySet().size());
        for (final Map.Entry<Long, Object> longObjectEntry : sorted_map.entrySet()) {
            if (longObjectEntry.getKey() <= endDate.getTime()) {
                retObj.add((Value) longObjectEntry.getValue());
            }

        }
        return retObj;
    }

    private static <E> List<E> mergeAndSort(final Collection<E> first, final Collection<E> second, final Range<Long> timespan) {
        first.addAll(second);
        final Map<Long, E> valueMap = new TreeMap<Long, E>();
        for (final E v : first) {
            valueMap.put(((Value)v).getTimestamp().getTime(), v);
        }


        final ValueComparator bvc = new ValueComparator(valueMap);
        final Map<Long, E> sorted_map = new TreeMap<Long, E>(bvc);

        sorted_map.putAll(valueMap);
        final List<E> retObj = new ArrayList<E>(sorted_map.keySet().size());
        for (final Map.Entry<?, E> longObjectEntry : sorted_map.entrySet()) {
            long l = (Long)longObjectEntry.getKey();
            if (l >= timespan.getMinimum() - SEC && l <= timespan.getMaximum() + SEC) {
                retObj.add(longObjectEntry.getValue());
            }
        }
        return retObj;
    }




    static class ValueComparator implements Comparator {

        Map base;

        ValueComparator(final Map base) {
            this.base = base;
        }


        public int compare(final Object a, final Object b) {

            return ((Value) base.get(a)).getTimestamp().getTime() < ((Value) base.get(b)).getTimestamp().getTime()
                    ? 1
                    : ((Value) base.get(a)).getTimestamp().getTime() == ((Value) base.get(b)).getTimestamp().getTime()
                    ? 0
                    : -1;
        }
    }


}
