package com.nimbits.client.model.server;

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
public class ServerModel implements Serializable, Server {

    private int idServer;

    private String baseUrl;

    private String ownerEmail;

    private String serverVersion;

    private Date ts;

    public ServerModel(final String baseUrl, final EmailAddress ownerEmail, final String serverVersion) {
        this.baseUrl = baseUrl;
        this.ownerEmail = ownerEmail.getValue();
        this.serverVersion = serverVersion;
    }

    public ServerModel(final Server server) throws NimbitsException {

        this.idServer = server.getIdServer();
        this.baseUrl = server.getBaseUrl();
        this.ownerEmail = server.getOwnerEmail().getValue();
        this.serverVersion = server.getServerVersion();
        this.ts = server.getTs();
    }

    public ServerModel() {
    }

    @Override
    public int getIdServer() {
        return idServer;
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
    public String getServerVersion() {
        return serverVersion;
    }

    @Override
    public  Date getTs() {
        return ts;
    }
}
