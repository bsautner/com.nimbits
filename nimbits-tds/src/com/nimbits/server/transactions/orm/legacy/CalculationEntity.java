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

package com.nimbits.server.transactions.orm.legacy;

import javax.jdo.annotations.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/24/11
 * Time: 4:40 PM
 */


@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
@Deprecated
public class CalculationEntity  {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    public com.google.appengine.api.datastore.Key id;

    @Persistent
    public String formula;

    @Persistent
    public String trigger;

    @Persistent
    public Boolean enabled;

    @Persistent
    @Deprecated
    public Long target;

    @Persistent
    @Deprecated
    public Long x;

    @Persistent
    @Deprecated
    public Long y;

    @Persistent
    @Deprecated
    public Long z;



    @Deprecated
    @Persistent(mappedBy = "calculationEntity")
    private DataPoint point;

    public CalculationEntity() {
    }



    public String getTrigger() {
        return this.trigger;
    }



    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }


    public Boolean getEnabled() {
        return enabled == null ? false : enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }


    public void setEnabled(boolean b) {
        this.enabled = b;
    }

}
