package com.nimbits.client.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.model.topic.Topic;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@PersistenceCapable
@JsonIgnoreProperties(ignoreUnknown = true)

@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class Filter extends Listener implements Serializable {

    private final static EntityType entityType = EntityType.filter;


    @Persistent
    private int filterType;

    @Persistent
    private double filterValue;


    public Filter(String name, String description, EntityType entityType, String parent, String owner, List<Topic> targets, List<Topic> triggers,
                  boolean enabled, FilterType filterType, double filterValue, boolean execute) {
        super(name, description, entityType, parent, owner, targets, triggers, enabled, execute);
        this.filterType = filterType.getCode();
        this.filterValue = filterValue;
    }

    public Filter() {
        super(entityType);
    }

    @Override
    public void update(Entity entity) {
        super.update(entity);
        Filter update = (Filter) entity;
        this.filterType = update.getFilterType().getCode();
        this.filterValue = update.getFilterValue();

    }

    public FilterType getFilterType() {
        return FilterType.get(filterType);
    }

    public void setFilterType(FilterType filterType) {
        this.filterType = filterType.getCode();
    }

    public double getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(double filterValue) {
        this.filterValue = filterValue;
    }

    public static class Builder extends TriggerBuilder {


        private FilterType filterType;


        private double filterValue;


        public Builder filterType(FilterType filterType) {
            this.filterType = filterType;
            return this;
        }

        public Builder filterValue(double filterValue) {
            this.filterValue = filterValue;
            return this;
        }


        @Override
        public Builder targets(List v) {
            if (this.targets == null) {
                this.targets = new ArrayList<Topic>(v.size());
            }
            this.targets.addAll(v);
            return this;
        }

        @Override
        public Builder triggers(List v) {
            if (this.triggers == null) {
                this.triggers = new ArrayList(v.size());
            }
            this.triggers.addAll(v);
            return this;
        }

        @Override
        public Builder target(Topic v) {
            if (this.targets == null) {
                this.targets = new ArrayList<Topic>();
            }
            this.targets.add(v);

            return this;
        }

        @Override
        public Builder trigger(Topic v) {
            if (this.triggers == null) {
                this.triggers = new ArrayList<Topic>();
            }
            this.triggers.add(v);
            return this;
        }


        public Builder enabled(boolean v) {
            this.enabled = v;
            return this;
        }

        public Builder name(String name) {
            this.name = CommonFactory.createName(name, entityType);
            return this;
        }


        public Builder execute(boolean execute) {
            this.execute = execute;
            return this;
        }

        public Filter create() {

            return new Filter(name, description, entityType, parent, owner, targets,
                    triggers, enabled, filterType, filterValue, execute);
        }


        public Builder parent(String parent) {

            this.parent = parent;
            return this;
        }


        public Builder init(Filter c) {
            super.init(c);


            return this;
        }


        public Builder description(String description) {
            this.description = description;
            return this;
        }


        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }


    }
}
