package com.nimbits.client.model.schedule;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.common.CommonIdentifier;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;

import java.io.Serializable;
import java.util.Date;

public class ScheduleModel extends EntityModel implements Serializable, Schedule {

    private Boolean enabled;
    private Long interval;
    private String source;
    private String target;
    private Long lastProcessed;


    private ScheduleModel() {

    }

    public ScheduleModel(CommonIdentifier name, String description, EntityType entityType, ProtectionLevel protectionLevel, String parent, String owner, String uuid, boolean enabled, long interval, String source, String target) {
        super(name, description, entityType, protectionLevel, parent, owner, uuid);
        this.enabled = enabled;
        this.interval = interval;
        this.source = source;
        this.target = target;
        this.lastProcessed = new Date().getTime();
    }

    public ScheduleModel(Entity anEntity, Boolean enabled, Long interval, String source, String target) {
        super(anEntity);
        this.enabled = enabled;
        this.interval = interval;
        this.source = source;
        this.target = target;

    }

    public ScheduleModel(Schedule schedule) {
        super(schedule);
        this.enabled = schedule.isEnabled();
        this.interval = schedule.getInterval();
        this.source = schedule.getSource();
        this.target = schedule.getTarget();
    }

    @Override
    public Boolean isEnabled() {
        return enabled;
    }
    @Override
    public Long getInterval() {
        return interval;
    }
    @Override
    public String getSource() {
        return source;
    }
    @Override
    public String getTarget() {
        return target;
    }
    @Override
    public void setLastProcessed(Long lastProcessed) {
        this.lastProcessed = lastProcessed;
    }
    @Override
    public Long getLastProcessed() {

        return lastProcessed == null ? 0 : lastProcessed;
    }
    @Override
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    @Override
    public void setInterval(Long interval) {
        this.interval = interval;
    }
    @Override
    public void setSource(String source) {
        this.source = source;
    }
    @Override
    public void setTarget(String target) {
        this.target = target;
    }
}
