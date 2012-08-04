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

package com.nimbits.server.transactions.service.summary;

import com.nimbits.client.service.summary.SummaryService;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 12:38 PM
 */
public class SummaryServiceFactory {

    private SummaryServiceFactory() {
    }

    private static class SummaryServiceHolder {
        static final SummaryService instance = new SummaryServiceImpl();

        private SummaryServiceHolder() {
        }
    }

    public static SummaryService getInstance() {
        return SummaryServiceHolder.instance;
    }
}
