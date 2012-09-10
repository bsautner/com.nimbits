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

package com.nimbits.server.admin.system;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/27/12
 * Time: 4:33 PM
 */
public class SystemServiceFactory {

    private SystemServiceFactory() {
    }

    private static class SystemServiceHolder {
        static final SystemService instance = new SystemServiceImpl();

        private SystemServiceHolder() {
        }
    }

    public static SystemService getInstance() {
        return SystemServiceHolder.instance;

    }
}
