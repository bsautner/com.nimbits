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

package com.nimbits.server.api.openid;



import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.step2.ConsumerHelper;
import org.openid4java.consumer.ConsumerAssociationStore;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;

/**
 * Simple wrapper around Guice for applications that use other frameworks/dependency injection
 * methods.  Provides access to a ConsumerHelper instance for implementing an OpenID relying party.
 */
public class ConsumerFactory {

    protected ConsumerHelper helper;

    public ConsumerFactory() {
        this(new InMemoryConsumerAssociationStore());
    }

    public ConsumerFactory(ConsumerAssociationStore store) {
        Injector injector = Guice.createInjector(new GuiceModule(store));
        helper = injector.getInstance(ConsumerHelper.class);
    }

    public ConsumerHelper getConsumerHelper() {
        return helper;
    }
}
