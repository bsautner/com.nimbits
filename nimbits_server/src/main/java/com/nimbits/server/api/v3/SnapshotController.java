package com.nimbits.server.api.v3;

import com.google.common.base.Optional;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.hal.*;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.process.task.ValueTask;
import com.nimbits.server.transaction.entity.EntityService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@RestController
public class SnapshotController extends RestAPI {


    @Autowired
    public SnapshotController(EntityService entityService, ValueService valueService, UserService userService, EntityDao entityDao, ValueTask valueTask) {
        super(entityService, valueService, userService, entityDao, valueTask);
    }

    @RequestMapping(value = "/{uuid}/snapshot", method = RequestMethod.POST)
    public ResponseEntity postSnapshot(
            @RequestHeader(name = AUTH_HEADER) String authorization,
            @RequestBody Value value,
            @PathVariable String uuid) throws Exception {

        User user = userService.getUser(authorization);
        Optional<Entity> entityOptional =  entityDao.getEntity(user, uuid, EntityType.point);

        if (entityOptional.isPresent()) {
            valueTask.process(user, (Point) entityOptional.get(), value);

            return new ResponseEntity(HttpStatus.OK);
        }
        else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }


    }


    @RequestMapping(value = "sync/{uuid}/snapshot", method = RequestMethod.POST)
    public ResponseEntity<Value> postSnapshotSync(
            @RequestHeader(name = AUTH_HEADER) String authorization,
            @RequestBody Value value,
            @PathVariable String uuid) throws Exception {

        User user = userService.getUser(authorization);
        Optional<Entity> entityOptional =  entityDao.getEntity(user, uuid, EntityType.point);

        if (entityOptional.isPresent()) {

            Value rx = valueTask.processSync(user, (Point) entityOptional.get(), value);


            return new ResponseEntity<>(rx, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{uuid}/snapshot", method = RequestMethod.GET)
    public ResponseEntity<String> getSnapshot(HttpServletRequest request,
                                              @RequestHeader(name = AUTH_HEADER) String authorization,
                                              @RequestParam(name = "sd", required = false) Long sd,
                                              @RequestParam(name = "sd", required = false) Long ed,
                                              @PathVariable String uuid) {

        try {


            User user = userService.getUser(authorization);

            Self self = new Self(String.valueOf(getCurrentUrl(request) + uuid));
            Parent parent = new Parent(getCurrentUrl(request) + uuid);
            Sample sample = new Sample(getCurrentUrl(request) + uuid + "/snapshot", "get snapshot");

            Links links = new Links(self, parent, sample);


            EmbeddedValues valueEmbedded = null;
            if (sd != null && ed != null) {

                valueEmbedded = new EmbeddedValues(new ArrayList<Value>());

            }
            Optional<Entity> optional = entityDao.getEntity(user, uuid, EntityType.point);
            if (optional.isPresent()) {
                Value snapshot = valueService.getCurrentValue(optional.get());
                ValueContainer valueContainer = new ValueContainer(links, valueEmbedded, snapshot);
                return new ResponseEntity<>(gson.toJson(valueContainer), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

}
