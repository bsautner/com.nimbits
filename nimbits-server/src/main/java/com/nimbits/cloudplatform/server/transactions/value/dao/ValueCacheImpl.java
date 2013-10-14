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

package com.nimbits.cloudplatform.server.transactions.value.dao;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.memcache.InvalidValueException;
import com.nimbits.cloudplatform.client.constants.Const;
import com.nimbits.cloudplatform.client.enums.MemCacheKey;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.user.User;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.cloudplatform.server.process.task.TaskImpl;
import com.nimbits.cloudplatform.server.transactions.cache.NimbitsCache;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceFactory;
import com.nimbits.cloudplatform.server.transactions.entity.service.EntityService;
import com.nimbits.cloudplatform.server.transactions.value.ValueServiceFactory;
import org.apache.commons.lang3.Range;
import org.springframework.stereotype.Component;

import javax.jdo.PersistenceManagerFactory;
import java.io.IOException;
import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 11/27/11
 * Time: 12:40 PM
 */
@Component("valueCache")
@SuppressWarnings("unchecked") //TODO
public class ValueCacheImpl implements ValueCache {

    protected final String KEY_BUFFERED_VALUE_LIST = "KEY_BUFFERED_VALUE_LIST";
    private static final int SEC = 1000;
    private final ValueDao valueDAO;
    private final NimbitsCache cache;
    private final EntityService entityService = EntityServiceFactory.getInstance();
    public ValueCacheImpl(PersistenceManagerFactory pmf, NimbitsCache cache) {
        this.valueDAO = ValueServiceFactory.getDaoInstance(pmf);
        this.cache = cache;
    }

    @Override
    public List<Value> addValueToBuffer(final Entity entity, final Value value) {
        String bufferedListCacheKey = bufferCacheKey(entity);
        List<Value> buffer;
        if (cache.containsKey(bufferedListCacheKey)) {
             buffer = (List<Value>) cache.get(bufferedListCacheKey);
             if (buffer == null) {
                 cache.delete(bufferedListCacheKey);
                 buffer = new ArrayList<Value>(1);
             }


        }
        else {
            buffer = new ArrayList<Value>(1);
        }
        buffer.add(value);
        cache.put(bufferedListCacheKey, buffer);
        return buffer;
    }
    @Override
    public List<Value> getValueBuffer(final Entity entity) {
        if (cache.containsKey(bufferCacheKey(entity))) {
           return (List<Value>) cache.get(bufferCacheKey(entity));


        }
        else {
          return Collections.emptyList();
        }
    }
    //todo lookup and delete before put may not be neccesary
    @Override
    public Value recordValue(final User user,
                             final Entity entity,
                             final Value value)  {


        addPointToActiveList(entity);
        List<Value> buffer = addValueToBuffer(entity, value);

        if (buffer != null && buffer.size() > Const.CONST_MAX_CACHED_VALUE_SIZE) {
            TaskImpl.startMoveCachedValuesToStoreTask(entity);
        }




        return value;

    }

    @Override
    public void addPointToActiveList(final Entity point) {
        Map<String, Entity> points;
        if (cache.contains(MemCacheKey.activePoints)) {
            try {
                points = (Map<String, Entity>) cache.get(MemCacheKey.activePoints);
                if (points == null) { //contains a null map?
                    points = new HashMap<String, Entity>(1);
                    points.put(point.getKey(), point);
                    cache.delete(MemCacheKey.activePoints);
                    cache.put(MemCacheKey.activePoints, points);
                }
                else if (! points.containsKey(point.getKey())) {
                    points.put(point.getKey(), point);
                    cache.delete(MemCacheKey.activePoints);
                    cache.put(MemCacheKey.activePoints, points);

                }
            } catch (InvalidValueException e) {
                cache.reloadCache();
                points = new HashMap<String, Entity>(1);
                points.put(point.getKey(), point);
                cache.delete(MemCacheKey.activePoints);
                cache.put(MemCacheKey.activePoints, points);
            }

        }
        else {
            points = new HashMap<String, Entity>(1);
            points.put(point.getKey(), point);
            cache.put(MemCacheKey.activePoints, points);
        }
    }

