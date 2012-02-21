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
import com.nimbits.client.model.entity.*;

import java.io.Serializable;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 10/29/11
 * Time: 3:14 PM
 */
public interface Intelligence extends Serializable {

    boolean getEnabled();

    String getTrigger();

    void setTrigger(String trigger);

    String getUuid();

    void setUuid(String uuid);

    String getUUID();

    void setEnabled(final boolean enabled);

    IntelligenceResultTarget getResultTarget();

    void setResultTarget(final IntelligenceResultTarget resultTarget);

    String getTarget();

    void setTarget(final String target);

    String getInput();

    void setInput(String input);

    String getNodeId();

    void setNodeId(final String nodeId);

    boolean getResultsInPlainText();

    void setResultsInPlainText(final boolean resultsInPlainText);


}
