package com.nimbits.server.api.v3;

import com.google.common.base.Optional;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.client.model.user.UserSource;
import com.nimbits.server.process.task.ValueTask;
import com.nimbits.server.transaction.entity.EntityService;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.user.dao.UserDao;
import com.nimbits.server.transaction.user.service.UserService;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class UserController extends RestAPI {


    private final UserDao userDao;

    @Autowired
    public UserController(UserDao userDao, EntityService entityService, ValueService valueService, UserService userService, EntityDao entityDao, ValueTask valueTask) {
        super(entityService, valueService, userService, entityDao, valueTask);
        this.userDao = userDao;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<String> postUser(@RequestHeader(name = AUTH_HEADER) String authorization,
                                           @RequestBody UserModel newUser) throws IOException {

        User user = userService.getUser(authorization);

        if (user.getIsAdmin()) {

            User createdUser = userService.createUserRecord(newUser.getEmail(), newUser.getPassword(), UserSource.local);
            return new ResponseEntity<>(gson.toJson(createdUser), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }


    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public ResponseEntity putUser(@RequestHeader(name = AUTH_HEADER) String authorization, @RequestBody User update) {


        User user = userService.getUser(authorization);
        if (user.getIsAdmin()) {
            boolean success;
            if (!StringUtils.isEmpty(update.getPassword())) {


                userService.updatePassword(update, update.getPassword());
                success = true;

            } else {

                success = entityDao.updateEntity(user, update);

            }
            if (success) {
                return new ResponseEntity(HttpStatus.OK);
            }
            else {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

    }


    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/admin/user", method = RequestMethod.GET)
    public ResponseEntity<String> getUser(@RequestHeader(name = AUTH_HEADER) String authorization,
                                          @RequestParam String email) throws IOException {

        User admin = userService.getUser(authorization);

        if (admin.getIsAdmin()) {

            Optional<User> client = userDao.getUserByEmail(email);
            if (client.isPresent()) {
                return new ResponseEntity<>(gson.toJson(client.get()), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }


    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/admin/user", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteUser(@RequestHeader(name = AUTH_HEADER) String authorization,
                                          @RequestParam String email) throws IOException {

        User admin = userService.getUser(authorization);

        if (admin.getIsAdmin()) {

            Optional<User> client = userDao.getUserByEmail(email);
            if (client.isPresent()) {
                entityDao.deleteEntity(admin, client.get(), EntityType.user);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }


    }


}
