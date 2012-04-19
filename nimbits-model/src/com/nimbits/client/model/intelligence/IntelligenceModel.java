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


import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.trigger.*;

import java.io.*;


public class IntelligenceModel  extends TriggerModel implements Serializable, Intelligence {

    private static final long serialVersionUID =1L;

    private String input;

    private String nodeId;

    private boolean resultsInPlainText;

    @SuppressWarnings("unused")
    private IntelligenceModel() {
    }

    public IntelligenceModel(final Entity entity,
                             final boolean enabled,
                             final String target,
                             final String input,
                             final String nodeId,
                             final boolean resultsInPlainText,
                             final String trigger) throws NimbitsException {
        super(entity, target, trigger, enabled);
        this.input = input;
        this.nodeId = nodeId;
        this.resultsInPlainText = resultsInPlainText;



    }

    public IntelligenceModel(final Intelligence intelligence) throws NimbitsException {
        super(intelligence);

        this.input = intelligence.getInput();
        this.nodeId = intelligence.getNodeId();
        this.resultsInPlainText = intelligence.getResultsInPlainText();


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


}
