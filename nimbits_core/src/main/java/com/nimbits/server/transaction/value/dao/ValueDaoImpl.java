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

package com.nimbits.server.transaction.value.dao;


import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.server.NimbitsEngine;
import com.nimbits.server.io.blob.BlobStore;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/22/12
 * Time: 11:05 AM
 */

public class ValueDaoImpl implements ValueDao {
    private static final int INT = 1024;
    private static final int TOP = 1;
   private final BlobStore blobStore;

    public ValueDaoImpl(NimbitsEngine engine) {
       blobStore = engine.getBlobStore();
    }







    @Override
    public List<Value> getRecordedValuePrecedingTimestamp(final Entity entity, final Date timestamp)   {

        return blobStore.getTopDataSeries(entity, TOP, timestamp);


    }


    @Override
    public List<Value> getTopDataSeries(final Entity entity, final int maxValues)  {
        return blobStore.getTopDataSeries(entity, maxValues, new Date());

    }




    private static Date zeroOutDateToStart(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MILLISECOND, c.get(Calendar.MILLISECOND) * -1);
        c.add(Calendar.SECOND, c.get(Calendar.SECOND) * -1);
        c.add(Calendar.MINUTE, c.get(Calendar.MINUTE) * -1);
        c.add(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) * -1);
        return c.getTime();
    }



    @Override
    public List<ValueBlobStore> recordValues(final Entity entity, final List<Value> values)  {
        if (!values.isEmpty()) {

            final Map<Long, ValueDayHolder> individualDaysValueMap = new HashMap<Long, ValueDayHolder>(values.size());
            // final Range<Date> timerange = getTimeRange(values);
            // final Range<Date> baseTimerange = Range.closed(zeroOutDateToStart(timerange.lowerEndpoint()), zeroOutDateToStart(timerange.upperEndpoint()));

            //log.info("ValueDAO: recording " + values.size() + " to " + entity.getKey());
            for (final Value value : values) {
                if (valueHealthy(value)) {
                    //zero out the date of the current value we're working with
                    final Date zero =  zeroOutDateToStart(value.getTimestamp());
                    if (individualDaysValueMap.containsKey(zero.getTime())) {

                        individualDaysValueMap.get(zero.getTime()).addValue(value);

                    } else {
                        //create a new list for a new day
                        ValueDayHolder holder = new ValueDayHolder(zero, value);
                        individualDaysValueMap.put(zero.getTime(), holder);

                    }
                }
            }

            final List<ValueBlobStore> retObj = new ArrayList<ValueBlobStore>(individualDaysValueMap.size());
            for (final ValueDayHolder longListEntry : individualDaysValueMap.values()) {

                List<ValueBlobStore> b = blobStore.createBlobStoreEntity(entity, longListEntry);
                if (! b.isEmpty()) {
                    retObj.addAll(b);
                }

            }
            return retObj;
        }
        else {
            return Collections.emptyList();
        }

    }


    private boolean valueHealthy(final Value value) {

        return !Double.isInfinite(value.getDoubleValue())
                && !Double.isNaN(value.getDoubleValue());


    }







    @Override
    public void consolidateBlobs(final Point entity) {

        final List<ValueBlobStore> stores = blobStore.getAllStores(entity);
        if (! stores.isEmpty()) {
            final Collection<Long> dates = new ArrayList<Long>(stores.size());
            final Collection<Long> dupDates = new ArrayList<Long>(stores.size());
            for (final ValueBlobStore store : stores) {
                //consolidate blobs that have more than one date.

                if ( dates.contains(store.getTimestamp().getTime()) && ! dupDates.contains(store.getTimestamp().getTime())) {

                    dupDates.add(store.getTimestamp().getTime());
                }
                else {

                    dates.add(store.getTimestamp().getTime());
                }
            }

            for (Long l : dupDates) {

                 List<Value> values = blobStore.consolidateDate(entity, new Date(l));
                 recordValues(entity, values);
            }
        }
    }



}
