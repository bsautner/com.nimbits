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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model.intelligence;


import com.nimbits.client.enums.IntelligenceResultTarget;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;

import java.io.Serializable;


public class IntelligenceModel  extends EntityModel implements Serializable, Intelligence {
    private static final long serialVersionUID =1L;

    private boolean enabled;

    private int resultTarget;

    private String target;

    private String input;

    private String nodeId;

    private boolean resultsInPlainText;

    private String trigger;

    private IntelligenceModel() {
    }

    public IntelligenceModel(final Entity entity,
                             final boolean enabled,
                             final IntelligenceResultTarget resultTarget,
                             final String target,
                             final String input,
                             final String nodeId,
                             final boolean resultsInPlainText,
                             final String trigger) throws NimbitsException {
        super(entity);
        this.enabled = enabled;
        this.resultTarget = resultTarget.getCode();
        this.target = target;
        this.input = input;
        this.nodeId = nodeId;
        this.resultsInPlainText = resultsInPlainText;
        this.trigger = trigger;


    }

    public IntelligenceModel(final Intelligence intelligence) throws NimbitsException {
        super(intelligence);
        this.enabled = intelligence.getEnabled();
        this.resultTarget = intelligence.getResultTarget().getCode();
        this.target = intelligence.getTarget();
        this.input = intelligence.getInput();
        this.nodeId = intelligence.getNodeId();
        this.resultsInPlainText = intelligence.getResultsInPlainText();

        this.trigger = intelligence.getTrigger();

    }

    @Override
    public boolean getEnabled() {
        return enabled;
    }


    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public IntelligenceResultTarget getResultTarget() {
        return IntelligenceResultTarget.get(resultTarget);
    }

    @Override
    public void setResultTarget(IntelligenceResultTarget resultTarget) {
        this.resultTarget = resultTarget.getCode();
    }

    @Override
    public String getTarget() {
        return this.target;
    }

    @Override
    public void setTarget(String target) {
        this.target = target;
    }


    @Override
    public String getInput() {
        return input;
    }

    @Override
    public void setInput(final String formula) {
        this.input = formula;
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }

    @Override
    public void setNodeId(final String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public boolean getResultsInPlainText() {
        return resultsInPlainText;
    }

    @Override
    public void setResultsInPlainText(boolean resultsInPlainText) {
        this.resultsInPlainText = resultsInPlainText;
    }
    @Override
    public String getTrigger() {
        return trigger;
    }
    @Override
    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

}