    @Override
    public void removePointFromActiveList(final Entity point) {
        Map<String, Entity> points;
        if (cache.contains(MemCacheKey.activePoints)) {
            try {
                points = (Map<String, Entity>) cache.get(MemCacheKey.activePoints);

                if (points != null && ! points.containsKey(point.getKey())) {
                    points.remove(point.getKey());
                    cache.delete(MemCacheKey.activePoints);
                    cache.put(MemCacheKey.activePoints, points);

                }
            } catch (InvalidValueException e) {
                cache.reloadCache();
                points = new HashMap<String, Entity>(1);
                points.put(point.getKey(), point);
                cache.delete(MemCacheKey.activePoints);
                cache.put(MemCacheKey.activePoints, points);
            }

        }

    }



    @Override
    public List<Value> getRecordedValuePrecedingTimestamp(final Entity entity, final Date timestamp)   {

        List<Value> buffer = getValueBuffer(entity);
        if (buffer.isEmpty()) {
             buffer = valueDAO.getRecordedValuePrecedingTimestamp(entity, timestamp);
        }
        return getClosestMatchToTimestamp(buffer, timestamp);


    }


    @Override
    public List<Value> getClosestMatchToTimestamp(final List<Value> values, final Date timestamp) {

        List<Value> shucked = new ArrayList<Value>(values.size());
        for (Value v : values) {
            if (v.getTimestamp().getTime() <= timestamp.getTime()) {
                shucked.add(v);
            }
        }


        Collections.sort(shucked);

        return shucked;





    }

    @Override
    public List<Value> getTopDataSeries(final Entity entity, final int maxValues)  {
        //log.info("getting top " + maxValues + " data series for " + entity.getKey());
        final List<Value> cached = getValueBuffer(entity);
        //log.info("found in buffer:" + cached.size());
        final List<Value> stored = valueDAO.getTopDataSeries(entity, maxValues);
        //log.info("found in storage:" + stored.size());
        return mergeAndSort(cached, stored, maxValues);
    }


    //gets the most recent values for a entity up to a max count. If  count is in the buffer, just return them otherwise
    //get more values from the store.

    @Override
    public List<Value> getTopDataSeries(final Entity entity, final int maxValues, final Date endDate)  {
        final List<Value> cached = getValueBuffer(entity);
        if (cached != null && cached.size() > maxValues) {
            return cached;
        } else {
            final List<Value> stored = valueDAO.getTopDataSeries(entity, maxValues, endDate);
            return stored.isEmpty() ? cached : mergeAndSort(stored, cached, endDate);
        }

    }


    @Override
    public List<Value> getDataSegment(final Entity entity, final Range<Long> timespan, final Range<Integer> range)  {
        final List<Value> stored = valueDAO.getDataSegment(entity, timespan);
        final List<Value> cached = getValueBuffer(entity);
        List<Value> allData =  mergeAndSort(stored, cached, timespan);   //todo cache result
        List<Value> result = new ArrayList<Value>();
        int max = range.getMaximum() > allData.size() ? allData.size() : range.getMaximum();
        for (int i = range.getMinimum(); i < max; i++) {
            result.add(allData.get(i));
        }
        return result;

    }

    @Override
    public List<Value> getSeries(final Entity entity, final Range<Long> timespan)  {
        final List<Value> stored = valueDAO.getDataSegment(entity, timespan);
        final List<Value> cached = getValueBuffer(entity);
        List<Value> allData =  mergeAndSort(stored, cached, timespan);   //todo cache result

        return allData;

    }


    @Override
    public List<ValueBlobStore> recordValues(final Entity entity, final List<Value> values)  {
        return valueDAO.recordValues(entity, values);
    }



    @Override
    public List<ValueBlobStore> getAllStores(final Entity entity)  {
        return valueDAO.getAllStores(entity);
    }


    @Override
    public void consolidateDate(final Entity entity, final Date timestamp) throws IOException {
        valueDAO.consolidateDate(entity, timestamp);
    }


