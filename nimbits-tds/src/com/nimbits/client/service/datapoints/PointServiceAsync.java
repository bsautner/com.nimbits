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

public interface PointServiceAsync {
    void getPoints(final AsyncCallback<List<Point>> asyncCallback);
    void updatePoint(final Point point, final AsyncCallback<Point> asyncCallback);
    void getPointByID(final long id, final AsyncCallback<Point> asyncCallback);
    void getPoints(final User u, final AsyncCallback<List<Point>> callback);
    void getPointByUUID(final String uuid, final AsyncCallback<Point> asyncCallback);
    void getPointByID(final User u, final long id, final AsyncCallback<Point> async);
    void exportData(final Map<EntityName, Entity> points, ExportType exportType, final Map<EntityName, List<Value>> values, AsyncCallback<String> async);
    void getAllPoints(final int start, final int end, AsyncCallback<List<Point>> async);
    void getIdlePoints(final AsyncCallback<List<Point>> async);
    void updatePoint(final User u, final Point point, AsyncCallback<Point> async);
    void getAllPoints(final AsyncCallback<List<Point>> async);
    void addPoint(final User user, final Entity entity, AsyncCallback<Point> async);
    void copyPoint(User u, Entity originalEntity, EntityName newName, AsyncCallback<Entity> async) throws NimbitsException;
    void getPoints(Map<String, Entity> entities, AsyncCallback<Map<String, Point>> async);
    void addPoint(User user, Entity entity, Point point, AsyncCallback<Point> async) throws NimbitsException;
    void addPoint(EntityName name, AsyncCallback<Point> async) throws NimbitsException;
    void getPoints(final User u, List<Entity> entities, AsyncCallback<List<Point>> async);
}
