package com.nimbits.client.model.instance;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.email.*;

import java.io.*;
import java.util.*;


/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 12:48 PM
 */
public class InstanceModel implements Serializable, Instance {

    private int id;

    private String baseUrl;

    private String ownerEmail;

    private String version;

    private Date ts;

    public InstanceModel(final String baseUrl, final EmailAddress ownerEmail, final String serverVersion) {
        this.baseUrl = baseUrl;
        this.ownerEmail = ownerEmail.getValue();
        this.version = serverVersion;
    }

    public InstanceModel(final Instance server) throws NimbitsException {

        this.id = server.getId();
        this.baseUrl = server.getBaseUrl();
        this.ownerEmail = server.getOwnerEmail().getValue();
        this.version = server.getVersion();
        this.ts = server.getTs();
    }

    public InstanceModel() {
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public EmailAddress getOwnerEmail() throws NimbitsException {
        return CommonFactoryLocator.getInstance().createEmailAddress(ownerEmail);
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public  Date getTs() {
        return ts;
    }
}