    @Override
    public List<ValueBlobStore> getBlobStoreByBlobKey(BlobKey key)  {
        return valueDAO.getBlobStoreByBlobKey(key);
    }


    @Override
    public ValueBlobStore mergeTimespan(final Entity entity, final Range<Date> timespan) throws IOException {
        return valueDAO.mergeTimespan(entity, timespan);

    }


    @Override
    public void purgeValues(final Entity entity)  {


        if (cache.contains(bufferCacheKey(entity))) {
            cache.delete(bufferCacheKey(entity));
        }
        removePointFromActiveList(entity);
        valueDAO.purgeValues(entity);
    }


    @Override
    public void deleteExpiredData(final Entity entity) {
        valueDAO.deleteExpiredData(entity);
    }

    @Override
    public List<List<Value>> splitUpList(final List<Value> original) {

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


    @Override
    public int preloadTimespan(final Entity entity, final Range timespan) throws Exception {
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
            if (cache.contains(smallKey)) {
                cache.delete(smallKey);
            }
            cache.put(smallKey, small);
            section += Const.CONST_QUERY_CHUNK_SIZE;
        }
        return count;



    }


    @Override
    public List<Value> getPreload(final Entity entity, final int section)  {

        String key = MemCacheKey.preload.getText() + entity.getUUID() + section;
        //log.info(key);

        if (cache.contains(key)) {
            return  (((List<Value>) cache.get(key)));
            //  c += Const.CONST_QUERY_CHUNK_SIZE;
        }
        else {
            return Collections.emptyList();
        }



    }


//    @Override
//    public List<Value> getBuffer(final Entity entity) {
//
//
//        if (cache.contains(bufferCacheKey(entity))) {
//            final Collection<Long> bufferedValueList = (Collection<Long>) cache.get(bufferCacheKey(entity));
//            final Map<Long, Object> valueMap = cache.getAll(bufferedValueList);
//            final ValueComparator bvc = new ValueComparator(valueMap);
//            final Map<Long, Object> sorted_map = new TreeMap<Long, Object>(bvc);
//           sorted_map.putAll(valueMap);
//           final List<Value> retObj  = new ArrayList<Value>( sorted_map.keySet().size());
//           for (final Map.Entry<Long, Object> longObjectEntry : sorted_map.entrySet()) {
//                retObj.add((Value) longObjectEntry.getValue());
//           }
//            return retObj;
//        }
//        else
//        {
//            return new ArrayList<Value>(0); //return an empty list to avoid a npe
//        }
//
//
//
//    }


    @Override
    public void moveValuesFromCacheToStore(final Entity entity) {
            List<Value> buffer = getValueBuffer(entity);

            if (! buffer.isEmpty()) {
                    cache.delete(bufferCacheKey(entity));
                    valueDAO.recordValues(entity, buffer);

                }
            }


    private List<Value> mergeAndSort(final Collection<Value> first, final Collection<Value> second, final int max) {

        final List<Value> retObj = new ArrayList<Value>(max);
        final List<Value> merged = new ArrayList<Value>(first);
        merged.addAll(second);
        Collections.sort(merged);
        for (int i = 0; i < max; i++) {
            if (merged.size() > i) {
                retObj.add(merged.get(i));
            }
            else {
                break;
            }
        }

        return retObj;


    }


    private List<Value> mergeAndSort(final Collection<Value> first, final Collection<Value> second, final Date endDate) {
        final List<Value> retObj = new ArrayList<Value>(first.size() + second.size());
        final List<Value> merged = new ArrayList<Value>(first);
        merged.addAll(second);
        Collections.sort(merged);
        for (Value v : merged) {
            if (v.getTimestamp().getTime() < endDate.getTime()) {
                retObj.add(v);
            }
        }

        return retObj;
    }

    private <E> List<E> mergeAndSort(final Collection<E> first, final Collection<E> second, final Range<Long> timespan) {
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

    private String bufferCacheKey(Entity entity) {
        return (KEY_BUFFERED_VALUE_LIST + entity.getKey());
    }


    class ValueComparator implements Comparator {

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
