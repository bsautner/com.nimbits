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
import com.nimbits.client.model.Const;

import java.io.Serializable;


public class IntelligenceModel implements Serializable, Intelligence {
    private static final long serialVersionUID = Const.DEFAULT_SERIAL_VERSION;
    private boolean enabled;

    private int resultTarget;

    private long targetPointId;

    private String input;

    private String nodeId;

    private boolean resultsInPlainText;


    public IntelligenceModel() {
    }

    public IntelligenceModel(final boolean enabled,
                             final IntelligenceResultTarget resultTarget,
                             final long targetPointId,
                             final String input,
                             final String nodeId,
                             final boolean resultsInPlainText) {
        this.enabled = enabled;
        this.resultTarget = resultTarget.getCode();
        this.targetPointId = targetPointId;
        this.input = input;
        this.nodeId = nodeId;
        this.resultsInPlainText = resultsInPlainText;

    }

    public IntelligenceModel(final Intelligence intelligence) {
        this.enabled = intelligence.getEnabled();
        this.resultTarget = intelligence.getResultTarget().getCode();
        this.targetPointId = intelligence.getTargetPointId();
        this.input = intelligence.getInput();
        this.nodeId = intelligence.getNodeId();
        this.resultsInPlainText = intelligence.getResultsInPlainText();
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
    public long getTargetPointId() {
        return targetPointId;
    }

    @Override
    public void setTargetPointId(long targetPointId) {
        this.targetPointId = targetPointId;
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

//    @Override
//    public String toString() {
////        JSONObject object = new JSONObject();
////        object.put("enabled",enabled);
////
////        object.put("resultTarget",resultTarget);
////
////        object.put("targetPointId",targetPointId);
////
////        object.put("input",input);
////
////        object.put("nodeId",nodeId);
////
////        object.put("resultsInPlainText",resultsInPlainText);
////
////
////        return  object.toString();
//        return "";
//
////        return "IntelligenceModel{" +
////                "enabled=" + enabled +
////                ", resultTarget=" + resultTarget +
////                ", targetPointId=" + targetPointId +
////                ", input='" + input + '\'' +
////                ", nodeId='" + nodeId + '\'' +
////                ", resultsInPlainText=" + resultsInPlainText +
////                '}';
//    }
}
