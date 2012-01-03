package com.nimbits.server.orm;

import com.nimbits.client.model.point.Calculation;

import javax.jdo.annotations.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/24/11
 * Time: 4:40 PM
 */


@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")

public class CalculationEntity implements Calculation {
    private static final long serialVersionUID = 2L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private com.google.appengine.api.datastore.Key id;

    @Persistent
    private String formula;

    @Persistent
    private Boolean enabled;

    @Persistent
    private Long target;

    @Persistent
    private Long x;

    @Persistent
    private Long y;

    @Persistent
    private Long z;

    @Persistent(mappedBy = "calculationEntity")
    private DataPoint point;

    public CalculationEntity() {
    }

    public CalculationEntity(String formula, Boolean enabled, Long target, Long x, Long y, Long z) {
        this.formula = formula;
        this.enabled = enabled;
        this.target = target;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public CalculationEntity(Calculation calculation) {
        this.formula = calculation.getFormula();
        this.enabled = calculation.getEnabled();
        this.target = calculation.getTarget();
        this.x = calculation.getX();
        this.y = calculation.getY();
        this.z = calculation.getZ();
    }


    @Override
    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    @Override
    public Boolean getEnabled() {
        return enabled == null ? false : enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Long getTarget() {
        return target;
    }

    public void setTarget(Long target) {
        this.target = target;
    }

    @Override
    public Long getX() {
        return x;
    }

    public void setX(Long x) {
        this.x = x;
    }

    @Override
    public Long getY() {
        return y;
    }

    public void setY(Long y) {
        this.y = y;
    }

    @Override
    public Long getZ() {
        return z;
    }

    public void setZ(Long z) {
        this.z = z;
    }
}
