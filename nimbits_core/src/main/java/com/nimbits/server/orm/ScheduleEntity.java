/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.orm;

import com.nimbits.client.model.schedule.Schedule;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.io.Serializable;

@PersistenceCapable()
public class ScheduleEntity extends EntityStore implements Serializable, Schedule {

    @Persistent
    private Boolean enabled;

    @Persistent
    private Long interval;

    @Persistent
    private String source;

    @Persistent
    private String target;

    @Persistent
    private Long lastProcessed;


    public ScheduleEntity(Schedule schedule) {
        super(schedule);
        this.enabled = schedule.isEnabled();
        this.interval = schedule.getInterval();
        this.source = schedule.getSource();
        this.target = schedule.getTarget();
        this.lastProcessed = schedule.getLastProcessed();
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

        return lastProcessed == null ? 0 : Long.valueOf(lastProcessed);
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
        this.setSource(source);
    }

    @Override
    public void setTarget(String target) {
        this.setTarget(target);
    }


}
