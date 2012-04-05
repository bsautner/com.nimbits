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

package com.nimbits.server.point;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 9/30/11
 * Time: 2:12 PM
 */
public interface PointTransactions {

    //List<Point> getPoints() throws NimbitsException;



    Point updatePoint(final Point point) throws NimbitsException;

  //  void deletePoint(final Point p) throws NimbitsException;

    Point checkPoint(final HttpServletRequest req, final EmailAddress email, final Point point) throws NimbitsException;

    List<Point> getAllPoints(final int start,final  int end);

    List<Point> getIdlePoints();

    Point getPointByKey(final String uuid) throws NimbitsException;

    List<Point> getAllPoints();

    Point addPoint(final Entity entity);

    Point addPoint(final Entity entity, final Point point);

    List<Point>  getPoints(final List<Entity> entities);

    Point deletePoint(final Entity entity);
}
