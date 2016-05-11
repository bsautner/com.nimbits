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

package com.nimbits.server.api.v3.actions;


import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.hal.*;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.entity.service.EntityService;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class RestAction {

    public final Gson gson = GsonFactory.getInstance(true);

    public final EntityService entityService;
    public final ValueService valueService;
    public final UserService userService;
    public final EntityDao entityDao;

    public RestAction(EntityService entityService, ValueService valueService, UserService userService, EntityDao entityDao) {
        this.entityService = entityService;
        this.valueService = valueService;
        this.userService = userService;
        this.entityDao = entityDao;

    }

    public enum Action {
        me, root, snapshot, series, entity, table, nearby, children, file
    }



    public String getEntityUUID(String path) {
        String[] parts = path.split("/");
        Action action = getAction(path);
        int inx = 0;
        switch (action) {
            case root:
            case me:
            case entity:
                inx = 1;
                break;
            case snapshot:
            case nearby:
            case children:
            case series:
            case table:
            case file:
                inx = 2;
                break;


        }
        return parts[parts.length - inx];
    }

    public Action getAction(String path) {

        if (isApiRoot(path)) {
            return Action.root;
        }
        else if (isMePAth(path)) {
            return Action.me;
        }
        else if (isSnapshotPath(path)) {
            return Action.snapshot;

        }
        else if (isSeriesPath(path)) {
            return Action.series;

        }
        else if (isNearbyPath(path)) {
            return Action.nearby;

        }
        else if (isChildrenPath(path)) {
            return Action.children;

        }
        else if (isTablePath(path)) {
            return Action.table;
        }
        else if (isFilePath(path)) {
            return Action.file;

        }
        else {
            return Action.entity;
        }

    }

    public String getContent(final HttpServletRequest req) {

        BufferedReader reader;
        try {
            reader = req.getReader();
            if (req.getContentLength() > 0) {
                StringBuilder jb = new StringBuilder(req.getContentLength());
                String line;
                while ((line = reader.readLine()) != null) {
                    jb.append(line);
                }


                return jb.toString();
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    private boolean isApiRoot(String path) {
        path = trimQueryString(path);
        return path.endsWith("/rest");
    }

    private boolean isSnapshotPath(String path) {
        path = trimQueryString(path);
        return path.endsWith("/snapshot");
    }

    private boolean isTablePath(String path) {
        path = trimQueryString(path);
        return path.endsWith("/table");
    }

    private boolean isFilePath(String path) {
        path = trimQueryString(path);
        return path.contains("/file/");
    }

    private boolean isSeriesPath(String path) {
        path = trimQueryString(path);
        return path.endsWith("/series");
    }

    private boolean isNearbyPath(String path) {
        path = trimQueryString(path);
        return path.endsWith("/nearby");
    }

    private boolean isChildrenPath(String path) {
        path = trimQueryString(path);
        return path.endsWith("/children");
    }

    private boolean isMePAth(String path) {
        path = trimQueryString(path);
        return path.endsWith("/me");
    }

    private String trimQueryString(String path) {
        if (path.contains("?")) {
            path = path.substring(0, path.indexOf("?"));
        }
        return path;
    }

    public void setHAL(User user, Entity entity, List<Entity> childList, String path, Integer index) {


        Parent parent;
        if (entity.getEntityType().equals(EntityType.user)) {
            parent = new Parent(path + user.getId());
        }
        else {
            Optional<Entity> rootParentEntity = entityDao.findEntity(user, entity.getParent());
            if (rootParentEntity.isPresent()) {
                parent = new Parent(path + rootParentEntity.get().getId());
            }
            else {
                parent = new Parent(path + user.getId());
            }
        }


        Self self = new Self(path + entity.getId());
        Series series = null;
        DataTable dataTable = null;
        Snapshot snapshot = null;
        Next next = null;
        Nearby nearby = null;
        Children children;

        if (entity.getEntityType().equals(EntityType.point)) {
            Point point = (Point) entity;
            series =new Series(path + entity.getId() + "/series");
            dataTable =new DataTable(path + entity.getId() + "/table");
            snapshot =new Snapshot(path + entity.getId() + "/snapshot");
            if (point.getPointType().equals(PointType.location)) {
                nearby = new Nearby(path + entity.getId() + "/nearby");
            }


        }
        else if (entity.getEntityType().equals(EntityType.user)) {
            series =new Series(path + entity.getId() + "/series");
            if (index != null) {
                next = new Next(path.substring(0, path.lastIndexOf("/")) + "?index=" + ++index);
            }


        }
        children =new Children(path + entity.getId() + "/children");
        Links links = new Links(self, parent, series, snapshot, dataTable, next, nearby, children);
        List<EntityChild> entityChildren = new ArrayList<>();



        for (Entity child : childList) {
            if (child.getParent().equals(entity.getId()) && ! child.getId().equals(entity.getId())) {

                Self eSelf = new Self(path + child.getId());
                Series cseries = null;
                DataTable cdataTable = null;
                Snapshot csnapshot = null;
                Nearby cnearby = null;
                Children cchildren;

                if (child.getEntityType().equals(EntityType.point)) {
                    cseries =new Series(path + child.getId() + "/series");
                    cdataTable =new DataTable(path + child.getId() + "/table");
                    csnapshot  =new Snapshot(path + child.getId() + "/snapshot");
                    Point point1 = (Point) child;
                    if (point1.getPointType().equals(PointType.location)) {
                        cnearby = new Nearby(path + child.getId() + "/nearby");
                    }
                }
              //  Entity parentEntity = childMap.get(child.getParent());

                Parent eParent;
                if (child.getParent().equals(user.getId())) {
                    eParent = new Parent(path + "me");
                }
                else {
                    eParent  = new Parent(path + entity.getId());
                }
                cchildren  =new Children(path + entity.getId() + "/children");
                Links eLinks = new Links(eSelf, eParent, cseries, csnapshot, cdataTable, null, cnearby, cchildren);

                entityChildren.add(new EntityChild(eLinks, child.getName().getValue()));
            }

        }
        Embedded embedded = new Embedded(entityChildren);
        entity.setEmbedded(embedded);

        entity.setLinks(links);
    }


}
