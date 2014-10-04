package com.nimbits.io.helper.impl;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.CalculationModel;
import com.nimbits.client.model.calculation.CalculationModelFactory;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryModel;
import com.nimbits.client.model.common.SimpleValue;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.trigger.TargetEntity;
import com.nimbits.client.model.trigger.TriggerEntity;
import com.nimbits.io.NimbitsClient;
import com.nimbits.io.helper.EntityHelper;
import com.nimbits.io.helper.HelperFactory;
import com.nimbits.io.helper.PointHelper;
import com.nimbits.io.http.NimbitsClientFactory;

import java.util.List;

public class EntityHelperImpl implements EntityHelper {
    protected final EmailAddress email;
    protected final NimbitsClient nimbitsClient;
    protected final Server server;
    protected final String accessKey;
    public EntityHelperImpl(Server server, EmailAddress email, String accessKey) {
        this.email = email;
        this.server = server;
        this.accessKey = accessKey;
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

    @Override
    public Category addCategory(Category category) {
        List<Entity> sample = addEntity(category,  CategoryModel.class);
        if (sample.isEmpty()) {
            throw new RuntimeException("Couldn't create category");

        }
        else {
            return (Category) sample.get(0);
        }
    }

    @Override
    public Entity getCategory(String key) {
        List<Entity> sample = nimbitsClient.getEntity(
                SimpleValue.getInstance(key), EntityType.category, CategoryModel.class);
        if (sample.isEmpty()) {
            throw new RuntimeException("Category not found");

        }
        else {
            return  sample.get(0);
        }

    }

    @Override
    public Point createPoint(String name, EntityType entityType, Entity parent) {
        Entity entity = EntityModelFactory.createEntity(name, entityType);
        entity.setParent(parent.getKey());
        entity.setOwner(email.getValue());

        Point point =  PointModelFactory.createPoint(entity);
        List<Entity> sample = addEntity(point,  PointModel.class);
        if (sample.isEmpty()) {
            throw new RuntimeException("Couldn't create point");

        }
        else {
            return (Point) sample.get(0);
        }

    }

    @Override
    public Point createPoint(String name, int expire, FilterType filterType, EntityType entityType, Entity parent) {
        Entity entity = EntityModelFactory.createEntity(name, entityType);
        entity.setParent(parent.getKey());
        entity.setOwner(email.getValue());

        Point point =  PointModelFactory.createPoint(entity);
        point.setExpire(expire);
        point.setFilterType(filterType);
        List<Entity> sample = addEntity(point,  PointModel.class);
        if (sample.isEmpty()) {
            throw new RuntimeException("Couldn't create point");

        }
        else {
            return (Point) sample.get(0);
        }

    }

    @Override
    public void deleteEntity(String name, EntityType type) {
        SimpleValue<String> id;
        if (! name.startsWith(email.getValue())) {
            id = SimpleValue.getInstance(email + "/" + name);
        }
        else {
            id = SimpleValue.getInstance(name);
        }

        List<Point> sample;

        sample = nimbitsClient.getEntity(id, type, EntityModel.class);

        if (sample.isEmpty()) {
            throw new IllegalStateException("Entity Not Found");
        }
        else {
             deleteEntity(sample.get(0));
        }

    }


    @Override
    public Calculation createCalculation(String name, String trigger, String target, String formula, String xVar, String yVar, String zVar) {
        PointHelper helper = HelperFactory.getPointHelper(this.server, this.email, this.accessKey);
        Point triggerPoint = helper.getPoint(trigger);
        Point targetPoint = helper.getPoint(target);
        String x = null;
        String y = null;
        String z = null;
        if (xVar != null) {
            Point xPoint = helper.getPoint(xVar);
            x = xPoint.getKey();

        }
        if (yVar != null) {
            Point yPoint = helper.getPoint(yVar);
            y = yPoint.getKey();
        }

        if (zVar != null) {
            Point zPoint = helper.getPoint(zVar);
            z = zPoint.getKey();

        }

        Entity entity = EntityModelFactory.createEntity(name, EntityType.calculation);
        TriggerEntity trigger1 =EntityModelFactory.createTrigger(triggerPoint.getKey());
        TargetEntity targetEntity = EntityModelFactory.createTarget(targetPoint.getKey());
        Calculation calculation =  CalculationModelFactory.createCalculation(entity, trigger1, true, formula, targetEntity, x, y, z);
        calculation.setParent(triggerPoint.getKey());
        calculation.setOwner(email.getValue());
        List<Entity> result  =  addEntity(calculation, CalculationModel.class);
        if (result.isEmpty()) {
            throw new RuntimeException("failed to create calculation");

        }
        else {
            return (Calculation) result.get(0);
        }

    }

}
