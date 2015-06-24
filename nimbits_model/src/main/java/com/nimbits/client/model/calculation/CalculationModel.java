/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model.calculation;

import com.google.gson.annotations.Expose;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.trigger.TargetEntity;
import com.nimbits.client.model.trigger.TriggerEntity;
import com.nimbits.client.model.trigger.TriggerModel;

import java.io.Serializable;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/24/11
 * Time: 4:53 PM
 */
public class CalculationModel extends TriggerModel implements Serializable, Calculation {
    private static final long serialVersionUID = 1L;


    @Expose
    private String formula;

    @Expose
    private String x;

    @Expose
    private String y;

    @Expose
    private String z;


    @SuppressWarnings("unused")
    private CalculationModel() {
        super();
    }

    public CalculationModel(final Calculation calculation) {
        super(calculation);

        this.formula = calculation.getFormula();
        this.x = calculation.getX();
        this.y = calculation.getY();
        this.z = calculation.getZ();

    }


    public CalculationModel(final Entity entity,
                            final TriggerEntity trigger,
                            final boolean enabled,
                            final String f,
                            final TargetEntity target,
                            final String x,
                            final String y,
                            final String z) {
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
    public String getVar(String var) {
//TODO GWT 2.5.1 does not support java 7 - so no enum yet.

        if (var.equals("x")) {
            return getX();
        } else if (var.equals("y")) {
            return getY();
        } else if (var.equals("z")) {
            return getZ();
        } else {
            return "";
        }


    }

    @Override
    public void update(Entity update) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CalculationModel that = (CalculationModel) o;

        if (!formula.equals(that.formula)) return false;
        if (x != null ? !x.equals(that.x) : that.x != null) return false;
        if (y != null ? !y.equals(that.y) : that.y != null) return false;
        if (z != null ? !z.equals(that.z) : that.z != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + formula.hashCode();
        result = 31 * result + (x != null ? x.hashCode() : 0);
        result = 31 * result + (y != null ? y.hashCode() : 0);
        result = 31 * result + (z != null ? z.hashCode() : 0);
        return result;
    }
}
