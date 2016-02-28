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
import com.nimbits.client.exception.ValueException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.summary.Summary;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.data.DataProcessor;
import com.nimbits.server.geo.GeoSpatialDao;
import com.nimbits.server.process.BlobStore;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.process.task.ValueTask;
import com.nimbits.server.transaction.calculation.CalculationService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.subscription.SubscriptionService;
import com.nimbits.server.transaction.sync.SyncService;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service
public class SummaryServiceImpl implements SummaryService {

    private static final Logger logger = Logger.getLogger(SummaryServiceImpl.class.getName());




    public SummaryServiceImpl(TaskService taskService)
    {



    }



    @Override
    public void process(final GeoSpatialDao geoSpatialDao,
                        final TaskService taskService,
                        final UserService userService,
                        final EntityDao entityDao,
                        final ValueTask valueTask,
                        final EntityService entityService,
                        final BlobStore blobStore,
                        final ValueService valueService,
                        final SummaryService summaryService,
                        final SyncService syncService,
                        final SubscriptionService subscriptionService,
                        final CalculationService calculationService,
                        final DataProcessor dataProcessor,
                        final User user, final Point point, final Value v) throws ValueException {
        final Optional<Entity> optional = entityDao.getEntityByTrigger(user, point, EntityType.summary);
        if (optional.isPresent()) {

            final Date now = new Date();
            final Summary summary = (Summary) optional.get();

            if (summary.getLastProcessed().getTime() + summary.getSummaryIntervalMs() < new Date().getTime()) {

                final  Entity source = entityDao.getEntityByKey(user, summary.getTrigger(), EntityType.point).get();

                final Optional<Range<Date>> timespan = Optional.of(Range.closed(new Date(now.getTime() - summary.getSummaryIntervalMs()), now));

                final Value value;
                if (summary.getSummaryType().equals(SummaryType.delta)) {
                    Point pointSource = ((Point) source);
                    pointSource.setDeltaSeconds(summary.getSummaryIntervalSeconds());
                    double delta = valueService.calculateDelta(blobStore, pointSource);
                    value = new Value.Builder().doubleValue(delta).timestamp(new Date()).create();

                } else {
                    final List<Value> values = valueService.getSeries(blobStore, source, timespan, Optional.<Range<Integer>>absent(), Optional.<String>absent());
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


                final Point target = (Point) entityDao.getEntityByKey(user, summary.getTarget(), EntityType.point).get();
                logger.info("DP:: " + this.getClass().getName() + " " + (dataProcessor == null));

                taskService.process(geoSpatialDao, taskService, userService, entityDao, valueTask,
                        entityService,
                        blobStore,
                        valueService,
                        summaryService,
                        syncService,
                        subscriptionService,
                        calculationService, dataProcessor, user, target, value);
                summary.setLastProcessed(new Date());
                entityService.addUpdateEntity(valueService, user, summary);




            }

        }

    }

    @Override
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
