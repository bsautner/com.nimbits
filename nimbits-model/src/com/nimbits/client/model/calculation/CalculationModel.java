package com.nimbits.client.model.calculation;

import com.nimbits.client.model.Const;

import java.io.Serializable;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/24/11
 * Time: 4:53 PM
 */
public class CalculationModel implements Serializable, Calculation {
    private static final long serialVersionUID =1L;
    private long id;
    private String target;
    private String formula;
    private String x;
    private String y;
    private String z;
    private boolean enabled;
    private String trigger;
    private String uuid;

    public CalculationModel() {
    }

    public CalculationModel(Calculation calculation) {

        this.target = calculation.getTarget();
        this.formula = calculation.getFormula();
        this.x = calculation.getX();
        this.y = calculation.getY();
        this.z = calculation.getZ();
        this.enabled = calculation.getEnabled();
        this.uuid = calculation.getUUID();
        this.trigger = calculation.getTrigger();
    }


    public CalculationModel(final String trigger, final String uuid, final boolean enabled, final String f, final String target, final String x, final String y, final String z) {
        this.trigger = trigger;
        this.target = target;
        this.formula = f;
        this.x = x;
        this.y = y;
        this.z = z;
        this.enabled = enabled;
        this.uuid = uuid;

    }

    @Override
    public String getFormula() {
        return formula;
    }

    @Override
    public Boolean getEnabled() {
        return enabled;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public String getX() {
        return x;
    }

    @Override
    public String getY() {
        return y;
    }

    @Override
    public String getZ() {
        return z;

    }

    @Override
    public void setEnabled(boolean b) {
        this.enabled = b;
    }

    public String getUUID() {
        return uuid;
    }

    @Override
    public String getTrigger() {
       return  this.trigger;
    }
}
