package com.nimbits.client.model.schedule;

import com.nimbits.client.model.entity.Entity;

public class ScheduleFactory {

    public static Schedule getInstance(final Schedule schedule) {

        return new ScheduleModel(schedule);

    }

    public static Schedule getInstance(Entity anEntity, Boolean enabled, Long interval, String source, String target) {

       return new ScheduleModel(anEntity, enabled, interval, source, target);

    }
}
