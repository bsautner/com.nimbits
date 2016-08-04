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

import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.trigger.Trigger;
import com.nimbits.client.model.user.User;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;


@PersistenceCapable
public class CalcEntity extends TriggerEntity implements Calculation {


    @Persistent
    private String formula;

    @Persistent
    private String xVar;

    @Persistent
    private String yVar;

    @Persistent
    private String zVar;


    @SuppressWarnings("unused")
    protected CalcEntity() {
    }


    public CalcEntity(final Calculation calculation) {
        super(calculation);

        this.formula = calculation.getFormula();
        this.xVar = calculation.getX();
        this.yVar = calculation.getY();
        this.zVar = calculation.getZ();

    }

    @Override
    public void setFormula(String formula) {
        this.formula = formula;
    }

    @Override
    public void setY(String xVar) {
        this.xVar = xVar;
    }

    @Override
    public void setX(String yVar) {
        this.yVar = yVar;
    }

    @Override
    public void setZ(String zVar) {
        this.zVar = zVar;
    }

    @Override
    public String getFormula() {
        return formula;
    }

    @Override
    public String getX() {
        return xVar;
    }

    @Override
    public String getY() {
        return yVar;
    }

    @Override
    public String getZ() {
        return zVar;
    }


    @Override
    public void update(final Entity update) {
        super.update(update);
        final Calculation c = (Calculation) update;
        this.formula = c.getFormula();
        this.xVar = c.getX();
        this.yVar = c.getY();
        this.zVar = c.getZ();
    }

    @Override
    public void validate(User user) {
        super.validate(user);
        if (formula == null || formula.isEmpty()) {
            throw new IllegalArgumentException("Invalid Formula");
        }

        if (formula.contains("x") && xVar.isEmpty()) {
            throw new IllegalArgumentException("Error in calc, your formula contains an x variable but no x point source.");
        }

        if (formula.contains("X")) {
            throw new IllegalArgumentException("Error in calc. Please use a lower case x instead of X");
        }

        if (formula.contains("y") && yVar.isEmpty()) {
            throw new IllegalArgumentException("Error in calc, your formula contains a y variable but no y point source..");
        }

        if (formula.contains("Y")) {
            throw new IllegalArgumentException("Error in calc. Please use a lower case y instead of Y");
        }

        if (formula.contains("z") && zVar.isEmpty()) {
            throw new IllegalArgumentException("Error in calc, your formula contains a z variable but no z point source.");
        }

        if (formula.contains("Z")) {
            throw new IllegalArgumentException("Error in calc. Please use a lower case z instead of Z");
        }

        //  RecursionValidation.validate(this);

    }

    @Override
    public void init(Entity anEntity) {

    }


    @Override
    public void init(Trigger entity) {

    }
}
