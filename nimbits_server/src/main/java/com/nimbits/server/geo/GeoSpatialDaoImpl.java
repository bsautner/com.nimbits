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

import com.google.common.base.Optional;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.dao.EntityDaoImpl;

import java.util.List;

//TODO delete when point is deleted

public class GeoSpatialDaoImpl implements GeoSpatialDao {

    public GeoSpatialDaoImpl() {
    }

    public GeoSpatialDaoImpl(EntityDao entityDao) {
    }


    @Override
    public List<Point> getNearby(User user, double x, double y, double meters) {
        return null;
    }

    @Override
    public void updateSpatial(String uuid, double x, double y) {

    }

    @Override
    public Optional<String> getFile(String id) {
        return null;
    }

    @Override
    public void addFile(String messageId, String encodedBitmap) {

    }

    @Override
    public void deleteSpatial(String uuid) {

    }
}
