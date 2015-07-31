package com.nimbits.io.helper.impl;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.enums.SummaryType;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.CalculationModelFactory;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryFactory;
import com.nimbits.client.model.common.SimpleValue;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.summary.Summary;
import com.nimbits.client.model.summary.SummaryModelFactory;
import com.nimbits.client.model.trigger.TargetEntity;
import com.nimbits.client.model.trigger.TriggerEntity;
import com.nimbits.io.NimbitsClient;
import com.nimbits.io.helper.EntityHelper;
import com.nimbits.io.helper.HelperFactory;
import com.nimbits.io.helper.PointHelper;
import com.nimbits.io.http.NimbitsClientFactory;

import java.util.Date;
import java.util.List;


public class EntityHelperImpl implements EntityHelper {

    protected final NimbitsClient nimbitsClient;
    protected final Server server;

    public EntityHelperImpl(Server server) {

        this.server = server;

        this.nimbitsClient = NimbitsClientFactory.getInstance(server);
    }

    @Override
    public void deleteEntity(Entity entity) {
        nimbitsClient.deleteEntity(entity);
    }

    @Override
    public Entity addEntity(Entity e) {

        return nimbitsClient.addEntity(e);
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
        return (Category) addEntity(category);

    }

    @Override
    public Entity getCategory(String key) {
        Entity sample = nimbitsClient.getEntity(
                SimpleValue.getInstance(key), EntityType.category);

        return sample;

    }

    @Override
    public Point createPoint(String name, EntityType entityType, Entity parent) {
        Entity entity = EntityModelFactory.createEntity(name, entityType);
        entity.setParent(parent.getKey());
        entity.setOwner(server.getEmail().getValue());

        Point point = PointModelFactory.createPoint(entity);
        return (Point) addEntity(point);


    }

    @Override
    public Point createPoint(String name, int expire, FilterType filterType, EntityType entityType, Entity parent) {
        Entity entity = EntityModelFactory.createEntity(name, entityType);
        entity.setParent(parent.getKey());
        entity.setOwner(server.getEmail().getValue());

        Point point = PointModelFactory.createPoint(entity);
        point.setExpire(expire);
        point.setFilterType(filterType);
        return (Point) addEntity(point);


    }

    @Override
    public void deleteEntity(String name, EntityType type) {
        SimpleValue<String> id;
        if (!name.startsWith(server.getEmail().getValue())) {
            id = SimpleValue.getInstance(server.getEmail().getValue() + "/" + name);
        } else {
            id = SimpleValue.getInstance(name);
        }

        Entity sample = nimbitsClient.getEntity(id, type);
        deleteEntity(sample);


    }


    @Override
    public Calculation createCalculation(String name, String trigger, String target, String formula, String xVar, String yVar, String zVar) {
        PointHelper helper = HelperFactory.getPointHelper(this.server);
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
        TriggerEntity trigger1 = EntityModelFactory.createTrigger(triggerPoint.getKey());
        TargetEntity targetEntity = EntityModelFactory.createTarget(targetPoint.getKey());
        Calculation calculation = CalculationModelFactory.createCalculation(entity, trigger1, true, formula, targetEntity, x, y, z);
        calculation.setParent(triggerPoint.getKey());
        calculation.setOwner(server.getEmail().getValue());
        return (Calculation) addEntity(calculation);


    }

    @Override
    public Category createFolder(Entity parent, String name) {
        Category category = CategoryFactory.createCategory(parent, name);


        return addCategory(category);

    }

    @Override
    public Summary createSummary(String name, String trigger, String target, SummaryType summaryType, long intervalMs) {
        PointHelper helper = HelperFactory.getPointHelper(this.server);
        Point triggerPoint = helper.getPoint(trigger);
        Point targetPoint = helper.getPoint(target);

        Entity entity = EntityModelFactory.createEntity(name, EntityType.summary);
        TriggerEntity trigger1 = EntityModelFactory.createTrigger(triggerPoint.getKey());
        TargetEntity targetEntity = EntityModelFactory.createTarget(targetPoint.getKey());
        Summary summary = SummaryModelFactory.createSummary(entity, trigger1, targetEntity, true, summaryType, intervalMs,new Date());
        summary.setParent(triggerPoint.getKey());
        summary.setOwner(server.getEmail().getValue());
        return (Summary) addEntity(summary);


    }

}
