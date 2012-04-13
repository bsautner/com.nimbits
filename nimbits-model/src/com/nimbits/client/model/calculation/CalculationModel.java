package com.nimbits.client.model.calculation;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;

import java.io.Serializable;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/24/11
 * Time: 4:53 PM
 */
public class CalculationModel extends EntityModel implements Serializable, Calculation {
    private static final long serialVersionUID =1L;

    private String target;
    private String formula;
    private String x;
    private String y;
    private String z;
    private boolean enabled;
    private String trigger;


    protected CalculationModel() {
    }

    public CalculationModel(Calculation calculation) throws NimbitsException {
        super(calculation);
        this.target = calculation.getTarget();
        this.formula = calculation.getFormula();
        this.x = calculation.getX();
        this.y = calculation.getY();
        this.z = calculation.getZ();
        this.enabled = calculation.getEnabled();
        this.trigger = calculation.getTrigger();
    }


//    public CalculationModel(final String trigger, final boolean enabled, final String f, final String target, final String x, final String y, final String z) {
//        this.trigger = trigger;
//        this.target = target;
//        this.formula = f;
//        this.x = x;
//        this.y = y;
//        this.z = z;
//        this.enabled = enabled;
//
//    }

    public CalculationModel(Entity entity, String trigger, boolean enabled, String f, String target, String x, String y, String z) throws NimbitsException {
        super(entity);
        this.trigger = trigger;
        this.target = target;
        this.formula = f;
        this.x = x;
        this.y = y;
        this.z = z;
        this.enabled = enabled;
    }
    @Override
    public void setTarget(String target) {
        this.target = target;
    }
    @Override
    public void setFormula(String formula) {
        this.formula = formula;
    }
    @Override
    public void setX(String x) {
        this.x = x;
    }
    @Override
    public void setY(String y) {
        this.y = y;
    }
    @Override
    public void setZ(String z) {
        this.z = z;
    }
    @Override
    public void setTrigger(String trigger) {
        this.trigger = trigger;
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

    @Override
    public String getTrigger() {
        return  this.trigger;
    }
    @Override
    public void update(Entity update) throws NimbitsException {
        super.update(update);
        Calculation c = (Calculation) update;
        this.enabled = c.getEnabled();
        this.formula = c.getFormula();
        this.target = c.getTarget();
        this.x = c.getX();
        this.y = c.getY();
        this.z = c.getZ();
    }
}
