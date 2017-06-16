package com.nimbits.client.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nimbits.client.model.user.User;
import org.apache.commons.lang3.RandomStringUtils;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;


@JsonIgnoreProperties(ignoreUnknown = true)
@PersistenceCapable //(table = Tables.SESSION)
public class Session {

    @Persistent
    private String sessionId;

    @Persistent
    private String userId;

    @Persistent
    private Long timestamp;

    @Persistent
    private Boolean rememberMe;


    public Session(User user, boolean rememberMe) {
        this.userId = user.getId();
        this.timestamp = System.currentTimeMillis();
        this.sessionId = RandomStringUtils.randomAlphanumeric(255);
        this.rememberMe = rememberMe;

    }


    public String getUserId() {
        return userId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }


}
