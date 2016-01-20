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
}
