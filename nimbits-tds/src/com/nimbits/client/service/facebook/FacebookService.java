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

package com.nimbits.client.service.facebook;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.email.EmailAddress;

import java.io.UnsupportedEncodingException;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/13/11
 * Time: 12:28 PM
 */
@RemoteServiceRelativePath("facebook")
public interface FacebookService extends RemoteService {
    EmailAddress facebookLogin(final String code) throws UnsupportedEncodingException, NimbitsException;

    String updateStatus(final String token, final String message, final String picture, final String link, final String name, final String captions, final String description);

}
