package com.nimbits.server.gson;

import com.google.gson.InstanceCreator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.instance.Instance;

import java.lang.reflect.Type;

/**
 * Created by bsautner on 12/11/15.
 */
public class EntityInstanceCreator implements InstanceCreator<Entity> {
    @Override
    public Entity createInstance(Type type) {
        return null;
    }
}
