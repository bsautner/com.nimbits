package com.nimbits.server.memcache.value;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.recordedvalue.RecordedValueTransactionFactory;
import com.nimbits.server.recordedvalue.RecordedValueTransactions;
import com.nimbits.server.task.TaskFactoryLocator;

import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 11/27/11
 * Time: 12:40 PM
 */
public class ValueMemCacheImpl implements RecordedValueTransactions {

    MemcacheService cache;
    MemcacheService systemCache;
    // private PointName pointName;
    private final Point p;

    public ValueMemCacheImpl(final Point point) {
        this.p = point;
        cache = MemcacheServiceFactory.getMemcacheService(point.getUUID());
        systemCache = MemcacheServiceFactory.getMemcacheService(Const.CONST_SERVER_VERSION + Const.CACHE_KEY_SYSTEM + Const.CONST_SERVER_VERSION);
    }

    private String currentValueCacheKey(String uuid) {
        return Const.CONST_SERVER_VERSION + Const.CACHE_KEY_PREFIX + "MOST_RECENT_VALUE_CACHE" + uuid;
    }

    private String cacheKey(String uuid) {
        return Const.CONST_SERVER_VERSION + Const.CACHE_KEY_PREFIX + "BUFFERMEM" + uuid;
    }

    @Override
    public Value getRecordedValuePrecedingTimestamp(final Date timestamp) {
        Value retObj;
        final String key = currentValueCacheKey(p.getUUID());

        try {
            if (cache.contains(key)) {
                final Value value = (Value) cache.get(key);
                if (value == null) {
                    cache.delete(key);
                    retObj = RecordedValueTransactionFactory.getDaoInstance(p).getRecordedValuePrecedingTimestamp(timestamp);
                    if (retObj != null) {
                        cache.put(key, retObj);
                    }
                } else {
                    if (timestamp.getTime() > value.getTimestamp().getTime()) {
                        retObj = value;
                    } else {
                        retObj = RecordedValueTransactionFactory.getDaoInstance(p).getRecordedValuePrecedingTimestamp(timestamp);
                    }
                }
            } else {
                retObj = RecordedValueTransactionFactory.getDaoInstance(p).getRecordedValuePrecedingTimestamp(timestamp);
                if (retObj != null) {
                    cache.put(key, retObj);
                }

            }
        } catch (ClassCastException e) { //old cache data causing a provblem when upgrading.
            cache.delete(key);
            retObj = RecordedValueTransactionFactory.getDaoInstance(p).getRecordedValuePrecedingTimestamp(timestamp);
            if (retObj != null) {
                cache.put(key, retObj);
            }

        }


        return retObj;
    }

    @Override
    public Value recordValue(final Value v) throws NimbitsException {


        // final Value retObj = RecordedValueTransactionFactory.getDaoInstance().recordValue(point, v);
        final String k = currentValueCacheKey(p.getUUID());
        final String b = cacheKey(p.getUUID());

        List<Long> activityLog;
        if (systemCache.contains(Const.PARAM_POINTS)) {
            activityLog = (List<Long>) systemCache.get(Const.PARAM_POINTS);
            if (!activityLog.contains(p.getId())) {
                activityLog.add(p.getId());
                systemCache.delete(Const.PARAM_POINTS);
                systemCache.put(Const.PARAM_POINTS, activityLog);

            }
        }


        List<Long> stored;
        if (cache.contains(b)) {
            stored = (List<Long>) cache.get(b);
            stored.add(v.getTimestamp().getTime());
            cache.delete(stored);
            cache.put(b, stored);
        } else {
            stored = new ArrayList<Long>();
            stored.add(v.getTimestamp().getTime());
            cache.put(b, stored);
        }
        cache.put(v.getTimestamp().getTime(), v);
        if (stored.size() > Const.CONST_MAX_CACHED_VALUE_SIZE) {
            TaskFactoryLocator.getInstance().startMoveCachedValuesToStoreTask(p);
        }

        if (cache.contains(k)) {
            final Value mostRecentCache = (Value) cache.get(k);

            if (mostRecentCache == null || (v.getTimestamp().getTime() > mostRecentCache.getTimestamp().getTime())) {
                cache.delete(k);
                cache.put(k, v);
            }
        } else {
            cache.put(k, v);
        }

        return v;

    }

    @Override
    public List<Value> getTopDataSeries(final int maxValues) {
        List<Value> cached = getCache();
        List<Value> stored = RecordedValueTransactionFactory.getDaoInstance(p).getTopDataSeries(maxValues);
        return mergeAndSort(cached, stored, maxValues);
    }

    @Override
    public List<Value> getTopDataSeries(final int maxValues, final Date endDate) {
        List<Value> cached = getCache();
        if (cached.size() > maxValues) {
            return cached;
        } else {
            List<Value> stored = RecordedValueTransactionFactory.getDaoInstance(p).getTopDataSeries(maxValues, endDate);
            if (stored.size() > 0) {
                return mergeAndSort(stored, cached, endDate);
            } else {
                return cached;
            }
        }

    }

