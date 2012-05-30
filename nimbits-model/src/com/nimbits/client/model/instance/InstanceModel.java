package com.nimbits.client.model.instance;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;

import java.io.Serializable;
import java.util.Date;


/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 12:48 PM
 */
public class InstanceModel extends EntityModel implements Serializable, Instance {

    private int id;

    private String baseUrl;

    private String ownerEmail;

    private String version;

    private Date ts;

    public InstanceModel(final Entity baseEntity, final String baseUrl, final EmailAddress ownerEmail, final String serverVersion) throws NimbitsException {
        super(baseEntity);
        this.baseUrl = baseUrl;
        this.ownerEmail = ownerEmail.getValue();
        this.version = serverVersion;
    }

    public InstanceModel(final Instance server) throws NimbitsException {
        super(server);
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
