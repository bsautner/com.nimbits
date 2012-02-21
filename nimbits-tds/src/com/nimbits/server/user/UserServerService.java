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

package com.nimbits.server.user;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.user.*;

import javax.servlet.http.*;

/**
 * Created by Benjamin Sautner
 * User: benjamin
 * Date: 4/17/11
 * Time: 7:17 PM
 */
public interface UserServerService {
    User getHttpRequestUser(final HttpServletRequest req) throws NimbitsException;
    User getUserByID(final long id) throws NimbitsException;
}
