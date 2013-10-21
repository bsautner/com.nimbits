/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.process.task;


import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by benjamin on 10/18/13.
 */
public interface TaskService {
    void startDeleteDataTask(Entity point,
                             boolean onlyExpired,
                             int exp);

    void startDeleteBlobTask(String key);

    void startDataDumpTask(Entity entity, Timespan timespan);

    void startUploadTask(User user, Point entity, String blobKey);

    void startProcessBatchTask(User user, HttpServletRequest req, HttpServletResponse resp);

    void startRecordValueTask(User u, Entity point, Value value);

    void startIncomingMailTask(String fromAddress, String inContent);

    void startPointMaintTask(Entity e);

    void startMoveCachedValuesToStoreTask(Entity point);
}
