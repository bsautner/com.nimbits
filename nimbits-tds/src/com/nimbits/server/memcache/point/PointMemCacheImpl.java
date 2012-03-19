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

import com.google.appengine.api.memcache.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.memcache.*;
import com.nimbits.server.point.*;
import com.nimbits.server.user.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 9/29/11
 * Time: 4:00 PM
 */
public class PointMemCacheImpl implements PointTransactions {

    private final User u;

    private final MemcacheService cache;

    @Override
    public Point addPoint(Entity entity, Point point) {
        final Point retObj = PointTransactionsFactory.getDaoInstance(u).addPoint(entity, point);
        purgeMemCache(retObj);
        updateMap(retObj);
        return retObj;
    }


    public PointMemCacheImpl(final User user) {
        this.u = user;
        if (user != null) {
            cache = MemcacheServiceFactory.getMemcacheService(MemCacheKey.userPointNamespace.name() + u.getUuid());
        } else {
            cache = MemcacheServiceFactory.getMemcacheService(MemCacheKey.defaultNamespace.name());

        }

    }

    public void purgeMemCache(final Point p)  {
        if (cache.contains(p.getUUID())) {
            cache.delete(p.getUUID());
        }

    }

    private void updateMap(final Point p)  {
        if (p != null) {
            purgeMemCache(p);
            cache.put(p.getUUID(), p);
        }
    }



    @Override
    public List<Point> getPoints() throws NimbitsException {

        return PointTransactionsFactory.getDaoInstance(u).getPoints();

    }

    @Override
    public Point getPointByID(final long id) throws NimbitsException {
        return  PointTransactionsFactory.getDaoInstance(u).getPointByID(id);
    }

    @Override
    public Point updatePoint(final Point point) throws NimbitsException {

        Point retObj = PointTransactionsFactory.getDaoInstance(u).updatePoint(point);
        updateMap(retObj);
        return retObj;

    }

//    @Override
//    public void deletePoint(final Point point) throws NimbitsException {
//        final User u = UserTransactionFactory.getInstance().getNimbitsUserByID(point.getUserFK());
//
//        purgeMemCache(point);
//        PointTransactionsFactory.getDaoInstance(u).deletePoint(point);
//
//    }

    //these should not use the cache, since we don't know the user
    @Override
    public Point getPointByUUID(final String uuid)  {
        return PointTransactionsFactory.getDaoInstance(u).getPointByUUID(uuid);
    }

    @Override
    public List<Point> getAllPoints() {
        return PointTransactionsFactory.getDaoInstance(u).getAllPoints();

    }

    @Override
    public Point addPoint(Entity entity) {
        final Point retObj = PointTransactionsFactory.getDaoInstance(u).addPoint(entity);
        updateMap(retObj);
        return retObj;
    }

    @Override
    public List<Point> getPoints(List<Entity> entities) {
        return PointTransactionsFactory.getDaoInstance(u).getPoints(entities);
    }

    @Override
    public Point deletePoint(Entity entity) {
       if (cache.contains(entity.getEntity())) {
           cache.delete(entity.getEntity());
       }
       return PointTransactionsFactory.getDaoInstance(u).deletePoint(entity);
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
