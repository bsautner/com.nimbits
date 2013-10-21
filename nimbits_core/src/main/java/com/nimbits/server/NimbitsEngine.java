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

package com.nimbits.server;

import com.nimbits.server.communication.xmpp.XmppService;
import com.nimbits.server.io.blob.BlobStore;
import com.nimbits.server.transaction.cache.NimbitsCache;
import com.nimbits.server.transaction.user.service.AuthenticationMechanism;

import javax.jdo.PersistenceManagerFactory;

/**
 * Created by benjamin on 10/18/13.
 */
public class NimbitsEngine {

    private final PersistenceManagerFactory pmf;
    private final NimbitsCache cache;

    private final BlobStore blobStore;
    private final XmppService xmppService;
    private final AuthenticationMechanism authenticationMechanism;

    public NimbitsEngine(PersistenceManagerFactory pmf, NimbitsCache cache,  BlobStore blobStore, XmppService xmppService, AuthenticationMechanism authenticationMechanism) {
        this.pmf = pmf;
        this.cache = cache;

        this.blobStore = blobStore;
        this.xmppService = xmppService;
        this.authenticationMechanism = authenticationMechanism;
    }

    public PersistenceManagerFactory getPmf() {
        return pmf;
    }

    public NimbitsCache getCache() {
        return cache;
    }



    public BlobStore getBlobStore() {
        return blobStore;
    }

    public XmppService getXmppService() {
        return xmppService;
    }

    public AuthenticationMechanism getAuthenticationMechanism() {
        return authenticationMechanism;
    }
}
