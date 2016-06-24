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

package com.nimbits.server.geo;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.search.*;
import com.google.common.base.Optional;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.server.transaction.entity.dao.EntityDao;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoSpatialDaoImpl implements GeoSpatialDao {
//    public static final String UUID = "uuid";
    public static final String LOCATION = "location";

    private final String KIND = "SpatialEntity";
    private final String PHOTO = "PhotoEntity";

    private final EntityDao entityDao;

    private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    private static final Logger logger = LoggerFactory.getLogger(GeoSpatialDao.class.getName());

    public GeoSpatialDaoImpl(EntityDao entityDao) {
        this.entityDao = entityDao;
    }

    @Override
    public List<Point> getNearby(User user, double x, double y, double meters) {

        IndexSpec indexSpec = IndexSpec.newBuilder().setName(KIND).build();
        Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);

        String search = "distance(" + LOCATION + ", geopoint(" + x + ", " + y + ")) < " + meters;
        Results<ScoredDocument> results = index.search(search);

        List<Point> points = new ArrayList<>((int)results.getNumberFound());

        for (ScoredDocument scoredDocument : results) {
            String id = scoredDocument.getId();
            Optional<Entity> p = entityDao.getEntity(user, id, EntityType.point);
            if (p.isPresent()) {
                points.add((Point) p.get());
            }
            else {
                logger.warn("deleting a file since point wasn't found: " + id);
                index.delete(id);
            }
        }
        return points;




    }

    @Override
    public void updateSpatial(String uuid, double x, double y) {

        IndexSpec indexSpec = IndexSpec.newBuilder().setName(KIND).build();
        Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);

        index.delete(uuid);

        Document doc = Document.newBuilder()
                .setId(uuid) // Setting the document identifer is optional. If omitted, the search service will create an identifier.
              //  .addField(Field.newBuilder().setName("content").setText("the rain in spain"))
                .addField(Field.newBuilder().setName(LOCATION).setGeoPoint(new GeoPoint(x, y)))

               // .addField(Field.newBuilder().setName("published").setDate(new Date()))
                .build();




        try {
            index.putAsync(doc);
        } catch (PutException e) {
            if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())) {
                // retry putting the document
            }
        }


    }

    @Override
    public void deleteSpatial(String uuid) {

        IndexSpec indexSpec = IndexSpec.newBuilder().setName(KIND).build();
        Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);

        index.delete(uuid);



    }


    @Override
    public Optional<String> getFile(String id) {

        IndexSpec indexSpec = IndexSpec.newBuilder().setName(PHOTO).build();
        Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);


            Document document = index.get(id);
            if (document != null) {
               Iterator<Field> fieldIterator = document.getFields(PHOTO).iterator();
                if (fieldIterator.hasNext()) {
                    Field field = fieldIterator.next();
                    String e = field.getText();
                    return Optional.of(e);
                }

            }
        return Optional.absent();


    }

    @Override
    public void addFile(String messageId, String encodedBitmap) {

        IndexSpec indexSpec = IndexSpec.newBuilder().setName(PHOTO).build();
        Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);

        Document doc = Document.newBuilder()
                .setId(messageId)
                .addField(Field.newBuilder().setName(PHOTO).setText(encodedBitmap))


                .build();




        try {
            index.putAsync(doc);
        } catch (PutException e) {
            if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())) {
                // retry putting the document
            }
        }

    }



}
