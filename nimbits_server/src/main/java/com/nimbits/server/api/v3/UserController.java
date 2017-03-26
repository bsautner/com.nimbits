package com.nimbits.server.api.v3;

import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.client.model.user.UserSource;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.process.task.ValueTask;
import com.nimbits.server.transaction.entity.EntityService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class UserController extends RestAPI {

    @Autowired
    public UserController(EntityService entityService, ValueService valueService, UserService userService, EntityDao entityDao, ValueTask valueTask) {
        super(entityService, valueService, userService, entityDao, valueTask);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<String> postUser(@RequestHeader(name = "Authorization") String authorization,
                                           @RequestBody String json) throws IOException {

        User user = userService.getUser(authorization);

        if (user.getIsAdmin()) {
            User newUser = GsonFactory.getInstance(false).fromJson(json, UserModel.class);
            User createdUser = userService.createUserRecord(newUser.getEmail(), newUser.getPassword(), UserSource.local);
            return new ResponseEntity<>(gson.toJson(createdUser), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }


    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public ResponseEntity putUser(@RequestHeader(name = "Authorization") String authorization, @RequestBody User update) {


        User user = userService.getUser(authorization);
        if (user.getIsAdmin()) {
            if (!StringUtils.isEmpty(update.getPassword())) {


                userService.updatePassword(update, update.getPassword());

            } else {

                entityService.addUpdateEntity(user, update);
            }
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

    }



}
