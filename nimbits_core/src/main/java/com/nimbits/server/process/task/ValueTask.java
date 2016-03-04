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

package com.nimbits.server.process.task;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.AlertType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.ValueException;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.data.DataProcessor;
import com.nimbits.server.geo.GeoSpatialDao;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.orm.socket.SocketStore;
import com.nimbits.server.process.BlobStore;
import com.nimbits.server.transaction.BaseProcessor;
import com.nimbits.server.transaction.calculation.CalculationService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.subscription.SubscriptionService;
import com.nimbits.server.transaction.summary.SummaryService;
import com.nimbits.server.transaction.sync.SyncService;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


public class ValueTask extends HttpServlet implements BaseProcessor {
    private final Logger logger = Logger.getLogger(ValueTask.class.getName());

    @Autowired
    private EntityService entityService;

    @Autowired
    private UserService userService;

    @Autowired
    private CalculationService calculationService;

    @Autowired
    private SummaryService summaryService;

    @Autowired
    private SyncService syncService;

    @Autowired
    private ValueService valueService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private DataProcessor dataProcessor;

    @Autowired
    private BlobStore blobStore;

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private ValueTask valueTask;

    @Autowired
    private TaskService taskService;

    @Autowired
    private GeoSpatialDao geoSpatialDao;



