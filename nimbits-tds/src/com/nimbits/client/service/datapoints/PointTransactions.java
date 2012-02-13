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

package com.nimbits.client.service.datapoints;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 9/30/11
 * Time: 2:12 PM
 */
public interface PointTransactions {

    List<Point> getPoints() throws NimbitsException;

    Point getPointByID(final long id) throws NimbitsException;

    Point updatePoint(final Point point) throws NimbitsException;

    Point getPointByName(final EntityName name) throws NimbitsException;

    void deletePoint(final Point p) throws NimbitsException;

    //Point showEntityData(final Point point, final Category c) throws NimbitsException;

    Point addPoint(final Point point);

   // Point showEntityData(final EntityName pointName, final Category c) throws NimbitsException;

   //  List<Point> getPointsByCategory(final Category c);

    Point checkPoint(final HttpServletRequest req, final EmailAddress email, final Point point) throws NimbitsException;

    Point publishPoint(Point p) throws NimbitsException;

    List<Point> getAllPoints(int start, int end);

    List<Point> getIdlePoints();

    Point getPointByUUID(final String uuid);

    List<Point> getAllPoints();

    Point addPoint(Entity entity);


    List<Point>  getPoints(List<Entity> entities);
}
