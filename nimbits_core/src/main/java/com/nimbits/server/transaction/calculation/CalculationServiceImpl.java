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

package com.nimbits.server.transaction.calculation;

import com.google.common.base.Optional;
import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.ValueException;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.data.DataProcessor;
import com.nimbits.server.geo.GeoSpatialDao;
import com.nimbits.server.math.MathEvaluator;
import com.nimbits.server.math.MathEvaluatorImpl;
import com.nimbits.server.process.BlobStore;
import com.nimbits.server.process.task.TaskService;
import com.nimbits.server.process.task.ValueTask;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.subscription.SubscriptionService;
import com.nimbits.server.transaction.summary.SummaryService;
import com.nimbits.server.transaction.sync.SyncService;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CalculationServiceImpl implements CalculationService {

    private static final Logger logger = LoggerFactory.getLogger(CalculationService.class.getName());


    //TODO DI
    private MathEvaluator m;


    public CalculationServiceImpl(   ) {


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
                        final User u, final Point point, final Value value) throws ValueException {

        final Optional<Entity> optional = entityDao.getEntityByTrigger(u, point, EntityType.calculation);
        if (optional.isPresent()) {


            Calculation c = (Calculation) optional.get();


            final Optional<Entity> target = entityDao.getEntity(u, c.getTarget(), EntityType.point);

            if (target.isPresent()) {
                final Optional<Value> result = solveEquation(entityDao, blobStore, valueService, u, c, point, value);
                if (result.isPresent()) {

                    Value v = new Value.Builder().initValue(result.get()).timestamp(value.getTimestamp()).create();


                        valueTask.process(geoSpatialDao, taskService, userService, entityDao, valueTask, entityService, blobStore, valueService, summaryService,
                                syncService, subscriptionService, calculationService, dataProcessor, u, (Point) target.get(), v);

                }
            }



        }
    }

    @Override
    public Optional<Value> solveEquation(EntityDao entityDao, BlobStore blobStore, ValueService valueService, final User user, final Calculation calculation, Entity point, Value value) {


        m = new MathEvaluatorImpl(calculation.getFormula());
        if (calculation.getFormula().contains("x") && !StringUtils.isEmpty(calculation.getX())) {
            addVar(entityDao,blobStore,  valueService, user, calculation, point, value, "x", calculation.getX());
        }
        if (calculation.getFormula().contains("y") && !StringUtils.isEmpty(calculation.getY())) {
            addVar(entityDao, blobStore, valueService, user, calculation, point, value, "y", calculation.getY());
        }
        if (calculation.getFormula().contains("z") && !StringUtils.isEmpty(calculation.getZ())) {
            addVar(entityDao, blobStore, valueService, user, calculation, point, value, "z", calculation.getZ());
        }


        final Double retVal = m.getValue();


        if (retVal == null) {
            return Optional.absent();

        } else {
            Value v = new Value.Builder().doubleValue(retVal).create();
            return Optional.of(v);
        }

    }

    private void addVar(EntityDao entityDao, BlobStore blobStore, ValueService valueService, User user, Calculation calculation, Entity point, Value value, String var, String varEntityId) {
        if (!Utils.isEmptyString(calculation.getX()) && calculation.getFormula().contains(var)) {

            double currentValue = 0;
            if (varEntityId.equals(point.getId())) {
                currentValue = value.getDoubleValue();
            } else {
                Optional<Entity> sample = entityDao.getEntity(user, varEntityId, EntityType.point);

                if (sample.isPresent()) {
                    final Value val = valueService.getCurrentValue(blobStore, sample.get());
                    currentValue = val.getDoubleValue();
                }

            }
            m.addVariable(var, currentValue);

        }
    }
}
