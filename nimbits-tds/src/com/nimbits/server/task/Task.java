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

package com.nimbits.server.task;

import com.nimbits.client.enums.Action;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 10/7/11
 * Time: 2:09 PM
 */
public interface Task {

    void startDeleteDataTask(final Point point, final boolean onlyExpired, final int exp);

    void startProcessBatchTask(final HttpServletRequest req, final HttpServletResponse resp) throws NimbitsException;

    void startRecordValueTask(final User u, final Point point, final Value value, final boolean loopFlag);

    void startIncomingMailTask(final String address, final String content);

    void startPointMaintTask(final Point point);

    void startMoveCachedValuesToStoreTask(final Point point);

    void startUpgradeTask(Action action, Entity entity);

    void startSummaryTask(Entity entity);
}
