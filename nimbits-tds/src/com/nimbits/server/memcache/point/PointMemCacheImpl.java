/*
 * Copyright (c) 2010 Tonic Solutions LLC.
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

package com.nimbits.server.memcache.point;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.datapoints.PointTransactions;
import com.nimbits.server.memcache.MemCacheHelper;
import com.nimbits.server.point.PointTransactionsFactory;
import com.nimbits.server.user.UserTransactionFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 9/29/11
 * Time: 4:00 PM
 */
public class PointMemCacheImpl implements PointTransactions {

    private final MemcacheService cache;

    @Override
    public Point addPoint(Point point) {
        final Point retObj = PointTransactionsFactory.getDaoInstance(u).addPoint(point);
        purgeMemCache(retObj);
        updateMap(retObj);
        return retObj;
    }

    private final User u;
    private final String pointListKey;

    public PointMemCacheImpl(final User user) {
        this.u = user;
        if (user != null) {
            //must be the same namespace categorymemcache uses
            cache = MemcacheServiceFactory.getMemcacheService(Const.CONST_SERVER_VERSION + u.getUuid());
            pointListKey = MemCacheHelper.pointListKey(u);
        } else {
            cache = MemcacheServiceFactory.getMemcacheService(MemCacheHelper.DEFAULT_CACHE_NAMESPACE);
            pointListKey = MemCacheHelper.defaultPointCache();
        }

    }

    public void purgeMemCache(final Point p)  {
        if (cache.contains(Const.CONST_SERVER_VERSION + Const.CACHE_KEY_POINT_PREFIX + p.getId())) {
            cache.delete(Const.CONST_SERVER_VERSION +  Const.CACHE_KEY_POINT_PREFIX + p.getId());
        }
        if (cache.contains(Const.CONST_SERVER_VERSION + Const.CACHE_KEY_POINT_PREFIX + p.getUUID())) {
            cache.delete(Const.CONST_SERVER_VERSION + Const.CACHE_KEY_POINT_PREFIX + p.getUUID());
        }
        if (cache.contains(Const.CONST_SERVER_VERSION + Const.CACHE_KEY_POINT_PREFIX + p.getName().getValue())) {
            cache.delete(Const.CONST_SERVER_VERSION + Const.CACHE_KEY_POINT_PREFIX + p.getName().getValue());
        }
        if (cache.contains(pointListKey)) {
            cache.delete(pointListKey);
        }
        if (cache.contains(MemCacheHelper.defaultPointCache())) {
            cache.delete(MemCacheHelper.defaultPointCache());
        }
        //CategoryTransactionFactory.getInstance(u).purgeMemCache();


    }

    private void updateMap(final Point p)  {
        if (p != null) {
            purgeMemCache(p);
            cache.put(Const.CONST_SERVER_VERSION + Const.CACHE_KEY_POINT_PREFIX + p.getUUID(), p);
            cache.put(Const.CONST_SERVER_VERSION + Const.CACHE_KEY_POINT_PREFIX + p.getId(), p);
            cache.put(Const.CONST_SERVER_VERSION + Const.CACHE_KEY_POINT_PREFIX + p.getName().getValue(), p);
        }
    }

    private Point getPointFromMap(final EntityName name) {
        try {
            if (cache.contains(Const.CONST_SERVER_VERSION + Const.CACHE_KEY_POINT_PREFIX + name.getValue())) {
                return (Point) cache.get(Const.CONST_SERVER_VERSION + Const.CACHE_KEY_POINT_PREFIX + name.getValue());
            }
            else {
                return null;
            }
        } catch (com.google.appengine.api.memcache.InvalidValueException e) {

            return null;
        }
    }

    private Point getPointFromMap(final long id) {
        try {
            if (cache.contains(Const.CONST_SERVER_VERSION + Const.CACHE_KEY_POINT_PREFIX + id)) {
                return (Point) cache.get(Const.CONST_SERVER_VERSION + Const.CACHE_KEY_POINT_PREFIX + id);
            }
            else {
                return null;
            }
        } catch (com.google.appengine.api.memcache.InvalidValueException e) {

            return null;
        }
    }

    private  List<Point> getPointListFromCache() {
        List<Point> retObj;
        try {
            if (cache.contains(pointListKey)) {
                retObj = (List<Point>) cache.get(pointListKey);
            }
            else {
                retObj= null;
            }
        } catch (com.google.appengine.api.memcache.InvalidValueException e) {
            cache.delete(pointListKey);
            retObj= null;
        }
        return retObj;
    }

