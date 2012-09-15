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

package com.nimbits.server.transactions.service.summary;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.SummaryType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.summary.Summary;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.timespan.TimespanModelFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.summary.SummaryService;
import com.nimbits.client.service.value.ValueService;
import com.nimbits.server.admin.logging.LogHelper;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 10:08 AM
 */
@Service("summaryService")
@Transactional
public class SummaryServiceImpl  extends RemoteServiceServlet implements SummaryService {


    private EntityService entityService;
    private ValueService valueService;

    @Override
    public void processSummaries(final User user,final  Point point) throws NimbitsException {
        final List<Entity> list = entityService.getEntityByTrigger(user, point, EntityType.summary);


        for (final Entity entity : list) {
            final Date now = new Date();
            Summary summary = (Summary) entity;
            final long d = new Date().getTime() - summary.getSummaryIntervalMs();
            if (summary.getLastProcessed().getTime() < d) {

                try {

                    final List<Entity> results =  entityService.getEntityByKey(summary.getTrigger(),EntityType.point);
                    if (! results.isEmpty()) {
                        final Entity source = results.get(0);
                        final Timespan span = TimespanModelFactory.createTimespan(new Date(now.getTime() - summary.getSummaryIntervalMs()), now);


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


                        final List<Entity> targetResults =  entityService.getEntityByKey(summary.getTarget(), EntityType.point);
                        if (! targetResults.isEmpty()) {
                            final Entity target = targetResults.get(0);
                            valueService.recordValue(user, target, value);
                            summary.setLastProcessed(new Date());
                            entityService.addUpdateEntity(user, summary);

                        }

                    }




                } catch (NimbitsException e) {
                    summary.setEnabled(false);
                    entityService.addUpdateEntity(user, summary);
                    LogHelper.logException(this.getClass(), e);


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


    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    public EntityService getEntityService() {
        return entityService;
    }

    public void setValueService(ValueService valueService) {
        this.valueService = valueService;
    }

    public ValueService getValueService() {
        return valueService;
    }
}
