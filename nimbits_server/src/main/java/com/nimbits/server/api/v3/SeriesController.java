package com.nimbits.server.api.v3;

import com.google.common.base.Optional;
import com.google.common.collect.Range;
import com.google.gson.reflect.TypeToken;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

@RestController
public class SeriesController extends RestAPI {

    @Autowired
    public SeriesController(EntityService entityService, ValueService valueService, UserService userService, EntityDao entityDao, ValueTask valueTask) {
        super(entityService, valueService, userService, entityDao, valueTask);
    }

    @RequestMapping(value = "/{uuid}/series", method = RequestMethod.POST)
    public ResponseEntity postSeries(
            @RequestHeader(name = AUTH_HEADER) String authorization,
            @RequestBody String json,
            @PathVariable String uuid) throws IOException {


        Type listType = new TypeToken<ArrayList<Value>>() {
        }.getType();
        User user = userService.getUser(authorization);
        Optional<Entity> optional = entityDao.getEntity(user, uuid, EntityType.point);
        List<Value> values = gson.fromJson(json, listType);

        if (optional.isPresent() && ! values.isEmpty()) {
            if (values.size() == 1) {

                valueTask.process(user, (Point) optional.get(), values.get(0));

            } else {

                valueService.recordValues(user, (Point) optional.get(), values);
            }
        }
        return new ResponseEntity(HttpStatus.OK);


    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{uuid}/series", method = RequestMethod.GET)
    public ResponseEntity<String> getSeries(@RequestHeader(name = AUTH_HEADER) String authorization,
                                            @PathVariable String uuid,
                                            @RequestParam(value = "start", required = false) String startParam,
                                            @RequestParam(value = "end", required = false) String endParam,
                                            @RequestParam(value = "count", required = false) String countParam,
                                            @RequestParam(value = "mask", required = false) String maskParam) throws IOException {

        User user = userService.getUser(authorization);

        Optional<String> mask = StringUtils.isEmpty(maskParam) ? Optional.<String>absent() : Optional.of(maskParam);
        Date start = StringUtils.isEmpty(startParam) ? new Date(1) : new Date(Long.valueOf(startParam));
        Date end = StringUtils.isEmpty(endParam) ? new Date() : new Date(Long.valueOf(endParam));

        Optional<Integer> count = (StringUtils.isEmpty(countParam)) ? Optional.<Integer>absent() : Optional.of(Integer.valueOf(countParam));

        Optional<Range<Integer>> range;
        if (count.isPresent()) {
            range = Optional.of(Range.closed(0, count.get()));
        } else {
            range = Optional.absent();
        }

        Optional<Range<Long>> timespan = Optional.of(Range.closed(start.getTime(), end.getTime()));


        if (user.getId().equals(uuid)) {
            List<Entity> entities = entityDao.getEntitiesByType(user, EntityType.point);

            Map<String, List<Value>> map = new HashMap<>(entities.size());
            for (Entity e : entities) {
                List<Value> values = valueService.getSeries(e, timespan, range, mask);

                map.put(e.getId(), values);


            }
            String resp = gson.toJson(map);
            return new ResponseEntity<>(resp, HttpStatus.OK);


        } else {
            Optional<Entity> optional = entityDao.getEntity(user, uuid, EntityType.point);
            if (optional.isPresent()) {


                List<Value> values = valueService.getSeries(optional.get(), timespan, range, mask);

                String resp = gson.toJson(values);
                return new ResponseEntity<>(resp, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }


    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{uuid}/table", method = RequestMethod.GET)
    public ResponseEntity<String> getTable(@RequestHeader(name = AUTH_HEADER) String authorization,
                                           @PathVariable String uuid,
                                           @RequestParam(value = "start", required = false) String startParam,
                                           @RequestParam(value = "end", required = false) String endParam,
                                           @RequestParam(value = "count", required = false) String countParam,
                                           @RequestParam(value = "mask", required = false) String maskParam) throws IOException {

        User user = userService.getUser(authorization);
        Optional<String> mask = StringUtils.isEmpty(maskParam) ? Optional.<String>absent() : Optional.of(maskParam);
        Optional<Integer> count = StringUtils.isNotEmpty(countParam) ? Optional.of(Integer.valueOf(countParam)) : Optional.<Integer>absent();

        Optional<Range<Long>> timespan;


        if (!StringUtils.isEmpty(startParam) && !StringUtils.isEmpty(endParam)) {
            long start = (Long.valueOf(startParam));
            long end = (Long.valueOf(endParam));
            timespan = Optional.of(Range.closed(start, end));

        } else {
            timespan = Optional.absent();
        }

        if (timespan.isPresent() || count.isPresent()) {

            Optional<Entity> optional = entityDao.getEntity(user, uuid, EntityType.point);
            if (optional.isPresent()) {
                List<Entity> children = entityDao.getChildren(user, optional.get());
                String chartData = valueService.getChartTable(user, optional.get(), children, timespan, count, mask);
                return new ResponseEntity<>(chartData, HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } else {

            throw new RuntimeException(
                    "Please provide a start and end date parameter or a count parameter in unix epoch format including ms for example:?count=100 or ?count=100&mask=regex  or ?start="
                            + (System.currentTimeMillis() - 10000) + "&end=" + System.currentTimeMillis());
        }
    }

}
