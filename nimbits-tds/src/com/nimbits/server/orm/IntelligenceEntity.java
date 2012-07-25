/*
 * Copyright (c) 2010 Nimbits Inc.
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
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.intelligence.*;

import javax.jdo.annotations.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 10/29/11
 * Time: 10:50 AM
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class IntelligenceEntity extends TriggerEntity implements Intelligence {



    @Persistent
    private String input;

    @Persistent
    private String nodeId;

    @Persistent
    private Boolean resultsInPlainText;


    public IntelligenceEntity(final Intelligence intelligence) throws NimbitsException {
        super(intelligence);

        this.input = intelligence.getInput();
        this.nodeId = intelligence.getNodeId();
        this.resultsInPlainText = intelligence.getResultsInPlainText();

    }



    @Override
    public String getInput() {
        return input == null ? "" : input;
    }

    @Override
    public void setInput(final String input) {
        this.input = input;
    }

    @Override
    public String getNodeId() {
        return nodeId == null ? "" : nodeId;
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
    public void setResultsInPlainText(final boolean resultsInPlainText) {
        this.resultsInPlainText = resultsInPlainText;
    }



    @Override
    public void update(Entity update) throws NimbitsException {
        super.update(update);
        Intelligence c = (Intelligence) update;
        this.input = c.getInput();
        this.nodeId = c.getNodeId();
        this.resultsInPlainText = c.getResultsInPlainText();
        validate();
    }

    @Override
    public void validate() throws NimbitsException {


        super.validate();


    }
}
