/**
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.nimbits.server.external.google.openid;


import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.inject.*;
import com.google.step2.discovery.DefaultHostMetaFetcher;
import com.google.step2.discovery.HostMetaFetcher;
import com.google.step2.http.HttpFetcher;
import com.google.step2.hybrid.HybridOauthMessage;
import com.google.step2.openid.ax2.AxMessage2;
import com.google.step2.xmlsimplesign.*;
import com.nimbits.server.external.google.openid.appengine.AppEngineHttpFetcher;
import com.nimbits.server.external.google.openid.appengine.AppEngineTrustsRootProvider;
import com.nimbits.server.external.google.openid.appengine.Openid4javaFetcher;
import org.openid4java.consumer.ConsumerAssociationStore;
import org.openid4java.message.Message;
import org.openid4java.message.MessageException;

/**
 * Guice module for configuring the Step2 library.  Modified from the original example consumer
 * in the Step2 library to be slightly simpler.
 */
public class GuiceModule extends AbstractModule {

    ConsumerAssociationStore associationStore;

    public GuiceModule(ConsumerAssociationStore associationStore) {
        this.associationStore = associationStore;
    }

    @Override
    protected void configure() {
        try {
            Message.addExtensionFactory(AxMessage2.class);
        } catch (MessageException e) {
            throw new CreationException(null);
        }

        try {
            Message.addExtensionFactory(HybridOauthMessage.class);
        } catch (MessageException e) {
            throw new CreationException(null);
        }

        bind(ConsumerAssociationStore.class)
                .toInstance(associationStore);


        if (isRunningOnAppengine()) {
            install(new AppEngineModule());
        }
    }

    /**
     * Simple detection of whether or not we're running under GAE.
     * @return True if running on app engine.
     */
    private boolean isRunningOnAppengine() {
        if (System.getSecurityManager() == null) {
            return false;
        }
        return System.getSecurityManager().getClass().getCanonicalName()
            .startsWith("com.google");
    }

    @Provides
    @Singleton
    public CertValidator provideCertValidator(DefaultCertValidator defaultValidator) {
        CertValidator hardCodedValidator = new CnConstraintCertValidator() {
            @Override
            protected String getRequiredCn(String authority) {
                // Trust Google for signing discovery documents
                return "hosted-id.google.com";
            }
        };

        return new DisjunctiveCertValidator(defaultValidator, hardCodedValidator);
    }

    @Provides
    @Singleton
    public HostMetaFetcher provideHostMetaFetcher(
            DefaultHostMetaFetcher fetcher1,
            GoogleHostedHostMetaFetcher fetcher2) {
        // Domains may opt to host their own host-meta documents instead of outsourcing
        // to Google.  To try the domain's own host-meta, uncomment the SerialHostMetaFetcher
        // line to adopt a strategy that tries the domain's own version first then falls back
        // on the Google hosted version if that fails.  A parallel fetching strategy can also
        // be used to speed up fetching.
        //return new SerialHostMetaFetcher(fetcher1, fetcher2);
        return fetcher2; 
    }


    @Provides
    @Singleton
    public URLFetchService provideUrlFetchService() {
        return URLFetchServiceFactory.getURLFetchService();
    }

    /**
     * Overrides for running on GAE.  Need to ue special HTTP fetchers & explicitly set the trust roots
     * since the built-in java equivalents are not available when running in GAE's sandbox.
     */
    public static class AppEngineModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(HttpFetcher.class)
                .to(AppEngineHttpFetcher.class).in(Scopes.SINGLETON);
            bind(TrustRootsProvider.class)
                .to(AppEngineTrustsRootProvider.class).in(Scopes.SINGLETON);
            bind(org.openid4java.util.HttpFetcher.class)
                .to(Openid4javaFetcher.class)
                .in(Scopes.SINGLETON);
         }
    }

}
