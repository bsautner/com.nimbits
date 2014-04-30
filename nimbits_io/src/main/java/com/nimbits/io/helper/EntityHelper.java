package com.nimbits.io.helper;

import com.nimbits.client.model.entity.Entity;

import java.util.List;

public interface EntityHelper {

    void deleteEntity(Entity entity);

    List<Entity> addEntity(Entity e, Class clz);

    List<Entity> updateEntity(Entity e, Class clz);

    List<Entity> getTree();
}
