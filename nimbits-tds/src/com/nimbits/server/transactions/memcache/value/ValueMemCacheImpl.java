/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.transactions.memcache.value;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.memcache.InvalidValueException;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.MemCacheKey;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.server.admin.logging.LogHelper;
import com.nimbits.server.process.task.Task;
import com.nimbits.server.transactions.service.value.ValueTransactions;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 11/27/11
 * Time: 12:40 PM
 */
@SuppressWarnings("unchecked")
@Component("valueCache")
public class ValueMemCacheImpl implements ValueTransactions {

    private static final Logger log = Logger.getLogger(ValueMemCacheImpl.class.getName());
    private ValueTransactions valueDao;
    private Task taskFactory;
    private MemcacheService cacheFactory;


    private MemcacheService getBufferService(final Entity entity) {

        final String bufferNamespace =MemCacheKey.getKey(MemCacheKey.valueCache, entity.getKey());


        return MemcacheServiceFactory.getMemcacheService(bufferNamespace);

    }

    protected void addPointToActiveList(final Entity point) {
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
    protected void removePointFromActiveList(final Entity point) {
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


    @Override
    public List<Value> getRecordedValuePrecedingTimestamp(final Entity entity, final Date timestamp) throws NimbitsException {
        final List<Value> result = new ArrayList<Value>(1);

        String currentValueCacheKey = MemCacheKey.getKey(MemCacheKey.currentValueCache, entity.getKey());
        MemcacheService buffer = getBufferService(entity);
        try {
            if (buffer.contains(currentValueCacheKey)) {
                final Value value = (Value) buffer.get(currentValueCacheKey);
                if (value == null) {
                    buffer.delete(currentValueCacheKey);
                    List<Value> sample = valueDao.getRecordedValuePrecedingTimestamp(entity, timestamp);
                    if (! sample.isEmpty()) {
                        buffer.put(currentValueCacheKey, sample.get(0));
                        result.addAll(sample);
                    }

                } else {
                    if (timestamp.getTime() >= value.getTimestamp().getTime()) {
                        result.add(value);
                    }
                    else {
                        List<Value> bufferValues = getBuffer(entity);
                        List<Value> values = valueDao.getRecordedValuePrecedingTimestamp(entity, timestamp);
                        result.addAll(values);
                        for (Value v : bufferValues) {
                            if (v.getTimestamp().getTime() < timestamp.getTime()) {
                                result.add(v);
                            }
                        }
                    }
                }
            } else {
                LogHelper.log(this.getClass(), "Accessing data store for current value");

                List<Value> sample = valueDao.getRecordedValuePrecedingTimestamp(entity, timestamp);
                //TODO - keep a memchach list of known empty points to avoid repeated datastore calls here
                if (! sample.isEmpty()) {

                    buffer.put(currentValueCacheKey, sample.get(0));
                    result.addAll(sample);
                }



            }
        } catch (InvalidValueException e) {
            buffer.delete(currentValueCacheKey);
            List<Value> sample = valueDao.getRecordedValuePrecedingTimestamp(entity, timestamp);
            if (! sample.isEmpty()) {
                buffer.put(currentValueCacheKey, sample.get(0));
                result.addAll(sample);
            }
        } catch (ClassCastException e) { //old cache data causing a problem when upgrading.
            buffer.delete(currentValueCacheKey);
            List<Value> sample = valueDao.getRecordedValuePrecedingTimestamp(entity, timestamp);
            if (! sample.isEmpty()) {
                buffer.put(currentValueCacheKey, sample.get(0));
                result.addAll(sample);
            }

        }


        return getClosestMatchToTimestamp(result, timestamp);
    }


    protected static List<Value> getClosestMatchToTimestamp(final List<Value> values, final Date timestamp) {

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
    public Value recordValue(final Entity entity, final Value v)  {
        String bufferedListCacheKey = MemCacheKey.getKey(MemCacheKey.bufferedValueList, entity.getKey());
        String currentValueCacheKey = MemCacheKey.getKey(MemCacheKey.currentValueCache, entity.getKey());
        MemcacheService buffer = getBufferService(entity);
        addPointToActiveList(entity);
        try {
            final List<Long> stored;
            if (buffer.contains(bufferedListCacheKey)) {
                stored = (List<Long>) buffer.get(bufferedListCacheKey);
                stored.add(v.getTimestamp().getTime());
                buffer.delete(stored);
                buffer.put(bufferedListCacheKey, stored);
            } else {
                stored = new ArrayList<Long>(10);
                stored.add(v.getTimestamp().getTime());
                buffer.put(bufferedListCacheKey, stored);
            }
            buffer.put(v.getTimestamp().getTime(), v);
            if (stored.size() > Const.CONST_MAX_CACHED_VALUE_SIZE) {
                taskFactory.startMoveCachedValuesToStoreTask(entity);
            }

            if (buffer.contains(currentValueCacheKey)) {
                final Value mostRecentCache = (Value) buffer.get(currentValueCacheKey);

                if (mostRecentCache == null || v.getTimestamp().getTime() > mostRecentCache.getTimestamp().getTime()) {
                    buffer.delete(currentValueCacheKey);
                    buffer.put(currentValueCacheKey, v);
                }
            } else {
                buffer.put(currentValueCacheKey, v);
            }
        } catch (Exception e) {
            buffer.delete(currentValueCacheKey);
            buffer.delete(bufferedListCacheKey);
        }

        return v;

    }


    @Override
    public List<Value> getTopDataSeries(final Entity entity,final int maxValues) throws NimbitsException {
        final List<Value> cached = getBuffer(entity);
        final List<Value> stored = valueDao.getTopDataSeries(entity, maxValues);
        return mergeAndSort(cached, stored, maxValues);
    }


    //gets the most recent values for a entity up to a max count. If  count is in the buffer, just return them otherwise
    //get more values from the store.
    @Override
    public List<Value> getTopDataSeries(final Entity entity, final int maxValues, final Date endDate) throws NimbitsException {
        final List<Value> cached = getBuffer(entity);
        if (cached != null && cached.size() > maxValues) {
            return cached;
        } else {
            final List<Value> stored = valueDao.getTopDataSeries(entity, maxValues, endDate);
            return stored.isEmpty() ? cached : mergeAndSort(stored, cached, endDate);
        }

    }

    @Override
    public List<Value> getDataSegment(final Entity entity,final Timespan timespan) throws NimbitsException {
        final List<Value> stored = valueDao.getDataSegment(entity, timespan);
        final List<Value> cached = getBuffer(entity);
        return mergeAndSort(stored, cached, timespan);
    }

    @Override
    public List<Value> getDataSegment(final Entity entity,final Timespan timespan, final int start, final int end) throws NimbitsException {
        return valueDao.getDataSegment(entity, timespan, start, end);
    }

    @Override
    public List<ValueBlobStore> recordValues(final Entity entity,final List<Value> values) throws NimbitsException {
        return valueDao.recordValues(entity, values);
    }

    @Override
    public List<Value> getCache(final Entity entity, final Timespan timespan) {
        String bufferedListCacheKey = MemCacheKey.getKey(MemCacheKey.bufferedValueList, entity.getKey());
        MemcacheService buffer = getBufferService(entity);
        List<Value> retObj = null;
        if (buffer.contains(bufferedListCacheKey)) {
            final Collection<Long> x = (Collection<Long>) buffer.get(bufferedListCacheKey);
            final Map<Long, Object> valueMap = buffer.getAll(x);
            final ValueComparator bvc = new ValueComparator(valueMap);
            final Map<Long, Object> sorted_map = new TreeMap(bvc);
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

    @Override
    public List<ValueBlobStore> getAllStores(final Entity entity) throws NimbitsException {
        return valueDao.getAllStores(entity);
    }

    @Override
    public void consolidateDate(final Entity entity, final Date timestamp) throws NimbitsException {
        valueDao.consolidateDate(entity, timestamp);
    }

    @Override
    public List<ValueBlobStore> getBlobStoreByBlobKey(BlobKey key) throws NimbitsException {
        return valueDao.getBlobStoreByBlobKey(key);
    }

    @Override
    public ValueBlobStore mergeTimespan(final Entity entity, final Timespan timespan) throws NimbitsException {
        return valueDao.mergeTimespan(entity, timespan);

    }

    @Override
    public void purgeValues(final Entity entity) throws NimbitsException {
        String bufferedListCacheKey = MemCacheKey.getKey(MemCacheKey.bufferedValueList, entity.getKey());
        MemcacheService buffer = getBufferService(entity);
        if (buffer.contains(bufferedListCacheKey)) {
            buffer.delete(bufferedListCacheKey);
        }
        removePointFromActiveList(entity);
        valueDao.purgeValues(entity);
    }

    @Override
    public void deleteExpiredData(final Entity entity) {
        valueDao.deleteExpiredData(entity);
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
    public int preloadTimespan(final Entity entity, final Timespan timespan) throws NimbitsException {
        List<Value> stored = getDataSegment(entity, timespan);
        String key = MemCacheKey.preload.getText() + entity.getUUID();
        log.info("Storing " + stored.size());
        MemcacheService buffer = getBufferService(entity);
        List<List<Value>> split = splitUpList(stored);
        log.info("split up into " + split.size() + " pieces");
        int section = 0;
        int count = 0;
        for (List<Value> small : split) {
            String n = key + section;
            log.info("Stored key : " + n + "  " + small.size());
            count += small.size();
            if (buffer.contains(n)) {
                buffer.delete(n);
            }
            buffer.put(n, small);
            section += Const.CONST_QUERY_CHUNK_SIZE;
        }
        return count;



    }

    @Override
    public List<Value> getPreload(final Entity entity, final int section) throws NimbitsException {
        // int c = 0;
        // List<Value> values = new ArrayList<Value>(Const.CONST_QUERY_CHUNK_SIZE);


        //  while (c < count) {
        MemcacheService buffer = getBufferService(entity);
        String key = MemCacheKey.preload.getText() + entity.getUUID() + section;
        log.info(key);

        if (buffer.contains(key)) {
            return  (((List<Value>) buffer.get(key)));
            //  c += Const.CONST_QUERY_CHUNK_SIZE;
        }
        else {
            return Collections.emptyList();
        }



    }




    @Override
    public List<Value> getBuffer(final Entity entity) {
        String bufferedListCacheKey = MemCacheKey.getKey(MemCacheKey.bufferedValueList, entity.getKey());
        MemcacheService buffer = getBufferService(entity);
        if (buffer.contains(bufferedListCacheKey)) {
            final Collection<Long> x = (Collection<Long>) buffer.get(bufferedListCacheKey);
            final Map<Long, Object> valueMap = buffer.getAll(x);
            final ValueComparator bvc = new ValueComparator(valueMap);
            final Map<Long, Object> sorted_map = new TreeMap(bvc);
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

    @Override
    public void moveValuesFromCacheToStore(final Entity entity) {
        String bufferedListCacheKey = MemCacheKey.getKey(MemCacheKey.bufferedValueList, entity.getKey());
        MemcacheService buffer = getBufferService(entity);
        try {
            if (buffer.contains(bufferedListCacheKey)) {
                final Collection<Long> x = (Collection<Long>) buffer.get(bufferedListCacheKey);
                if (x != null && !x.isEmpty()) {
                    buffer.delete(bufferedListCacheKey);
                    final Map<Long, Object> valueMap = buffer.getAll(x);
                    buffer.deleteAll(x);
                    final List<Value> values = new ArrayList<Value>(valueMap.keySet().size());
                    //  int count = values.size();
                    for (final Map.Entry<Long, Object> longObjectEntry : valueMap.entrySet()) {
                        values.add((Value) longObjectEntry.getValue());
                    }
                    valueDao.recordValues(entity, values);
                }
            }
        } catch (Exception e) {
            buffer.delete(bufferedListCacheKey);
        }


    }

    private static List<Value> mergeAndSort(final Collection<Value> first, final Collection<Value> second, final int max) {
        first.addAll(second);
        final Map<Long, Object> valueMap = new TreeMap<Long, Object>();
        for (final Value v : first) {
            valueMap.put(v.getTimestamp().getTime(), v);
        }


        final ValueComparator bvc = new ValueComparator(valueMap);
        final Map<Long, Object> sorted_map = new TreeMap(bvc);
        sorted_map.putAll(valueMap);
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

    private static List<Value> mergeAndSort(final Collection<Value> first, final Collection<Value> second, final Date endDate) {
        first.addAll(second);
        final Map<Long, Object> valueMap = new TreeMap<Long, Object>();
        for (final Value v : first) {
            valueMap.put(v.getTimestamp().getTime(), v);
        }


        final ValueComparator bvc = new ValueComparator(valueMap);
        final Map<Long, Object> sorted_map = new TreeMap(bvc);
        sorted_map.putAll(valueMap);
        final List<Value> retObj = new ArrayList<Value>(sorted_map.keySet().size());
        for (final Map.Entry<Long, Object> longObjectEntry : sorted_map.entrySet()) {
            if (longObjectEntry.getKey() <= endDate.getTime()) {
                retObj.add((Value) longObjectEntry.getValue());
            }

        }
        return retObj;
    }

    private static List<Value> mergeAndSort(final Collection<Value> first, final Collection<Value> second, final Timespan timespan) {
        first.addAll(second);
        final Map<Long, Object> valueMap = new TreeMap<Long, Object>();
        for (final Value v : first) {
            valueMap.put(v.getTimestamp().getTime(), v);
        }


        final ValueComparator bvc = new ValueComparator(valueMap);
        final Map<Long, Object> sorted_map = new TreeMap(bvc);
        sorted_map.putAll(valueMap);
        final List<Value> retObj = new ArrayList<Value>(sorted_map.keySet().size());
        for (final Map.Entry<Long, Object> longObjectEntry : sorted_map.entrySet()) {
            if (longObjectEntry.getKey() >= timespan.getStart().getTime() - 1000 && longObjectEntry.getKey() <= timespan.getEnd().getTime() + 1000) {
                retObj.add((Value) longObjectEntry.getValue());
            }
        }
        return retObj;
    }

    public void setValueDao(ValueTransactions valueDao) {
        this.valueDao = valueDao;
    }


    public void setTaskFactory(Task taskFactory) {
        this.taskFactory = taskFactory;
    }

    public void setCacheFactory(MemcacheService cacheFactory) {
        this.cacheFactory = cacheFactory;
    }


    static class ValueComparator implements Comparator {

        Map base;

        ValueComparator(final Map base) {
            this.base = base;
        }

        @Override
        public int compare(final Object a, final Object b) {

            return ((Value) base.get(a)).getTimestamp().getTime() < ((Value) base.get(b)).getTimestamp().getTime()
                    ? 1
                    : ((Value) base.get(a)).getTimestamp().getTime() == ((Value) base.get(b)).getTimestamp().getTime()
                    ? 0
                    : -1;
        }
    }


}
