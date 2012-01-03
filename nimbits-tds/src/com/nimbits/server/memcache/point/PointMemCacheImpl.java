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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.memcache.point;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryName;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointName;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.datapoints.PointTransactions;
import com.nimbits.server.memcache.MemCacheHelper;
import com.nimbits.server.point.PointTransactionsFactory;
import com.nimbits.server.pointcategory.CategoryTransactionFactory;
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
    private final User u;
    private final String lk;

    public PointMemCacheImpl(User user) {
        this.u = user;
        if (user != null) {
            //must be the same namespace categorymemcache uses
            cache = MemcacheServiceFactory.getMemcacheService(u.getUuid());
            lk = MemCacheHelper.pointListKey(u);
        } else {
            cache = MemcacheServiceFactory.getMemcacheService();
            lk = "default";
        }

    }

    public void purgeMemCache(Point p) throws NimbitsException {
        if (cache.contains(p.getId())) {
            cache.delete(p.getId());
        }
        if (cache.contains(p.getUUID())) {
            cache.delete(p.getUUID());
        }
        if (cache.contains(p.getName())) {
            cache.delete(p.getName());
        }
        if (cache.contains(lk)) {
            cache.delete(lk);
        }
        CategoryTransactionFactory.getInstance(u).purgeMemCache();


    }

    private void updateMap(Point p) throws NimbitsException {
        if (p != null) {
            purgeMemCache(p);
            cache.put(p.getUUID(), p);
            cache.put(p.getId(), p);
            cache.put(p.getName(), p);
        }
    }


    @Override
    public List<Point> getPoints() throws NimbitsException {
        List<Point> retObj;
        if (cache.contains(lk)) {
            retObj = (List<Point>) cache.get(lk);
            return retObj;
        } else {
            retObj = PointTransactionsFactory.getDaoInstance(u).getPoints();
            cache.put(lk, retObj);
            return retObj;
        }

    }

    @Override
    public Point getPointByID(long id) throws NimbitsException {

        if (cache.contains(id)) {
            return (Point) cache.get(id);
        } else {
            Point p = PointTransactionsFactory.getDaoInstance(u).getPointByID(id);

            updateMap(p);
            return p;
        }


    }

    @Override
    public Point updatePoint(Point point) throws NimbitsException {
        // final User u = UserTransactionFactory.getInstance().getNimbitsUserByID(point.getUserFK());
        purgeMemCache(point);
        Point retObj = PointTransactionsFactory.getDaoInstance(u).updatePoint(point);
        updateMap(retObj);
        return retObj;

    }

    @Override
    public Point getPointByName(PointName name) throws NimbitsException {
        if (cache.contains(name)) {
            return (Point) cache.get(name);
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

    @Override
    public Point movePoint(final PointName pointName, final CategoryName newCategoryName) throws NimbitsException {
        Point movedPoint = PointTransactionsFactory.getDaoInstance(u).movePoint(pointName, newCategoryName);
        purgeMemCache(movedPoint);
        updateMap(movedPoint);
        return movedPoint;

    }

    @Override
    public Point addPoint(final Point point, final Category c) throws NimbitsException {
        final Point retObj = PointTransactionsFactory.getDaoInstance(u).addPoint(point, c);
        purgeMemCache(retObj);
        updateMap(retObj);
        return retObj;
    }

    @Override
    public Point addPoint(final PointName pointName, final Category c) throws NimbitsException {

        final Point retObj = PointTransactionsFactory.getDaoInstance(u).addPoint(pointName, c);
        purgeMemCache(retObj);
        updateMap(retObj);
        return retObj;
    }


    @Override
    public List<Point> getPointsByCategory(Category c) {
        return PointTransactionsFactory.getDaoInstance(u).getPointsByCategory(c);
    }

    @Override
    public Point publishPoint(Point p) throws NimbitsException {
        purgeMemCache(p);
        Point retObj = PointTransactionsFactory.getDaoInstance(u).publishPoint(p);
        updateMap(p);
        return retObj;

    }

    //these should not use the cache, since we don't know the user
    @Override
    public Point getPointByUUID(String uuid) throws NimbitsException {
        return PointTransactionsFactory.getDaoInstance(u).getPointByUUID(uuid);
    }

    @Override
    public List<Point> getAllPoints(int start, int end) {
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
