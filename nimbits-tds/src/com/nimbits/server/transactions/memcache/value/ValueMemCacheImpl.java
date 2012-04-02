/*
 * Copyright (c) 2010 Tonic Solutions LLC.
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

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.MemCacheKey;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.server.transactions.memcache.MemCacheHelper;
import com.nimbits.server.task.TaskFactory;
import com.nimbits.server.value.RecordedValueTransactionFactory;
import com.nimbits.server.value.RecordedValueTransactions;

import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 11/27/11
 * Time: 12:40 PM
 */
@SuppressWarnings("unchecked")
public class ValueMemCacheImpl implements RecordedValueTransactions {

    MemcacheService buffer;
    MemcacheService cacheShared;
   // MemcacheService systemCache;
    // private EntityName pointName;
    private final Point point;

    public ValueMemCacheImpl(final Point point) {
        this.point = point;
        buffer = MemcacheServiceFactory.getMemcacheService(MemCacheHelper.valueMemCacheNamespace(point));
        cacheShared = MemcacheServiceFactory.getMemcacheService();
    }

    private void addPointToActiveList() {
        if (cacheShared.contains(MemCacheKey.activePoints)) {
            final Map<String, Point> points = (Map<String, Point>) cacheShared.get(MemCacheKey.activePoints);
            if (! points.containsKey(point.getKey())) {
                points.put(point.getKey(), point);
                cacheShared.delete(MemCacheKey.activePoints);
                cacheShared.put(MemCacheKey.activePoints, points);

            }

        }
        else {
            final Map<String, Point> points = new HashMap<String, Point>(1);
            points.put(point.getKey(), point);
            cacheShared.put(MemCacheKey.activePoints, points);
        }
    }


    @Override
    public Value getRecordedValuePrecedingTimestamp(final Date timestamp) throws NimbitsException {
        Value retObj;
        final String key =MemCacheHelper.currentValueCacheKey(point.getKey());

        try {
            if (buffer.contains(key)) {
                final Value value = (Value) buffer.get(key);
                if (value == null) {
                    buffer.delete(key);
                    retObj = RecordedValueTransactionFactory.getDaoInstance(point).getRecordedValuePrecedingTimestamp(timestamp);
                    if (retObj != null) {
                        buffer.put(key, retObj);
                    }
                } else {
                    if (timestamp.getTime() > value.getTimestamp().getTime()) {
                        retObj = value;
                    } else {
                        retObj = RecordedValueTransactionFactory.getDaoInstance(point).getRecordedValuePrecedingTimestamp(timestamp);
                    }
                }
            } else {
                retObj = RecordedValueTransactionFactory.getDaoInstance(point).getRecordedValuePrecedingTimestamp(timestamp);
                if (retObj != null) {
                    buffer.put(key, retObj);
                }

            }
        } catch (ClassCastException e) { //old cache data causing a provblem when upgrading.
            buffer.delete(key);
            retObj = RecordedValueTransactionFactory.getDaoInstance(point).getRecordedValuePrecedingTimestamp(timestamp);
            if (retObj != null) {
                buffer.put(key, retObj);
            }

        }


        return retObj;
    }

    @Override
    public Value recordValue(final Value v)  {

        final String k = MemCacheHelper.currentValueCacheKey(point.getKey());
        final String b = MemCacheHelper.valueBufferCacheKey(point);
        addPointToActiveList();
        try {
            final List<Long> stored;
            if (buffer.contains(b)) {
                stored = (List<Long>) buffer.get(b);
                stored.add(v.getTimestamp().getTime());
                buffer.delete(stored);
                buffer.put(b, stored);
            } else {
                stored = new ArrayList<Long>(10);
                stored.add(v.getTimestamp().getTime());
                buffer.put(b, stored);
            }
            buffer.put(v.getTimestamp().getTime(), v);
            if (stored.size() > Const.CONST_MAX_CACHED_VALUE_SIZE) {
                TaskFactory.getInstance().startMoveCachedValuesToStoreTask(point);
            }

            if (buffer.contains(k)) {
                final Value mostRecentCache = (Value) buffer.get(k);

                if (mostRecentCache == null || (v.getTimestamp().getTime() > mostRecentCache.getTimestamp().getTime())) {
                    buffer.delete(k);
                    buffer.put(k, v);
                }
            } else {
                buffer.put(k, v);
            }
        } catch (Exception e) {
            buffer.delete(k);
            buffer.delete(b);
        }

        return v;

    }

