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

package com.nimbits.server.transaction.entity;


import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.communication.mail.EmailService;
import com.nimbits.server.geo.GeoSpatialDao;
import com.nimbits.server.process.BlobStore;
import com.nimbits.server.socket.ConnectedClients;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.value.service.ValueService;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


public class EntityServiceImpl implements EntityService {

    public static final String POINT_INITIALISED = "POINT_INITIALISED";


    private final EmailService emailService;


    private final ConnectedClients connectedClients;

    private final EntityDao entityDao;


    private final BlobStore blobStore;

    private final GeoSpatialDao geoSpatialDao;


    final static Logger logger = Logger.getLogger(EntityServiceImpl.class.getName());

    public EntityServiceImpl(GeoSpatialDao geoSpatialDao, EmailService emailService, ConnectedClients connectedClients, EntityDao entityDao, BlobStore blobStore) {

        this.emailService = emailService;
        this.connectedClients = connectedClients;
        this.entityDao = entityDao;
        this.blobStore = blobStore;
        this.geoSpatialDao = geoSpatialDao;
    }

    @Override
    public void deleteEntity(final User user,  Entity entity) {

        List<Entity> children = entityDao.getChildren(user, Collections.singletonList(entity));


        if (entity.getEntityType().equals(EntityType.socket)) {
            connectedClients.remove(user, (com.nimbits.client.model.socket.Socket) entity);
        }

        if (entity.getEntityType().equals(EntityType.point)) {
            Point point = (Point) entity;

                blobStore.deleteAllData(point);
            if (point.getPointType().equals(PointType.location)) {
                geoSpatialDao.deleteSpatial(point.getId());
            }

            // taskService.startDeleteDataTask((Point) entity);

        }
        for (Entity c : children) {
            deleteEntity(user, c);
        }
        entityDao.deleteEntity(user, entity, entity.getEntityType());



    }


    @Override
    public List<Entity> getEntities(final User user) {
        final List<Entity> retVal = entityDao.getEntities(user);

        Collections.sort(retVal);
        return retVal;
    }

    @Override
    public List<Entity> getEntitiesByType(final User user, EntityType type) {
        final List<Entity> retVal = entityDao.getEntitiesByType(user, type);

        Collections.sort(retVal);
        return retVal;
    }








    @Override
    public Entity  addUpdateEntity(final ValueService valueService, final User user, Entity entity) {


        switch (entity.getEntityType()) {
            case connection:
                Connection c = (Connection) entity;
                if (!c.isApproved()) {
                      emailService.sendConnectionRequest(user, c);
                }
                break;
            case point:
                Value init = new Value.Builder().doubleValue(0.0).timestamp(new Date(0)).meta(POINT_INITIALISED).create();

                 valueService.recordValues(blobStore, user, (Point) entity, Collections.singletonList(init));

              //  if (entity.getEntityType().equals(EntityType.))

                break;





        }

        return entityDao.addUpdateEntity(user, entity);


    }

}


