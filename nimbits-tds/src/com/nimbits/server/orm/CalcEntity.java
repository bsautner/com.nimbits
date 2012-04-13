/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.orm;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.calculation.*;
import com.nimbits.client.model.entity.*;

import javax.jdo.annotations.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/24/11
 * Time: 4:40 PM
 */


@PersistenceCapable
public class CalcEntity extends EntityStore implements Calculation {


    private static final long serialVersionUID = 9086813823531611499L;
    @Persistent
    private String formula;

    @Persistent
    private String trigger;

    @Persistent
    private Boolean enabled;

    @Persistent
    private String xVar;

    @Persistent
    private String yVar;

    @Persistent
    private String zVar;

    @Persistent
    private String targetVar;


    @SuppressWarnings("unused")
    protected CalcEntity() {
    }

    public CalcEntity(final Entity entity,
                      final String formula,
                      final String trigger,
                      final Boolean enabled,
                      final String xVar,
                      final String yVar,
                      final String zVar,
                      final String targetVar) throws NimbitsException {
        super(entity);
        this.formula = formula;
        this.trigger = trigger;
        this.enabled = enabled;
        this.xVar = xVar;
        this.yVar = yVar;
        this.zVar = zVar;
        this.targetVar = targetVar;
    }

    public CalcEntity(final Calculation calculation) throws NimbitsException {
        super(calculation);

        this.formula = calculation.getFormula();
        this.enabled = calculation.getEnabled();
        this.targetVar = calculation.getTarget();
        this.xVar = calculation.getX();
        this.yVar = calculation.getY();
        this.zVar = calculation.getZ();
        this.trigger = calculation.getTrigger();
    }

    @Override
    public void setFormula(String formula) {
        this.formula = formula;
    }

    @Override
    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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
    public void setTarget(String targetVar) {
        this.targetVar = targetVar;
    }

    @Override
    public String getTrigger() {
        return this.trigger;
    }


    @Override
    public String getFormula() {
        return formula;
    }

    @Override
    public Boolean getEnabled() {
        return enabled == null ? false : enabled;
    }

    @Override
    public String getTarget() {
        return targetVar;
    }

    @Override
    public String getX() {
        return xVar;
    }


    @Override
    public String getY() {
        return yVar ;
    }

    @Override
    public String getZ() {
        return zVar;
    }

    @Override
    public void setEnabled(final boolean b) {
        this.enabled = b;
    }

    @Override
    public void update(final Entity update) throws NimbitsException {
        super.update(update);
        final Calculation c = (Calculation) update;
        this.enabled = c.getEnabled();
        this.formula = c.getFormula();
        this.targetVar = c.getTarget();
        this.xVar = c.getX();
        this.yVar = c.getY();
        this.zVar = c.getZ();
    }
}
