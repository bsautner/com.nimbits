package com.nimbits.io.helper.impl;

import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.server.Server;
import com.nimbits.io.NimbitsClient;
import com.nimbits.io.helper.EntityHelper;
import com.nimbits.io.http.NimbitsClientFactory;

import java.util.List;

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

    @Override
    public List<Entity> addEntity(Entity e, Class clz) {

        return nimbitsClient.addEntity(e, clz);
    }

    @Override
    public List<Entity> updateEntity(Entity e, Class clz) {
        return nimbitsClient.updateEntity(e, clz);
    }

    @Override
    public List<Entity> getTree() {
        return nimbitsClient.getTree();
    }
}
