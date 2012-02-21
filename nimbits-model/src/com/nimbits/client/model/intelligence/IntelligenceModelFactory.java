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

/**
 * Created by bsautner
 * User: benjamin
 * Date: 11/10/11
 * Time: 11:20 AM
 */
public class IntelligenceModelFactory {

    public static Intelligence createIntelligenceModel(Intelligence i) {
        return new IntelligenceModel(i);

    }


    public static Intelligence createIntelligenceModel(final String uuid,
                                                       final boolean enabled,
                                                       final IntelligenceResultTarget resultTarget,
                                                       final String targetPoint,
                                                       final String input,
                                                       final String nodeId,
                                                       final boolean resultsInPlainText,
                                                       final String trigger) {

        return new IntelligenceModel(uuid, enabled, resultTarget, targetPoint, input, nodeId, resultsInPlainText, trigger);

    }

}
