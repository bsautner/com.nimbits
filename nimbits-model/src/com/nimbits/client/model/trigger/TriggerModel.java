package com.nimbits.client.model.trigger;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/19/12
 * Time: 12:30 PM
 */
public class TriggerModel extends EntityModel implements Serializable, Trigger {

    private String target;
    private String trigger;
    private boolean enabled;



    @SuppressWarnings("unused")
    protected TriggerModel() {
    }

    public TriggerModel(final Trigger anEntity) throws NimbitsException {
        super(anEntity);
        this.target = anEntity.getTarget();
        this.trigger = anEntity.getTrigger();
        this.enabled = anEntity.isEnabled();
    }

    public TriggerModel(final Entity anEntity, final TriggerEntity trigger, final TargetEntity target, boolean enabled) throws NimbitsException {
        super(anEntity);
        this.target = target.getValue();
        this.trigger =trigger.getValue();
        this.enabled =enabled;

    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public String getTrigger() {
        return trigger;
    }
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    @Override
    public void setTarget(String target) {
        this.target = target;
    }
    @Override
    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }
}
