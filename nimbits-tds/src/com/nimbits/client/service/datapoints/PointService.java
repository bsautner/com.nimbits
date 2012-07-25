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

package com.nimbits.client.service.datapoints;


import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.value.*;

import java.io.*;
import java.util.*;

@RemoteServiceRelativePath("point")
public interface PointService extends RemoteService {

    String exportData(final Map<EntityName, Entity> points, final ExportType exportType, final Map<EntityName, List<Value>> values) throws NimbitsException, IOException;

   // Entity copyPoint(final User u, final Entity originalEntity, final EntityName newName) throws NimbitsException;


}
