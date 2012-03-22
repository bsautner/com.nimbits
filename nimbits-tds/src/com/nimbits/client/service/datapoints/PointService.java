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


import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;

import java.util.*;

@RemoteServiceRelativePath("point")
public interface PointService extends RemoteService {

    Point addPoint(User user, Entity entity, Point point) throws NimbitsException;

    Point addPoint(EntityName name) throws NimbitsException;

    Point addPoint(final User user, final Entity entity) throws NimbitsException;

    Point getPointByID(final User u, final long id) throws NimbitsException;

    List<Point> getPoints() throws NimbitsException;

    List<Point> getPoints(final User u, List<Entity> entities);

    Point updatePoint(final Point point) throws NimbitsException;

    Point updatePoint(final User u, final Point point) throws NimbitsException;

    List<Point> getPoints(final User u) throws NimbitsException;

    Point getPointByUUID(final String uuid);

    Point getPointByID(final long id) throws NimbitsException;

    String exportData(final Map<EntityName, Entity> points, final ExportType exportType, final Map<EntityName, List<Value>> values) ;

    List<Point> getAllPoints(final int start, final int end);

    List<Point> getIdlePoints();

    List<Point> getAllPoints();

    Entity copyPoint(final User u, final Entity originalEntity, final EntityName newName) throws NimbitsException;

    Map<String,Point> getPoints(final Map<String, Entity> entities) throws NimbitsException;

    void deletePoint(final User user, final Entity entity);
}
