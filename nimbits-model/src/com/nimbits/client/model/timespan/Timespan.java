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

package com.nimbits.client.model.timespan;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/25/11
 * Time: 7:03 PM
 */
public interface Timespan extends Serializable {

    Date getStart();

    Date getEnd();

    boolean isEndRequiresOffset();

    boolean isStartRequiresOffset();
}
