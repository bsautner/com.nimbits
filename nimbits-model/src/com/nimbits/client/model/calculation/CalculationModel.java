package com.nimbits.client.model.calculation;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.trigger.*;

import java.io.Serializable;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/24/11
 * Time: 4:53 PM
 */
public class CalculationModel extends TriggerModel implements Serializable, Calculation {
    private static final long serialVersionUID =1L;


    private String formula;
    private String x;
    private String y;
    private String z;



    @SuppressWarnings("unused")
    private CalculationModel() {
        super();
    }

    public CalculationModel(final Calculation calculation) throws NimbitsException {
        super(calculation);

        this.formula = calculation.getFormula();
        this.x = calculation.getX();
        this.y = calculation.getY();
        this.z = calculation.getZ();

    }



    public CalculationModel(final Entity entity,
                            final String trigger,
                            final boolean enabled,
                            final String f,
                            final String target,
                            final String x,
                            final String y,
                            final String z) throws NimbitsException {
        super(entity, trigger, target, enabled);
        this.formula = f;
        this.x = x;
        this.y = y;
        this.z = z;
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
    public String getFormula() {
        return formula;
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
    public void update(Entity update) throws NimbitsException {
      throw new NimbitsException("Not Implemented");
    }
}
