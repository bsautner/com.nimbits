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
import com.nimbits.server.process.task.TaskFactory;
import com.nimbits.server.transactions.service.value.ValueTransactionFactory;
import com.nimbits.server.transactions.service.value.ValueTransactions;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 11/27/11
 * Time: 12:40 PM
 */
@SuppressWarnings("unchecked")
public class ValueMemCacheImpl implements ValueTransactions {

    private final MemcacheService buffer;
    private final MemcacheService cacheShared;
    private final Entity point;

    private final String currentValueCacheKey;
    private final String bufferedListCacheKey;


    static final Logger log = Logger.getLogger(ValueMemCacheImpl.class.getName());



    public ValueMemCacheImpl(final Entity point) {
        this.point = point;

        final String bufferNamespace =MemCacheKey.getKey(MemCacheKey.valueCache, point.getKey());
        bufferedListCacheKey = MemCacheKey.getKey(MemCacheKey.bufferedValueList, point.getKey());
        currentValueCacheKey = MemCacheKey.getKey(MemCacheKey.currentValueCache, point.getKey());
        buffer = MemcacheServiceFactory.getMemcacheService(bufferNamespace);
        cacheShared = MemcacheServiceFactory.getMemcacheService();
    }



    protected void addPointToActiveList() {
        Map<String, Entity> points;
        if (cacheShared.contains(MemCacheKey.activePoints)) {
            try {
                points = (Map<String, Entity>) cacheShared.get(MemCacheKey.activePoints);
                if (points == null) { //contains a null map?
                    points = new HashMap<String, Entity>(1);
                    points.put(point.getKey(), point);
                    cacheShared.delete(MemCacheKey.activePoints);
                    cacheShared.put(MemCacheKey.activePoints, points);
                }
                else if (! points.containsKey(point.getKey())) {
                    points.put(point.getKey(), point);
                    cacheShared.delete(MemCacheKey.activePoints);
                    cacheShared.put(MemCacheKey.activePoints, points);

                }
            } catch (InvalidValueException e) {
                cacheShared.clearAll();
                points = new HashMap<String, Entity>(1);
                points.put(point.getKey(), point);
                cacheShared.delete(MemCacheKey.activePoints);
                cacheShared.put(MemCacheKey.activePoints, points);
            }

        }
        else {
            points = new HashMap<String, Entity>(1);
            points.put(point.getKey(), point);
            cacheShared.put(MemCacheKey.activePoints, points);
        }
    }
    protected void removePointFromActiveList() {
        Map<String, Entity> points;
        if (cacheShared.contains(MemCacheKey.activePoints)) {
            try {
                points = (Map<String, Entity>) cacheShared.get(MemCacheKey.activePoints);

                if (points != null && ! points.containsKey(point.getKey())) {
                    points.remove(point.getKey());
                    cacheShared.delete(MemCacheKey.activePoints);
                    cacheShared.put(MemCacheKey.activePoints, points);

                }
            } catch (InvalidValueException e) {
                cacheShared.clearAll();
                points = new HashMap<String, Entity>(1);
                points.put(point.getKey(), point);
                cacheShared.delete(MemCacheKey.activePoints);
                cacheShared.put(MemCacheKey.activePoints, points);
            }

        }

    }


    @Override
    public List<Value> getRecordedValuePrecedingTimestamp(final Date timestamp) throws NimbitsException {
        final List<Value> result = new ArrayList<Value>(1);

        try {
            if (buffer.contains(currentValueCacheKey)) {
                final Value value = (Value) buffer.get(currentValueCacheKey);
                if (value == null) {
                    buffer.delete(currentValueCacheKey);
                    List<Value> sample = ValueTransactionFactory.getDaoInstance(point).getRecordedValuePrecedingTimestamp(timestamp);
                    if (! sample.isEmpty()) {
                        buffer.put(currentValueCacheKey, sample.get(0));
                        result.addAll(sample);
                    }

                } else {
                    if (timestamp.getTime() >= value.getTimestamp().getTime()) {
                        result.add(value);
                    }
                    else {
                        List<Value> buffer = getBuffer();
                        List<Value> values = ValueTransactionFactory.getDaoInstance(point).getRecordedValuePrecedingTimestamp(timestamp);
                        result.addAll(values);
                        for (Value v : buffer) {
                            if (v.getTimestamp().getTime() < timestamp.getTime()) {
                                result.add(v);
                            }
                        }
                    }
                }
            } else {
                LogHelper.log(this.getClass(), "Accessing data store for current value");

                List<Value> sample = ValueTransactionFactory.getDaoInstance(point).getRecordedValuePrecedingTimestamp(timestamp);
                //TODO - keep a memchach list of known empty points to avoid repeated datastore calls here
                if (! sample.isEmpty()) {

                    buffer.put(currentValueCacheKey, sample.get(0));
                    result.addAll(sample);
                }



            }
        } catch (InvalidValueException e) {
            buffer.delete(currentValueCacheKey);
            List<Value> sample = ValueTransactionFactory.getDaoInstance(point).getRecordedValuePrecedingTimestamp(timestamp);
            if (! sample.isEmpty()) {
                buffer.put(currentValueCacheKey, sample.get(0));
                result.addAll(sample);
            }
        } catch (ClassCastException e) { //old cache data causing a problem when upgrading.
            buffer.delete(currentValueCacheKey);
            List<Value> sample = ValueTransactionFactory.getDaoInstance(point).getRecordedValuePrecedingTimestamp(timestamp);
            if (! sample.isEmpty()) {
                buffer.put(currentValueCacheKey, sample.get(0));
                result.addAll(sample);
            }

        }


        return getClosestMatchToTimestamp(result, timestamp);
    }


