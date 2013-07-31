package com.nimbits.android.content;

import com.nimbits.cloudplatform.Nimbits;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.entity.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by benjamin on 7/25/13.
 */
public class ContentProvider {

    public static List<Entity> tree;
    public static Entity currentEntity;

    public static List<Entity> getTree() {
        return tree == null ? Collections.<Entity>emptyList() : tree;
    }

    public static void setTree(List<Entity> tree) {
        ContentProvider.tree = tree;
    }

    public static Entity getCurrentEntity() {
        return currentEntity == null ? Nimbits.session : currentEntity;
    }

    public static void setCurrentEntity(Entity currentEntity) {
        ContentProvider.currentEntity = currentEntity;
    }
    public static void setCurrentEntityToParent() {
        ContentProvider.setCurrentEntity(getParentEntity());
    }
    public static Entity getParentEntity() {
        for (Entity e :tree ) {
            if (ContentProvider.currentEntity.getParent().equals(e.getKey())) {
                return e;

            }
        }
        return Nimbits.session;
    }

    public static List<Entity> getChildEntities( ) {
        List<Entity> current = new ArrayList<Entity>();
        if (tree != null) {
        for (Entity entity : tree) {
            if (entity.getParent().equals(currentEntity.getKey()) && ! entity.getEntityType().equals(EntityType.user) && entity.getEntityType().isTreeGridItem()) {

                current.add(entity);
            }
        }
        return current;
        }
        else {
            return Collections.emptyList();
        }
    }

    public static void addEntities(List response) {
        tree.addAll(response);

    }
}
