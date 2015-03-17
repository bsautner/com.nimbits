package com.nimbits.io.helper;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.model.calculation.Calculation;
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


    Entity addEntity(Entity e);

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
    Entity addCategory(Category category);

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
     * @param filterType the type of filter to use. @See FilterType
     * @param expire how many days old data can be before being purged
     * @param parent the id of the parent entity
     * @return new point
     *
     */
    Point createPoint(String name, int expire, FilterType filterType, EntityType entityType, Entity parent);

    void deleteEntity(String name, EntityType type);

    /**
     *
     * @param name the name of the new calc
     * @param trigger the id of the point that will trigger this calc
     * @param target the id of the point that will get the result recorded to it
     * @param formula the formula to computer e.g. (x * 2) or (x + y + z)
     * @param xVar  nullable - the id of the point to use as the x variable
     * @param yVar nullable - the id of the point to use as the y variable
     * @param zVar  nullable - the id of the point to use as the z variable
     */

    Calculation createCalculation(String name, String trigger, String target, String formula, String xVar, String yVar, String zVar);

    /**
     * Create a new folder under the parent
     * @param name the new folder name
     * @param parent the parent entity, can be a user, point, another folder etc.
     */
    Category createFolder(Entity parent, String name);
}
