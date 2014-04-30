package com.nimbits.io.helper.impl;

import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.server.Server;
import com.nimbits.io.NimbitsClient;
import com.nimbits.io.helper.EntityHelper;
import com.nimbits.io.http.NimbitsClientFactory;

public class EntityHelperImpl implements EntityHelper {
    protected final EmailAddress email;
    protected final NimbitsClient nimbitsClient;

    public EntityHelperImpl(Server server, EmailAddress email, String accessKey) {
        this.email = email;
        this.nimbitsClient = NimbitsClientFactory.getInstance(server, email, accessKey);
    }

    @Override
    public void deleteEntity(Entity entity) {
        nimbitsClient.deleteEntity(entity);
    }
}
