package com.nimbits.server.api.v3;

import com.google.common.base.Optional;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.process.task.ValueTask;
import com.nimbits.server.transaction.entity.EntityService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class EntityController extends RestAPI {

    private static final String ENTITY_TYPE = "entityType";

    @Autowired
    public EntityController(EntityService entityService, ValueService valueService, UserService userService, EntityDao entityDao, ValueTask valueTask) {
        super(entityService, valueService, userService, entityDao, valueTask);
    }


    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{uuid}", method = RequestMethod.POST)
    public ResponseEntity<String> postEntity(@RequestHeader(name = AUTH_HEADER) String authorization,
                                             @RequestBody String json,
                                             @PathVariable String uuid) throws IOException {

        User user = userService.getUser(authorization);
        EntityType type = getEntityType(json);
        Entity newEntity = (Entity) gson.fromJson(json, type.getClz());
        Optional<Entity> parentOptional = entityDao.findEntity(user, uuid);



        if (parentOptional.isPresent()) {
            Entity parent = parentOptional.get();
            newEntity.setParent(parent.getId());
            newEntity.setOwner(user.getId());
            if (entityDao.nameIsValid(user, newEntity)) {
                Entity stored = entityService.addEntity(user, newEntity);


                return new ResponseEntity<>(gson.toJson(stored), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }

    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{uuid}/children", method = RequestMethod.GET)
    public ResponseEntity<String> getChildren(HttpServletRequest request,
                                              @RequestHeader(name = AUTH_HEADER) String authorization,
                                              @PathVariable String uuid) throws IOException {

        User user = userService.getUser(authorization);


        Optional<Entity> optional = entityDao.findEntity(user, uuid);


        if (optional.isPresent()) {
            List<Entity> children = entityDao.getChildren(user, optional.get());
            for (Entity e : children) {
                setHAL(user, e, Collections.<Entity>emptyList(), getCurrentUrl(request), null);
            }
            return new ResponseEntity<>(gson.toJson(children), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{uuid}", method = RequestMethod.GET)
    public ResponseEntity<String> getEntity(HttpServletRequest request,
                                            @RequestHeader(name = AUTH_HEADER) String authorization,
                                            @PathVariable String uuid,
                                            @RequestParam(name = "children", required = false) boolean includeChildren,
                                            @RequestParam(name = "name", required = false) String name,
                                            @RequestParam(name = "point", required = false) String point,
                                            @RequestParam(name = "type", required = false) String t) throws IOException {


        User user = userService.getUser(authorization);
        String searchName = null;
        EntityType searchType;

        if (StringUtils.isNotEmpty(name)) {
            searchName = name;
        } else if (StringUtils.isNotEmpty(point)) {
            searchName = name;
        }

        if (StringUtils.isEmpty(t)) {
            searchType = EntityType.point;
        } else {
            int type = Integer.valueOf(t);
            searchType = EntityType.get(type);
            if (searchType == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        if (StringUtils.isNotEmpty(searchName)) {

            Optional<Entity> e = entityDao.getEntityByName(user, CommonFactory.createName(name, searchType), searchType);
            if (e.isPresent()) {
                Entity entity = e.get();
                if (! entity.getOwner().equals(user.getId())) {
                    logger.warn("attempt to return an entity that did not belong to the user");
                    throw new RuntimeException();
                }
                else {
                    String json = gson.toJson(e.get());

                    return new ResponseEntity<>(json, HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } else if (uuid.equals("me")) {
            User u = getMe(request, user, includeChildren);
            return new ResponseEntity<>(GsonFactory.getInstance(true).toJson(u), HttpStatus.OK);
        } else {
            Optional<Entity> optional = entityDao.findEntity(user, uuid);// entityMap.get(uuid);

            if (optional.isPresent()) {
                Entity entity = optional.get();


                List<Entity> children;
                if (includeChildren) {
                    children = entityDao.getChildren(user, entity);
                } else {
                    children = Collections.emptyList();
                }

                setHAL(user, entity, children, getCurrentUrl(request), null);

                entity.setChildren(children);
                return new ResponseEntity<>(GsonFactory.getInstance(true).toJson(entity), HttpStatus.OK);
                //return new ResponseEntity<>(entity, HttpStatus.OK);

            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

    }


    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{uuid}", method = RequestMethod.DELETE)
    public ResponseEntity doDelete(@RequestHeader(name = AUTH_HEADER) String authorization,
                                   @PathVariable String uuid) throws IOException {

        User user = userService.getUser(authorization);
        Optional<Entity> optional = entityDao.findEntity(user, uuid);
        if (optional.isPresent()) {
            Entity entity = optional.get();
            if (!user.getIsAdmin() && entity.getEntityType() != EntityType.user && entity.getOwner().equals(user.getId())) {
                entityService.deleteEntity(user, entity);
                if (entity.getEntityType().equals(EntityType.point)) {
                    Point point = (Point) entity;

                    valueService.deleteAllData(point);


                    // taskService.startDeleteDataTask((Point) entity);

                }
            } else if (user.getIsAdmin()) {
                entityService.deleteEntity(user, entity);
                if (entity.getEntityType().equals(EntityType.point)) {
                    Point point = (Point) entity;

                    valueService.deleteAllData(point);


                    // taskService.startDeleteDataTask((Point) entity);

                }
            } else if (!entity.getOwner().equals(user.getId())) {
                throw new SecurityException("You can not delete an entity you don't own if your not the system admin");
            }
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }


    @RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
    public ResponseEntity putEntity(
            @RequestHeader(name = AUTH_HEADER) String authorization,
            @RequestBody String json) {


        User user = userService.getUser(authorization);
        EntityType type = getEntityType(json);
        Entity entity = (Entity) gson.fromJson(json, type.getClz());

        boolean success = entityDao.updateEntity(user, entity);
        if (success) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }


    }


    @RequestMapping(value = "/sync/{uuid}", method = RequestMethod.PUT)
    public ResponseEntity<String> putEntitySync(
            @RequestHeader(name = AUTH_HEADER) String authorization,
            @RequestBody String json) {


        User user = userService.getUser(authorization);
        EntityType type = getEntityType(json);
        Entity entity = (Entity) gson.fromJson(json, type.getClz());

        boolean success = entityDao.updateEntity(user, entity);
        if (success) {
            Optional<Entity> o = entityDao.getEntity(user, entity.getId(), type);
            if (o.isPresent()) {
                String r = gson.toJson(o.get());
                return new ResponseEntity<>(r, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);


    }


    private EntityType getEntityType(String json) {

        Map jsonMap = gson.fromJson(json, Map.class);
        int t = Double.valueOf(String.valueOf(jsonMap.get(ENTITY_TYPE))).intValue();
        return EntityType.get(t);
    }
}
