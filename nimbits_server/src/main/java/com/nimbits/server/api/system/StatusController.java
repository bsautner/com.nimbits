package com.nimbits.server.api.system;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.nimbits.client.model.user.User;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transaction.user.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

@RestController
public class StatusController  {



    private final UserDao userDao;



    @Autowired
    public StatusController(UserDao userDao) {

        this.userDao = userDao;


    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/status", method = RequestMethod.GET)
    public ResponseEntity<Status> getStatus() {
        User admin = userDao.getAdmin();
        Status status = new Status(admin.getEmail().getValue(), "");
        return new ResponseEntity<>(status, HttpStatus.OK);
    }


    private class Status {

        String admin;
        String version;

        public Status(String admin, String version) {
            this.admin = admin;
            this.version = version;
        }

        public String getAdmin() {
            return admin;
        }

        public String getVersion() {
            return version;
        }
    }

}
