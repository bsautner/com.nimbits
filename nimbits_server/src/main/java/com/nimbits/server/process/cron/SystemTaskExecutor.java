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

package com.nimbits.server.process.cron;


import com.google.common.base.Optional;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.schedule.Schedule;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.PMF;
import com.nimbits.server.orm.PointEntity;
import com.nimbits.server.process.task.ValueTask;
import com.nimbits.server.transaction.entity.EntityService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.subscription.SubscriptionService;
import com.nimbits.server.transaction.user.dao.UserDao;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.ValueDao;
import com.nimbits.server.transaction.value.service.ValueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Component
public class SystemTaskExecutor {
    private final Logger logger = LoggerFactory.getLogger(SystemTaskExecutor.class.getName());

    private EntityDao entityDao;

    private UserDao userDao;

    private UserService userService;

    private ValueDao valueDao;

    private ValueService valueService;

    private ValueTask valueTask;

    private EntityService entityService;

    private PersistenceManagerFactory persistenceManagerFactory;

    private SubscriptionService subscriptionService;

    @org.springframework.beans.factory.annotation.Value("${system.task.schedule.enabled}")
    private Boolean scheduleEnabled;

    @org.springframework.beans.factory.annotation.Value("${system.task.idle.enabled}")
    private Boolean idleEnabled;

    @org.springframework.beans.factory.annotation.Value("${system.task.idle.limit}")
    private String idleLimit;

    @Autowired
    public SystemTaskExecutor(PMF pmf, UserDao userDao,
                              SubscriptionService subscriptionService,
                              EntityDao entityDao, UserService userService, ValueService valueService,
                              ValueTask valueTask, EntityService entityService, ValueDao valueDao) {

        this.persistenceManagerFactory = pmf.get();
        this.subscriptionService = subscriptionService;

        this.entityDao = entityDao;
        this.userService = userService;
        this.valueService = valueService;
        this.valueTask = valueTask;
        this.entityService = entityService;
        this.userDao = userDao;
        this.valueDao = valueDao;
    }




    @Scheduled(
            initialDelayString = "${system.task.idle.initialDelay}",
            fixedDelayString = "${system.task.idle.fixedRate}"
    )
    private void processIdlePoints() throws IOException {

        if (idleEnabled) {

            logger.info("Processing Idle Points");
            String batchID = UUID.randomUUID().toString();
            markIdleBatch(batchID);


            Transaction tx = null;

            final PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
            try {

                final Query processQuery = pm.newQuery(PointEntity.class);
                processQuery.setFilter("batchId == b");
                processQuery.declareParameters("String b");


                tx = pm.currentTransaction();

                tx.begin();


                final List<Point> result = (List<Point>) processQuery.execute(batchID);
                logger.info("Idle Points Being Processed: " + result.size());
                for (Point entity : result) {

                    Optional<User> userOptional = userDao.getUserById(entity.getOwner());
                    if (userOptional.isPresent()) {
                        entityDao.setIdleAlarmSentFlag(entity.getId(), true, true);

                        Value value = valueDao.getSnapshot(entity);

                        subscriptionService.process(userOptional.get(), entity, new Value.Builder().initValue(value).alertType(AlertType.IdleAlert).create());

                    }


                }


            } catch (Throwable throwable) {
                if (tx != null) {
                    tx.rollback();
                    logger.error("idle point processing failed", throwable);
                }
            }
            finally  {

                if (tx != null) {
                    tx.commit();
                }

                pm.close();
            }
        }


    }

    @Scheduled(
            initialDelayString = "${system.task.schedule.initialDelay}",
            fixedDelayString = "${system.task.schedule.fixedRate}"
    )
    private void processSchedules() throws IOException {

        if (scheduleEnabled) {
            logger.info("Processing Schedules");

            List<Schedule> schedules = entityDao.getSchedules();

            for (Schedule schedule : schedules) {

                if (schedule.getProcessedTimestamp() + schedule.getInterval() < new Date().getTime()) {

                    Optional<User> ownerOptional = userService.getUserByKey(schedule.getOwner());

                    if (ownerOptional.isPresent()) {
                        User owner = ownerOptional.get();

                        schedule.setProcessedTimestamp(new Date().getTime());

                        entityDao.addUpdateEntity(owner, schedule);
                        Optional<Entity> sourcePoint = entityDao.getEntity(owner, schedule.getSource(), EntityType.point);
                        Optional<Entity> targetPoint = entityDao.getEntity(owner, schedule.getTarget(), EntityType.point);

                        if (sourcePoint.isPresent() && targetPoint.isPresent()) {
                            Value value = valueService.getCurrentValue(sourcePoint.get());
                            Value newValue = new Value.Builder().initValue(value).timestamp(System.currentTimeMillis()).create();// ValueFactory.createValue(value, new Date());


                            valueTask.process(owner, (Point) targetPoint.get(), newValue);
                        } else {
                            schedule.setEnabled(false);
                            entityService.addUpdateEntity(owner, schedule);
                        }
                    }


                }


            }
        }

    }

    private void markIdleBatch(String batchID) {
        final PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        String query = "UPDATE POINTENTITY P" +
                "\nSET BATCHID = \"" + batchID + "\"" +
                "\nwhere not exists(" +
                "\nselect 1 from VALUESTORE V" +
                "\n    WHERE   V.ENTITYID = P.ID" +
                "\n    AND ((((UNIX_TIMESTAMP() * 1000) -  V.TIMESTAMP) / 1000) < P.IDLESECONDS)" +
                "\n    AND P.BATCHID is NULL " +
                "\n    ORDER BY P.PROCESSEDTIMESTAMP ASC" +
                "\n    LIMIT " + idleLimit +
                "\n)" +
                "\nAND IDLEALARMON = true AND IDLEALARMSENT = false";

        Query q = pm.newQuery("javax.jdo.query.SQL",query);

        logger.info(query);

        Transaction tx = pm.currentTransaction();

        try {

            tx.begin();
            q.execute();

        }
        catch (Exception ex) {
            logger.error("Error in Idle Processing", ex);
            tx.rollback();
            throw ex;
        }
        finally {
            tx.commit();
            pm.close();
        }
    }




}
