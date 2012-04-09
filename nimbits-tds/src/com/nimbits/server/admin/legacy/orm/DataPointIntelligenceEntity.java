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

package com.nimbits.server.admin.legacy.orm;

import com.nimbits.client.enums.*;

import javax.jdo.annotations.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 10/29/11
 * Time: 10:50 AM
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
@Deprecated
public class DataPointIntelligenceEntity  {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private com.google.appengine.api.datastore.Key id;

    @Deprecated
    @Persistent(mappedBy = "dataPointIntelligenceEntity")
    private DataPoint point;



    @Persistent
    private Boolean enabled;

    @Persistent
    private Integer resultTarget;

    @Persistent
    @Deprecated
    public Long targetPointId;

    @Persistent
    private String target;

    @Persistent
    private String input;

    @Persistent
    private String nodeId;

    @Persistent
    private String trigger;

    @Persistent
    private String uuid;

    @Persistent
    private Boolean resultsInPlainText;

    public DataPointIntelligenceEntity() {
    }





    public boolean getEnabled() {
        return enabled;
    }


    public String getTrigger() {
        return trigger;
    }


    public void setTrigger(String trigger) {
      this.trigger = trigger;
    }


    public String getKey() {
        return uuid;
    }


    public void setKey(String uuid) {
        this.uuid = uuid;
    }


    public String getUUID() {
        return uuid;
    }


    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }


    public IntelligenceResultTarget getResultTarget() {
        return IntelligenceResultTarget.get(resultTarget);
    }


    public void setResultTarget(final IntelligenceResultTarget resultTarget) {
        this.resultTarget = resultTarget.getCode();
    }


    public String getInput() {
        return input == null ? "" : input;
    }


    public void setInput(final String input) {
        this.input = input;
    }


    public String getNodeId() {
        return nodeId == null ? "" : nodeId;
    }


    public void setNodeId(final String nodeId) {
        this.nodeId = nodeId;
    }


    public boolean getResultsInPlainText() {
        return resultsInPlainText;
    }


    public void setResultsInPlainText(final boolean resultsInPlainText) {
        this.resultsInPlainText = resultsInPlainText;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
