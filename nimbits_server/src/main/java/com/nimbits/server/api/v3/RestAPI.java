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

package com.nimbits.server.api.v3;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.hal.*;
import com.nimbits.client.model.user.User;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.process.task.ValueTask;
import com.nimbits.server.transaction.entity.EntityService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public abstract class RestAPI {

    public final static String AUTH_HEADER = "Authorization";

    final static Logger logger = LoggerFactory.getLogger(RestAPI.class);
    final EntityService entityService;
    final ValueService valueService;
    final UserService userService;
    final EntityDao entityDao;
    final Gson gson;
    final ValueTask valueTask;


    public RestAPI(EntityService entityService, ValueService valueService, UserService userService,
                   EntityDao entityDao, ValueTask valueTask ) {

        this.valueTask = valueTask;
        this.entityService = entityService;
        this.valueService = valueService;
        this.userService = userService;
        this.entityDao = entityDao;
        this.gson = GsonFactory.getInstance(true);


    }


    String getCurrentUrl(HttpServletRequest request) {
        URL url;
        try {
            url = new URL(request.getRequestURL().toString());

            String host = url.getHost();
            String userInfo = url.getUserInfo();
            String scheme = url.getProtocol();
            int port = url.getPort();
            String path = (String) request.getAttribute("javax.servlet.forward.request_uri");
            String query = (String) request.getAttribute("javax.servlet.forward.query_string");

            URI uri = new URI(scheme, userInfo, host, port, path, query, null);
            return uri.toString() + "/service/v3/rest/";
        } catch (MalformedURLException | URISyntaxException e) {
            return e.getMessage();
        }
    }

    User getMe(HttpServletRequest request, User user, boolean withChildren) throws IOException {


        List<Entity> children = getChildEntitiesIfRequested(user, withChildren);

        setHAL(user, user, children, getCurrentUrl(request), null);
        user.setChildren(children);
        return user;

    }

    List<Entity> getChildEntitiesIfRequested(User user, boolean includeChildren) {


        List<Entity> children;
        if (includeChildren) {
            children = entityDao.getChildren(user, user);
        } else {
            children = Collections.emptyList();
        }
        return children;
    }

    void setHAL(User user, Entity entity, List<Entity> childList, String path, Integer index) {


        Parent parent;
        if (entity.getEntityType().equals(EntityType.user)) {
            parent = new Parent(path + user.getId());
        } else {
            Optional<Entity> rootParentEntity = entityDao.findEntity(user, entity.getParent());
            if (rootParentEntity.isPresent()) {
                parent = new Parent(path + rootParentEntity.get().getId());
            } else {
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

            series = new Series(path + entity.getId() + "/series");
            dataTable = new DataTable(path + entity.getId() + "/table");
            snapshot = new Snapshot(path + entity.getId() + "/snapshot");


        } else if (entity.getEntityType().equals(EntityType.user)) {
            series = new Series(path + entity.getId() + "/series");
            if (index != null) {
                next = new Next(path.substring(0, path.lastIndexOf("/")) + "?index=" + ++index);
            }


        }
        children = new Children(path + entity.getId() + "/children");
        Links links = new Links(self, parent, series, snapshot, dataTable, next, nearby, children);
        List<EntityChild> entityChildren = new ArrayList<>();


        for (Entity child : childList) {
            if (child.getParent().equals(entity.getId()) && !child.getId().equals(entity.getId())) {

                Self eSelf = new Self(path + child.getId());
                Series cseries = null;
                DataTable cdataTable = null;
                Snapshot csnapshot = null;
                Nearby cnearby = null;
                Children cchildren;

                if (child.getEntityType().equals(EntityType.point)) {
                    cseries = new Series(path + child.getId() + "/series");
                    cdataTable = new DataTable(path + child.getId() + "/table");
                    csnapshot = new Snapshot(path + child.getId() + "/snapshot");

                }
                //  Entity parentEntity = childMap.get(child.getParent());

                Parent eParent;
                if (child.getParent().equals(user.getId())) {
                    eParent = new Parent(path + "me");
                } else {
                    eParent = new Parent(path + entity.getId());
                }
                cchildren = new Children(path + entity.getId() + "/children");
                Links eLinks = new Links(eSelf, eParent, cseries, csnapshot, cdataTable, null, cnearby, cchildren);

                entityChildren.add(new EntityChild(eLinks, child.getName().getValue()));
            }

        }
        Embedded embedded = new Embedded(entityChildren);
        entity.setEmbedded(embedded);

        entity.setLinks(links);
    }




}