    protected static List<Value> getClosestMatchToTimestamp(final List<Value> values, final Date timestamp) {
        List<Value> result = new ArrayList<Value>(1);
        Value value;

        long delta = 0;
        if (! values.isEmpty()) {
            value = values.get(0);
            for (Value v : values) {
                if (v.getTimestamp().getTime() == timestamp.getTime()) {
                    result.add(v);
                    return result; //perfect match
                }
                else if (v.getTimestamp().getTime() < timestamp.getTime()) {
                    delta = v.getTimestamp().getTime()  - timestamp.getTime();
                    if (delta  > (value.getTimestamp().getTime()  - timestamp.getTime())) {
                        value = v;
                    }
                }


            }
            result.add(value);
        }
       return result;


    }


    @Override
    public Value recordValue(final Value v)  {



        addPointToActiveList();
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
                TaskFactory.getInstance().startMoveCachedValuesToStoreTask(point);
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
    public List<Value> getTopDataSeries(final int maxValues) throws NimbitsException {
        final List<Value> cached = getBuffer();
        final List<Value> stored = ValueTransactionFactory.getDaoInstance(point).getTopDataSeries(maxValues);
        return mergeAndSort(cached, stored, maxValues);
    }


    //gets the most recent values for a point up to a max count. If  count is in the buffer, just return them otherwise
    //get more values from the store.
    @Override
    public List<Value> getTopDataSeries(final int maxValues, final Date endDate) throws NimbitsException {
        final List<Value> cached = getBuffer();
        if (cached != null && cached.size() > maxValues) {
            return cached;
        } else {
            final List<Value> stored = ValueTransactionFactory.getDaoInstance(point).getTopDataSeries(maxValues, endDate);
            return stored.isEmpty() ? cached : mergeAndSort(stored, cached, endDate);
        }

    }

    @Override
    public List<Value> getDataSegment(final Timespan timespan) throws NimbitsException {
        final List<Value> stored = ValueTransactionFactory.getDaoInstance(point).getDataSegment(timespan);
        final List<Value> cached = getBuffer();
        return mergeAndSort(stored, cached, timespan);
    }

    @Override
    public List<Value> getDataSegment(final Timespan timespan, final int start, final int end) throws NimbitsException {
        return ValueTransactionFactory.getDaoInstance(point).getDataSegment(timespan, start, end);
    }

    @Override
    public List<ValueBlobStore> recordValues(final List<Value> values) throws NimbitsException {
        return ValueTransactionFactory.getDaoInstance(point).recordValues(values);
    }

    @Override
    public List<Value> getCache(final Timespan timespan) {

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
    public List<ValueBlobStore> getAllStores() throws NimbitsException {
        return ValueTransactionFactory.getDaoInstance(point).getAllStores();
    }

    @Override
    public void consolidateDate(final Date timestamp) throws NimbitsException {
        ValueTransactionFactory.getDaoInstance(point).consolidateDate(timestamp);
    }

    @Override
    public List<ValueBlobStore> getBlobStoreByBlobKey(BlobKey key) throws NimbitsException {
        return ValueTransactionFactory.getDaoInstance(point).getBlobStoreByBlobKey(key);
    }

    @Override
    public ValueBlobStore mergeTimespan(Timespan timespan) throws NimbitsException {
        return ValueTransactionFactory.getDaoInstance(point).mergeTimespan(timespan);

    }

    @Override
    public void purgeValues() throws NimbitsException {
        if (buffer.contains(bufferedListCacheKey)) {
            buffer.delete(bufferedListCacheKey);
        }
        removePointFromActiveList();
        ValueTransactionFactory.getDaoInstance(point).purgeValues();
    }

    @Override
    public void deleteExpiredData() {
        ValueTransactionFactory.getDaoInstance(point).deleteExpiredData();
    }

    protected static List<List<Value>> splitUpList(final List<Value> original) {

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
    public int preloadTimespan(Timespan timespan) throws NimbitsException {
        List<Value> stored = getDataSegment(timespan);
        String key = MemCacheKey.preload.getText() + point.getUUID();
        log.info("Storing " + stored.size());

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
    public List<Value> getPreload(int section) throws NimbitsException {
        // int c = 0;
        // List<Value> values = new ArrayList<Value>(Const.CONST_QUERY_CHUNK_SIZE);


        //  while (c < count) {

        String key = MemCacheKey.preload.getText() + point.getUUID() + section;
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
    public List<Value> getBuffer() {


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
    public void moveValuesFromCacheToStore() {


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
                    ValueTransactionFactory.getDaoInstance(point).recordValues(values);
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
