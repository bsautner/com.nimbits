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

package com.nimbits.server.summary;

import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.summary.Summary;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 11:13 AM
 */
public interface SummaryTransactions {

    void addOrUpdateSummary(final Entity entity,final Summary summary);

    Summary readSummary(final Entity entity);

    void updateLastProcessed(final Entity entity);

    void deleteSummary(final Entity entity);
}
