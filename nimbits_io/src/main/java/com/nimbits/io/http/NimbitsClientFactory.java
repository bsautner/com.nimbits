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

package com.nimbits.io.http;

import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;
import com.nimbits.io.NimbitsClient;


public class NimbitsClientFactory {

    public static NimbitsClient getInstance(final Server server, final EmailAddress email, final String accessKey)  {
        return new NimbitsClientImpl(server, email, accessKey);
    }

    public static NimbitsClient getInstance(Server server, EmailAddress emailAddress) {
        return new NimbitsClientImpl(server, emailAddress);
    }
}
