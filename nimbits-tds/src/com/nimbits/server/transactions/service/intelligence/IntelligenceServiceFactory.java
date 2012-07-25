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

package com.nimbits.server.transactions.service.intelligence;

import com.nimbits.client.service.intelligence.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 11/10/11
 * Time: 6:47 PM
 */
public class IntelligenceServiceFactory {

    private IntelligenceServiceFactory() {
    }

    private static class IntelligenceServiceHolder {
        static final IntelligenceService instance = new IntelligenceServiceImpl();

        private IntelligenceServiceHolder() {
        }
    }

    public static IntelligenceService getInstance() {
        return IntelligenceServiceHolder.instance;
    }



}
