/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.transaction.summary;

import com.google.common.base.Optional;
import com.google.common.collect.Range;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.SummaryType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.summary.Summary;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.process.task.ValueGeneratedListener;
import com.nimbits.server.process.task.ValueTask;
import com.nimbits.server.transaction.entity.EntityService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.subscription.SubscriptionService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class SummaryService {


    private final EntityDao entityDao;
    private final ValueService valueService;
    private final EntityService entityService;
    private final SubscriptionService subscriptionService;


    @Autowired
    public SummaryService(final EntityDao entityDao,
                          final ValueService valueService,
                          final EntityService entityService,
                          final SubscriptionService subscriptionService) {

        this.entityDao = entityDao;
        this.valueService = valueService;
        this.entityService = entityService;
        this.subscriptionService = subscriptionService;

    }

    public void process(final User user, final Point point, ValueGeneratedListener valueGeneratedListener)  {
        final Optional<Entity> optional = entityDao.getEntityByTrigger(user, point, EntityType.summary);
        if (optional.isPresent()) {

            final Long now = System.currentTimeMillis();
            final Summary summary = (Summary) optional.get();

            if (summary.getLastProcessed().getTime() + summary.getSummaryIntervalMs() < new Date().getTime()) {

                final Entity source = entityDao.getEntity(user, summary.getTrigger(), EntityType.point).get();

                final Optional<Range<Long>> timespan = Optional.of(Range.closed((now - summary.getSummaryIntervalMs()), now));

                final Value value;
                if (summary.getSummaryType().equals(SummaryType.delta)) {
                    Point pointSource = ((Point) source);
                    pointSource.setDeltaSeconds(summary.getSummaryIntervalSeconds());
                    double delta = subscriptionService.calculateDelta(pointSource);
                    value = new Value.Builder().doubleValue(delta).timestamp(new Date()).create();

                } else {
                    final List<Value> values = valueService.getSeries(source, timespan, Optional.<Range<Integer>>absent(), Optional.<String>absent());
                    if (!values.isEmpty()) {
                        final double[] doubles = new double[values.size()];
                        for (int i = 0; i < values.size(); i++) {
                            doubles[i] = values.get(i).getDoubleValue();
                        }

                        final double result = getValue(summary.getSummaryType(), doubles);

                        value = new Value.Builder().doubleValue(result).create();
                    } else {
                        return; //nothing to do
                    }
                }


                final Point target = (Point) entityDao.getEntity(user, summary.getTarget(), EntityType.point).get();

                valueGeneratedListener.newValue(user, target, value);
                summary.setLastProcessed(new Date());
                entityService.addUpdateEntity(user, summary);


            }

        }

    }

    public double getValue(final SummaryType type, final double[] doubles) {
        final DescriptiveStatistics d = new DescriptiveStatistics(doubles);

        switch (type) {

            case average:
                return d.getMean();
            case standardDeviation:
                return d.getStandardDeviation();
            case max:
                return d.getMax();
            case min:
                return d.getMin();
            case skewness:
                return d.getSkewness();
            case sum:
                return d.getSum();
            case variance:
                return d.getVariance();


            default:
                return 0;
        }
    }

}
