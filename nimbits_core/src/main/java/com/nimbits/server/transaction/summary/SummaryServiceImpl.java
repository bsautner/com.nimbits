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

package com.nimbits.server.transaction.summary;

import com.google.common.collect.Range;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.SummaryType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.summary.Summary;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.server.NimbitsEngine;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.transaction.entity.EntityServiceFactory;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.value.ValueServiceFactory;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class SummaryServiceImpl implements SummaryService {

    private final EntityService entityService;
    private final ValueService valueService;

    public SummaryServiceImpl(NimbitsEngine engine, TaskService taskService) {
        NimbitsEngine engine1 = engine;
        entityService = EntityServiceFactory.getInstance(engine);
        valueService = ValueServiceFactory.getInstance(engine, taskService);
    }

    @Override
    public void processSummaries(final User user, final Point point) {
        final List<Entity> list = entityService.getEntityByTrigger(user, point, EntityType.summary);


        for (final Entity entity : list) {
            final Date now = new Date();
            final Summary summary = (Summary) entity;
            final long d = new Date().getTime() - summary.getSummaryIntervalMs();

            if (summary.getLastProcessed().getTime() + summary.getSummaryIntervalMs() < new Date().getTime()) {



                    final List<Entity> results =  entityService.getEntityByKey(user, summary.getTrigger(), EntityType.point);
                    if (! results.isEmpty()) {
                        final Entity source = results.get(0);
                        final Range<Date> span = Range.closed(new Date(now.getTime() - summary.getSummaryIntervalMs()), now);


                        final Value value;
                        if (summary.getSummaryType().equals(SummaryType.delta)) {
                            Point pointSource = ((Point)source);
                            pointSource.setDeltaSeconds(summary.getSummaryIntervalSeconds());
                            double delta = valueService.calculateDelta(pointSource);
                            value = ValueFactory.createValueModel(delta);

                        }
                        else {
                            final List<Value> values = valueService.getDataSegment(source, span);
                            if (!values.isEmpty()) {
                                final double[] doubles = new double[values.size()];
                                for (int i = 0; i< values.size(); i++) {
                                    doubles[i] = values.get(i).getDoubleValue();
                                }

                                final double result = getValue(summary.getSummaryType(), doubles);

                                value = ValueFactory.createValueModel(result);
                            }
                            else {
                                return; //nothing to do
                            }
                        }


                        final List<Entity> targetResults =  entityService.getEntityByKey(user, summary.getTarget(), EntityType.point);
                        if (! targetResults.isEmpty()) {
                            final Entity target = targetResults.get(0);
                            valueService.recordValue(user, target, value);
                            summary.setLastProcessed(new Date());
                            entityService.addUpdateEntity(user, Arrays.<Entity>asList(summary));

                        }

                    }


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