    @Override
    public List<Value> getTopDataSeries(final int maxValues) throws NimbitsException {
        final List<Value> cached = getBuffer();
        final List<Value> stored = RecordedValueTransactionFactory.getDaoInstance(point).getTopDataSeries(maxValues);
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
            final List<Value> stored = RecordedValueTransactionFactory.getDaoInstance(point).getTopDataSeries(maxValues, endDate);
            if (stored.size() > 0) {
                return mergeAndSort(stored, cached, endDate);
            } else {
                return cached;
            }
        }

    }

    @Override
    public List<Value> getDataSegment(final Timespan timespan) throws NimbitsException {
        final List<Value> stored = RecordedValueTransactionFactory.getDaoInstance(point).getDataSegment(timespan);
        final List<Value> cached = getBuffer();
        return mergeAndSort(stored, cached, timespan);
    }

    @Override
    public List<Value> getDataSegment(final Timespan timespan, final int start, final int end) throws NimbitsException {
        return RecordedValueTransactionFactory.getDaoInstance(point).getDataSegment(timespan, start, end);
    }

    @Override
    public void recordValues(final List<Value> values) throws NimbitsException {
        RecordedValueTransactionFactory.getDaoInstance(point).recordValues(values);
    }

    public List<Value> getCache(final Timespan timespan) {
        final String b = MemCacheHelper.valueBufferCacheKey(point);
        List<Value> retObj = null;
        final List<Long> x;
        if (buffer.contains(b)) {
            x = (List<Long>) buffer.get(b);
            final Map<Long, Object> valueMap = buffer.getAll(x);
            final ValueComparator bvc = new ValueComparator(valueMap);
            final TreeMap<Long, Object> sorted_map = new TreeMap(bvc);
            sorted_map.putAll(valueMap);
            retObj = new ArrayList<Value>(sorted_map.keySet().size());
            for (final Long ts : sorted_map.keySet()) {
                if (ts >= timespan.getStart().getTime() || ts <= timespan.getStart().getTime()) {
                    retObj.add((Value) sorted_map.get(ts));
                }
            }
        }

        return retObj;
    }

    @Override
    public List<ValueBlobStore> getAllStores() throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }

    @Override
    public void consolidateDate(final Date timestamp) throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }

    public List<Value> getBuffer() {
        final String b = MemCacheHelper.valueBufferCacheKey(point);


            final List<Long> x;
            if (buffer.contains(b)) {
                x = (List<Long>) buffer.get(b);
                final Map<Long, Object> valueMap = buffer.getAll(x);
                final ValueComparator bvc = new ValueComparator(valueMap);
                final TreeMap<Long, Object> sorted_map = new TreeMap(bvc);
                sorted_map.putAll(valueMap);
                final List<Value> retObj  = new ArrayList<Value>( sorted_map.keySet().size());
                for (final Long ts : sorted_map.keySet()) {
                    retObj.add((Value) sorted_map.get(ts));
                }
                return retObj;
            }
            else
            {
                return new ArrayList<Value>(0); //return an empty list to avoid a npe
            }



    }

    public void moveValuesFromCacheToStore() {

        final String b = MemCacheHelper.valueBufferCacheKey(point);

        try {
            if (buffer.contains(b)) {
                final List<Long> x = (List<Long>) buffer.get(b);
                if (x != null && x.size() > 0) {
                    buffer.delete(b);
                    final Map<Long, Object> valueMap = buffer.getAll(x);
                    buffer.deleteAll(x);
                    final List<Value> values = new ArrayList<Value>(valueMap.keySet().size());
                  //  int count = values.size();
                    for (final Map.Entry<Long, Object> longObjectEntry : valueMap.entrySet()) {
                        values.add((Value) longObjectEntry.getValue());
                    }
                    RecordedValueTransactionFactory.getDaoInstance(point).recordValues(values);
                }
             }
        } catch (Exception e) {
            buffer.delete(b);
        }


    }

    private static List<Value> mergeAndSort(final List<Value> first, final List<Value> second, final int max) {
        first.addAll(second);
        final Map<Long, Object> valueMap = new TreeMap<Long, Object>();
        for (final Value v : first) {
            valueMap.put(v.getTimestamp().getTime(), v);
        }


        final ValueComparator bvc = new ValueComparator(valueMap);
        final TreeMap<Long, Object> sorted_map = new TreeMap(bvc);
        sorted_map.putAll(valueMap);
        int c = 0;
        final List<Value> retObj = new ArrayList<Value>( sorted_map.keySet().size());
        for (final Long ts : sorted_map.keySet()) {
            c++;
            retObj.add((Value) sorted_map.get(ts));
            if (c >= max) {
                break;
            }
        }
        return retObj;
    }

    private static List<Value> mergeAndSort(final List<Value> first, final List<Value> second, final Date endDate) {
        first.addAll(second);
        final Map<Long, Object> valueMap = new TreeMap<Long, Object>();
        for (final Value v : first) {
            valueMap.put(v.getTimestamp().getTime(), v);
        }


        final ValueComparator bvc = new ValueComparator(valueMap);
        final TreeMap<Long, Object> sorted_map = new TreeMap(bvc);
        sorted_map.putAll(valueMap);
        final List<Value> retObj = new ArrayList<Value>(sorted_map.keySet().size());
        for (final Long ts : sorted_map.keySet()) {
            if (ts <= endDate.getTime()) {
                retObj.add((Value) sorted_map.get(ts));
            }

        }
        return retObj;
    }

    private static List<Value> mergeAndSort(final List<Value> first, final List<Value> second, final Timespan timespan) {
        first.addAll(second);
        final Map<Long, Object> valueMap = new TreeMap<Long, Object>();
        for (final Value v : first) {
            valueMap.put(v.getTimestamp().getTime(), v);
        }


        final ValueComparator bvc = new ValueComparator(valueMap);
        final TreeMap<Long, Object> sorted_map = new TreeMap(bvc);
        sorted_map.putAll(valueMap);
        final List<Value> retObj = new ArrayList<Value>(sorted_map.keySet().size());
        for (final Long ts : sorted_map.keySet()) {
            if ((ts >= timespan.getStart().getTime() - 1000) && (ts <= timespan.getEnd().getTime() + 1000)) {
                retObj.add((Value) sorted_map.get(ts));
            }
        }
        return retObj;
    }


    static class ValueComparator implements Comparator {

        Map base;

        public ValueComparator(final Map base) {
            this.base = base;
        }

        public int compare(final Object a, final Object b) {

            if (((Value) base.get(a)).getTimestamp().getTime() < ((Value) base.get(b)).getTimestamp().getTime()) {
                return 1;
            } else if (((Value) base.get(a)).getTimestamp().getTime() == ((Value) base.get(b)).getTimestamp().getTime()) {
                return 0;
            } else {
                return -1;
            }
        }
    }


}
