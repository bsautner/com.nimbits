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

package com.nimbits.cloudplatform.server.http;

import java.io.IOException;
import java.net.ProtocolException;

/**
 * Created by Benjamin Sautner
 * User: benjamin
 * Date: 3/28/11
 * Time: 2:19 PM
 */
public interface HttpCommon {


    String doGet(final String postUrl, final String params, final String authCookie) ;

    String doPost(final String postUrl, final String params, final String authCookie) throws IOException;

    String doGet(final String postUrl, final String params);

    String doPost(final String postUrl, final String params);

    byte[] doGetBytes(final String postUrl, final String params, final String authCookie) throws Exception;

    String doJsonPost(final String postUrl, final String params, final String json);


}