    @Override
    public void init() throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);


    }


    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        logger.info("value task post");

        String u = req.getParameter(Parameters.user.getText());
        String j = req.getParameter(Parameters.json.getText());
        String id = req.getParameter(Parameters.id.getText());
        Gson gson =  GsonFactory.getInstance(true);


        User user  = userService.getUserByKey(u).get();
        Value value = gson.fromJson(j, Value.class);


        Point point = (Point) entityDao.getEntityByKey(user, id, EntityType.point).get();


        try {
            process(geoSpatialDao, taskService, userService, entityDao, valueTask, entityService, blobStore, valueService, summaryService, syncService, subscriptionService,
                    calculationService, dataProcessor, user, point, value);
        } catch (ValueException e) {
            logger.severe(ExceptionUtils.getStackTrace(e));
        }





    }

    @Override
    public void process(
            final GeoSpatialDao geoSpatialDao,
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
            final User user, final Point point, Value value) throws ValueException  {

        final boolean ignored = false;
        final boolean ignoredByDate = dataProcessor.ignoreDataByExpirationDate(point, value, ignored);
        final Value sample = valueService.getCurrentValue(blobStore, point);

        boolean ignoredByCompression = false;

        if (value.getTimestamp() != null && (value.getTimestamp().getTime() > sample.getTimestamp().getTime())) {
            ignoredByCompression = dataProcessor.ignoreByFilter(point, sample, value);
        }


        Value previousValue;

        if (!ignoredByDate && !ignoredByCompression) {

            switch (point.getPointType()) {
                case basic:

                    valueService.recordValues(blobStore, user, point, Collections.singletonList(value));
                    break;
                case location:
                    logger.info("Processing new location " + value.toString());
                    geoSpatialDao.updateSpatial(point.getUUID(), value.getLatitude(), value.getLongitude());
                    valueService.recordValues(blobStore, user, point, Collections.singletonList(value));

                    String[] mynames = value.getMetaData().split(",");
                    if (mynames.length == 2) {
                        String mypush = mynames[0];
                        Optional<Entity> optional = entityDao.getEntityByName(user, CommonFactory.createName(mypush, EntityType.point), EntityType.point);
                        logger.info("my push point: " + mypush);

                        if (optional.isPresent()) {
                            logger.info("found push point: " + mypush);
                            Point pushPoint = (Point) optional.get();
                            List<Point> points = geoSpatialDao.getNearby(user, value.getLatitude(), value.getLongitude(), 100000);
                            logger.info("nearby location points: " + points.size());
                            for (Point nearby : points) {
                                Value last = valueService.getCurrentValue(blobStore, nearby);
                                if (!StringUtils.isEmpty(last.getMetaData())) {
                                    //meta contains the push and broadcast points for yodel
                                    String[] names = last.getMetaData().split(",");
                                    if (names.length == 2) {
                                        String broadcast = names[1];
                                        logger.info("other user's broadcast point: " + broadcast);
                                        Optional<Entity> optional1 = entityDao.getEntityByName(user, CommonFactory.createName(broadcast, EntityType.point), EntityType.point);
                                        if (optional1.isPresent()) {
                                            logger.info("found other user's point: " + broadcast);
                                            Point otherUsersBroadcastPoint = (Point) optional1.get();
                                            Value lastBroadcast = valueService.getCurrentValue(blobStore, otherUsersBroadcastPoint);
                                            if (lastBroadcast.getTimestamp().getTime() > 1000) {
                                                logger.info("got last broadcast:" + lastBroadcast.toString());
                                                logger.info("recording last broadcast to : " + pushPoint.getName().getValue());
                                                taskService.process(geoSpatialDao, taskService, userService, entityDao, valueTask, entityService, blobStore,
                                                        valueService, summaryService, syncService, subscriptionService, calculationService, dataProcessor, user, pushPoint, lastBroadcast);
                                            } else {
                                                logger.warning("ignored value since it was the init value: " + lastBroadcast.toString());
                                            }

                                        }
                                        else {
                                            logger.warning("other user's broadcast point no longer exists.");
                                        }
                                    }
                                }
                            }
                        }



                    }

                    break;

                case backend:
                    valueService.recordValues(blobStore, user, point, Collections.singletonList(value));
                    break;
                case cumulative:
                    previousValue = valueService.getCurrentValue(blobStore, point);


                    if (previousValue.getTimestamp().getTime() < value.getTimestamp().getTime()) {
                        value= new Value.Builder().initValue(value).doubleValue(value.getDoubleValue() + previousValue.getDoubleValue()).create();
                        valueService.recordValues(blobStore, user, point, Collections.singletonList(value));
                    }
                    break;
                case timespan:
                    valueService.recordValues(blobStore, user, point, Collections.singletonList(value));
                    break;
                case flag:
                    Integer whole = BigDecimal.valueOf(value.getDoubleValue()).intValue();
                    double d = whole != 0 ? 1.0 : 0.0;
                    value =  new Value.Builder().initValue(value).doubleValue(d).create();
                    valueService.recordValues(blobStore, user, point, Collections.singletonList(value));
                    break;
                case high:
                    previousValue = valueService.getCurrentValue(blobStore, point);

                    if (value.getDoubleValue() > previousValue.getDoubleValue()) {
                        valueService.recordValues(blobStore, user, point, Collections.singletonList(value));
                    }

                    break;
                case low:
                    previousValue = valueService.getCurrentValue(blobStore, point);

                    if (value.getDoubleValue() < previousValue.getDoubleValue()) {
                        valueService.recordValues(blobStore, user, point, Collections.singletonList(value));
                    }

                    break;
                default:
                    return;

            }


            final AlertType t = valueService.getAlertType(point, value);
            final Value v =  new Value.Builder().initValue(value).timestamp(new Date()).alertType(t).create();
            completeRequest(geoSpatialDao, taskService, entityDao, entityService, blobStore, valueService, summaryService, syncService, subscriptionService,
                    calculationService, dataProcessor, userService, user, point, v);



        } else {
            logger.info("Value was ignored by date or compression setting");
        }



    }




    private void completeRequest(final GeoSpatialDao geoSpatialDao,
                                 TaskService taskService,
                                 EntityDao entityDao,
                                 EntityService entityService,
                                 BlobStore blobStore,
                                 ValueService valueService,
                                 SummaryService summaryService,
                                 SyncService syncService,
                                 SubscriptionService subscriptionService,
                                 CalculationService calculationService,
                                 DataProcessor dataProcessor,
                                 UserService userService,
                                 User u,
                                 Point point,
                                 Value value) throws ValueException {
        try {
            if (point.isIdleAlarmOn() && point.getIdleAlarmSent()) {
                point.setIdleAlarmSent(false);
                entityService.addUpdateEntity(valueService, u, point);
            }


            Value snapshot = blobStore.getSnapshot(point);
            if (snapshot.getTimestamp().getTime() < value.getTimestamp().getTime()) {
                blobStore.saveSnapshot(point, value);
            }
            logger.info("DP:: " + this.getClass().getName() + " " + (dataProcessor == null));
            calculationService.process(geoSpatialDao, taskService, userService, entityDao, this, entityService, blobStore, valueService, summaryService, syncService, subscriptionService, calculationService, dataProcessor, u, point, value);

            summaryService.process(geoSpatialDao, taskService, userService, entityDao, this, entityService, blobStore, valueService, summaryService, syncService, subscriptionService, calculationService, dataProcessor,u, point, value);

            syncService.process(geoSpatialDao, taskService, userService, entityDao, this, entityService, blobStore, valueService, summaryService, syncService, subscriptionService, calculationService, dataProcessor,u, point, value);

            subscriptionService.process(geoSpatialDao, taskService, userService, entityDao, this, entityService, blobStore, valueService, summaryService, syncService, subscriptionService, calculationService, dataProcessor,u, point, value);

            //TODO - turned this off  trying to fix logging
//   value         try {
//             //   connectedClients.sendLiveEvents(u, point, value, u.getToken());
//            } catch (IOException e) {
//                logger.severe(e.getMessage());
//            }getMessage

            List<SocketStore> socketStores = Collections.emptyList();    //userDao.getSocketSessions(u);

            //TODO outbound sockets?
            //logger.info("*****processing socket relay " + socketStores.size());
            for (SocketStore socketStore : socketStores) {


                try {
                    //my socket relay instance
                    URL url = new URL("http://" + Const.SOCKET_RELAY + "/service/v2/socket");
                    logger.info(url.toString());

                    // create and open the connection using POST
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");

                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    Gson gson =  GsonFactory.getInstance(true);
                    String json = gson.toJson(value);
                    String userJson = gson.toJson(u);
                    String pointJson = gson.toJson(point);
                    writer.write("user=" + userJson + "&point=" + pointJson + "&json=" + json + "&session=" + socketStore.getSession());
                    logger.info("user=" + userJson + "&point=" + pointJson + "&json=" + json + "&session=" + socketStore.getSession());
                    writer.close();

                    // this is where you can check for success/fail.
                    // Even if this doesn't properly try again, you could make another
                    // request in the ELSE for extra error handling! :)
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        logger.info("post ok");
                    } else {
                        logger.severe("post to socket relay error " + connection.getResponseCode());
                    }
                } catch (Exception e) {
                    logger.severe(e.getMessage());
                    e.printStackTrace();
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.severe(ex.getMessage());
        }


    }
}