    @Override
    public List<Value> getDataSegment(final Timespan timespan) {
        List<Value> stored = RecordedValueTransactionFactory.getDaoInstance(p).getDataSegment(timespan);
        List<Value> cached = getCache();
        return mergeAndSort(stored, cached, timespan);
    }

    @Override
    public List<Value> getDataSegment(final Timespan timespan, final int start, final int end) {
        return RecordedValueTransactionFactory.getDaoInstance(p).getDataSegment(timespan, start, end);
    }


    @Override
    public void recordValues(List<Value> values) {
        RecordedValueTransactionFactory.getDaoInstance(p).recordValues(values);
    }

    public List<Value> getCache(final Timespan timespan) {
        final String b = cacheKey(p.getUUID());
        List<Value> retObj = new ArrayList<Value>();
        List<Long> x;
        if (cache.contains(b)) {
            x = (List<Long>) cache.get(b);
            Map<Long, Object> valueMap = cache.getAll(x);
            ValueComparator bvc = new ValueComparator(valueMap);
            TreeMap<Long, Object> sorted_map = new TreeMap(bvc);
            sorted_map.putAll(valueMap);
            for (Long ts : sorted_map.keySet()) {
                if (ts >= timespan.getStart().getTime() || ts <= timespan.getStart().getTime()) {
                    retObj.add((Value) sorted_map.get(ts));
                }
            }
        }

        return retObj;
    }

    public List<Value> getCache() {
        final String b = cacheKey(p.getUUID());
        List<Value> retObj = new ArrayList<Value>();
        List<Long> x;
        if (cache.contains(b)) {
            x = (List<Long>) cache.get(b);
            Map<Long, Object> valueMap = cache.getAll(x);
            ValueComparator bvc = new ValueComparator(valueMap);
            TreeMap<Long, Object> sorted_map = new TreeMap(bvc);
            sorted_map.putAll(valueMap);
            for (Long ts : sorted_map.keySet()) {
                retObj.add((Value) sorted_map.get(ts));
            }
        }

        return retObj;
    }

    public void moveValuesFromCacheToStore() {

        final String b = cacheKey(p.getUUID());
        if (cache.contains(b)) {
            final List<Long> x = (List<Long>) cache.get(b);
            if (x != null && x.size() > 0) {
                cache.delete(b);
                final Map<Long, Object> valueMap = cache.getAll(x);
                cache.deleteAll(x);
                final List<Value> values = new ArrayList<Value>();

                for (final Long ts : valueMap.keySet()) {
                    values.add((Value) valueMap.get(ts));
                }
                RecordedValueTransactionFactory.getDaoInstance(p).recordValues(values);
            }

        }


    }

    private List<Value> mergeAndSort(final List<Value> first, final List<Value> second, final int max) {
        first.addAll(second);
        Map<Long, Object> valueMap = new TreeMap<Long, Object>();
        for (Value v : first) {
            valueMap.put(v.getTimestamp().getTime(), v);
        }
        List<Value> retObj = new ArrayList<Value>();

        ValueComparator bvc = new ValueComparator(valueMap);
        TreeMap<Long, Object> sorted_map = new TreeMap(bvc);
        sorted_map.putAll(valueMap);
        int c = 0;
        for (Long ts : sorted_map.keySet()) {
            c++;
            retObj.add((Value) sorted_map.get(ts));
            if (c >= max) {
                break;
            }
        }
        return retObj;
    }

    private List<Value> mergeAndSort(final List<Value> first, final List<Value> second, Date endDate) {
        first.addAll(second);
        Map<Long, Object> valueMap = new TreeMap<Long, Object>();
        for (Value v : first) {
            valueMap.put(v.getTimestamp().getTime(), v);
        }
        List<Value> retObj = new ArrayList<Value>();

        ValueComparator bvc = new ValueComparator(valueMap);
        TreeMap<Long, Object> sorted_map = new TreeMap(bvc);
        sorted_map.putAll(valueMap);

        for (Long ts : sorted_map.keySet()) {
            if (ts <= endDate.getTime()) {
                retObj.add((Value) sorted_map.get(ts));
            }

        }
        return retObj;
    }

    private List<Value> mergeAndSort(final List<Value> first, final List<Value> second, Timespan timespan) {
        first.addAll(second);
        Map<Long, Object> valueMap = new TreeMap<Long, Object>();
        for (Value v : first) {
            valueMap.put(v.getTimestamp().getTime(), v);
        }
        List<Value> retObj = new ArrayList<Value>();

        ValueComparator bvc = new ValueComparator(valueMap);
        TreeMap<Long, Object> sorted_map = new TreeMap(bvc);
        sorted_map.putAll(valueMap);


        for (final Long ts : sorted_map.keySet()) {

            Date start = timespan.getStart();
            Date end = timespan.getEnd();
            Date vts = new Date(ts);
            if ((ts >= timespan.getStart().getTime() - 1000) && (ts <= timespan.getEnd().getTime() + 1000)) {
                retObj.add((Value) sorted_map.get(ts));
            }


        }
        return retObj;
    }


    class ValueComparator implements Comparator {

        Map base;

        public ValueComparator(Map base) {
            this.base = base;
        }

        public int compare(Object a, Object b) {

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
