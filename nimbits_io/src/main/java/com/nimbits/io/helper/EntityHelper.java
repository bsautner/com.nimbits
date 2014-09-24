package com.nimbits.io.helper;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;

import java.util.List;


/*
Helps create various objects on your account, such as points, folders and helps download those objects


 */
public interface EntityHelper {

    /**
     *
     * @param entity the target entity to delete
     */
    void deleteEntity(Entity entity);


    List<Entity> addEntity(Entity e, Class clz);

    List<Entity> updateEntity(Entity e, Class clz);

    /**
     *
     * @return all of the entities belonging to the user
     */
    List<Entity> getTree();

    /**
     *
     * @param category a new category (folder) for keeping things in.
     *      *
     */
    Category addCategory(Category category);

    /**
     *
     * @param key the id of the category
     * @return the entity if it exists
     */
    Entity getCategory(String key);


    /**
     * Creates a new Data Point
     * @param name a valid point name
     * @param entityType @see EntityType
     * @return new point
     *
     */
    Point createPoint(String name, EntityType entityType, Entity parent);

    /**
     * Creates a new Data Point
     * @param name a valid point name
     * @param entityType @see EntityType
     * @param expire how many days old data can be before being purged
     * @return new point
     *
     */
    Point createPoint(String name, int expire, EntityType entityType, Entity parent);

    void deleteEntity(String name, EntityType type);
}
