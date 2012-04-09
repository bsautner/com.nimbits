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

package com.nimbits.server.intelligence;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.intelligence.Intelligence;

import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/21/12
 * Time: 11:48 AM
 */
public interface IntelligenceTransactions {

    public Intelligence getIntelligence(Entity entity) throws NimbitsException;

    List<Intelligence> getIntelligences(Entity point) throws NimbitsException;

    void deleteIntelligence(Entity entity);
}
