package com.nimbits.client.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class ServicePayload<T extends Serializable> implements Serializable {

    @Expose
    private String email;

    private String token;

    @Expose
    private String nextRecord;
    @Expose
    private T payload;

    public ServicePayload(String email, String token, T payload) {
        this.email = email;
        this.token = token;
        this.payload = payload;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public T getPayload() {
        return payload;
    }
}
