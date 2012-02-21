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

package com.nimbits.server.recordedvalue;

import com.nimbits.client.service.recordedvalues.*;

public class RecordedValueServiceFactory {

    private static RecordedValueService instance;


    protected RecordedValueServiceFactory() {
        // Exists only to defeat instantiation.
    }

    public static RecordedValueService getInstance() {
        if (instance == null) {
            instance = new RecordedValueServiceImpl();
        }
        return instance;


    }
}
