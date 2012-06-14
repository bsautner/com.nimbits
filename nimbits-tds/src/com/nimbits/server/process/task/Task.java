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

package com.nimbits.server.process.task;

import com.google.appengine.api.blobstore.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;

import javax.servlet.http.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 10/7/11
 * Time: 2:09 PM
 */
public interface Task {

    void startDeleteDataTask(final Point point, final boolean onlyExpired, final int exp);

    void startProcessBatchTask(User user, final HttpServletRequest req, final HttpServletResponse resp) throws NimbitsException;

    void startRecordValueTask(final User u, final Entity point, final Value value);

    void startIncomingMailTask(final String address, final String content);

    void startPointMaintTask(final Entity point);

    void startMoveCachedValuesToStoreTask(final Entity point);

    void startUpgradeTask(final Action action, final Entity entity, int s);

    void startDeleteOrphanedBlobTask(final BlobKey key);

    void startCoreTask(Entity entity, Action action, String instanceURL);
}
