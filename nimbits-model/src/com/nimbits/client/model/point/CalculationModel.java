package com.nimbits.client.model.point;

import com.nimbits.client.model.Const;

import java.io.Serializable;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/24/11
 * Time: 4:53 PM
 */
public class CalculationModel implements Serializable, Calculation {
    private static final long serialVersionUID = Const.DEFAULT_SERIAL_VERSION;
    private long id;
    private long target;
    private String formula;
    private long x;
    private long y;
    private long z;
    private boolean enabled;


    public CalculationModel() {
    }

    public CalculationModel(Calculation calculation) {

        this.target = calculation.getTarget();
        this.formula = calculation.getFormula();
        this.x = calculation.getX();
        this.y = calculation.getY();
        this.z = calculation.getZ();
        this.enabled = calculation.getEnabled();


    }

    public CalculationModel(final boolean enabled, final String f, final long target, final long x, final long y, final long z) {
        this.target = target;
        this.formula = f;
        this.x = x;
        this.y = y;
        this.z = z;
        this.enabled = enabled;

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
    public Long getTarget() {
        return target;
    }

    @Override
    public Long getX() {
        return x;
    }

    @Override
    public Long getY() {
        return y;
    }

    @Override
    public Long getZ() {
        return z;

    }
}
