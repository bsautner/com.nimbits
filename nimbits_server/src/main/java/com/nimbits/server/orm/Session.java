package com.nimbits.server.orm;


import com.nimbits.client.model.user.User;

import javax.jdo.annotations.Cacheable;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.util.UUID;

@Cacheable("false")
@PersistenceCapable
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
        this.sessionId = UUID.randomUUID().toString();
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
