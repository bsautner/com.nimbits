/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.service.docs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 8/3/12
 * Time: 3:21 PM
 */

@RemoteServiceRelativePath("driveService")
public interface DriveService extends RemoteService {
    String createGoogleDoc(Entity entity, String title) throws NimbitsException;

    void setSpreadsheetSize(Entity entity, int count, String title) throws NimbitsException;

    void addSpreadsheetHeader(Entity entity, String title) throws NimbitsException;

    int dumpValues(Entity entity, int count) throws NimbitsException;


    static class App {
        private static DriveServiceAsync ourInstance = GWT.create(DriveService.class);

        public static synchronized DriveServiceAsync getInstance() {
            return ourInstance;
        }
    }


}
