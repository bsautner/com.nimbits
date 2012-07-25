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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model.intelligence;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 11/10/11
 * Time: 11:20 AM
 */
public class IntelligenceModelFactory {

    private IntelligenceModelFactory() {
    }

    public static Intelligence createIntelligenceModel(Intelligence i) throws NimbitsException {
        return new IntelligenceModel(i);

    }


    public static Intelligence createIntelligenceModel(final Entity entity,
                                                       final boolean enabled,
                                                       final String targetPoint,
                                                       final String input,
                                                       final String nodeId,
                                                       final boolean resultsInPlainText,
                                                       final String trigger) throws NimbitsException {

        return new IntelligenceModel(entity, enabled, targetPoint, input, nodeId, resultsInPlainText, trigger);

    }

}
