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

package com.nimbits.client.model.common;

import com.nimbits.client.model.category.CategoryName;
import com.nimbits.client.model.diagram.DiagramName;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.point.PointName;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 8/6/11
 * Time: 11:08 AM
 */
public abstract interface CommonFactory {


    EmailAddress createEmailAddress(final String value);
    CategoryName createCategoryName(final String value);
    PointName createPointName(final String value);
    DiagramName createDiagramName(final String value);
}