    @Override
    public List<Point> getPoints() throws NimbitsException {
        List<Point> retObj = getPointListFromCache();
        if (retObj != null) {
            return retObj;
        } else {
            retObj = PointTransactionsFactory.getDaoInstance(u).getPoints();
            cache.put(pointListKey, retObj);
            return retObj;
        }

    }

    @Override
    public Point getPointByID(final long id) throws NimbitsException {
        Point retObj = getPointFromMap(id);
        if (retObj != null) {
            return retObj;
        } else {
            Point p = PointTransactionsFactory.getDaoInstance(u).getPointByID(id);

            updateMap(p);
            return p;
        }


    }

    @Override
    public Point updatePoint(final Point point) throws NimbitsException {
        // final User u = UserTransactionFactory.getInstance().getNimbitsUserByID(point.getUserFK());
        purgeMemCache(point);
        Point retObj = PointTransactionsFactory.getDaoInstance(u).updatePoint(point);
        updateMap(retObj);
        return retObj;

    }

    @Override
    public Point getPointByName(final EntityName name) throws NimbitsException {
        Point point = getPointFromMap(name);

        if (point != null) {
            return  point;

        } else {
            Point p = PointTransactionsFactory.getDaoInstance(u).getPointByName(name);
            if (p != null) {
                updateMap(p);
            }
            return p;
        }


    }

    @Override
    public void deletePoint(final Point point) throws NimbitsException {
        final User u = UserTransactionFactory.getInstance().getNimbitsUserByID(point.getUserFK());

        purgeMemCache(point);
        PointTransactionsFactory.getDaoInstance(u).deletePoint(point);

    }

//    @Override
//    public Point movePoint(final Point point, final EntityName newEntityName) throws NimbitsException {
//        Point movedPoint = PointTransactionsFactory.getDaoInstance(u).movePoint(point, newEntityName);
//        purgeMemCache(movedPoint);
//        updateMap(movedPoint);
//        return movedPoint;
//
//    }

//    @Override
//    public Point showEntityData(final Point point, final Category c) throws NimbitsException {
//        final Point retObj = PointTransactionsFactory.getDaoInstance(u).showEntityData(point, c);
//        purgeMemCache(retObj);
//        updateMap(retObj);
//        return retObj;
//    }

//    @Override
//    public Point showEntityData(final EntityName pointName, final Category c) throws NimbitsException {
//
//        final Point retObj = PointTransactionsFactory.getDaoInstance(u).showEntityData(pointName, c);
//        purgeMemCache(retObj);
//        updateMap(retObj);
//        return retObj;
//    }


//    @Override
//    public List<Point> getPointsByCategory(final Category c) {
//        return PointTransactionsFactory.getDaoInstance(u).getPointsByCategory(c);
//    }

    @Override
    public Point publishPoint(final Point p) throws NimbitsException {
        purgeMemCache(p);
        Point retObj = PointTransactionsFactory.getDaoInstance(u).publishPoint(p);
        updateMap(p);
        return retObj;
    }

    //these should not use the cache, since we don't know the user
    @Override
    public Point getPointByUUID(final String uuid)  {
        return PointTransactionsFactory.getDaoInstance(u).getPointByUUID(uuid);
    }

    @Override
    public List<Point> getAllPoints() {
        List<Point> retObj = getPointListFromCache();
        if (retObj == null) {
            retObj = PointTransactionsFactory.getDaoInstance(u).getAllPoints();

            if (retObj != null && retObj.size() > 0) {
                cache.put(pointListKey, retObj);
            }
        }
        return retObj;

    }

    @Override
    public Point addPoint(Entity entity) {
        final Point retObj = PointTransactionsFactory.getDaoInstance(u).addPoint(entity);
        purgeMemCache(retObj);
        updateMap(retObj);
        return retObj;
    }

    @Override
    public List<Point> getPoints(List<Entity> entities) {
        return PointTransactionsFactory.getDaoInstance(u).getPoints(entities);
    }


    @Override
    public List<Point> getAllPoints(final int start,final  int end) {

        return PointTransactionsFactory.getDaoInstance(u).getAllPoints(start, end);

    }

    @Override
    public List<Point> getIdlePoints() {

        return PointTransactionsFactory.getDaoInstance(u).getIdlePoints();
    }

    @Override
    public Point checkPoint(final HttpServletRequest req, final EmailAddress email, final Point point) throws NimbitsException {
        return PointTransactionsFactory.getDaoInstance(u).checkPoint(req, email, point);
    }



}
