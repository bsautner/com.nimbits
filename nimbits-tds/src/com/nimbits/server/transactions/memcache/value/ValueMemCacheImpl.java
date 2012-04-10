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

import com.google.appengine.api.memcache.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.timespan.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.model.valueblobstore.*;
import com.nimbits.server.logging.*;
import com.nimbits.server.task.*;
import com.nimbits.server.value.*;

import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 11/27/11
 * Time: 12:40 PM
 */
@SuppressWarnings("unchecked")
public class ValueMemCacheImpl implements RecordedValueTransactions {

    private final MemcacheService buffer;
    private final MemcacheService cacheShared;
    private final Entity point;
    private final String currentValueCacheKey;
    private final static String valueListCacheKey = SettingType.serverVersion.getDefaultValue() + "VALUE_LIST_CACHE_KEY";

    public ValueMemCacheImpl(final Entity point) {
        this.point = point;
        final String safe = point.getKey().replace('@', '-').replace('/', '-').replace(' ', '_');
        final String bufferNamespace = MemCacheKey.valueCache + safe;

        currentValueCacheKey = MemCacheKey.currentValueCache + safe;
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


    @Override
    public Value getRecordedValuePrecedingTimestamp(final Date timestamp) throws NimbitsException {
        Value retObj;

        try {
            if (buffer.contains(currentValueCacheKey)) {
                final Value value = (Value) buffer.get(currentValueCacheKey);
                if (value == null) {
                    buffer.delete(currentValueCacheKey);
                    retObj = RecordedValueTransactionFactory.getDaoInstance(point).getRecordedValuePrecedingTimestamp(timestamp);
                    if (retObj != null) {
                        buffer.put(currentValueCacheKey, retObj);
                    }
                } else {
                    retObj = timestamp.getTime() > value.getTimestamp().getTime() ? value : RecordedValueTransactionFactory.getDaoInstance(point).getRecordedValuePrecedingTimestamp(timestamp);
                }
            } else {
                LogHelper.log(this.getClass(), "Accessing data store for current value");

                retObj = RecordedValueTransactionFactory.getDaoInstance(point).getRecordedValuePrecedingTimestamp(timestamp);

                if (retObj != null) {
                    LogHelper.log(this.getClass(), "Found value in store" + retObj.getValueWithNote());
                    buffer.put(currentValueCacheKey, retObj);
                }
                else {
                    LogHelper.log(this.getClass(), "Nothing found in store");
                }


            }
        } catch (ClassCastException e) { //old cache data causing a provblem when upgrading.
            buffer.delete(currentValueCacheKey);
            retObj = RecordedValueTransactionFactory.getDaoInstance(point).getRecordedValuePrecedingTimestamp(timestamp);
            if (retObj != null) {
                buffer.put(currentValueCacheKey, retObj);
            }

        }


        return retObj;
    }

    @Override
    public Value recordValue(final Value v)  {



        addPointToActiveList();
        try {
            final List<Long> stored;
            if (buffer.contains(valueListCacheKey)) {
                stored = (List<Long>) buffer.get(valueListCacheKey);
                stored.add(v.getTimestamp().getTime());
                buffer.delete(stored);
                buffer.put(valueListCacheKey, stored);
            } else {
                stored = new ArrayList<Long>(10);
                stored.add(v.getTimestamp().getTime());
                buffer.put(valueListCacheKey, stored);
            }
            buffer.put(v.getTimestamp().getTime(), v);
            if (stored.size() > Const.CONST_MAX_CACHED_VALUE_SIZE) {
                TaskFactory.getInstance().startMoveCachedValuesToStoreTask(point);
            }

            if (buffer.contains(currentValueCacheKey)) {
                final Value mostRecentCache = (Value) buffer.get(currentValueCacheKey);

                if (mostRecentCache == null || (v.getTimestamp().getTime() > mostRecentCache.getTimestamp().getTime())) {
                    buffer.delete(currentValueCacheKey);
                    buffer.put(currentValueCacheKey, v);
                }
            } else {
                buffer.put(currentValueCacheKey, v);
            }
        } catch (Exception e) {
            buffer.delete(currentValueCacheKey);
            buffer.delete(valueListCacheKey);
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
            return stored.isEmpty() ? cached : mergeAndSort(stored, cached, endDate);
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

    @Override
    public List<Value> getCache(final Timespan timespan) {

        List<Value> retObj = null;
        final List<Long> x;
        if (buffer.contains(valueListCacheKey)) {
            x = (List<Long>) buffer.get(valueListCacheKey);
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
        throw new NimbitsException("Not Implemented");
    }

    @Override
    public void consolidateDate(final Date timestamp) throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }

    @Override
    public List<Value> getBuffer() {


            final List<Long> x;
            if (buffer.contains(valueListCacheKey)) {
                x = (List<Long>) buffer.get(valueListCacheKey);
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
            if (buffer.contains(valueListCacheKey)) {
                final Collection<Long> x = (Collection<Long>) buffer.get(valueListCacheKey);
                if (x != null && !x.isEmpty()) {
                    buffer.delete(valueListCacheKey);
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
            buffer.delete(valueListCacheKey);
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
            if ((longObjectEntry.getKey() >= timespan.getStart().getTime() - 1000) && (longObjectEntry.getKey() <= timespan.getEnd().getTime() + 1000)) {
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

            return ((Value) base.get(a)).getTimestamp().getTime() < ((Value) base.get(b)).getTimestamp().getTime() ? 1 : ((Value) base.get(a)).getTimestamp().getTime() == ((Value) base.get(b)).getTimestamp().getTime() ? 0 : -1;
        }
    }


}
